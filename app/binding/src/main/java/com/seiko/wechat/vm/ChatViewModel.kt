package com.seiko.wechat.vm

import androidx.lifecycle.*
import com.seiko.wechat.data.db.model.MessageBean
import com.seiko.wechat.data.model.PeerBean
import com.seiko.wechat.data.repo.MessageRepository
import com.seiko.wechat.util.toDayFirstTimeMillis

class ChatViewModel(private val repo: MessageRepository): ViewModel() {

    private val _peer = MutableLiveData<PeerBean>()
    val messageList: LiveData<List<MessageBean>> = _peer.switchMap { peer ->
        liveData(viewModelScope.coroutineContext) {
            emitSource(repo.getMessageList(peer.uuid, toDayFirstTimeMillis()))
        }
    }

    /**
     * 设置当前聊天用户
     */
    fun setPeer(peer: PeerBean) {
        _peer.value = peer
    }

}