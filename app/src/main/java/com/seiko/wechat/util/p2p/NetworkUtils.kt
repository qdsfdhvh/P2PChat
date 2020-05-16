package com.seiko.wechat.util.p2p

import android.net.TrafficStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.io.core.ByteReadPacket
import kotlinx.io.core.buildPacket
import timber.log.Timber
import java.io.IOException
import java.net.*

object NetworkUtils {

    private const val TAG = "NetworkUtils"

    /**
     * 获取本地IP集合
     */
    fun getAddresses(): List<Inet4Address> {
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
     * @param body 数据
     * @param port 目标端口
     */
    suspend fun broadcast(body: ByteArray, port: Int) {
        return sendUdp("255.255.255.255", body, port)
    }

    /**
     * 为目标ip发送UDP数据包
     * @param targetIp 目标ip
     * @param body 数据
     * @param port 目标端口
     */
    suspend fun sendUdp(targetIp: String, body: ByteArray, port: Int) {
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
     * @param port 端口号
     */
    @ExperimentalCoroutinesApi
    fun listenerUdpPort(port: Int): Flow<ByteReadPacket> {
        return channelFlow {
            val serverSocket = DatagramSocket(port)
            Timber.tag(TAG).d("开启UDP端口($port)...")
            invokeOnClose {
                serverSocket.close()
                Timber.tag(TAG).d("关闭UDP端口($port)")
            }
            val buffer = ByteArray(65536)
            try {
                while (isActive) {
                    val receivePacket = DatagramPacket(buffer, buffer.size)

                    withContext(Dispatchers.IO) {
                        serverSocket.receive(receivePacket)
                    }

                    val bytePacket = buildPacket {
                        writeFully(buffer, 0, receivePacket.length)
                    }
                    send(bytePacket)
                }
            } catch (e: SocketException) {
                // socket exception are expected if flow is terminated
            }
        }
    }

    /**
     * 监听TCP端口
     * @param port 端口号
     */
    @ExperimentalCoroutinesApi
    fun listenerTcpPort(port: Int): Flow<Socket> {
        return channelFlow {
            val serverSocket = try {
                ServerSocket(port)
            } catch (e: IOException) {
                close(e)
                return@channelFlow
            }
            Timber.tag(TAG).d("开启TCP端口($port)...")
            invokeOnClose {
                serverSocket.close()
                Timber.tag(TAG).d("关闭TCP端口($port)")
            }
            while (isActive) {
                try {
                    val socket = serverSocket.accept()
                    TrafficStats.tagSocket(socket)
                    send(socket)
                } catch (e: IOException) {
                    // socket exception are expected if flow is terminated
                }
            }
        }
    }
}