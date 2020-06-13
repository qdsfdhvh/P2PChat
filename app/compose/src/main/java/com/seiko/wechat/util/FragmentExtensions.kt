package com.seiko.wechat.util

import android.view.ViewGroup
import androidx.compose.Composable
import androidx.compose.Composition
import androidx.compose.Recomposer
import androidx.fragment.app.Fragment
import androidx.ui.core.setContent
import com.seiko.wechat.compose.theme.WeChatTheme

fun Fragment.setContent(
    recomposer: Recomposer = Recomposer.current(),
    content: @Composable() () -> Unit
): Composition {
    return (requireView() as ViewGroup).setContent(recomposer, content)
}

fun Fragment.setThemeContent(
    content: @Composable() () -> Unit
): Composition {
    return setContent {
        WeChatTheme(content = content)
    }
}