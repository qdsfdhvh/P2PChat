package com.seiko.wechat.util.extension

import kotlinx.coroutines.channels.SendChannel

@Suppress("NOTHING_TO_INLINE")
inline fun <E> SendChannel<E>.safeOffer(element: E) {
    if (!isClosedForSend) {
        offer(element)
    }
}