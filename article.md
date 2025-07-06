---
title: 'Seamless UI Architecture: Integrating Koin with Circuit in Compose Multiplatform'
published: false
description: 'A step-by-step guide to integrating Koin for dependency injection with Slacks Circuit library for a clean and scalable UI architecture in your Compose Multiplatform projects.'
tags: kotlin, compose, android, koin, multiplatform
---

Building UIs with Compose Multiplatform is getting more and more popular. If you're looking for a solid way to structure your app, you might have heard of [Circuit](https://slackhq.github.io/circuit/) from Slack for UI architecture and [Koin](https://insert-koin.io/) for dependency injection. They work great together!

This post will show you how to integrate Koin and Circuit in your Compose Multiplatform project. We'll build a simple app that navigates between two screens to see how it all fits together.

## What are Koin and Circuit?

*   **Koin**: A lightweight dependency injection framework for Kotlin. It provides a simple and pragmatic way to manage dependencies in your application.
*   **Circuit**: A UI architecture library from Slack. It helps in building UI that is state-driven, event-based, and decoupled from the underlying platform. It's built on top of Jetpack Compose and works seamlessly with Compose Multiplatform.

## Step 1: Setting Up Dependencies

First, let's add the necessary dependencies for Koin and Circuit to our project.

### 1.1. Define Versions in `gradle/libs.versions.toml`
In your `gradle/libs.versions.toml` file, define the versions and libraries:

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

### 1.2. Apply Dependencies in `composeApp/build.gradle.kts`
Next, apply these dependencies to the `commonMain` source set in your `composeApp/build.gradle.kts` file. This makes Koin and Circuit available to all your multiplatform targets.

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

## Step 2: Creating the Circuit Instance with Koin

The core of the integration lies in how we instantiate the `Circuit` object. We can do this within a Koin module, allowing Koin to manage the lifecycle of our Circuit instance and inject its dependencies.

Here’s how you can define a `uiModule` that provides the `Circuit` instance.

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
                    // Injecting a UseCase into the Presenter's factory
                    helloUseCase = get() 
                )
            )
            .build()
    }
}
```

In this module:
1.  We define a `single` instance of `Circuit`.
2.  We use `Circuit.Builder()` to configure it.
3.  `addUi` links a `Screen` to its Composable UI implementation.
4.  `addPresenterFactory` links a `Screen` to its `Presenter`.
5.  Crucially, inside `BarPresenter.Factory`, we use `get()` to resolve a dependency (`HelloUseCase`) from Koin. This demonstrates how easily you can inject other parts of your application (like data or domain layers) into your presenters.

## Step 3: Injecting Dependencies into a Presenter

Let's look at the `BarPresenter`. Its factory takes `HelloUseCase` as a constructor parameter. Koin provides this dependency when creating the `Circuit` instance.

`composeApp/src/commonMain/kotlin/dev/yuyuyuyuyu/koincircuitintegrationexample/ui/bar/BarPresenter.kt`
```kotlin
class BarPresenter(
    private val navigator: Navigator,
    private val helloUseCase: HelloUseCase, // Injected dependency
) : Presenter<BarScreen.State> {
    @Composable
    override fun present(): BarScreen.State {
        // ... presenter logic
    }

    class Factory(
        private val helloUseCase: HelloUseCase
    ) : Presenter.Factory {
        override fun create(screen: Screen, navigator: Navigator, context: CircuitContext): Presenter<*>? {
            return when (screen) {
                is BarScreen -> BarPresenter(navigator, helloUseCase)
                else -> null
            }
        }
    }
}
```
This pattern keeps your presenters clean and testable, as their dependencies are explicitly provided.

For presenters without external dependencies, like `FooPresenter`, the setup is even simpler.

`composeApp/src/commonMain/kotlin/dev/yuyuyuyuyu/koincircuitintegrationexample/ui/foo/FooPresenter.kt`
```kotlin
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

## Step 4: Setting Up the Application Root

Finally, we need to tie everything together at the application's entry point. We'll use `KoinApplication` to set up our Koin modules and then provide the `Circuit` instance to our UI using `CircuitCompositionLocals`.

`composeApp/src/commonMain/kotlin/dev/yuyuyuyuyu/koincircuitintegrationexample/ui/KoinCircuitIntegrationExampleApp.kt`
```kotlin
@Composable
@Preview
fun KoinCircuitIntegrationExampleApp() {
    val backStack = rememberSaveableBackStack(root = FooScreen)
    val navigator = rememberCircuitNavigator(backStack) {}

    KoinApplication(
        application = {
            printLogger()
            // Provide all your modules here
            modules(koinCircuitIntegrationExampleAppModule) 
        },
    ) {
        MaterialTheme {
            // Inject the Circuit instance and provide it to the composable tree
            CircuitCompositionLocals(circuit = koinInject()) {
                NavigableCircuitContent(navigator, backStack)
            }
        }
    }
}
```

A key thing to note here is the use of the `KoinApplication` composable. It handles the Koin setup for your entire Compose Multiplatform app. You might be used to calling `startKoin { ... }` in the `Application` class on Android or in `main` functions for other targets. With `koin-compose`, you just need to wrap your root composable with `KoinApplication`, and you're good to go across all platforms—no need for platform-specific `startKoin` calls!

Here's the flow:
1.  `KoinApplication` initializes Koin with all the necessary modules (`koinCircuitIntegrationExampleAppModule` would include our `uiModule` and others).
2.  `koinInject()` retrieves the singleton `Circuit` instance that we defined in our module.
3.  `CircuitCompositionLocals` makes this `Circuit` instance available to all composables down the tree, including `NavigableCircuitContent` which handles the screen navigation and rendering.

## Conclusion

By integrating Koin with Circuit, you get the best of both worlds: Circuit's clean, composable UI architecture and Koin's simple, powerful dependency injection. This combination allows you to build scalable, testable, and maintainable UIs in your Compose Multiplatform applications with minimal boilerplate. The setup is straightforward and provides a solid foundation for managing dependencies across your entire app, from the data layer right up to the UI presenters.

Happy coding!
