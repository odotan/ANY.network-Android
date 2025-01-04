package com.anynetwork.app.db

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            val dbInstance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "anynetwork_database"
            )
                .fallbackToDestructiveMigration()
                .build()
            instance = dbInstance
            dbInstance
        }
    }
}