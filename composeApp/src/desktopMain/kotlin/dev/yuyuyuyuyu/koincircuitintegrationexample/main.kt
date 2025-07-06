package dev.yuyuyuyuyu.koincircuitintegrationexample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.yuyuyuyuyu.koincircuitintegrationexample.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "koincircuitintegrationexample",
    ) {
        App()
    }
}