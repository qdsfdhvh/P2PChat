package com.seiko.wechat.service

import com.seiko.wechat.data.db.model.MessageBean
import com.seiko.wechat.util.p2p.MesAdapter
import kotlinx.coroutines.delay
import timber.log.Timber
import java.io.DataInputStream
import java.io.DataOutputStream

class MessageAdapter : MesAdapter<MessageBean>() {
    companion object {
        private const val TAG = "MessageAdapter"
    }

    override suspend fun decode(input: DataInputStream): MessageBean? {
        Timber.tag(TAG).d("解析一次数据")
        delay(500)
        return null
    }

    override suspend fun encode(data: MessageBean, output: DataOutputStream) {

    }
}