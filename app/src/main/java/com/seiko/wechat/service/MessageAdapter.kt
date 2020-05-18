package com.seiko.wechat.service

import com.seiko.wechat.data.db.model.EmptyData
import com.seiko.wechat.data.db.model.MessageBean
import com.seiko.wechat.data.db.model.TextData
import com.seiko.wechat.util.annotation.MessageType
import com.seiko.wechat.util.extension.toUUID
import com.seiko.wechat.util.p2p.MesAdapter
import timber.log.Timber
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class MessageAdapter : MesAdapter<DataInputStream, DataOutputStream, MessageBean>() {
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
        check(version == 1.toByte()) { "Only version v1 is supported" }
        Timber.d("read version = $version")

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
                TextData(text = text)
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
