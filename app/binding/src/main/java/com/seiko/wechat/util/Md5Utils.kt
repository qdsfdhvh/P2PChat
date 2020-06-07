package com.seiko.wechat.util

import java.io.File
import java.security.MessageDigest

private  val md5Instance by lazy(LazyThreadSafetyMode.NONE) {
    MessageDigest.getInstance("MD5")
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

fun File.getMd5(): ByteArray {
    val buffer = ByteArray(1024)
    inputStream().use {
        var read = it.read(buffer)
        while (read != -1) {
            md5Instance.update(buffer, 0, read)
            read = it.read(buffer)
        }
        return md5Instance.digest()
    }
}