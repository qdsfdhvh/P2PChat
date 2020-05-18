package com.seiko.wechat.util.extension

import okio.Okio
import java.nio.ByteBuffer
import java.util.*

fun UUID.toByteArray(): ByteArray {
    val bb = ByteBuffer.wrap(ByteArray(16))
    bb.putLong(mostSignificantBits)
    bb.putLong(leastSignificantBits)
    return bb.array()
}

inline fun String.toUUID(): UUID {
    return UUID.fromString(this)
}