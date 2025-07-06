package dev.yuyuyuyuyu.koincircuitintegrationexample.di

import dev.yuyuyuyuyu.koincircuitintegrationexample.domain.useCase.HelloUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::HelloUseCase)
}
