package com.motycka.edu.order

import com.motycka.edu.config.suspendTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class OrderRepositoryImpl : OrderRepository {

    override fun selectAll(): List<OrderDTO> = suspendTransaction {
        OrderDAO.all().map { it.toDTO() }
    }

    override fun selectById(id: OrderId): OrderDTO? = suspendTransaction {
        OrderDAO.findById(id)?.toDTO()
    }

    override fun create(order: OrderDTO): OrderDTO = suspendTransaction {
        OrderDAO.new {
            customerId = order.customerId
            status = order.status
        }.toDTO()
    }

    override fun update(order: OrderDTO): OrderDTO = suspendTransaction {
        val existing = OrderDAO.findById(order.id!!)
        requireNotNull(existing)
        existing.status = order.status
        existing.toDTO()
    }
}
