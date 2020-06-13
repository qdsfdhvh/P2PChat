package com.seiko.wechat.compose.theme

import androidx.compose.Composable
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.graphics.Color
import androidx.ui.material.ColorPalette
import androidx.ui.material.MaterialTheme
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette

private val LightThemeColors = lightColorPalette(
    primary = colorPrimary,
    primaryVariant = colorPrimaryDark,
    onPrimary = Color.White,
    secondary = colorSecondPrimary,
    secondaryVariant = colorSecondPrimary,
    onSecondary = Color.White,
    error = Red800
)

private val DarkThemeColors = darkColorPalette(
    primary = Red300,
    primaryVariant = Red700,
    onPrimary = Color.Black,
    secondary = Red300,
    onSecondary = Color.White,
    error = Red200
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