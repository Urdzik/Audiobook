package com.urdzik.feature.player.data.di

import com.urdzik.feature.player.data.repository.PlayerRepository
import com.urdzik.feature.player.data.repository.PlayerRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val playerDataModule = module {
    factoryOf(::PlayerRepositoryImpl) { bind<PlayerRepository>()}
}