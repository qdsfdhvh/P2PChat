package com.seiko.wechat.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.seiko.wechat.util.setThemeContent
import com.seiko.wechat.vm.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : ComposeBaseFragment() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setThemeContent {
            LoginScreen(viewModel)
        }
    }

}