package com.seiko.wechat.util.p2p

import com.seiko.wechat.util.p2p.model.Address
import com.seiko.wechat.util.p2p.model.Peer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import timber.log.Timber
import java.net.*

object NetworkUtils {

    private const val TAG = "NetworkUtils"

    /**
     * 获取本地IP集合
     */
    fun getAddresses(): List<Address> {
        return NetworkInterface.getNetworkInterfaces().asSequence()
            .filter { !it.isLoopback }
            .flatMapTo(ArrayList(5)) { networkInterface ->
                networkInterface.inetAddresses.asSequence()
                    .mapNotNull { it as? Inet4Address }
            }
    }

    /**
     * 获取一个可用端口
     */
    fun getFreePort(): Int {
        return ServerSocket(0).use {
            it.localPort
        }
    }

    /**
     * 广播UDP数据包
     * @param peer 数据
     * @param port 目标端口
     */
    suspend fun broadcast(peer: Peer, port: Int) {
        return send("255.255.255.255", peer, port)
    }

    /**
     * 为目标ip发送UDP数据包
     * @param targetIp 目标ip
     * @param peer 数据
     * @param port 目标端口
     */
    suspend fun send(targetIp: String, peer: Peer, port: Int) {
        val body = peer.createBinaryMessage().readBytes()
        val address = Inet4Address.getByName(targetIp)

        val packet = DatagramPacket(body, body.size, address, port)

        val datagramSocket = DatagramSocket()
        datagramSocket.broadcast = true
        withContext(Dispatchers.IO) {
            datagramSocket.send(packet)
        }
        datagramSocket.close()
    }

    /**
     * 监听UDP端口
     * @param port udp端口
     */
    fun listenForPeers(port: Int): Flow<Peer> {
        return channelFlow {
            val serverSocket = DatagramSocket(port)

            invokeOnClose {
                serverSocket.close()
            }

            val buffer = ByteArray(65536)
            try {
                while (true) {
                    val receivePacket = DatagramPacket(buffer, buffer.size)

                    withContext(Dispatchers.IO) {
                        serverSocket.receive(receivePacket)
                    }

                    val bytePacket = buildPacket {
                        writeFully(buffer, 0, receivePacket.length)
                    }

                    try {
                        val peer = Peer.fromBinary(bytePacket)
                        send(peer)
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        Timber.tag(TAG).d("Failed parse packet %s", e.localizedMessage)
                    } finally {
                        bytePacket.release()
                    }
                }
            } catch (e: SocketException) {
                // socket exception are expected if flow is terminated
            }
        }
    }
}