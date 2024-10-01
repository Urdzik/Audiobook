package com.urdzik.feature.player.presentation.di

import com.urdzik.feature.chapters.presentation.contoller.getChaptersUIControllerDefinition
import com.urdzik.feature.player.domain.di.playerDomainModule
import com.urdzik.feature.player.presentation.contoller.getPlayerUIControllerDefinition
import com.urdzik.feature.player.presentation.ui.PlayerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val playerModule = module {
    includes(playerDomainModule)
    viewModelOf(::PlayerViewModel)
    scope<PlayerViewModel>{
        getPlayerUIControllerDefinition()
        getChaptersUIControllerDefinition()
    }
}