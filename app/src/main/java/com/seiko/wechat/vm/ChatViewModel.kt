package com.seiko.wechat.vm

import androidx.lifecycle.*
import com.seiko.wechat.data.db.model.MessageBean
import com.seiko.wechat.data.db.model.MessageData
import com.seiko.wechat.data.db.model.TextData
import com.seiko.wechat.data.model.PeerBean
import com.seiko.wechat.data.repo.MessageRepository
import com.seiko.wechat.util.annotation.ItemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(private val repo: MessageRepository): ViewModel() {

    private val _peer = MutableLiveData<PeerBean>()
    val messageList: LiveData<List<MessageBean>> = _peer.switchMap { peer ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(repo.getMessageList(peer.uuid))
        }
    }

    /**
     * 设置当前聊天用户
     */
    fun setPeer(peer: PeerBean) {
        _peer.value = peer
    }

    fun sendText(peer: PeerBean, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val bean = TextData(text).toBean(peer, ItemType.SEND_TEXT)
            repo.saveMessage(bean)
        }
    }
}

private fun MessageData.toBean(peer: PeerBean, @ItemType type: Int): MessageBean {
    return MessageBean(
        type = type,
        uuid = peer.uuid,
        time = System.currentTimeMillis(),
        data = this
    )
}