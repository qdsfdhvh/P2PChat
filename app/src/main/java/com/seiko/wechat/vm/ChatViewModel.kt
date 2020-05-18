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
        liveData(viewModelScope.coroutineContext) {
            emitSource(repo.getMessageList(peer.uuid))
        }
    }

    /**
     * 设置当前聊天用户
     */
    fun setPeer(peer: PeerBean) {
        _peer.value = peer
    }

}