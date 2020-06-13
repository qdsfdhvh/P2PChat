package com.seiko.wechat.libs.p2p.extensions

import java.io.Closeable
import java.io.IOException

@Suppress("NOTHING_TO_INLINE")
inline fun Closeable.safeClose() {
    try {
        close()
    } catch (e: IOException) {
    }
}