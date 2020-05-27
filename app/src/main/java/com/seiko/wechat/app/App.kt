package com.seiko.wechat.app

import android.app.Application
import android.net.TrafficStats
import android.os.Build
import android.os.StrictMode
import android.os.strictmode.UntaggedSocketViolation
import com.seiko.wechat.BuildConfig
import com.seiko.wechat.util.fix.IMMLeaks
import com.seiko.wechat.util.timber.NanoDebugTree
import com.seiko.wechat.work.WorkerHelper
import com.tencent.mmkv.MMKV
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import java.util.concurrent.Executors

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(NanoDebugTree())
        }

        MMKV.initialize(this)

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }

        WorkerHelper.init(this)

        IMMLeaks.fixFocusedViewLeak(this)

        setupStrictMode()
    }
}

/**
 * 开启严格模式
 */
private fun setupStrictMode() {
    if (BuildConfig.DEBUG) {
        val penaltyListenerExecutor = Executors.newSingleThreadExecutor()
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        penaltyListener(penaltyListenerExecutor, StrictMode.OnThreadViolationListener {
                            Timber.tag("StrictMode").w(it)
                        })
                    } else {
                        penaltyLog()
                    }
                }
                .penaltyLog()
                .build())
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        penaltyListener(penaltyListenerExecutor, StrictMode.OnVmViolationListener {
                            when(it) {
                                is UntaggedSocketViolation -> {
                                    return@OnVmViolationListener
                                }
                            }
                            Timber.tag("StrictMode").w(it)
                        })
                    } else {
                        penaltyLog()
                    }
                }
                .build())
        TrafficStats.setThreadStatsTag(10000)
    }
}