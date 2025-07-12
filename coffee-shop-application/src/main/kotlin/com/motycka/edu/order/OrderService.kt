package com.motycka.edu.order

import com.motycka.edu.customer.InternalCustomerService
import com.motycka.edu.menu.InternalMenuService
import com.motycka.edu.menu.MenuItemDTO
import com.motycka.edu.menu.MenuItemResponse
import com.motycka.edu.menu.MenuItemId
import com.motycka.edu.security.IdentityDTO
import com.motycka.edu.user.UserRole
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val menuService: InternalMenuService,
    private val customerService: InternalCustomerService
) {

    suspend fun getAllOrders(identity: IdentityDTO): List<OrderResponse> {
        logger.info { "Fetching all orders for user: ${identity.userId}" }
        val orders = orderRepository.selectAll()
        return orders.map { toOrderResponse(it, identity) }
    }

    suspend fun getOrderById(identity: IdentityDTO, id: OrderId): OrderResponse? {
        logger.info { "Fetching order by ID: $id for user: ${identity.userId}" }
        val order = orderRepository.selectById(id) ?: return null
        return toOrderResponse(order, identity)
    }

    suspend fun createOrder(identity: IdentityDTO, request: OrderRequest): OrderResponse {
        logger.info { "Creating order for user: ${identity.userId}" }

        val customerId = customerService.getCustomer(identity.userId)?.id
            ?: error("Customer not found for user ${identity.userId}")

        val orderDTO = OrderDTO(
            id = null,
            customerId = customerId,
            status = OrderStatus.PENDING
        )
        val savedOrder = orderRepository.create(orderDTO)

        val orderItems = request.items.map {
            OrderItemDTO(
                id = null,
                orderId = savedOrder.id!!,
                menuItemId = it.menuItemId,
                quantity = it.quantity
            )
        }
        orderItemRepository.createOrderItems(orderItems)

        return toOrderResponse(savedOrder, identity)
    }

    suspend fun updateOrder(identity: IdentityDTO, id: OrderId, request: OrderUpdateRequest): OrderResponse? {
        logger.info { "Updating order $id to status ${request.status}" }
        val existingOrder = orderRepository.selectById(id) ?: return null
        val updated = existingOrder.copy(status = request.status)
        orderRepository.update(updated)
        return toOrderResponse(updated, identity)
    }

    private suspend fun toOrderResponse(order: OrderDTO, identity: IdentityDTO): OrderResponse {
        val orderItems = orderItemRepository.selectByOrderId(order.id!!)
        val menuItemIds = orderItems.map { it.menuItemId }.toSet()
        val menuItemsMap = menuService.getMenuItems(menuItemIds).associateBy { it.id }

        val discount = customerService.getDiscountPercent(identity.userId)
        val totalPrice = PriceCalculator.calculatePrice(
            menuItems = menuItemsMap.values.toList(),
            discountInPercent = discount,
            orderItems = orderItems
        )

        val itemResponses = orderItems.map {
            val menu = menuItemsMap[it.menuItemId] ?: error("Menu item not found")
            OrderItemResponse(
                menuItem = MenuItemResponse(
                    id = menu.id!!,
                    name = menu.name,
                    description = menu.description,
                    price = menu.price
                ),
                quantity = it.quantity
            )
        }

        return OrderResponse(
            id = order.id!!,
            menuItems = itemResponses,
            totalPrice = totalPrice,
            status = order.status
        )
    }
}
