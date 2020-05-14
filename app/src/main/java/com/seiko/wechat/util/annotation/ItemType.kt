package com.seiko.wechat.util.annotation

import androidx.annotation.IntDef

/**
 * 聊天列表的item类型
 * Created by 陈健宇 at 2019/6/24
 */
@IntDef(
    ItemType.SEND_TEXT, ItemType.RECEIVE_TEXT,
    ItemType.SEND_AUDIO, ItemType.RECEIVE_AUDIO,
    ItemType.SEND_IMAGE, ItemType.RECEIVE_IMAGE,
    ItemType.SEND_FILE, ItemType.RECEIVE_FILE,
    ItemType.OTHER, ItemType.ERROR
)
@Retention(AnnotationRetention.SOURCE)
annotation class ItemType {
    companion object {
        const val SEND_TEXT = 0
        const val RECEIVE_TEXT = 1
        const val SEND_AUDIO = 2
        const val RECEIVE_AUDIO = 3
        const val SEND_IMAGE = 4
        const val RECEIVE_IMAGE = 5
        const val SEND_FILE = 6
        const val RECEIVE_FILE = 7
        const val OTHER = 8
        const val ERROR = 9
    }
}