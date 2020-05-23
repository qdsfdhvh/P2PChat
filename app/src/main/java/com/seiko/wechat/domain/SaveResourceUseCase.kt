package com.seiko.wechat.domain

import android.content.Context
import com.seiko.wechat.data.model.Result
import com.seiko.wechat.util.getResourceDir
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.util.*

/**
 * 保存资源
 */
class SaveResourceUseCase : KoinComponent {

    private val context: Context by inject()

    /**
     * @param uuid 目标设备uuid
     * @param md5 资源MD5
     * @param bytes 数据
     */
    operator fun invoke(uuid: UUID, md5: String, bytes: ByteArray): Result<String> {
        val uuidDir = File(context.getResourceDir(), uuid.toString())

        val sp1 = md5.substring(0, 2)
        val sp2 = md5.substring(2)

        val dir = File(uuidDir, sp1)
        if (!dir.exists() && !dir.mkdirs()) {
            return Result.Error(FileSystemException(dir))
        }

        val file = File(dir, sp2)
        if (file.exists()) {
            file.delete()
        }

        file.writeBytes(bytes)

        return Result.Success(file.absolutePath)
    }
}