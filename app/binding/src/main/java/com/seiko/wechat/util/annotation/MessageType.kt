package com.seiko.wechat.util.annotation

import androidx.annotation.IntDef

/**
 * 消息类型
 * Created by 陈健宇 at 2019/6/16
 */
@IntDef(
    MessageType.TEXT,
    MessageType.AUDIO,
    MessageType.IMAGE,
    MessageType.FILE,
    MessageType.ERROR
)
@Retention(AnnotationRetention.SOURCE)
annotation class MessageType {
    companion object {
        const val TEXT = 1  // 文字
        const val AUDIO = 2 // 音频
        const val IMAGE = 3 // 图片
        const val FILE = 4  // 文件
        const val ERROR = 5 // 错误
    }
}