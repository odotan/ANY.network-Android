package com.anynetwork.app.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anynetwork.app.db.entity.DbContact
import com.anynetwork.app.db.entity.DbProfile

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles LIMIT 1")
    suspend fun getProfile(): DbProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: DbProfile)
}