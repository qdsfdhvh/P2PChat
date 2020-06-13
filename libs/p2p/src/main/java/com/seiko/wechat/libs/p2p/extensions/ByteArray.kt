package com.seiko.wechat.libs.p2p.extensions

import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.*

fun ByteArray.toAddress(): Inet4Address {
    return Inet4Address.getByAddress(this) as Inet4Address
}

fun ByteArray.toUUID(): UUID {
    val bb = ByteBuffer.wrap(this)
    val high = bb.long
    val low = bb.long
    return UUID(high, low)
}