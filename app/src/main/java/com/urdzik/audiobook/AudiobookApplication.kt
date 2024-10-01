package com.urdzik.audiobook

import android.app.Application
import com.urdzik.audiobook.di.setupKoin

class AudiobookApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        setupKoin()
    }
}