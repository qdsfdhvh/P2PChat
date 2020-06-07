package com.seiko.wechat.data.db

import androidx.room.TypeConverter
import com.seiko.wechat.data.db.model.EmptyData
import com.seiko.wechat.data.db.model.ImageData
import com.seiko.wechat.data.db.model.MessageData
import com.seiko.wechat.data.db.model.TextData
import com.seiko.wechat.util.annotation.MessageType
import java.util.*

private const val MESSAGE_FORMAT = "%02d%s"

class Converters {

    @TypeConverter
    fun formatUUID(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUUID(uuidStr: String): UUID {
        return UUID.fromString(uuidStr)
    }

    @TypeConverter
    fun formatMessageData(data: MessageData): String {
        return when(data) {
            is EmptyData -> ""
            is TextData -> MESSAGE_FORMAT.format(MessageType.TEXT, data.text)
            is ImageData -> MESSAGE_FORMAT.format(MessageType.IMAGE, data.imagePath)
        }
    }

    @TypeConverter
    fun toMessageData(str: String?): MessageData {
        if (str.isNullOrEmpty()) return EmptyData

        if (str.length < 2) return TextData(str)

        val type  = str.substring(0, 2).toIntOrNull() ?: return TextData(str)
        val value = str.substring(2)

        return when(type) {
            MessageType.TEXT -> TextData(value)
            MessageType.IMAGE -> ImageData(value)
            else -> TextData(str)
        }
    }

}