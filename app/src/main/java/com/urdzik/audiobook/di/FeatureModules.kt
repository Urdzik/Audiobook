package com.urdzik.audiobook.di

import com.urdzik.feature.player.presentation.di.playerModule
import org.koin.dsl.module

val featureModules = module {
    includes(playerModule)
}