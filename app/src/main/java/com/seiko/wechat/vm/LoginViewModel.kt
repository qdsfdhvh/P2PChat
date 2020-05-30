package com.seiko.wechat.vm

import androidx.lifecycle.*
import com.seiko.wechat.core.resource.LOCAL_LOGO_LIST
import com.seiko.wechat.data.model.LogoBean
import com.seiko.wechat.data.pref.PrefDataSource
import com.seiko.wechat.util.extension.zipFlatMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val prefs: PrefDataSource
) : ViewModel() {

    val logoList: LiveData<List<LogoBean>> = liveData {
        val list = withContext(Dispatchers.Default) {
            LOCAL_LOGO_LIST.map { LogoBean(it) }
        }
        emit(list)
    }

    private var _userLogoIndex = MutableLiveData<Int>().apply { value = prefs.userLogoIndex }
    val userLogo: LiveData<Int> = _userLogoIndex.zipFlatMap(logoList) { index, list ->
        liveData {
            if (index == null || list == null) {
                return@liveData
            }
            emit(list[index].resId)
        }
    }

    val userName: LiveData<String> get() = liveData {
        delay(200)
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