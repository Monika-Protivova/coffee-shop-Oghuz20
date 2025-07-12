package com.motycka.edu.order

import com.motycka.edu.security.getUserIdentity
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val logger = KotlinLogging.logger {}

private const val ORDER_NOT_FOUND = "Order not found"
private const val INVALID_ID = "Invalid ID format"

fun Route.orderRoutes(
    orderService: OrderService,
    basePath: String
) {
    route("$basePath/orders") {

        get {
            logger.info { "GET all orders requested" }
            val identity = getUserIdentity()
            val orders = orderService.getAllOrders(identity)
            call.respond(orders)
        }

        get("/{id}") {
            val idParam = call.parameters["id"]
            val id = idParam?.toLongOrNull() ?: run {
                logger.warn { "Invalid ID format: $idParam" }
                call.respond(HttpStatusCode.BadRequest, INVALID_ID)
                return@get
            }

            val identity = getUserIdentity()
            val order = orderService.getOrderById(identity, id)

            if (order != null) {
                call.respond(order)
            } else {
                call.respond(HttpStatusCode.NotFound, ORDER_NOT_FOUND)
            }
        }

        post {
            logger.info { "POST create order request" }
            val identity = getUserIdentity()
            val request = call.receive<OrderRequest>()
            val createdOrder = orderService.createOrder(identity, request)
            call.respond(HttpStatusCode.Created, createdOrder)
        }

        put("/{id}") {
            val idParam = call.parameters["id"]
            val id = idParam?.toLongOrNull() ?: run {
                logger.warn { "Invalid ID format: $idParam" }
                call.respond(HttpStatusCode.BadRequest, INVALID_ID)
                return@put
            }

            val identity = getUserIdentity()
            val request = call.receive<OrderUpdateRequest>()
            val updatedOrder = orderService.updateOrder(identity, id, request)

            if (updatedOrder != null) {
                call.respond(updatedOrder)
            } else {
                call.respond(HttpStatusCode.NotFound, ORDER_NOT_FOUND)
            }
        }
    }
}
