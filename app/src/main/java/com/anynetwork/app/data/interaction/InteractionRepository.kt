package com.anynetwork.app.data.interaction

import com.anynetwork.app.db.dao.InteractionDao
import com.anynetwork.app.db.entity.unwrap
import com.anynetwork.app.db.entity.wrap
import com.anynetwork.app.model.Interaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InteractionRepository @Inject constructor(private val interactionDao: InteractionDao) {
    suspend fun getAllInteractions(): List<Interaction> = withContext(Dispatchers.IO) {
        interactionDao.getAllInteractions()
            .map { it.unwrap() }
    }

    suspend fun getInteraction(interactionId: Long): Interaction? = withContext(Dispatchers.IO) {
        interactionDao.getInteraction(interactionId)
            ?.unwrap()
    }

    suspend fun insertInteraction(interaction: Interaction): Long = withContext(Dispatchers.IO) {
        interactionDao.insertInteraction(interaction.wrap())
    }

    suspend fun deleteInteractionById(id: Long) = withContext(Dispatchers.IO) {
        interactionDao.deleteInteractionById(id)
    }
}