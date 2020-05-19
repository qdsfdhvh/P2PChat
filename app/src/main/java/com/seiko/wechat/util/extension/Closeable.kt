package com.seiko.wechat.util.extension

import java.io.Closeable
import java.io.IOException

@Suppress("NOTHING_TO_INLINE")
inline fun Closeable.safeClose() {
    try {
        close()
    } catch (e: IOException) {
    }
}