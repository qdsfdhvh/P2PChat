package com.seiko.wechat.core.feature.debug

import android.annotation.SuppressLint
import android.net.TrafficStats
import android.os.Build
import android.os.StrictMode
import android.os.strictmode.UntaggedSocketViolation
import android.util.Log
import java.util.concurrent.Executors

/**
 * 严格模式
 */
@SuppressLint("LogNotTimber")
fun setupStickMode() {
    val penaltyListenerExecutor = Executors.newSingleThreadExecutor()
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    penaltyListener(penaltyListenerExecutor, StrictMode.OnThreadViolationListener {
                        Log.w("StrictMode", it)
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
                        Log.w("StrictMode", it)
                    })
                } else {
                    penaltyLog()
                }
            }
            .build())
    TrafficStats.setThreadStatsTag(10000)
}