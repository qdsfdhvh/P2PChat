package com.seiko.wechat.compose

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.Color
import androidx.ui.material.Surface
import com.seiko.wechat.compose.theme.WeChatTheme

@Composable
internal fun ThemedPreview(
    darkTheme: Boolean = false,
    children: @Composable() () -> Unit
) {
    WeChatTheme(darkTheme = darkTheme) {
        Surface {
            children()
        }
    }
}

@Composable
internal fun ThemedRawPreview(
    darkTheme: Boolean = false,
    children: @Composable() () -> Unit
) {
    WeChatTheme(darkTheme = darkTheme) {
        children()
    }
}