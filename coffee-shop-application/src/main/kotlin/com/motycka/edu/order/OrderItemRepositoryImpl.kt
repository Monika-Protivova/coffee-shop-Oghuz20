package com.motycka.edu.order

import com.motycka.edu.config.suspendTransaction

class OrderItemRepositoryImpl : OrderItemRepository {

    override fun selectByOrderId(orderId: OrderId): List<OrderItemDTO> = suspendTransaction {
        OrderItemDAO.find { OrderItemTable.orderId eq orderId }
            .map { it.toDTO() }
    }

    override fun createOrderItems(orderItems: List<OrderItemDTO>) = suspendTransaction {
        orderItems.forEach { item ->
            OrderItemDAO.new {
                this.orderId = item.orderId
                this.menuItemId = item.menuItemId
                this.quantity = item.quantity
            }
        }
    }
}
