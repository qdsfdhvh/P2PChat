package com.seiko.wechat.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.wechat.data.db.model.MessageBean
import java.util.*

@Dao
interface MessageDao {

    @Query("DELETE FROM Message_table WHERE uuid=:uuid")
    suspend fun delete(uuid: UUID): Int

    @Query("SELECT * FROM Message_table WHERE uuid=:uuid AND time>=:time ORDER BY time")
    fun all(uuid: UUID, time: Long): LiveData<List<MessageBean>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(bean: MessageBean): Long
}