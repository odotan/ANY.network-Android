package com.anynetwork.app.di

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class) // Install in ActivityComponent
object ActivityModule {

    @Provides
    @ActivityContext
    fun provideActivityContext(activity: Activity): Context = activity
}