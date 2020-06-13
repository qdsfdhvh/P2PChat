package com.seiko.wechat

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment

class MainActivity : FragmentActivity() {

    companion object {
        private const val CONTAINER_ID = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = FrameLayout(this)
        layout.id = CONTAINER_ID
        setContentView(layout)

        val fragment = NavHostFragment.create(R.navigation.nav_base)
        supportFragmentManager.commit {
            add(CONTAINER_ID, fragment)
        }
    }
}