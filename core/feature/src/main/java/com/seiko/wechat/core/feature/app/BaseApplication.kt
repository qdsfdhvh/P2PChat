package com.seiko.wechat.core.feature.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.seiko.wechat.core.feature.BuildConfig
import com.seiko.wechat.core.feature.debug.setupStickMode
import com.seiko.wechat.core.feature.timber.NanoDebugTree
import timber.log.Timber

abstract class BaseApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(NanoDebugTree())
            setupStickMode()
        }
    }
}