package com.anynetwork.app.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anynetwork.app.model.Interaction

@Entity(tableName = "interaction")
data class DbInteraction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactId: Long,
    val type: Int
)

fun DbInteraction.unwrap(): Interaction = Interaction(
    id = this.id,
    contactId = this.contactId,
    type = this.type
)

fun Interaction.wrap(): DbInteraction = DbInteraction(
    id = this.id,
    contactId = this.contactId,
    type = this.type
)