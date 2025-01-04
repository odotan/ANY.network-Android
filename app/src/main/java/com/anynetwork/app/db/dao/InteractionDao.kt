package com.anynetwork.app.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anynetwork.app.db.entity.DbContact
import com.anynetwork.app.db.entity.DbInteraction

@Dao
interface InteractionDao {
    @Query("SELECT * FROM interaction")
    suspend fun getAllInteractions(): List<DbInteraction>

    @Query("SELECT * FROM interaction WHERE id = :interactionId LIMIT 1")
    suspend fun getInteraction(interactionId: Long): DbInteraction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInteraction(interaction: DbInteraction): Long

    @Query("DELETE FROM interaction WHERE id = :id")
    suspend fun deleteInteractionById(id: Long)
}