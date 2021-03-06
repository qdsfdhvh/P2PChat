package com.seiko.wechat.data.repo

import androidx.lifecycle.LiveData
import com.seiko.wechat.data.db.dao.MessageDao
import com.seiko.wechat.data.db.model.MessageBean
import com.seiko.wechat.util.annotation.MessageState
import java.util.*

class MessageRepository(private val messageDao: MessageDao) {

    /**
     * 获得指定人员的消息记录
     */
    fun getMessageList(uuid: UUID, time: Long): LiveData<List<MessageBean>> {
        return messageDao.all(uuid, time)
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

    /**
     * 更新消息状态
     */
    suspend fun updateState(time: Long, @MessageState state: Int): Boolean {
        return messageDao.updateState(time, state) > 0
    }
}