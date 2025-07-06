package dev.yuyuyuyuyu.koincircuitintegrationexample.ui.foo

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.yuyuyuyuyu.koincircuitintegrationexample.ui.bar.BarScreen

class FooPresenter(
    private val navigator: Navigator,
) : Presenter<FooScreen.State> {
    @Composable
    override fun present(): FooScreen.State {
        return FooScreen.State { event ->
            when (event) {
                FooScreen.Event.NavigateBarButtonClicked -> navigator.goTo(BarScreen)
            }
        }
    }

    class Factory : Presenter.Factory {
        override fun create(screen: Screen, navigator: Navigator, context: CircuitContext): Presenter<*>? {
            return when (screen) {
                is FooScreen -> FooPresenter(navigator = navigator)
                else -> null
            }
        }
    }
}
