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
        const val TEXT = 0  // 文字
        const val AUDIO = 1 // 音频
        const val IMAGE = 2 // 图片
        const val FILE = 3  // 文件
        const val ERROR = 4 // 错误
    }
}