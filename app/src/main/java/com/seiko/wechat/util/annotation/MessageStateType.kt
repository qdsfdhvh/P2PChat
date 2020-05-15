package com.seiko.wechat.util.annotation

import androidx.annotation.IntDef

@IntDef(
    MessageStateType.ERROR,
    MessageStateType.NORMAL,
    MessageStateType.POSTED
)
@Retention(AnnotationRetention.SOURCE)
annotation class MessageStateType  {
    companion object {
        const val ERROR = -1 // 异常
        const val NORMAL = 0 // 默认状态、未发送
        const val POSTED = 1 // 已发送

    }
}