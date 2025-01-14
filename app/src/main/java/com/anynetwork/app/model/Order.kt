package com.anynetwork.app.model

data class Order(
    val id: Long = 0,
    val order: Int,
    val itemType: Int = Type.EMPTY,
    val itemId: Long?
) {
    object Type {
        const val EMPTY = -1
        const val FAVORITE_CONTACT = 0
        const val INTERACTION = 1
    }
}