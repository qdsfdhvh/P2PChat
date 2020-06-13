package com.seiko.wechat.ui

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.tooling.preview.Preview
import com.seiko.wechat.compose.ThemedPreview

@Composable
fun ChatScreen() {
    Text(text = "this is chat.")
}

@Preview
@Composable
fun ChatScreenPreview() {
    ThemedPreview {
        ChatScreen()
    }
}