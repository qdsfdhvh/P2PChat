package com.seiko.wechat.di

import com.seiko.wechat.data.db.WeChatDatabase
import com.seiko.wechat.data.repo.MessageRepository
import org.koin.dsl.module

val repoModule = module {
    single { createMessageRepository(get()) }
}

private fun createMessageRepository(database: WeChatDatabase): MessageRepository {
    return MessageRepository(database.messageDao())
}