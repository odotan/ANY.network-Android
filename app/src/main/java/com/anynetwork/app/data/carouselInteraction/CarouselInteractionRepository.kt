package com.anynetwork.app.data.carouselInteraction

import com.anynetwork.app.db.dao.CarouselInteractionDao
import com.anynetwork.app.db.dao.InteractionDao
import com.anynetwork.app.db.entity.DbCarouselInteraction
import com.anynetwork.app.db.entity.unwrap
import com.anynetwork.app.db.entity.wrap
import com.anynetwork.app.model.CarouselInteraction
import com.anynetwork.app.model.Interaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CarouselInteractionRepository @Inject constructor(private val carouselInteractionDao: CarouselInteractionDao) {
    suspend fun upsertInteraction(contactId: Long, interactionType: Int) = withContext(Dispatchers.IO) {
        val existingInteraction = carouselInteractionDao.getLatestInteraction(contactId)
        if (existingInteraction == null) {
            // Insert new interaction
            carouselInteractionDao.insertInteraction(
                DbCarouselInteraction(
                    contactId = contactId,
                    type = interactionType,
                    lastModified = System.currentTimeMillis()
                )
            )
        } else {
            // Update existing interaction
            carouselInteractionDao.updateInteraction(
                contactId = contactId,
                interactionType = interactionType,
                lastModified = System.currentTimeMillis()
            )
        }
    }

    suspend fun getLatestInteraction(contactId: Long): CarouselInteraction? = withContext(Dispatchers.IO) {
        carouselInteractionDao.getLatestInteraction(contactId)?.unwrap()
    }
}