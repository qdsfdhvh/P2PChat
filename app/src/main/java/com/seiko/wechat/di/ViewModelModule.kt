package com.seiko.wechat.di

import com.seiko.wechat.vm.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
}