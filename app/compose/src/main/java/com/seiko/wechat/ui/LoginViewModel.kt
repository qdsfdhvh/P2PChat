package com.seiko.wechat.ui

import androidx.lifecycle.*
import com.seiko.wechat.core.resource.LOCAL_LOGO_LIST
import kotlinx.coroutines.Dispatchers

class LoginViewModel : ViewModel() {

    val logoList: LiveData<List<Int>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(LOCAL_LOGO_LIST.toList())
        }

}