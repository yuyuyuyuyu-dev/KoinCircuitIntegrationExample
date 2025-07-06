package dev.yuyuyuyuyu.koincircuitintegrationexample

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.yuyuyuyuyu.koincircuitintegrationexample.ui.KoinCircuitIntegrationExampleApp
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        KoinCircuitIntegrationExampleApp()
    }
}