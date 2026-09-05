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
    val itemsSummary: String,
    val deliveryMan: String? = null
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

@Entity(tableName = "admin_products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val banglaName: String,
    val englishName: String,
    val categoryId: String,
    val categoryBangla: String,
    val price: Double,
    val originalPrice: Double,
    val weightOrVolume: String,
    val stockQuantity: Int = 50,
    val inStock: Boolean = true,
    val rating: Float = 4.8f,
    val reviewCount: Int = 120,
    val description: String,
    val isFlashSale: Boolean = false,
    val isPopular: Boolean = false,
    val isNewArrival: Boolean = false,
    val brand: String = "Shohoj Bazar",
    val tags: String = "",
    val emoji: String = "🛒"
)

@Entity(tableName = "admin_categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val banglaName: String,
    val englishName: String,
    val emoji: String,
    val itemCount: Int = 0
)

@Entity(tableName = "admin_coupons")
data class CouponEntity(
    @PrimaryKey
    val code: String,
    val discountType: String, // "PERCENTAGE" or "FIXED"
    val discountValue: Double,
    val minOrderAmount: Double,
    val isActive: Boolean = true,
    val expiryDate: String = "৩১ ডিসেম্বর ২০২৬",
    val description: String = ""
)

@Entity(tableName = "stock_logs")
data class StockLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: String,
    val productName: String,
    val changeType: String, // "STOCK_IN", "STOCK_OUT", "ADJUSTMENT"
    val quantityChanged: Int,
    val newStock: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String
)

@Entity(tableName = "delivery_men")
data class DeliveryManEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val phone: String,
    val area: String,
    val activeDeliveries: Int = 0
)

@Entity(tableName = "registered_users")
data class RegisteredUserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    
    // বর্তমান ঠিকানা (Present Address)
    val presentAddress: String,
    val presentPostOffice: String,
    val presentUpazila: String,
    val presentDistrict: String,
    val presentPostCode: String,
    
    // অস্থায়ী ঠিকানা (Temporary Address)
    val tempAddress: String,
    val tempPostOffice: String,
    val tempUpazila: String,
    val tempDistrict: String,
    val tempPostCode: String,
    val isTempSameAsPresent: Boolean = false,
    
    // স্থায়ী ঠিকানা (Permanent Address)
    val permanentAddress: String,
    val permanentPostOffice: String,
    val permanentUpazila: String,
    val permanentDistrict: String,
    val permanentPostCode: String,
    val isPermanentSameAsPresent: Boolean = false,
    
    val registeredAt: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE", // "ACTIVE", "BLOCKED"
    val tempDemoPassword: String? = null // Admin assigned demo password
)

@Entity(tableName = "password_reset_requests")
data class PasswordResetRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userPhoneOrEmail: String,
    val userName: String,
    val requestTimestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // "PENDING", "RESOLVED"
    val assignedDemoPassword: String? = null
)

