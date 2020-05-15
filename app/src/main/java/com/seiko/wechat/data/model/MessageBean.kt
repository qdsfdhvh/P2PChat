package com.seiko.wechat.data.model

import com.seiko.wechat.util.annotation.ItemType

sealed class MessageBean(val type: Int) {
    abstract val id: Long
}

data class SendTextBean(
    override val id: Long,
    val text: String
) : MessageBean(ItemType.SEND_TEXT)
