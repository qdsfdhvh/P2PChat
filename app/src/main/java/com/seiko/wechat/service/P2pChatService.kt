package com.seiko.wechat.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.seiko.wechat.R
import com.seiko.wechat.data.db.model.MessageBean
import com.seiko.wechat.data.model.PeerBean
import com.seiko.wechat.data.pref.PrefDataSource
import com.seiko.wechat.util.extension.safeOffer
import com.seiko.wechat.util.p2p.ConnectManager
import com.seiko.wechat.util.p2p.LivePeerManager
import com.seiko.wechat.util.p2p.model.Peer
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class P2pChatService : Service(), CoroutineScope by MainScope() {

    companion object {
        private const val ACTION_READY = "ACTION_READY"

        private const val ARGS_SERVICE_NAME = "ARGS_SERVICE_NAME"

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
    }

    sealed class State {
        object Started: State()
        object Stopped: State()
        data class PeersChange(val peers: List<PeerBean>): State()
    }

    /**
     * 服务状态的Flow广播
     */
    private val stateChannel = ConflatedBroadcastChannel<State>()
    private val stateFlow = stateChannel.asFlow()

    inner class P2pBinder: Binder() {

        fun getState(): Flow<State> {
            return stateFlow
        }

        suspend fun connect(peer: PeerBean): Flow<Boolean> {
            if (peer.address.isEmpty()) {
                return flowOf(false)
            }
            return connectManager.connect(peer.address)
        }
    }

    /**
     * 当前服务状态
     */
    private var state: State = State.Stopped
        set(value) {
            if (field != value) {
                field = value
                stateChannel.safeOffer(value)
            }
        }

    private val prefDataSource: PrefDataSource by inject()

    private lateinit var peerManager: LivePeerManager
    private lateinit var connectManager: ConnectManager<Int>

    private val peers = ConcurrentHashMap<UUID, Peer>(10)

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate")
        val selfPeer = Peer(uuid = UUID.fromString(prefDataSource.deviceUUID))
        peerManager = LivePeerManager(selfPeer)
        connectManager= ConnectManager(MessageAdapter())
        // 监听TCP服务
        launch {
            connectManager.listenForUser()
                .collect { pair ->
                    Timber.d("接受IP=${pair.first}的数据value=${pair.second}")
                }
        }
        // 监听UDP广播
        launch {
            peerManager.listenForPeers(peers)
                .onStart { state = State.Started }
                .onCompletion { state = State.Stopped }
                .map { list -> list.map { it.toBean() } }
                .flowOn(Dispatchers.Default)
                .collect { state = State.PeersChange(it) }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return P2pBinder()
    }

    override fun onDestroy() {
        super.onDestroy()
        launch(NonCancellable) {
            stateChannel.close()
            connectManager.release()
        }
        cancel()
        Timber.d("onDestroy")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_READY -> {
                launch {
                    val serviceName = intent.getStringExtra(ARGS_SERVICE_NAME)
                    if (!serviceName.isNullOrEmpty()) {
                        peers.clear()
                        peerManager.ready(serviceName)
                    }
                }
            }
        }
        return START_NOT_STICKY
    }

}

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