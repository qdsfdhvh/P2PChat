package com.seiko.wechat.data

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestRepository @Inject constructor() {
    fun aaa() {
        Timber.d("aaa")
    }
}