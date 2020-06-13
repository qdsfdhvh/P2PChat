package com.seiko.wechat.navigation

sealed class Screen {
    object Login: Screen()
    object Home: Screen()
    data class Chat(val id: String): Screen()
}
