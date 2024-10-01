package com.urdzik.audiobook.di

import com.urdzik.core.api.impl.di.apiModule
import com.urdzik.core.database.impl.databaseModule
import org.koin.dsl.module

val coreModules = module {
    includes(
        apiModule,
        databaseModule
    )
}