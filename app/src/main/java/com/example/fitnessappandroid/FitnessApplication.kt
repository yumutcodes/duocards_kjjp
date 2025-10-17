package com.example.fitnessappandroid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class FitnessApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Debug modunda Timber logging'i etkinleştir
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}

