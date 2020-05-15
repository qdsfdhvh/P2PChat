package com.seiko.wechat.util.p2p

import com.seiko.wechat.util.extension.safeOffer
import com.seiko.wechat.util.p2p.model.MetaInfoBuilder
import com.seiko.wechat.util.p2p.model.Peer
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 在线设备管理
 */
class LivePeerManager(private val selfPeer: Peer) {
    companion object {
        private const val TAG = "OnlineUserManager"
        private const val PORT = 9156

        private const val TYPE_READY_CHAT = 0x000
        private const val TYPE_EXIT_CHAT= 0x001
        private const val TYPE_REPLY_CHAT = 0x002

        private const val KEY_TYPE = "KEY_TYPE"
    }

    private val peersChannel = ConflatedBroadcastChannel<Map<UUID, Peer>>()
    private val peersFlow = peersChannel.asFlow()

    private val peers = ConcurrentHashMap<UUID, Peer>()

    /**
     * 获取在线设备的集合流
     */
    fun getPeersFlow() = peersFlow

    /**
     * 释放资源
     */
    fun release() {
        clear()
        peersChannel.close()
    }

    /**
     * 监听UDP广播
     *
     * A -Broad-> B
     * A <-Reply- B
     * A <=add= B (if B not A)
     * A =add=> B
     */
    suspend fun listenForPeers(): Flow<Peer> {
        return NetworkUtils.listenForPeers(PORT)
            .onCompletion { clear() }
            .onEach { peer ->
                when(peer.metaInfo.getInt(KEY_TYPE)) {
                    TYPE_READY_CHAT -> {
                        Timber.tag(TAG).d("GET <- 接收准备广播，来自name=%s(%s), address=%s",
                            peer.serviceName, peer.uuid, peer.addresses)
                        if (addPeer(peer)) {
                            Timber.tag(TAG).d("被动添加用户，name=%s(%s), address=%s",
                                peer.serviceName, peer.uuid, peer.addresses)
                        }
                        // 收到任何广播请求，都做反馈
                        reply(peer)
                    }
                    TYPE_EXIT_CHAT -> {
                        Timber.tag(TAG).d("GET <- 接收退出广播, name=%s(%s), address=%s",
                            peer.serviceName, peer.uuid, peer.addresses)
                        if (removePeer(peer)) {
                            Timber.tag(TAG).d("删除用户，name=%s(%s), address=%s",
                                peer.serviceName, peer.uuid, peer.addresses)
                        }
                    }
                    TYPE_REPLY_CHAT -> {
                        Timber.tag(TAG).d("GET <- 接收反馈广播, name=%s(%s), address=%s",
                            peer.serviceName, peer.uuid, peer.addresses)
                        if (addPeer(peer)) {
                            Timber.tag(TAG).d("主动添加用户，name=%s(%s), address=%s",
                                peer.serviceName, peer.uuid, peer.addresses)
                        }
                    }
                }
            }
    }

    /**
     * 添加设备
     * 先判断是否有Key 再判断peer数据是否变化
     */
    fun addPeer(peer: Peer): Boolean {
        if (!peers.containsKey(peer.uuid) || !peers.contains(peer)) {
            peers[peer.uuid] = peer
            peersChannel.safeOffer(peers)
            return true
        }
        return false
    }

    /**
     * 删除设备
     */
    fun removePeer(peer: Peer): Boolean {
        if (peers.containsKey(peer.uuid)) {
            peers.remove(peer.uuid)
            peersChannel.safeOffer(peers)
            return true
        }
        return false
    }

    /**
     * 清空在线设备集合
     */
    fun clear() {
        if (peers.isNotEmpty()) {
            peers.clear()
            peersChannel.safeOffer(peers)
        }
    }

    /**
     * 广播所有主机，准备好加入聊天
     * @param serviceName 名称
     */
    suspend fun ready(serviceName: String) {
        val addresses = NetworkUtils.getAddresses()
        selfPeer.addresses = addresses
        selfPeer.serviceName = serviceName
        selfPeer.metaInfo = MetaInfoBuilder()
            .putInt(KEY_TYPE, TYPE_READY_CHAT)
            .build()
        NetworkUtils.broadcast(selfPeer, PORT)
        Timber.tag(TAG).d("POST -> 发送准备广播，name=%s", serviceName)
    }

    /**
     * 广播所有主机，退出聊天
     */
    suspend fun exit() {
        selfPeer.metaInfo = MetaInfoBuilder()
            .putInt(KEY_TYPE, TYPE_EXIT_CHAT)
            .build()
        NetworkUtils.broadcast(selfPeer, PORT)
        Timber.tag(TAG).d("POST -> 发送退出广播，我要退出聊天")
    }

    /**
     * 回复目标ip，我已加入聊天
     * @param targetIp 目标ip
     */
    private suspend fun reply(targetIp: String) {
//        selfPeer.port = NetworkDriver.getFreePort().toUShort()
        selfPeer.metaInfo = MetaInfoBuilder()
            .putInt(KEY_TYPE, TYPE_REPLY_CHAT)
            .build()
        NetworkUtils.send(targetIp, selfPeer, PORT)
        Timber.tag(TAG).d("POST -> 发送反馈广播，ip=$targetIp")
    }

    suspend fun reply(peer: Peer) {
        for (address in peer.addresses) {
            reply(address.hostAddress)
        }
    }

    protected fun finalize() {
        release()
    }
}