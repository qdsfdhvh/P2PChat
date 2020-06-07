package com.seiko.wechat.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.seiko.wechat.data.pref.PrefDataSource
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class PrefCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val pref: PrefDataSource by inject()

    override suspend fun doWork(): Result {
        if (pref.deviceUUID.isEmpty()) {
            pref.deviceUUID = UUID.randomUUID().toString()
        }
        return Result.success()
    }
}