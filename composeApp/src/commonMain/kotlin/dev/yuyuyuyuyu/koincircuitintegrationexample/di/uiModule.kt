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
            .addPresenterFactory(factory = BarPresenter.Factory())

            .build()
    }
}
