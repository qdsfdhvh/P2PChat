package com.seiko.wechat.libs.p2p.model

import com.seiko.wechat.libs.p2p.extensions.toAddress
import com.seiko.wechat.libs.p2p.extensions.toByteArray
import com.seiko.wechat.libs.p2p.extensions.toUUID
import kotlinx.io.core.*
import java.net.Inet4Address
import java.util.*

/**
 * 局域网内的成员信息
 */
data class Peer(
    var addresses: List<Inet4Address> = emptyList(),
//    var port: UShort = 0u,
    var uuid: UUID = UUID.randomUUID(),
    var serviceName: String = "",
//    var transportProtocol: TransportProtocol = TransportProtocol.TCP,
    var metaInfo: MetaInfo = MetaInfo.EMPTY
) {

    override fun hashCode(): Int {
        var result = addresses.hashCode()
//        result = 31 * result + port.hashCode()
        result = 31 * result + uuid.hashCode()
        result = 31 * result + serviceName.hashCode()
//        result = 31 * result + transportProtocol.hashCode()
//        result = 31 * result + metaInfo.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        return if (other is Peer) {
            addresses == other.addresses
//                    && port == other.port
                    && uuid == other.uuid
                    && serviceName == other.serviceName
//                    && transportProtocol == other.transportProtocol
//                    && metaInfo == other.metaInfo
        } else false
    }

    @ExperimentalUnsignedTypes
    fun createBinaryMessage(): ByteReadPacket {
        val builder = BytePacketBuilder()

        // version
        builder.writeByte(1)

        // UUID
        builder.writeFully(uuid.toByteArray())

        // service name
        builder.writeUByte(serviceName.toByteArray().size.toUByte())
        builder.writeStringUtf8(serviceName)

//        // Transport protocol
//        builder.writeUByte(transportProtocol.byteName)

//        // port
//        builder.writeUShort(port)

        // ip
        builder.writeUByte(addresses.size.toUByte())
        for (address in addresses) {
            builder.writeFully(address.address)
        }

        // meta info
        builder.writeFully(metaInfo.data)

        return builder.build()
    }

    companion object {

        @ExperimentalUnsignedTypes
        fun fromBinary(data: ByteReadPacket): Peer {
            val version = data.readByte()
            check(version == 1.toByte()) { "Only version v1 is supported" }

            val uuid = data.readBytes(16).toUUID()

            val serviceNameLength = data.readUByte()
            val serviceName = data.readTextExactBytes(bytes = serviceNameLength.toInt())

//            val transportProtocol = data.readUByte().let { byte ->
//                TransportProtocol.values().find { it.byteName == byte }!!
//            }

//            val port = data.readUShort()

            val ipAddressNum = data.readUByte()
            val addresses = sequence {
                repeat(ipAddressNum.toInt()) {
                    yield(data.readBytes(4).toAddress())
                }
            }.toList()

            val metaInfoBinary = data.readBytes()

            return Peer(
                addresses = addresses,
//                port = port,
                uuid = uuid,
                serviceName = serviceName,
//                transportProtocol = transportProtocol,
                metaInfo = MetaInfo(
                    metaInfoBinary
                )
            )
        }
    }
}