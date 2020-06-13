package com.seiko.wechat.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.seiko.wechat.R
import com.seiko.wechat.data.db.model.ImageData
import com.seiko.wechat.data.db.model.MessageBean
import com.seiko.wechat.data.db.model.MessageData
import com.seiko.wechat.data.db.model.TextData
import com.seiko.wechat.data.model.PeerBean
import com.seiko.wechat.data.pref.PrefDataSource
import com.seiko.wechat.data.repo.MessageRepository
import com.seiko.wechat.util.annotation.ItemType
import com.seiko.wechat.util.annotation.MessageState
import com.seiko.wechat.util.extension.safeOffer
import com.seiko.wechat.p2p.ConnectManager
import com.seiko.wechat.p2p.LivePeerManager
import com.seiko.wechat.p2p.model.Peer
import com.seiko.wechat.util.extension.collect
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.*
import okio.BufferedSink
import okio.BufferedSource
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class P2pChatService : Service(), CoroutineScope by MainScope() {

    companion object {
        private const val ACTION_READY = "ACTION_READY"
        private const val ACTION_SEND = "ACTION_SEND"

        private const val ARGS_SERVICE_NAME = "ARGS_SERVICE_NAME"
        private const val ARGS_PEER_BEAN = "ARGS_PEER_BEAN"
        private const val ARGS_MESSAGE_DATA = "ARGS_MESSAGE_DATA"

        /**
         * 发送广播，用户获取局域网内所有用户的反馈
         */
        @JvmStatic
        fun ready(context: Context, serviceName: String) {
            val intent = Intent(context, P2pChatService::class.java)
            intent.action = ACTION_READY
            intent.putExtra(ARGS_SERVICE_NAME, serviceName)
            context.startService(intent)
        }

        /**
         * 为目标设备发送信息
         */
        @JvmStatic
        fun send(context: Context, peer: PeerBean, data: MessageData) {
            val intent = Intent(context, P2pChatService::class.java)
            intent.action = ACTION_SEND
            intent.putExtra(ARGS_PEER_BEAN, peer)
            intent.putExtra(ARGS_MESSAGE_DATA, data)
            context.startService(intent)
        }
    }

    sealed class State {
        object Started: State()
        object Stopped: State()
        data class PeersChange(val peers: List<PeerBean>): State()
    }

    /**
     * 服务状态的Flow广播
     */
    @ExperimentalCoroutinesApi
    private val stateChannel = ConflatedBroadcastChannel<State>()
    @FlowPreview
    @ExperimentalCoroutinesApi
    private val stateFlow = stateChannel.asFlow()

    /**
     * 当前服务状态
     */
    @ExperimentalCoroutinesApi
    private var state: State = State.Stopped
        set(value) {
            if (field != value) {
                field = value
                stateChannel.safeOffer(value)
            }
        }

    /**
     * 消息队列
     */
    @ObsoleteCoroutinesApi
    private val messageActor = actor<Pair<String, MessageBean>>(
        context = coroutineContext + Dispatchers.IO,
        capacity = Channel.BUFFERED
    ) {
        var ip: String
        var msg: MessageBean
        for (pair in channel) {
            ip = pair.first ; msg = pair.second
            // 目前使用时间戳确定消息，更新状态
            if (connectManager.send(ip, msg)) {
                messageRepo.updateState(msg.time, MessageState.POSTED)
            } else {
                messageRepo.updateState(msg.time, MessageState.ERROR)
            }
        }
    }

    private val prefDataSource: PrefDataSource by inject()
    private val messageRepo: MessageRepository by inject()
    private val messageAdapter: MessageAdapter by inject()

    private lateinit var selfPeer: Peer
    private lateinit var peerManager: LivePeerManager
    private lateinit var connectManager: ConnectManager<BufferedSource, BufferedSink, MessageBean>

    private val peers = ConcurrentHashMap<UUID, Peer>(10)

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate")
        selfPeer = Peer(uuid = UUID.fromString(prefDataSource.deviceUUID))
        peerManager = LivePeerManager(selfPeer)
        connectManager = ConnectManager(messageAdapter)
        // 监听TCP服务
        launch {
            connectManager.stream()
                .collect { pair ->
                    val msg = pair.second
                    messageRepo.saveMessage(msg)
                }
        }
        // 监听UDP广播
        launch {
            peerManager.stream(peers)
                .onStart { state = State.Started }
                .onCompletion { state = State.Stopped }
                .map { map -> map.values.map { it.toBean() } }
                .flowOn(Dispatchers.Default)
                .collect { state = State.PeersChange(it) }
        }
    }

    @ExperimentalCoroutinesApi
    override fun onDestroy() {
        super.onDestroy()
        launch(NonCancellable) {
            stateChannel.close()
            peerManager.exit()
            connectManager.release()
        }
        cancel()
        Timber.d("onDestroy")
    }

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_READY -> {
                val serviceName = intent.getStringExtra(ARGS_SERVICE_NAME)
                actionBroadReady(serviceName)
            }
            ACTION_SEND -> {
                val peer = intent.getParcelableExtra<PeerBean>(ARGS_PEER_BEAN)
                val data = intent.getParcelableExtra<MessageData>(ARGS_MESSAGE_DATA)
                actionSendMessage(peer, data)
            }
        }
        return START_NOT_STICKY
    }

    /**
     * 发送广播，本设备准备就绪
     */
    private fun actionBroadReady(serviceName: String?) {
        launch {
            if (!serviceName.isNullOrEmpty()) {
                peers.clear()
                peerManager.ready(serviceName)
            }
        }
    }

    /**
     * 给目标设备发送数据
     */
    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    private fun actionSendMessage(peer: PeerBean?, data: MessageData?) {
        if (peer == null || data == null) return
        connectManager.connect(peer.address).collect(this) { success ->
            // 让本部保存的消息与发送的消息使用相同的时间戳，消息发送成功后一起更新状态
            val time = System.currentTimeMillis()

            // 本地保存, 如果未连接直接标记异常
            val sendBean = data.toSendBean(peer.uuid, time)
            if (!success) {
                sendBean.state = MessageState.ERROR
            }
            messageRepo.saveMessage(sendBean)

            // 如果对方设备通讯是断开的，不用发送
            if (!success) return@collect

            // 发送
            val receiveBean = data.toReceiveBean(selfPeer.uuid, time)
            messageActor.send(peer.address to receiveBean)
        }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private val binder by lazy(LazyThreadSafetyMode.NONE) { P2pBinder() }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    inner class P2pBinder: Binder() {

        fun getState(): Flow<State> = stateFlow

        fun connect(peer: PeerBean): Flow<Boolean> {
            return if (peer.address.isEmpty()) {
                flowOf(false)
            } else {
                connectManager.connect(peer.address)
            }
        }
    }
}

/**
 * 将Peer转为PeerBean
 */
private fun Peer.toBean(): PeerBean {
    val name = if (serviceName.isEmpty()) uuid.toString() else serviceName
    val address = if (addresses.isEmpty()) "" else addresses[0].hostAddress
    return PeerBean(
        uuid = uuid,
        name = name,
        logoResId = R.drawable.wechat_iv_2,
        address = address
    )
}

/**
 * data转成发送的msg，用于保存本地
 */
private fun MessageData.toSendBean(uuid: UUID, time: Long): MessageBean {
    return MessageBean(
        type = when(this) {
            is TextData -> ItemType.SEND_TEXT
            is ImageData -> ItemType.SEND_IMAGE
            else -> ItemType.NORMAL
        },
        uuid = uuid,
        time = time,
        data = this
    )
}

/**
 * data转成接收的msg，用于发送给对方
 */
private fun MessageData.toReceiveBean(uuid: UUID, time: Long): MessageBean {
    return MessageBean(
        type = when(this) {
            is TextData -> ItemType.RECEIVE_TEXT
            is ImageData -> ItemType.RECEIVE_IMAGE
            else -> ItemType.NORMAL
        },
        uuid = uuid,
        time = time,
        data = this
    )
}
