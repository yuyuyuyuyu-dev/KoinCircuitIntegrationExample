package dev.yuyuyuyuyu.koincircuitintegrationexample.ui.bar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Bar(state: BarScreen.State, modifier: Modifier) = Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Text("Bar screen")
    Button(
        content = { Text("Hello, world!") },
        onClick = {
            println("Hello, Koin and Circuit integration!")
        },
    )
    Button(
        content = { Text("Navigate back") },
        onClick = {
            state.eventSink(BarScreen.Event.NavigateBackButtonClicked)
        },
    )
}
