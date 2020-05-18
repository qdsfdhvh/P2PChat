package com.seiko.wechat.data.repo

import androidx.lifecycle.LiveData
import com.seiko.wechat.data.db.dao.MessageDao
import com.seiko.wechat.data.db.model.MessageBean
import java.util.*

class MessageRepository(private val messageDao: MessageDao) {

    /**
     * 获得指定人员的消息记录
     */
    fun getMessageList(uuid: UUID): LiveData<List<MessageBean>> {
        return messageDao.all(uuid, toDayFirstTimeMillis())
    }

    /**
     * 保存消息
     */
    suspend fun saveMessage(bean: MessageBean): Boolean {
        if (bean.time == 0L) {
            bean.time = System.currentTimeMillis()
        }
        return messageDao.put(bean) > 0
    }
}

/**
 * 今天0时0分0秒的时间戳
 */
private fun toDayFirstTimeMillis(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}