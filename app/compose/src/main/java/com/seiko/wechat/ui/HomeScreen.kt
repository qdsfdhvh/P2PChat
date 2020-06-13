package com.seiko.wechat.ui

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.tooling.preview.Preview
import com.seiko.wechat.compose.ThemedPreview

@Composable
fun HomeScreen() {
    Text(text = "this is home.")
}

@Preview
@Composable
fun HomeScreenPreview() {
    ThemedPreview {
        HomeScreen()
    }
}