package com.anynetwork.app.model

data class CarouselInteraction(
    val id: Long = 0,
    val contactId: Long,
    val type: Int,
    val lastModified: Long = System.currentTimeMillis()
)