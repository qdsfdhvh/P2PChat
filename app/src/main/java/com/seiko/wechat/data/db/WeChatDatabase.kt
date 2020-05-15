package com.seiko.wechat.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.seiko.wechat.data.db.dao.MessageDao
import com.seiko.wechat.data.db.model.MessageBean

@Database(entities = [
    MessageBean::class
], version = 1)
@TypeConverters(Converters::class)
abstract class WeChatDatabase : RoomDatabase() {

    companion object {
        fun create(context: Context, name: String): WeChatDatabase {
            return Room.databaseBuilder(context, WeChatDatabase::class.java, name).build()
        }
    }

    abstract fun messageDao(): MessageDao

}