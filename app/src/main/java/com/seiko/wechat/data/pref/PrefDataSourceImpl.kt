package com.seiko.wechat.data.pref

import androidx.preference.PreferenceDataStore
import com.seiko.wechat.R
import com.seiko.wechat.util.int
import com.seiko.wechat.util.string

class PrefDataSourceImpl(prefs: PreferenceDataStore) : PrefDataSource {
    override var userLogoIndex by prefs.int("wechat_prefs_user_logo", R.drawable.wechat_iv_0)
    override var userName by prefs.string("wechat_prefs_user_name", "")
    override var deviceUUID by prefs.string("wechat_prefs_device_uuid", "")
}