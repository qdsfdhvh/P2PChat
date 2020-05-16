package com.seiko.wechat.util.annotation

import androidx.annotation.IntDef

@IntDef(
    MessageState.ERROR,
    MessageState.NORMAL,
    MessageState.POSTED
)
@Retention(AnnotationRetention.SOURCE)
annotation class MessageState  {
    companion object {
        const val ERROR = -1 // 异常
        const val NORMAL = 0 // 默认状态、未发送
        const val POSTED = 1 // 已发送

    }
}