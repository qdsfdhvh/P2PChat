package com.seiko.wechat.util

import java.security.MessageDigest

private  val md5Instance by lazy(LazyThreadSafetyMode.NONE) {
    MessageDigest.getInstance("MD5")
}

fun ByteArray.getMd5(): ByteArray {
    md5Instance.update(this)
    return md5Instance.digest()
}

fun ByteArray.getMd5Str(): String {
    return getMd5().toHexString()
}

fun ByteArray.toHexString(): String {
    return StringBuilder(32).apply {
        this@toHexString.forEach { bytes ->
            val value = bytes.toInt() and 0xFF
            val high = value / 16
            val low = value % 16
            append(if (high <= 9) '0' + high else 'a' - 10 + high)
            append(if (low <= 9) '0' + low else 'a' - 10 + low)
        }
    }.toString()
}