package com.seiko.wechat.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.seiko.wechat.R
import com.seiko.wechat.util.navigation.KeepStateNavigator

class MainActivity : AppCompatActivity(R.layout.wechat_activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.wechat_nav_host_fragment) as NavHostFragment

        val navigator = KeepStateNavigator(this,
            navHostFragment.childFragmentManager,
            R.id.wechat_nav_host_fragment)

        navHostFragment.navController.navigatorProvider.addNavigator(navigator)
        navHostFragment.navController.setGraph(R.navigation.wechat_navigation)
    }
}