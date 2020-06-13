package com.seiko.wechat.vm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.seiko.wechat.core.resource.LOCAL_LOGO_LIST
import com.seiko.wechat.data.TestRepository
import kotlinx.coroutines.Dispatchers

class LoginViewModel @ViewModelInject constructor(
    private val repo: TestRepository
) : ViewModel() {

    init {
        repo.aaa()
    }

    val logoList: LiveData<List<Int>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(LOCAL_LOGO_LIST.toList())
        }

}