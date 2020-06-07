package com.seiko.wechat.theme

import androidx.compose.Composable
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.graphics.Color
import androidx.ui.material.ColorPalette
import androidx.ui.material.MaterialTheme
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette

private val LightThemeColors = lightColorPalette(

)

private val DarkThemeColors = darkColorPalette(

)

@Composable
val ColorPalette.snackBarAction: Color
    get() = if (isLight) Red300 else Red700

@Composable
fun WeChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkThemeColors else LightThemeColors,
        typography = themeTypography,
        shapes = shapes,
        content = content
    )
}