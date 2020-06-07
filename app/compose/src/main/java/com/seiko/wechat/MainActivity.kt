package com.seiko.wechat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.setContent
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.MaterialTheme
import androidx.ui.tooling.preview.Preview
import com.koduok.compose.navigation.core.backStackController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }

    override fun onBackPressed() {
        if (backStackController.pop()) {
            return
        }
        super.onBackPressed()
    }
}

@Composable
fun MainScreen() {
    MaterialTheme {
        Column {
            Text("this is aaa")
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}