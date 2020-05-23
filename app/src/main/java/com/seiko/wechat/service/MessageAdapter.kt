package com.seiko.wechat.service

import com.seiko.wechat.data.db.model.EmptyData
import com.seiko.wechat.data.db.model.ImageData
import com.seiko.wechat.data.db.model.MessageBean
import com.seiko.wechat.data.db.model.TextData
import com.seiko.wechat.data.model.Result
import com.seiko.wechat.domain.SaveResourceUseCase
import com.seiko.wechat.util.annotation.MessageType
import com.seiko.wechat.util.extension.toUUID
import com.seiko.wechat.util.getMd5Str
import com.seiko.wechat.util.p2p.MesAdapter
import timber.log.Timber
import java.io.*
import java.net.Socket

class MessageAdapter(
    private val saveResource: SaveResourceUseCase
) : MesAdapter<DataInputStream, DataOutputStream, MessageBean>() {

    companion object {
        private const val TAG = "MessageAdapter"
    }

    override fun createSink(socket: Socket): DataOutputStream {
        return socket.getOutputStream().data()
    }

    override fun createSource(socket: Socket): DataInputStream {
        return socket.getInputStream().data()
    }

    override fun decode(source: DataInputStream): MessageBean? {
        Timber.tag(TAG).d("解析数据...")

        // version
        val version = source.readByte()
        if (version != 1.toByte()) return null
        check(version == 1.toByte()) { "Only version v1 is supported" }

        // uuid
        val uuid = source.readUTF().toUUID()
        Timber.d("read uuid = $uuid")

        // time
        val time = source.readLong()
        Timber.d("read time = $time")

        // type
        val type = source.readInt()
        Timber.d("read type = $type")

        // data
        val data = when(source.readInt()) {
            MessageType.TEXT -> {
                val text = source.readUTF()
                Timber.d("read text: $text")
                TextData(text = text)
            }
            MessageType.IMAGE -> {
                val bytesLength = source.readInt()
                Timber.d("read imageLength: $bytesLength")
                val md5 = source.readUTF()
                Timber.d("read imageMd5: $md5")
                val bytes = receiveBytes(source, bytesLength)
                val filePath = when(val result = saveResource.invoke(uuid, md5, bytes)) {
                    is Result.Success -> result.data
                    is Result.Error -> return null
                }
                Timber.d("read image: $filePath")
                ImageData(filePath)
            }
            else -> return null
        }

        return MessageBean(
            uuid = uuid,
            type = type,
            time = time,
            data = data)
    }

    override fun encode(sink: DataOutputStream, msg: MessageBean) {
        Timber.tag(TAG).d("发送数据：${msg.data}")

        // version
        sink.writeByte(1)
        Timber.d("write version = 1")

        // uuid
        sink.writeUTF(msg.uuid.toString())
        Timber.d("write uuid = ${msg.uuid}")

        // time
        sink.writeLong(msg.time)
        Timber.d("write time = ${msg.time}")

        // type
        sink.writeInt(msg.type)

        // data
        when(val data = msg.data) {
            is EmptyData -> { /* no thing to do */ }
            is TextData -> {
                sink.writeInt(MessageType.TEXT)
                sink.writeUTF(data.text)
                Timber.d("write text: ${data.text}")
            }
            is ImageData -> {
                sink.writeInt(MessageType.IMAGE)
                val bytes = File(data.imagePath).readBytes()
                val bytesLength = bytes.size
                sink.writeInt(bytesLength)
                Timber.d("write imageLength: ${bytesLength}")
                val md5 = bytes.getMd5Str()
                sink.writeUTF(md5)
                Timber.d("write imageMd5: $md5")
                sendBytes(sink, bytes, bytesLength)
                Timber.d("write image: ${data.imagePath}")
            }
        }

        sink.flush()
    }
}

private inline fun InputStream.data(): DataInputStream {
    return DataInputStream(this)
}

private inline fun OutputStream.data(): DataOutputStream {
    return DataOutputStream(this)
}

private const val MAX_SEND_DATA = 30 * 1024 // 大约30Kb
private const val MAX_FILE_SEND_DATA = 45 * 1024 * 1024 // 大约45Mb

/**
 * 发送数据
 */
private fun sendBytes(output: OutputStream, bytes: ByteArray, bytesLength: Int) {
    var start = 0
    var end = 0
    while(end < bytesLength) {
        end += MAX_SEND_DATA
        if (end >= bytesLength) {
            end = bytesLength
        }
        output.write(bytes, start, end - start)
        Timber.d("POST -> 数据传输中，offset=${end - start}, len=$bytesLength")
        start = end
    }
}

/**
 * 接收数据
 */
private fun receiveBytes(input: InputStream, bytesLength: Int): ByteArray {
    val os = ByteArrayOutputStream()

    var bytesCopied: Long = 0
    val buffer = ByteArray(MAX_SEND_DATA)
    var bytes = input.read(buffer)
    while (bytes >= 0) {
        os.write(buffer, 0, bytes)
        bytesCopied += bytes
        if (bytesCopied >= bytesLength) {
            break
        }
        Timber.d("GET <- 数据接收中，offset=$bytesCopied, len=$bytesLength")
        bytes = input.read(buffer)
    }
    return os.toByteArray()
}

