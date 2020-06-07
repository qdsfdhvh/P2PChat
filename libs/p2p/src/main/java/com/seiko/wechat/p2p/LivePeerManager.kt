package com.seiko.wechat.p2p

import com.seiko.wechat.p2p.model.MetaInfoBuilder
import com.seiko.wechat.p2p.model.Peer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.io.core.readBytes
import timber.log.Timber
import java.util.*
import java.util.concurrent.ConcurrentMap
/**
 * 在线设备管理 UDP广播
 */
class LivePeerManager(private val selfPeer: Peer) {
    companion object {
        private const val TAG = "OnlineUserManager"
        private const val PORT = 19156

        private const val TYPE_READY_CHAT = 0x000
        private const val TYPE_EXIT_CHAT= 0x001
        private const val TYPE_REPLY_CHAT = 0x002

        private const val KEY_TYPE = "KEY_TYPE"
    }

    /**
     * 监听UDP广播
     *
     * A -Broad-> B
     * A <-Reply- B
     * A <=add= B (if B not A)
     * A =add=> B
     */
    @FlowPreview
    @ExperimentalCoroutinesApi
    suspend fun stream(
        peers: ConcurrentMap<UUID, Peer>
    ): Flow<Map<UUID, Peer>> {
        return NetworkUtils.listenerUdpPort(PORT)
            .flatMapConcat { bytePacket ->
                flow {
                    try {
                        emit(Peer.fromBinary(bytePacket))
                    } catch (e: Exception) {
                        Timber.tag(TAG).d("Failed parse packet %s", e.localizedMessage)
                    } finally {
                        bytePacket.release()
                    }
                }
            }
            .flatMapConcat { peer ->
                flow<Map<UUID, Peer>> {
                    when(peer.metaInfo.getInt(KEY_TYPE)) {
                        TYPE_READY_CHAT -> {
                            Timber.tag(TAG).d("GET <- 接收准备广播，来自name=%s(%s), address=%s",
                                peer.serviceName, peer.uuid, peer.addresses)
                            if (peers.addPeer(peer)) {
                                emit(peers)
                                Timber.tag(TAG).d("被动添加用户，name=%s(%s), address=%s",
                                    peer.serviceName, peer.uuid, peer.addresses)
                            }
                            // 收到任何广播请求，都做反馈
                            reply(peer)
                        }
                        TYPE_EXIT_CHAT -> {
                            Timber.tag(TAG).d("GET <- 接收退出广播, name=%s(%s), address=%s",
                                peer.serviceName, peer.uuid, peer.addresses)
                            if (peers.removePeer(peer)) {
                                emit(peers)
                                Timber.tag(TAG).d("删除用户，name=%s(%s), address=%s",
                                    peer.serviceName, peer.uuid, peer.addresses)
                            }
                        }
                        TYPE_REPLY_CHAT -> {
                            Timber.tag(TAG).d("GET <- 接收反馈广播, name=%s(%s), address=%s",
                                peer.serviceName, peer.uuid, peer.addresses)
                            if (peers.addPeer(peer)) {
                                emit(peers)
                                Timber.tag(TAG).d("主动添加用户，name=%s(%s), address=%s",
                                    peer.serviceName, peer.uuid, peer.addresses)
                            }
                        }
                    }
                }
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
        broadcast(selfPeer, PORT)
        Timber.tag(TAG).d("POST -> 发送准备广播，name=%s", serviceName)
    }

    /**
     * 广播所有主机，退出聊天
     */
    suspend fun exit() {
        selfPeer.metaInfo = MetaInfoBuilder()
            .putInt(KEY_TYPE, TYPE_EXIT_CHAT)
            .build()
        broadcast(selfPeer, PORT)
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
        sendUdp(targetIp, selfPeer, PORT)
        Timber.tag(TAG).d("POST -> 发送反馈广播，ip=$targetIp")
    }

    private suspend fun reply(peer: Peer) {
        for (address in peer.addresses) {
            reply(address.hostAddress)
        }
    }
}

/**
 * 添加设备
 * 先判断是否有Key 再判断peer数据是否变化
 */
private fun ConcurrentMap<UUID, Peer>.addPeer(peer: Peer): Boolean {
    if (!containsKey(peer.uuid) || !containsValue(peer)) {
        put(peer.uuid, peer)
        return true
    }
    return false
}

/**
 * 删除设备
 */
private fun ConcurrentMap<UUID, Peer>.removePeer(peer: Peer): Boolean {
    if (containsKey(peer.uuid)) {
        remove(peer.uuid)
        return true
    }
    return false
}

/**
 * 广播本设备信息
 * @param peer 本设备信息
 * @param port 端口号
 */
@ExperimentalUnsignedTypes
private suspend fun broadcast(peer: Peer, port: Int) {
    val body = peer.createBinaryMessage().readBytes()
    NetworkUtils.broadcast(body, port)
}

/**
 * 为目标发送本设备信息
 * @param targetIp 目标设备
 * @param peer 本设备信息
 * @param port 端口号
 */
@ExperimentalUnsignedTypes
private suspend fun sendUdp(targetIp: String, peer: Peer, port: Int) {
    val body = peer.createBinaryMessage().readBytes()
    NetworkUtils.sendUdp(targetIp, body, port)
}