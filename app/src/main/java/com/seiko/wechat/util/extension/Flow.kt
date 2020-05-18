package com.seiko.wechat.util.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import java.util.concurrent.ConcurrentMap

/**
 * 过滤字典中已有的数据
 * @param map 字典
 * @param predicate 生成key
 */
inline fun <K, V> Flow<V>.filterTo(
    map: ConcurrentMap<K, V>,
    crossinline predicate: suspend (V) -> K
): Flow<V> = transform { value ->
    val key = predicate(value)
    if (map.containsKey(key).not()) {
        map[key] = value
        return@transform emit(value)
    }
}