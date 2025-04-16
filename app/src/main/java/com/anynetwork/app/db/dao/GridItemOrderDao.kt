package com.anynetwork.app.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anynetwork.app.db.entity.DbOrder

@Dao
interface GridItemOrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateOrder(orders: DbOrder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateOrders(orders: List<DbOrder>)

    @Query("SELECT * FROM grid_item_order ORDER BY `order` ASC")
    suspend fun getAllOrders(): List<DbOrder>

    @Query("SELECT * FROM grid_item_order ORDER BY `order` DESC LIMIT 1")
    suspend fun getOrderWithHighestOrder(): DbOrder?

    @Query("SELECT * FROM grid_item_order ORDER BY lastModified DESC")
    suspend fun getLastEditedOrder(): List<DbOrder>

    @Query("SELECT * FROM grid_item_order ORDER BY lastModified DESC, `order` DESC")
    suspend fun getLastEditedOrderWithHighestOrder(): List<DbOrder>

    @Query("DELETE FROM grid_item_order WHERE itemType = :itemType AND itemId = :itemId")
    suspend fun deleteOrderByItemTypeAndItemId(itemType: Int, itemId: Long?): Int
}