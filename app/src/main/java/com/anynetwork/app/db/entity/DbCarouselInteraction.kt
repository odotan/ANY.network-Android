package com.anynetwork.app.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anynetwork.app.model.CarouselInteraction

@Entity(tableName = "carousel_interaction")
data class DbCarouselInteraction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactId: Long,
    val type: Int,
    val lastModified: Long
)

fun DbCarouselInteraction.unwrap(): CarouselInteraction = CarouselInteraction(
    id = this.id,
    contactId = this.contactId,
    type = this.type,
    lastModified = this.lastModified
)

fun CarouselInteraction.wrap(): DbCarouselInteraction = DbCarouselInteraction(
    id = this.id,
    contactId = this.contactId,
    type = this.type,
    lastModified = this.lastModified
)