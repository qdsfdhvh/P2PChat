package com.seiko.wechat.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.seiko.wechat.util.getResourceDir
import com.seiko.wechat.util.toDayFirstTimeMillis
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.io.File

/**
 * 清空一天前接收的资源
 */
class DeleteOldResourceWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    override suspend fun doWork(): Result {
        val context: Context by inject()
        val resourceDir = context.getResourceDir()
        return if (resourceDir.deleteFile(toDayFirstTimeMillis())) {
            Result.success()
        } else {
            Result.failure()
        }
    }

}

/**
 * 删除文件夹中超过时间的文件
 * @param time 时间
 */
private fun File.deleteFile(time: Long): Boolean {
    return walkBottomUp().fold(true, { res, it ->
        Timber.d(it.absolutePath)
        val deleted = if (it.isFile && it.lastModified() <= time) {
            Timber.d("删除文件${it.absolutePath}")
            it.delete()
        } else if (it.isDirectory && it.listFiles().isNullOrEmpty()) {
            Timber.d("清空文件夹${it.absolutePath}")
            it.delete()
        } else {
            true
        }
        (deleted || !it.exists()) && res
    })
}