package com.seiko.wechat.util.p2p

import com.seiko.wechat.util.extension.safeOffer
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.net.Socket
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

/**
 * 设备通讯管理 TCP连接
 */
class ConnectManager<T>(private val adapter: MesAdapter<T>) : CoroutineScope {

    companion object {
        private const val TAG = "ConnectManager"
        private const val PORT = 19155
    }

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO

    private val clients = ConcurrentHashMap<String, Socket>(5)
    private val receiveJobs = ConcurrentHashMap<String, Job>(5)

    private val msgChannel = ConflatedBroadcastChannel<Pair<String, T>>()
    private val msgFlow = msgChannel.asFlow()

    /**
     * 开启TCP服务，等待客户端连接
     */
    @FlowPreview
    @ExperimentalCoroutinesApi
    suspend fun listenForUser(): Flow<Socket> {
        return NetworkUtils.listenerTcpPort(PORT)
            .flowOn(Dispatchers.IO)
            .onCompletion { clear() }
            .filter { !clients.containsKey(it.ip) }
            .onEach { putClient(it) }
    }

    /**
     * 连接客户端
     * @param targetIp 目标IP
     */
    suspend fun connect(targetIp: String): Flow<Boolean> {
        return flow {
            if (clients.containsKey(targetIp)) {
                Timber.tag(TAG).d("已经连接客户端IP=$targetIp")
                emit(true)
                return@flow
            }
            try {
                val socket = Socket()
                val address = InetSocketAddress(targetIp, PORT)
                socket.connect(address, 5000)
                putClient(socket)
                Timber.tag(TAG).d("连接成功客户端IP=$targetIp")
                emit(true)
            } catch (e: IOException) {
                e.printStackTrace()
                emit(false)
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 获得相应IP的Socket
     */
    fun getSocket(targetIp: String) = clients[targetIp]

    /**
     * 保存
     */
    private fun putClient(socket: Socket) {
        val ip = socket.ip
        clients[ip] = socket
        val job = launch {
            socket.receive(adapter)
                .collect { msgChannel.safeOffer(ip to it) }
        }
        receiveJobs[ip] = job
        Timber.d("有客户端IP连接到服务IP=${ip}")
    }

    /**
     * 清空数据
     */
    suspend fun clear() {
        clients.keys().asSequence()
            .mapNotNull { clients.remove(it) }
            .forEach { socket ->
                coroutineScope {
                    launch(NonCancellable) {
                        socket.close()
                    }
                }
            }
        receiveJobs.keys().asSequence()
            .mapNotNull { receiveJobs.remove(it) }
            .forEach { it.cancel() }
    }

    fun release() {
        msgChannel.close()
        cancel()
    }

    protected fun finalize() {
        release()
    }
}

/**
 * 接收字节，并解析为相应的字节
 */
@ExperimentalCoroutinesApi
private suspend fun <T> Socket.receive(decoder: MesDecoder<T>): Flow<T> {
    return flow<T> {
        val input = getInputStream().data()
        while (isConnected) {
            val data = decoder.decode(input)
            if (data != null) {
                emit(data)
            }
            delay(2)
        }
    }.flowOn(Dispatchers.IO)
}

/**
 * 获取Socket的IP
 */
private val Socket.ip get() = inetAddress.hostAddress

@Suppress("NOTHING_TO_INLINE")
private inline fun InputStream.data(): DataInputStream {
    return DataInputStream(this)
}