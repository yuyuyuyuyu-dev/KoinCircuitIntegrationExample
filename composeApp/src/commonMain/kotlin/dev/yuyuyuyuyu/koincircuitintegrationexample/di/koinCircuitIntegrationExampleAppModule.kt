package dev.yuyuyuyuyu.koincircuitintegrationexample.di

import org.koin.dsl.module

val koinCircuitIntegrationExampleAppModule = module {
    includes(uiModule, domainModule, dataModule)
}
