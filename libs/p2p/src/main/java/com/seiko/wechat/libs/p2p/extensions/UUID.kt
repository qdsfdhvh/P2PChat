package com.seiko.wechat.libs.p2p.extensions

import java.nio.ByteBuffer
import java.util.*

fun UUID.toByteArray(): ByteArray {
    val bb = ByteBuffer.wrap(ByteArray(16))
    bb.putLong(mostSignificantBits)
    bb.putLong(leastSignificantBits)
    return bb.array()
}

@Suppress("NOTHING_TO_INLINE")
inline fun String.toUUID(): UUID {
    return UUID.fromString(this)
}