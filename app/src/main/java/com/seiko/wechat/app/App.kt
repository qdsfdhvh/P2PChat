package com.seiko.wechat.app

import android.app.Application
import android.net.TrafficStats
import android.os.Build
import android.os.StrictMode
import android.os.strictmode.UntaggedSocketViolation
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.seiko.wechat.BuildConfig
import com.seiko.wechat.util.timber.NanoDebugTree
import com.seiko.wechat.work.PrefCheckWorker
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

        val request = OneTimeWorkRequestBuilder<PrefCheckWorker>().build()
        WorkManager.getInstance(this).enqueue(request)

        setupStrictModel()
    }
}

/**
 * 开启严格模式
 */
private fun setupStrictModel() {
    if (BuildConfig.DEBUG) {
        val penaltyListenerExecutor = Executors.newSingleThreadExecutor()
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        penaltyListener(penaltyListenerExecutor, StrictMode.OnThreadViolationListener {
                            Timber.w(it)
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
                            Timber.w(it)
                        })
                    } else {
                        penaltyLog()
                    }
                }
                .build())
        TrafficStats.setThreadStatsTag(10000)
    }
}