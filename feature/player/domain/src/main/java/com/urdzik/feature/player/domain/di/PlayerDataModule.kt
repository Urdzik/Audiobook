package com.urdzik.feature.player.domain.di

import com.urdzik.feature.player.data.di.playerDataModule
import com.urdzik.feature.player.domain.manager.AudioManager
import com.urdzik.feature.player.domain.manager.AudioManagerImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val playerDomainModule = module {
    includes(playerDataModule)
    singleOf(::AudioManagerImpl) { bind<AudioManager>() }
}