package com.anynetwork.app.di

import android.content.Context
import com.anynetwork.app.data.interaction.InteractionRepository
import com.anynetwork.app.db.DatabaseProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object InteractionModule {
    @Provides
    fun provideInteractionRepository(@ApplicationContext context: Context): InteractionRepository {
        return InteractionRepository(DatabaseProvider.getDatabase(context).interactionDao())
    }
}