package com.anynetwork.app

import android.app.Application
import com.anynetwork.app.BuildConfig.*
import com.anynetwork.app.utils.ReleaseTimberTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.*

@HiltAndroidApp
class AnyNetworkApp: Application() {

    override fun onCreate() {
        super.onCreate()

        setupTimber()
    }

    private fun setupTimber() {
        Timber.plant(if (DEBUG) DebugTree() else ReleaseTimberTree())
    }
}