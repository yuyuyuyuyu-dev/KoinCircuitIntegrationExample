package dev.yuyuyuyuyu.koincircuitintegrationexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.yuyuyuyuyu.koincircuitintegrationexample.ui.KoinCircuitIntegrationExampleApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            KoinCircuitIntegrationExampleApp()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    KoinCircuitIntegrationExampleApp()
}