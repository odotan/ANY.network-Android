package com.anynetwork.app.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anynetwork.app.db.entity.DbCarouselInteraction
import com.anynetwork.app.db.entity.DbInteraction

@Dao
interface CarouselInteractionDao {
    @Query("SELECT * FROM carousel_interaction")
    suspend fun getAllInteractions(): List<DbCarouselInteraction>

    @Query("SELECT * FROM carousel_interaction WHERE id = :interactionId LIMIT 1")
    suspend fun getInteraction(interactionId: Long): DbCarouselInteraction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInteraction(interaction: DbCarouselInteraction): Long

    @Query("DELETE FROM carousel_interaction WHERE id = :id")
    suspend fun deleteInteractionById(id: Long)

    @Query("UPDATE carousel_interaction SET lastModified = :lastModified, type = :interactionType WHERE contactId = :contactId")
    suspend fun updateInteraction(contactId: Long, lastModified: Long, interactionType: Int)

    @Query("SELECT * FROM carousel_interaction WHERE contactId = :contactId ORDER BY lastModified DESC LIMIT 1")
    fun getLatestInteraction(contactId: Long): DbCarouselInteraction?
}