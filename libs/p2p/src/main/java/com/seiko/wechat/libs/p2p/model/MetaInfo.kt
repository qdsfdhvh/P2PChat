package com.seiko.wechat.libs.p2p.model

import kotlinx.io.core.*

/**
 * Peer所带的参数
 */
class MetaInfo internal constructor(internal val data: ByteArray) {
    private val map: Map<String, ByteArray> by lazy {
        val packet = buildPacket {
            writeFully(data)
        }

        packet.use {
            val count = packet.readUByte().toInt()

            val keys = sequence {
                repeat(count) {
                    val size = packet.readUByte().toInt()
                    yield(packet.readTextExactBytes(bytes = size))
                }
            }.toList()

            val values = sequence {
                repeat(count) {
                    val size = packet.readUShort().toInt()
                    yield(packet.readBytesOf(size, size))
                }
            }.toList()

            check(!packet.isNotEmpty) { "Data packet is broken. There is ${packet.remaining} bytes left" }

            keys.zip(values).toMap()
        }
    }

    fun getInt(key: String): Int {
        return ByteReadPacket(map.getValue(key)).use {
            it.readInt()
        }
    }

    fun getString(key: String): String {
        return ByteReadPacket(map.getValue(key)).use {
            it.readText()
        }
    }

    fun getBoolean(key: String): Boolean {
        return ByteReadPacket(map.getValue(key)).use {
            val zero: Byte = 0
            it.readByte() != zero
        }
    }

    fun getByteArray(key: String): ByteArray {
        return map.getValue(key)
    }

    fun validateMetaInfo(): Boolean {
        return map.size >= 0
    }

    override fun toString(): String {
        return "MetaInfo([${map.keys.joinToString(", ")}])"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MetaInfo

        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return data.contentHashCode()
    }

    companion object {
        val EMPTY: MetaInfo
            get() = MetaInfoBuilder().build()
    }
}

/**
 * Not thread-safe
 */
class MetaInfoBuilder {
    private val data = mutableListOf<Pair<String, DataTypeWriter>>()

    private fun addData(key: String, rawValue: Any) = apply {
        val keyPacket = buildPacket { writeStringUtf8(key) }
        check(keyPacket.remaining < 256) { throw IllegalArgumentException("Key length must be less than 256") }
        keyPacket.release()

        val value = when (rawValue) {
            is String -> DataTypeWriter.ForString(
                rawValue
            )
            is Int -> DataTypeWriter.ForInt(
                rawValue
            )
            is Boolean -> DataTypeWriter.ForBoolean(
                rawValue
            )
            is ByteArray -> DataTypeWriter.ForByteArray(
                rawValue
            )
            else -> throw IllegalStateException("Value type of $rawValue is not supported")
        }

        val size = value.size()
        check(size < MAX_SIZE) { "Max size of value is $MAX_SIZE bytes, but actually it has $size bytes" }

        data += key to value
    }

    fun putInt(key: String, value: Int) = addData(key, value)

    fun putString(key: String, value: String) = addData(key, value)

    fun putBoolean(key: String, value: Boolean) = addData(key, value)

    fun putByteArray(key: String, value: ByteArray) = addData(key, value)

    fun build(): MetaInfo {
        val packet = buildPacket {
            //number of items
            writeUByte(data.size.toUByte())

            val keys = BytePacketBuilder()
            val values = BytePacketBuilder()

            try {
                for ((key, value) in data) {
                    val keyPacket = buildPacket { writeStringUtf8(key) }
                    keys.writeUByte(key.length.toUByte())
                    keys.writePacket(keyPacket)

                    values.writeUShort(value.size().toUShort())
                    values.writePacket(value.getBytes())
                }
            } catch (e: Throwable) {
                keys.release()
                values.release()
                throw e
            }

            writePacket(keys.build())
            writePacket(values.build())
        }

        return MetaInfo(packet.readBytes())
    }

    companion object {
        private val MAX_SIZE = UShort.MAX_VALUE.toLong()
    }

    private sealed class DataTypeWriter {
        abstract fun size(): Long
        abstract fun getBytes(): ByteReadPacket

        class ForString(val value: String) : DataTypeWriter() {
            override fun size(): Long {
                val bytes = getBytes()
                val size = bytes.remaining
                bytes.release()
                return size
            }

            override fun getBytes() = buildPacket { writeStringUtf8(value) }
        }

        class ForBoolean(val value: Boolean) : DataTypeWriter() {
            override fun size() = 1L

            override fun getBytes() = buildPacket { writeByte(if (value) 1 else 0) }
        }

        class ForInt(val value: Int) : DataTypeWriter() {
            override fun size() = 4L

            override fun getBytes() = buildPacket { writeInt(value) }
        }

        class ForByteArray(val value: ByteArray) : DataTypeWriter() {
            override fun size() = value.size.toLong()

            override fun getBytes() = buildPacket { writeFully(value) }
        }
    }
}
