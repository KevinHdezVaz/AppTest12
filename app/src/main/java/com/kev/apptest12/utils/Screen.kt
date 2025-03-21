package com.kev.apptest12.utils

sealed class Screen(val route: String) {
    object Chats : Screen("chats_screen")
    object Estados : Screen("estados_screen")
    object Llamadas : Screen("llamadas_screen")
}