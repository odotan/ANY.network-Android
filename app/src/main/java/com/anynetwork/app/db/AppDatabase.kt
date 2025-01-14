package com.anynetwork.app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anynetwork.app.db.dao.ContactDao
import com.anynetwork.app.db.dao.GridItemOrderDao
import com.anynetwork.app.db.dao.InteractionDao
import com.anynetwork.app.db.dao.ProfileDao
import com.anynetwork.app.db.entity.DbContact
import com.anynetwork.app.db.entity.DbOrder
import com.anynetwork.app.db.entity.DbInteraction
import com.anynetwork.app.db.entity.DbProfile

@Database(entities = [DbContact::class, DbProfile::class, DbInteraction::class, DbOrder::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun profileDao(): ProfileDao
    abstract fun interactionDao(): InteractionDao
    abstract fun gridItemOrderDao(): GridItemOrderDao
}