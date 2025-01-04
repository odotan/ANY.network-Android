package com.anynetwork.app.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anynetwork.app.db.entity.DbContact

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts")
    suspend fun getAllContacts(): List<DbContact>

    @Query("SELECT * FROM contacts WHERE id = :contactId LIMIT 1")
    suspend fun getContact(contactId: Long): DbContact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: DbContact)

    @Query("UPDATE contacts SET isFavorite = :isFavorite WHERE id = :contactId")
    suspend fun updateFavoriteStatus(contactId: Long, isFavorite: Boolean): Int

    @Delete
    suspend fun deleteContact(contact: DbContact)

    @Update
    suspend fun updateContact(contact: DbContact): Int

    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()
}