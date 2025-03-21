package com.kev.apptest12.data.model


data class App(
    val id: String,
    val name: String,
    val creator: String,
    val description: String,
    val category: String,
    val logo_path: String,
    val apk_path: String,
    val is_published: Boolean
)