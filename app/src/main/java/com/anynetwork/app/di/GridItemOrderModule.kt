package com.anynetwork.app.di

import android.content.Context
import com.anynetwork.app.data.order.OrderRepository
import com.anynetwork.app.data.profile.ProfileRepository
import com.anynetwork.app.db.DatabaseProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class GridItemOrderModule {
    @Provides
    fun provideOrderRepository(@ApplicationContext context: Context): OrderRepository {
        return OrderRepository(DatabaseProvider.getDatabase(context).gridItemOrderDao())
    }
}