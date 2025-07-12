package com.motycka.edu.order

import com.motycka.edu.menu.MenuItemDTO

object PriceCalculator {
    fun calculatePrice(
        menuItems: List<MenuItemDTO>,
        discountInPercent: Double,
        orderItems: List<OrderItemDTO>
    ): Double {
        val total = orderItems.sumOf { item ->
            val menuItem = menuItems.find { it.id == item.menuItemId }
                ?: error("Menu item not found for ID: ${item.menuItemId}")
            menuItem.price * item.quantity
        }
        return total * (1 - discountInPercent / 100)
    }
}
