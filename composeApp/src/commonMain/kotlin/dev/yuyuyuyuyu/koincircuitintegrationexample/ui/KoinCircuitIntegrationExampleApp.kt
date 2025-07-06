package dev.yuyuyuyuyu.koincircuitintegrationexample.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import dev.yuyuyuyuyu.koincircuitintegrationexample.di.koinCircuitIntegrationExampleAppModule
import dev.yuyuyuyuyu.koincircuitintegrationexample.ui.foo.FooScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
@Preview
fun KoinCircuitIntegrationExampleApp() {
    val backStack = rememberSaveableBackStack(root = FooScreen)
    val navigator = rememberCircuitNavigator(backStack) {}

    KoinApplication(
        application = {
            printLogger()
            modules(koinCircuitIntegrationExampleAppModule)
        },
    ) {
        MaterialTheme {
            CircuitCompositionLocals(circuit = koinInject()) {
                NavigableCircuitContent(navigator, backStack)
            }
        }
    }
}
