package com.anynetwork.app.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anynetwork.app.model.Order

@Entity(tableName = "grid_item_order")
data class DbOrder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val order: Int,
    val itemType: Int,
    val itemId: Long?,
    val lastModified: Long = System.currentTimeMillis()
)

fun DbOrder.unwrap(): Order = Order(
    id = this.id,
    order = this.order,
    itemType = this.itemType,
    itemId = this.itemId
)

fun Order.wrap(): DbOrder = DbOrder(
    id = this.id,
    order = this.order,
    itemType = this.itemType,
    itemId = this.itemId,
    lastModified = System.currentTimeMillis()
)