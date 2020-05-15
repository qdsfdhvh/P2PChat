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
        return messageDao.all(uuid)
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