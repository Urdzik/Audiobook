package com.urdzik.core.api.impl.di

import com.urdzik.core.api.contract.FirebaseApi
import com.urdzik.core.api.impl.FirebaseApiImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val apiModule = module {
    singleOf(::FirebaseApiImpl) {
        bind<FirebaseApi>()
    }
}