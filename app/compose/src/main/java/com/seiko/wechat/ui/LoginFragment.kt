package com.seiko.wechat.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.seiko.wechat.utils.setThemeContent

class LoginFragment : ComposeBaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        setThemeContent {
            LoginScreen(viewModel)
        }
    }

}