package com.seiko.wechat.vm

import androidx.lifecycle.*
import com.seiko.wechat.R
import com.seiko.wechat.data.model.LogoBean
import com.seiko.wechat.data.pref.PrefDataSource
import com.seiko.wechat.util.constants.LOCAL_LOGO_LIST

class LoginViewModel(
    private val prefs: PrefDataSource
) : ViewModel() {

    val logoList: LiveData<List<LogoBean>> = liveData {
        emit(LOCAL_LOGO_LIST)
    }

    private var _userLogoIndex = MutableLiveData<Int>().apply { value = prefs.userLogoIndex }
    val userLogo: LiveData<Int> = _userLogoIndex.map { index ->
        logoList.value?.get(index)?.resId ?: R.drawable.wechat_iv_0
    }

    val userName: LiveData<String> = liveData {
        emit(prefs.userName)
    }

    /**
     * 选择用户头像，并保存
     */
    fun selectUserLogo(index: Int) {
        _userLogoIndex.value = index
        prefs.userLogoIndex = index
    }

    /**
     * 保存用户名称
     */
    fun saveUserName(name: String) {
        prefs.userName = name
    }
}