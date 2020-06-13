package com.seiko.wechat.service

import com.seiko.wechat.core.data.Result
import com.seiko.wechat.data.db.model.EmptyData
import com.seiko.wechat.data.db.model.ImageData
import com.seiko.wechat.data.db.model.MessageBean
import com.seiko.wechat.data.db.model.TextData
import com.seiko.wechat.domain.GetResourceFileUseCase
import com.seiko.wechat.util.annotation.MessageType
import com.seiko.wechat.libs.p2p.extensions.toUUID
import com.seiko.wechat.util.getMd5
import com.seiko.wechat.libs.p2p.MesAdapter
import com.seiko.wechat.util.toHexString
import okio.*
import timber.log.Timber
import java.io.*
import java.lang.StringBuilder
import java.net.Socket
import java.util.*

class MessageAdapter(
    private val getResourceFile: GetResourceFileUseCase
) : MesAdapter<BufferedSource, BufferedSink, MessageBean>() {

    companion object {
        private const val TAG = "MessageAdapter"
    }

    override fun createSource(socket: Socket): BufferedSource {
        return socket.getInputStream().source().buffer()
    }

    override fun createSink(socket: Socket): BufferedSink {
        return socket.getOutputStream().sink().buffer()
    }

    override fun decode(source: BufferedSource): MessageBean? {

        // version
        val version = source.readByte()
        check(version == 1.toByte()) { "Only version v1 is supported" }
        Timber.tag(TAG).d("read version = 1")

        // uuid
        val uuid = source.readUUID()
        Timber.tag(TAG).d("read uuid = $uuid")

        Timber.tag(TAG).d("解析数据3...")

        // time
        val time = source.readLong()
        Timber.tag(TAG).d("read time = $time")

        // type
        val type = source.readInt()
        Timber.tag(TAG).d("read type = $type")

        // data
        val data = when(source.readInt()) {
            MessageType.TEXT -> {
                val text = source.readUtf8WithLength()
                Timber.tag(TAG).d("read text: $text")
                TextData(text = text)
            }
            MessageType.IMAGE -> {

                val fileLength = source.readLong()
                Timber.tag(TAG).d("read imageLength: $fileLength")

                val md5 = source.readMd5()
                Timber.tag(TAG).d("read imageMd5: $md5")

                val file = when(val result = getResourceFile.invoke(uuid, md5)) {
                    is Result.Success -> result.data
                    is Result.Error -> {
                        Timber.e(result.error)
                        return null
                    }
                }
                file.deleteOnExit()

                file.sink().buffer().use {
                    it.write(source, fileLength)
                }

                Timber.tag(TAG).d("read image: ${file.absolutePath}")
                ImageData(file.absolutePath)
            }
            else -> return null
        }

        return MessageBean(
            uuid = uuid,
            type = type,
            time = time,
            data = data)
    }

    override fun encode(sink: BufferedSink, msg: MessageBean) {
        Timber.tag(TAG).d("发送数据：${msg.data}")

        // version
        sink.writeByte(1)
        Timber.tag(TAG).d("write version = 1")

        // uuid
        sink.writeUUID(msg.uuid)
        Timber.tag(TAG).d("write uuid = ${msg.uuid}")

        // time
        sink.writeLong(msg.time)
        Timber.tag(TAG).d("write time = ${msg.time}")

        // type
        sink.writeInt(msg.type)

        // data
        when(val data = msg.data) {
            is EmptyData -> { /* no thing to do */ }
            is TextData -> {
                sink.writeInt(MessageType.TEXT)
                sink.writeUtf8withLength(data.text)
                Timber.tag(TAG).d("write text: ${data.text}")
            }
            is ImageData -> {
                sink.writeInt(MessageType.IMAGE)

                val file = File(data.imagePath)

                val fileLength = file.length()
                sink.writeLong(fileLength)
                Timber.tag(TAG).d("write imageLength: $fileLength")

                val md5 = file.getMd5()
                sink.write(md5)
                Timber.tag(TAG).d("write imageMd5: ${md5.toHexString()}")

                file.source().buffer().use {
                    sink.write(it, fileLength)
                }
                Timber.tag(TAG).d("write image: ${data.imagePath}")

            }
        }

        sink.flush()
    }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun BufferedSink.writeUtf8withLength(text: String) {
    writeInt(text.length)
    writeUtf8(text)
}

@Suppress("NOTHING_TO_INLINE")
private inline fun BufferedSource.readUtf8WithLength(): String {
    val length = readInt()
    return readUtf8(length.toLong())
}

@Suppress("NOTHING_TO_INLINE")
private inline fun BufferedSink.writeUUID(uuid: UUID) {
    return writeUtf8withLength(uuid.toString())
}

@Suppress("NOTHING_TO_INLINE")
private inline fun BufferedSource.readUUID(): UUID {
    return readUtf8WithLength().toUUID()
}

private const val MD5_SIZE = 16

@Suppress("NOTHING_TO_INLINE")
private inline fun BufferedSource.readMd5(): String {
    val md5 = StringBuilder(32)
    for (i in 1 .. MD5_SIZE) {
        md5.append(java.lang.String.format("%02x", readByte()))
    }
    return md5.toString()
}
