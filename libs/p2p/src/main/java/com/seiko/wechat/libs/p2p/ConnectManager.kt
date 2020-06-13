package com.seiko.wechat.libs.p2p

import com.seiko.wechat.libs.p2p.extensions.filterTo
import com.seiko.wechat.libs.p2p.extensions.safeClose
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.coroutines.CoroutineContext

/**
 * 设备通讯管理 TCP连接
 */
class ConnectManager<I, O, T>(
    private val decoder: MesDecoder<I, T>,
    private val encoder: MesEncoder<O, T>
) : CoroutineScope {

    constructor(adapter: MesAdapter<I, O, T>) : this(adapter, adapter)

    companion object {
        private const val TAG = "ConnectManager"
        private const val PORT = 19155
    }

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO

    /**
     * 客户端 -> 设备 方式连接的Socket集合
     */
    private val inputClients = ConcurrentHashMap<String, Socket>(5)

    /**
     * 设备 -> 客户端 方式连接的Socket集合
     */
    private val outputClients = ConcurrentHashMap<String, Socket>(5)
    private val sinks = ConcurrentHashMap<String, O>(5)

    /**
     * 开启TCP服务，等待客户端连接
     */
    @FlowPreview
    @ExperimentalCoroutinesApi
    suspend fun stream(): Flow<Pair<String, T>> {
        return NetworkUtils.listenerTcpPort(PORT)
            .onCompletion { clear() }
            .filterTo(inputClients) { it.ip }
            .flatMapMerge { socket ->
                val ip = socket.ip
                socket.receive(decoder).map { ip to it }
            }
    }

    /**
     * 连接客户端
     * @param targetIp 目标IP
     */
    @ExperimentalCoroutinesApi
    fun connect(targetIp: String): Flow<Boolean> {
        return flow {
            if (isConnect(targetIp)) {
                Timber.tag(TAG).d("已经连接客户端IP=$targetIp")
                emit(true)
                return@flow
            }
            val socket = Socket()
            try {
                val address = InetSocketAddress(targetIp, PORT)
                withContext(Dispatchers.IO) {
                    socket.connect(address, 5000)
                }
                outputClients[socket.ip] = socket
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
    fun getSocket(targetIp: String) = inputClients[targetIp]

    /**
     * 清空数据
     */
    fun clear() {
        inputClients.clearAndClose()
        outputClients.clearAndClose()
        sinks.clear()
    }

    /**
     * 发送数据
     * @param targetIp 目标IP
     * @param data 发送的数据
     */
    fun send(targetIp: String, data: T): Boolean {
        var sink = sinks[targetIp]
        if (sink == null) {
            val socket = outputClients[targetIp] ?: return false
            sink = encoder.createSink(socket)
            sinks[targetIp] = sink
        }
        return try {
            encoder.encode(sink!!, data)
            true
        } catch (e: SocketException) {
            Timber.tag(TAG).w(e)
            close(targetIp) // 通讯异常，关闭通讯
            false
        }
    }

    /**
     * 目前设备是否处于连接状态
     */
    fun isConnect(targetIp: String): Boolean {
        if (!outputClients.containsKey(targetIp)) {
            return false
        }
        return outputClients[targetIp]?.isConnected ?: false
    }

    /**
     * 关闭设备
     * @param targetIp 设备IP
     */
    fun close(targetIp: String) {
        val socket = outputClients.remove(targetIp)
        if (socket != null) {
            sinks.remove(targetIp)
            socket.safeClose()
        }
    }

    fun release() {
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
private suspend fun <I, T> Socket.receive(decoder: MesDecoder<I, T>): Flow<T> {
    return flow<T> {
        val input = withContext(Dispatchers.IO) {
            decoder.createSource(this@receive)
        }
        try {
            while (isConnected) {
                val data = decoder.decode(input)
                if (data != null) {
                    emit(data)
                }
            }
        } catch (ignored: IOException) {
            // java.net.SocketException: Socket closed
        }
    }.flowOn(Dispatchers.IO)
}

/**
 * 获取Socket的IP
 */
private val Socket.ip get() = inetAddress.hostAddress

/**
 * 清空字典并关闭Closeable
 */
private fun <K, V : Closeable> ConcurrentMap<K, V>.clearAndClose() {
    keys.asSequence()
        .mapNotNull { key -> remove(key) }
        .forEach { it.close() }
}