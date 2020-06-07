package com.seiko.wechat.util.annotation

import androidx.annotation.IntDef

/**
 * 消息类型
 */
@IntDef(
    ItemType.SEND_TEXT, ItemType.RECEIVE_TEXT,
    ItemType.SEND_AUDIO, ItemType.RECEIVE_AUDIO,
    ItemType.SEND_IMAGE, ItemType.RECEIVE_IMAGE,
    ItemType.SEND_FILE, ItemType.RECEIVE_FILE,
    ItemType.NORMAL, ItemType.ERROR
)
@Retention(AnnotationRetention.SOURCE)
annotation class ItemType {
    companion object {
        const val ERROR = -1
        const val NORMAL = 0
        const val SEND_TEXT = 1
        const val RECEIVE_TEXT = 2
        const val SEND_AUDIO = 3
        const val RECEIVE_AUDIO = 4
        const val SEND_IMAGE = 5
        const val RECEIVE_IMAGE = 6
        const val SEND_FILE = 7
        const val RECEIVE_FILE = 8
    }
}