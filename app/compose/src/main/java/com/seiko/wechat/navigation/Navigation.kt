package com.seiko.wechat.navigation

import androidx.compose.Stable

sealed class Screen {
    object Login: Screen()
    object Home: Screen()
    data class Chat(val id: String): Screen()
}

@Stable
object WechatStatus {
    var currentScreen: Screen = Screen.Login
}

fun navigation(destination: Screen) {
    WechatStatus.currentScreen = destination
}