package com.seiko.wechat.utils

import android.view.ViewGroup
import androidx.compose.Composable
import androidx.compose.Composition
import androidx.compose.Recomposer
import androidx.fragment.app.Fragment
import androidx.ui.core.setContent

fun Fragment.setContent(
    recomposer: Recomposer = Recomposer.current(),
    content: @Composable() () -> Unit
): Composition {
    return (view as ViewGroup).setContent(recomposer, content)
}