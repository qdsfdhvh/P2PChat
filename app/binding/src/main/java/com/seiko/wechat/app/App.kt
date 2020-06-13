package com.seiko.wechat.app

import com.seiko.wechat.core.feature.app.BaseApplication
import com.seiko.wechat.util.fix.IMMLeaks
import com.seiko.wechat.work.WorkerHelper
import com.tencent.mmkv.MMKV
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
        WorkerHelper.init(this)
        IMMLeaks.fixFocusedViewLeak(this)
    }
}