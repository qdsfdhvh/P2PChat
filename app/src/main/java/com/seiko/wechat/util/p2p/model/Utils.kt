package com.seiko.wechat.util.p2p.model

import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.*

typealias Address = Inet4Address

fun ByteArray.toAddress(): Address {
    return Inet4Address.getByAddress(this) as Inet4Address
}

fun UUID.toByteArray(): ByteArray {
    val bb = ByteBuffer.wrap(ByteArray(16))
    bb.putLong(mostSignificantBits)
    bb.putLong(leastSignificantBits)
    return bb.array()
}

fun ByteArray.toUUID(): UUID {
    val bb = ByteBuffer.wrap(this)
    val high = bb.long
    val low = bb.long
    return UUID(high, low)
}