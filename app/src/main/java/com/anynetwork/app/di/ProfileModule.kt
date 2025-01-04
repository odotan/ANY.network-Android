package com.anynetwork.app.di

import android.content.Context
import com.anynetwork.app.data.profile.ProfileRepository
import com.anynetwork.app.db.DatabaseProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {
    @Provides
    fun provideProfileRepository(@ApplicationContext context: Context): ProfileRepository {
        return ProfileRepository(DatabaseProvider.getDatabase(context).profileDao())
    }
}