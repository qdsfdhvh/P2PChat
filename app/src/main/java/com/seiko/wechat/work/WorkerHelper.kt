package com.seiko.wechat.work

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkerHelper {
    private const val TAG_DELETE_OLD_RESOURCE = "TAG_DELETE_OLD_RESOURCE"
    fun init(context: Context) {
        // 生存设备id
        val request1 = OneTimeWorkRequestBuilder<PrefCheckWorker>().build()

        // 删除一天前的旧资源
        val request2 = PeriodicWorkRequestBuilder<DeleteOldResourceWorker>(
            1, TimeUnit.DAYS)
            .addTag(TAG_DELETE_OLD_RESOURCE)
            .build()

        WorkManager.getInstance(context).enqueue(listOf(
            request1, request2
        ))
    }

}