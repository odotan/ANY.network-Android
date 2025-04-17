package com.anynetwork.app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anynetwork.app.db.dao.CarouselInteractionDao
import com.anynetwork.app.db.dao.ContactDao
import com.anynetwork.app.db.dao.GridItemOrderDao
import com.anynetwork.app.db.dao.InteractionDao
import com.anynetwork.app.db.dao.ProfileDao
import com.anynetwork.app.db.entity.DbCarouselInteraction
import com.anynetwork.app.db.entity.DbContact
import com.anynetwork.app.db.entity.DbOrder
import com.anynetwork.app.db.entity.DbInteraction
import com.anynetwork.app.db.entity.DbProfile

@Database(entities = [DbContact::class, DbProfile::class, DbInteraction::class, DbOrder::class, DbCarouselInteraction::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun profileDao(): ProfileDao
    abstract fun interactionDao(): InteractionDao
    abstract fun gridItemOrderDao(): GridItemOrderDao
    abstract fun carouselInteractionDao(): CarouselInteractionDao
}