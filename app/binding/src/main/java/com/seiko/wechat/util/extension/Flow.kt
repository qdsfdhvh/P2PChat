package com.seiko.wechat.util.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentMap


/**
 * 以指定范围的方式运行Flow
 */
inline fun <T> Flow<T>.collect(
    scope: CoroutineScope,
    crossinline action: suspend (value: T) -> Unit
): Job = scope.launch {
    this@collect.collect(action)
}