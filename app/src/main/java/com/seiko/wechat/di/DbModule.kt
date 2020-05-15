package com.seiko.wechat.di

import android.content.Context
import com.seiko.wechat.data.db.WeChatDatabase
import com.seiko.wechat.util.constants.APP_DB_NAME
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val dbModule = module {
    single { createLoginDatabase(androidContext()) }
}

private fun createLoginDatabase(context: Context): WeChatDatabase {
    return WeChatDatabase.create(context, APP_DB_NAME)
}