package com.anynetwork.app.model

data class Interaction(
    val id: Long = 0,
    val contactId: Long,
    val type: Int
) {
    sealed class Type {
        companion object {
            const val None = -1
            const val Phone = 0
            const val Email = 1
        }
    }
}