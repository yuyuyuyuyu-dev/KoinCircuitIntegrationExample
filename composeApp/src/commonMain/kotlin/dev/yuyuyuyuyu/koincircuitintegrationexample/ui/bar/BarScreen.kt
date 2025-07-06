package dev.yuyuyuyuyu.koincircuitintegrationexample.ui.bar

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

data object BarScreen : Screen {
    data class State(
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data object HelloButtonClicked : Event()
        data object NavigateBackButtonClicked : Event()
    }
}
