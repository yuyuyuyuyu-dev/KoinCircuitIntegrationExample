package dev.yuyuyuyuyu.koincircuitintegrationexample.ui.bar

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen

class BarPresenter(
    private val navigator: Navigator,
) : Presenter<BarScreen.State> {
    @Composable
    override fun present(): BarScreen.State {
        return BarScreen.State { event ->
            when (event) {
                BarScreen.Event.NavigateBackButtonClicked -> navigator.pop()
            }
        }
    }

    class Factory : Presenter.Factory {
        override fun create(screen: Screen, navigator: Navigator, context: CircuitContext): Presenter<*>? {
            return when (screen) {
                is BarScreen -> BarPresenter(navigator = navigator)
                else -> null
            }
        }
    }
}
