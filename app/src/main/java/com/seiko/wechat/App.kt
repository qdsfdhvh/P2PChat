package com.seiko.wechat

import android.app.Application
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.seiko.wechat.di.dbModule
import com.seiko.wechat.di.prefModule
import com.seiko.wechat.di.repoModule
import com.seiko.wechat.di.viewModelModule
import com.seiko.wechat.util.timber.NanoDebugTree
import com.seiko.wechat.work.PrefCheckWorker
import com.tencent.mmkv.MMKV
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(NanoDebugTree())
        }

        MMKV.initialize(this)

        startKoin {
            androidContext(this@App)
            modules(prefModule, dbModule,
                repoModule,
                viewModelModule)
        }

        val request = OneTimeWorkRequestBuilder<PrefCheckWorker>().build()
        WorkManager.getInstance(this).enqueue(request)
    }
}