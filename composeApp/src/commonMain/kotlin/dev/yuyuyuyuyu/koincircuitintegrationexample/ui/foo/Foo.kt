package dev.yuyuyuyuyu.koincircuitintegrationexample.ui.foo

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
fun Foo(state: FooScreen.State, modifier: Modifier = Modifier) = Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Text("Foo screen")
    Button(
        content = { Text("Navigate to Bar screen") },
        onClick = { state.eventSink(FooScreen.Event.NavigateBarButtonClicked) },
    )
}
