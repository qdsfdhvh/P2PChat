package com.seiko.wechat.util.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

inline fun <A, B, Z> LiveData<A>.zipFlatMap(
    b: LiveData<B>,
    crossinline transform: (a: A?, b: B?) -> LiveData<Z>
) : LiveData<Z> {
    var aValue: A? = null
    var bValue: B? = null

    val mergeLiveData = MediatorLiveData<Z>()
    mergeLiveData.addLiveSource(this) { value ->
        aValue = value
        transform(value, bValue)
    }
    mergeLiveData.addLiveSource(b) { value ->
        bValue = value
        transform(aValue, value)
    }
    return mergeLiveData
}

inline fun <X, Y> MediatorLiveData<Y>.addLiveSource(
    liveData: LiveData<X>,
    crossinline transform: (X) -> LiveData<Y>
) {
    addSource(liveData, object : Observer<X> {
        var source: LiveData<Y>? = null
        override fun onChanged(v: X) {
            val newLiveData = transform(v)
            if (source == newLiveData) {
                return
            }
            if (source != null) {
                removeSource(source!!)
            }
            source = newLiveData
            if (source != null) {
                addSource(source!!) { t ->
                    this@addLiveSource.value = t
                }
            }
        }
    })
}