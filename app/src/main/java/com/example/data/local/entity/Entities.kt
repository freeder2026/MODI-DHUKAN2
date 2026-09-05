package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey
    val productId: String,
    val banglaName: String,
    val englishName: String,
    val price: Double,
    val originalPrice: Double,
    val weightOrVolume: String,
    val quantity: Int = 1
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey
    val productId: String,
    val banglaName: String,
    val englishName: String,
    val price: Double,
    val originalPrice: Double,
    val weightOrVolume: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "customer_addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String, // যেমন "বাসা (Home)", "অফিস (Office)"
    val recipientName: String,
    val phone: String,
    val addressDetails: String,
    val area: String,
    val city: String = "ঢাকা (Dhaka)",
    val isDefault: Boolean = false
)

@Entity(tableName = "customer_orders")
data class OrderEntity(
    @PrimaryKey
    val orderId: String, // e.g. "SB-84912"
    val timestamp: Long = System.currentTimeMillis(),
    val totalItemsCount: Int,
    val subtotal: Double,
    val deliveryFee: Double,
    val discount: Double,
    val finalTotal: Double,
    val recipientName: String,
    val recipientPhone: String,
    val deliveryAddress: String,
    val deliveryMethod: String,
    val paymentMethod: String,
    val status: String = "অর্ডার গৃহীত হয়েছে (Order Placed)",
    val itemsSummary: String
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "মোহাম্মদ আলি (Md. Ali)",
    val phone: String = "01712345678",
    val email: String = "md.customer@gmail.com",
    val isLoggedIn: Boolean = false
)
