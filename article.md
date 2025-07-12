# How to Integrate Koin and Circuit in Compose Multiplatform

Building user interfaces (UIs) with Compose Multiplatform is becoming popular. When building your app, you might look for good ways to structure it. For UI architecture, you can use [Circuit](https://slackhq.github.io/circuit/) from Slack. For dependency injection, you can use [Koin](https://insert-koin.io/). They work well together.

This article explains how to use Koin and Circuit together in a Compose Multiplatform project. We will create a simple app that moves between two screens. This will help you understand how they connect.

## What are Koin and Circuit?

*   **Koin**: A simple dependency injection tool for Kotlin. It helps you manage the parts of your application in an easy way.
*   **Circuit**: A library from Slack for UI architecture. It helps you build UIs that are based on state and events. This makes your UI code separate from the platform it runs on. It is built for Jetpack Compose and works well with Compose Multiplatform.

## Step 1: Add Dependencies

First, we need to add the Koin and Circuit libraries to our project.

### 1.1. Define Versions in `gradle/libs.versions.toml`
In your `gradle/libs.versions.toml` file, add the versions and libraries:

```toml
[versions]
koin = "4.1.0"
circuit = "0.29.1"
# ... other versions

[libraries]
koin-compose = { group = "io.insert-koin", name = "koin-compose", version.ref = "koin" }
circuit = { group = "com.slack.circuit", name = "circuit-foundation", version.ref = "circuit" }
# ... other libraries
```

### 1.2. Add Dependencies in `composeApp/build.gradle.kts`
Next, add these libraries to the `commonMain` source set in your `composeApp/build.gradle.kts` file. This makes Koin and Circuit available for all platforms your app supports.

```kotlin
// ...
kotlin {
    // ...
    sourceSets {
        // ...
        commonMain.dependencies {
            // ...
            implementation(libs.koin.compose)
            implementation(libs.circuit)
        }
        // ...
    }
}
// ...
```

## Step 2: Create Screens with Circuit

Now that the libraries are added, we can create our screens. We will use Circuit's main parts: `Screen`, `Presenter`, and the UI. We will make two screens: `FooScreen` and `BarScreen`.

### 2.1. FooScreen: The First Screen

`FooScreen` is the first screen the user will see. It has a button to go to `BarScreen`.

`composeApp/src/commonMain/kotlin/dev/yuyuyuyuyu/koincircuitintegrationexample/ui/foo/FooScreen.kt`
```kotlin
package dev.yuyuyuyuyu.koincircuitintegrationexample.ui.foo

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

data object FooScreen : Screen {
    data class State(
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data object NavigateBarButtonClicked : Event()
    }
}
```

The `FooPresenter` contains the logic for navigation.

`composeApp/src/commonMain/kotlin/dev/yuyuyuyuyu/koincircuitintegrationexample/ui/foo/FooPresenter.kt`
```kotlin
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
```

Here is the UI for `FooScreen`:

`composeApp/src/commonMain/kotlin/dev/yuyuyuyuyu/koincircuitintegrationexample/ui/foo/Foo.kt`
```kotlin
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
```

### 2.2. BarScreen: A Screen with Dependencies

`BarScreen` shows how a presenter can get dependencies from Koin. It has a button that uses a `HelloUseCase` and another button to go back.

`composeApp/src/commonMain/kotlin/dev/yuyuyuyuyu/koincircuitintegrationexample/ui/bar/BarScreen.kt`
```kotlin
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
```

The `BarPresenter` needs a `HelloUseCase` to work.

`composeApp/src/commonMain/kotlin/dev/yuyuyuyuyu/koincircuitintegrationexample/ui/bar/BarPresenter.kt`
```kotlin
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
```

Here is the UI for `BarScreen`:

`composeApp/src/commonMain/kotlin/dev/yuyuyuyuyu/koincircuitintegrationexample/ui/bar/Bar.kt`
```kotlin
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
            state.eventSink(BarScreen.Event.HelloButtonClicked)
        },
    )
    Button(
        content = { Text("Navigate back") },
        onClick = {
            state.eventSink(BarScreen.Event.NavigateBackButtonClicked)
        },
    )
}
```

## Step 3: Create Koin Modules

Now, we will create Koin modules. These modules will provide the dependencies for our application. This includes the `Circuit` object and other dependencies like `HelloUseCase`.

### 3.1. Domain and Data Modules

First, we create modules for our domain and data layers. In this example, we only have a simple `HelloUseCase`.

`composeApp/src/commonMain/kotlin/dev/yuyuyuyuyu/koincircuitintegrationexample/di/domainModule.kt`
```kotlin
package dev.yuyuyuyuyu.koincircuitintegrationexample.di

import dev.yuyuyuyuyu.koincircuitintegrationexample.domain.useCase.HelloUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::HelloUseCase)
}
```

We also have a `dataModule`. But this sample app does not have a data layer, so the module is empty.

`composeApp/src/commonMain/kotlin/dev/yuyuyuyuyu/koincircuitintegrationexample/di/dataModule.kt`
```kotlin
package dev.yuyuyuyuyu.koincircuitintegrationexample.di

import org.koin.dsl.module

val dataModule = module {
    // add modules if needed
}
```

### 3.2. UI Module with Circuit

Next, we create a `uiModule` that provides the `Circuit` object. This is where we connect Koin and Circuit. We set up Circuit with our screens and presenters. We use Koin's `get()` function to give the `HelloUseCase` to the `BarPresenter.Factory`.

`composeApp/src/commonMain/kotlin/dev/yuyuyuyuyu/koincircuitintegrationexample/di/uiModule.kt`
```kotlin
package dev.yuyuyuyuyu.koincircuitintegrationexample.di

import com.slack.circuit.foundation.Circuit
import dev.yuyuyuyuyu.koincircuitintegrationexample.ui.bar.Bar
import dev.yuyuyuyuyu.koincircuitintegrationexample.ui.bar.BarPresenter
import dev.yuyuyuyuyu.koincircuitintegrationexample.ui.bar.BarScreen
import dev.yuyuyuyuyu.koincircuitintegrationexample.ui.foo.Foo
import dev.yuyuyuyuyu.koincircuitintegrationexample.ui.foo.FooPresenter
import dev.yuyuyuyuyu.koincircuitintegrationexample.ui.foo.FooScreen
import org.koin.dsl.module

val uiModule = module {
    single {
        Circuit.Builder()
            .addUi<FooScreen, FooScreen.State> { state, modifier ->
                Foo(state = state, modifier = modifier)
            }
            .addPresenterFactory(factory = FooPresenter.Factory())

            .addUi<BarScreen, BarScreen.State> { state, modifier ->
                Bar(state = state, modifier = modifier)
            }
            .addPresenterFactory(
                factory = BarPresenter.Factory(
                    helloUseCase = get()
                )
            )

            .build()
    }
}
```

### 3.3. Application Module

Finally, we create a main application module that includes all the other modules.

`composeApp/src/commonMain/kotlin/dev/yuyuyuyuyu/koincircuitintegrationexample/di/koinCircuitIntegrationExampleAppModule.kt`
```kotlin
package dev.yuyuyuyuyu.koincircuitintegrationexample.di

import org.koin.dsl.module

val koinCircuitIntegrationExampleAppModule = module {
    includes(uiModule, domainModule, dataModule)
}
```

## Step 4: Connect Koin and Circuit in the Main App

Now that we have our screens and Koin modules, the last step is to connect them in our main composable. We will use the `KoinApplication` composable to start Koin. We will use `CircuitCompositionLocals` to give the `Circuit` object to our UI.

`composeApp/src/commonMain/kotlin/dev/yuyuyuyuyu/koincircuitintegrationexample/ui/KoinCircuitIntegrationExampleApp.kt`
```kotlin
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
```

This is how it works:
1.  `KoinApplication` starts Koin with our `koinCircuitIntegrationExampleAppModule`.
2.  `koinInject()` gets the `Circuit` object that we defined in our `uiModule`.
3.  `CircuitCompositionLocals` makes the `Circuit` object available to all composables in the UI tree.
4.  `NavigableCircuitContent` uses the `Circuit` object to manage screen navigation and display.

A big benefit of the `koin-compose` library is the `KoinApplication` composable. If your Compose Multiplatform project does not use Android, you do not need to call `startKoin { ... }` for each platform. You can just use `KoinApplication` in your main composable to make Koin available.

## Conclusion

When you use Koin and Circuit together, you get two big benefits. Circuit gives you a clean UI architecture. Koin gives you a simple way to manage dependencies. This combination helps you build Compose Multiplatform applications that are easy to scale, test, and maintain. The setup is simple and gives you a strong base for managing dependencies in your whole app, from the data layer to the UI presenters.

Happy coding!
