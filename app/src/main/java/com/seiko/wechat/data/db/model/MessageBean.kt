package com.seiko.wechat.data.db.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.seiko.wechat.util.annotation.ItemType
import com.seiko.wechat.util.annotation.MessageState
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(
    tableName = "Message_table",
    indices = [
        Index(value = ["uuid"], unique = false) // 设置索引
    ]
)
@Parcelize
data class MessageBean(
    // 主键
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    // 数据类型
    @ItemType
    val type: Int = ItemType.NORMAL,
    // 人员id
    val uuid: UUID = UUID.randomUUID(),
    // 消息产生时间
    var time: Long = 0,
    // 消息状态 已发送 or 未发送 or Other
    @MessageState
    val state: Int = MessageState.NORMAL,
    // 消息数据
    val data: MessageData = EmptyData
): Parcelable

/**
 * 消息内容
 */
sealed class MessageData : Parcelable
// 空数据
@Parcelize
object EmptyData: MessageData() {
    override fun toString(): String {
        return "null"
    }
}
// 文字
@Parcelize
data class TextData(val text: String): MessageData() {
    override fun toString(): String {
        return text
    }
}
// 图片
@Parcelize
data class ImageData(val imagePath: String): MessageData() {
    override fun toString(): String {
        return imagePath
    }
}