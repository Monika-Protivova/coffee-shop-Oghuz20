# ☕ Coffee Shop API

This project is a sample backend application built in Kotlin for managing a coffee shop's order system. It demonstrates key backend development practices, including secure RESTful API design, JWT-based authentication, and business logic implementation (e.g., dynamic discounts for customers).

---

## 📌 Review Assignment Due Date

6 July 2025

---

## 📚 Overview

The Coffee Shop API allows users to:
- Place and retrieve orders
- View menus
- Get personalized discounts (based on customer profile)
- Update order statuses

All endpoints are secured and require authentication using JSON Web Tokens (JWT).

---

## 🔐 Authentication

Every endpoint requires a valid JWT token with either `CUSTOMER` or `STAFF` role.  
Send it in the `Authorization` header:
```
Authorization: Bearer <your_token>
```

---

## 📦 API Endpoints

### ✅ GET /api/v1/orders
Get all orders (STAFF or CUSTOMER)

### ✅ GET /api/v1/orders/{id}
Get specific order (applies discount if applicable)

### ✅ POST /api/v1/orders
Create new order  
Automatically applies discount using: 
```
finalPrice = originalPrice * (1 - discountPercent / 100)
```


### ✅ PUT /api/v1/orders/{id}
Update an order's status (e.g., to COMPLETED)
---

## 📊 Sample Data Models

### 🧾 Order
```json
{
  "id": 1,
  "customerId": 2,
  "menuItems": [
    {
      "menuItem": {
        "id": 1,
        "name": "Espresso",
        "description": "Strong coffee shot",
        "price": 2.50
      },
      "quantity": 2
    }
  ],
  "totalPrice": 4.50,
  "status": "PENDING",
  "isPaid": false
}
🍽️ MenuItem
{
  "id": 1,
  "name": "Espresso",
  "description": "Strong coffee shot",
  "price": 2.50
}
🧍 Customer
{
  "id": 2,
  "userId": 7,
  "name": "Jane Doe",
  "discountPercent": 10.0
}

## 📄 License

This project is provided for educational purposes...
