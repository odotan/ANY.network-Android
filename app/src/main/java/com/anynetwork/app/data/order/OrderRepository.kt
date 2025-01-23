package com.anynetwork.app.data.order

import com.anynetwork.app.db.dao.GridItemOrderDao
import com.anynetwork.app.db.entity.DbOrder
import com.anynetwork.app.db.entity.unwrap
import com.anynetwork.app.db.entity.wrap
import com.anynetwork.app.model.Order
import com.anynetwork.app.ui.screens.home.GridItem
import com.anynetwork.app.ui.utils.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val orderDao: GridItemOrderDao
) {

    suspend fun getOrder(): List<Order> = withContext(Dispatchers.IO) {
        orderDao.getAllOrders().map { it.unwrap() }
    }

    suspend fun updateItemOrders(orders: List<Order>) = withContext(Dispatchers.IO) {
        orders.forEach {
            Timber.i("update item order - itemId: ${it.itemId}, order: ${it.order}")
            orderDao.deleteOrderByItemTypeAndItemId(itemType = it.itemType, itemId = it.itemId)
            orderDao.insertOrUpdateOrder(orders = it.wrap())
        }
    }

    suspend fun insertItemOrder(itemType: Int, itemId: Long, order: Int = 0): Order = withContext(Dispatchers.IO) {
        Timber.i("insert item order - itemId: $itemId, order: $order")
        orderDao.deleteOrderByItemTypeAndItemId(itemType = itemType, itemId = itemId)
        val itemOrder = Order(
            order = order,
            itemType = itemType,
            itemId = itemId
        )

        orderDao.insertOrUpdateOrder(itemOrder.wrap())
        itemOrder
    }

    suspend fun latestOrder(): List<Order> = withContext(Dispatchers.IO) {
        orderDao.getLastEditedOrderWithHighestOrder().map { it.unwrap() }
    }
}
