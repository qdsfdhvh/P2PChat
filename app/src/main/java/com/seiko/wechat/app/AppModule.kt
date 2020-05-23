package com.seiko.wechat.app

import android.content.Context
import androidx.preference.PreferenceDataStore
import com.seiko.wechat.data.db.WeChatDatabase
import com.seiko.wechat.data.pref.PrefDataSource
import com.seiko.wechat.data.pref.PrefDataSourceImpl
import com.seiko.wechat.data.repo.MessageRepository
import com.seiko.wechat.domain.SaveResourceUseCase
import com.seiko.wechat.service.MessageAdapter
import com.seiko.wechat.util.constants.APP_DB_NAME
import com.seiko.wechat.util.constants.APP_PREF_NAME
import com.seiko.wechat.util.helper.MmkvPreferenceDataStore
import com.seiko.wechat.vm.ChatViewModel
import com.seiko.wechat.vm.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // pref
    single { createPreferenceDataStore() }
    single { createPrefDataSource(get()) }
    // db
    single { createLoginDatabase(androidContext()) }
    // repo
    single { createMessageRepository(get()) }
    // domain
    single { SaveResourceUseCase() }
    // other
    single { MessageAdapter(get()) }
    // viewModel
    viewModel { LoginViewModel(get()) }
    viewModel { ChatViewModel(get()) }
}

private fun createPreferenceDataStore(): PreferenceDataStore {
    return MmkvPreferenceDataStore(APP_PREF_NAME)
}

private fun createPrefDataSource(prefs: PreferenceDataStore): PrefDataSource {
    return PrefDataSourceImpl(prefs)
}

private fun createLoginDatabase(context: Context): WeChatDatabase {
    return WeChatDatabase.create(context, APP_DB_NAME)
}

private fun createMessageRepository(database: WeChatDatabase): MessageRepository {
    return MessageRepository(database.messageDao())
}