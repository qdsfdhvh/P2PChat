package com.seiko.wechat.di

import androidx.preference.PreferenceDataStore
import com.seiko.wechat.data.pref.PrefDataSource
import com.seiko.wechat.data.pref.PrefDataSourceImpl
import com.seiko.wechat.util.constants.APP_PREF_NAME
import com.seiko.wechat.util.helper.MmkvPreferenceDataStore
import org.koin.dsl.module

internal val prefModule = module {
    single { createPreferenceDataStore() }
    single { createPrefDataSource(get()) }
}

private fun createPreferenceDataStore(): PreferenceDataStore {
    return MmkvPreferenceDataStore(APP_PREF_NAME)
}

private fun createPrefDataSource(prefs: PreferenceDataStore): PrefDataSource {
    return PrefDataSourceImpl(prefs)
}