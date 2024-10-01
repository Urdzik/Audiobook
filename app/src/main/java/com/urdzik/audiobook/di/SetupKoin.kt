package com.urdzik.audiobook.di

import com.urdzik.audiobook.AudiobookApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

fun AudiobookApplication.setupKoin() {
    startKoin {
        androidContext(this@setupKoin)
        modules(
            coreModules,
            featureModules
        )
    }
}