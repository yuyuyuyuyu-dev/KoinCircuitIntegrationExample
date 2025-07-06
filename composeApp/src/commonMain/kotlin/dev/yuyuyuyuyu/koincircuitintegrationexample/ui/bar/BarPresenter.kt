package dev.yuyuyuyuyu.koincircuitintegrationexample.ui.bar

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.yuyuyuyuyu.koincircuitintegrationexample.domain.useCase.HelloUseCase

class BarPresenter(
    private val navigator: Navigator,
    private val helloUseCase: HelloUseCase,
) : Presenter<BarScreen.State> {
    @Composable
    override fun present(): BarScreen.State {
        return BarScreen.State { event ->
            when (event) {
                BarScreen.Event.HelloButtonClicked -> helloUseCase()
                BarScreen.Event.NavigateBackButtonClicked -> navigator.pop()
            }
        }
    }

    class Factory(
        private val helloUseCase: HelloUseCase,
    ) : Presenter.Factory {
        override fun create(screen: Screen, navigator: Navigator, context: CircuitContext): Presenter<*>? {
            return when (screen) {
                is BarScreen -> BarPresenter(helloUseCase = helloUseCase, navigator = navigator)
                else -> null
            }
        }
    }
}
