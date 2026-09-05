package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.AddressEntity
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.local.entity.WishlistItemEntity
import com.example.data.model.Product
import com.example.data.model.ProductCatalog
import com.example.data.model.ProductCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class BazarRepository(private val database: AppDatabase) {

    // Cart
    val cartItems: Flow<List<CartItemEntity>> = database.cartDao().getAllCartItems()

    suspend fun addToCart(product: Product, quantity: Int = 1) {
        val existingItems = database.cartDao().getAllCartItems().firstOrNull()
        val existing = existingItems?.firstOrNull { it.productId == product.id }
        if (existing != null) {
            database.cartDao().updateQuantity(product.id, existing.quantity + quantity)
        } else {
            database.cartDao().insertOrUpdate(
                CartItemEntity(
                    productId = product.id,
                    banglaName = product.banglaName,
                    englishName = product.englishName,
                    price = product.price,
                    originalPrice = product.originalPrice,
                    weightOrVolume = product.weightOrVolume,
                    quantity = quantity
                )
            )
        }
    }

    suspend fun updateCartQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            database.cartDao().deleteCartItem(productId)
        } else {
            database.cartDao().updateQuantity(productId, quantity)
        }
    }

    suspend fun removeFromCart(productId: String) {
        database.cartDao().deleteCartItem(productId)
    }

    suspend fun clearCart() {
        database.cartDao().clearCart()
    }

    // Wishlist
    val wishlistItems: Flow<List<WishlistItemEntity>> = database.wishlistDao().getAllWishlistItems()

    fun isWishlisted(productId: String): Flow<Boolean> = database.wishlistDao().isWishlisted(productId)

    suspend fun toggleWishlist(product: Product) {
        val items = database.wishlistDao().getAllWishlistItems().firstOrNull()
        val exists = items?.any { it.productId == product.id } == true
        if (exists) {
            database.wishlistDao().deleteWishlistItem(product.id)
        } else {
            database.wishlistDao().insertWishlistItem(
                WishlistItemEntity(
                    productId = product.id,
                    banglaName = product.banglaName,
                    englishName = product.englishName,
                    price = product.price,
                    originalPrice = product.originalPrice,
                    weightOrVolume = product.weightOrVolume
                )
            )
        }
    }

    suspend fun removeFromWishlist(productId: String) {
        database.wishlistDao().deleteWishlistItem(productId)
    }

    // Addresses
    val addresses: Flow<List<AddressEntity>> = database.addressDao().getAllAddresses()

    suspend fun addAddress(address: AddressEntity) {
        database.addressDao().insertAddress(address)
    }

    suspend fun deleteAddress(id: Long) {
        database.addressDao().deleteAddress(id)
    }

    suspend fun setDefaultAddress(id: Long) {
        database.addressDao().setAsDefaultAddress(id)
    }

    // Orders
    val orders: Flow<List<OrderEntity>> = database.orderDao().getAllOrders()

    suspend fun placeOrder(
        cartItems: List<CartItemEntity>,
        subtotal: Double,
        deliveryFee: Double,
        discount: Double,
        finalTotal: Double,
        address: AddressEntity,
        deliveryMethod: String,
        paymentMethod: String
    ): String {
        val orderNum = "SB-" + Random.nextInt(10000, 99999)
        val summary = cartItems.joinToString(", ") { "${it.banglaName} (${it.quantity}x)" }
        val order = OrderEntity(
            orderId = orderNum,
            timestamp = System.currentTimeMillis(),
            totalItemsCount = cartItems.sumOf { it.quantity },
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            discount = discount,
            finalTotal = finalTotal,
            recipientName = address.recipientName,
            recipientPhone = address.phone,
            deliveryAddress = "${address.addressDetails}, ${address.area}, ${address.city}",
            deliveryMethod = deliveryMethod,
            paymentMethod = paymentMethod,
            status = "অর্ডার গৃহীত হয়েছে (Order Placed)",
            itemsSummary = summary
        )
        database.orderDao().insertOrder(order)
        database.cartDao().clearCart()
        return orderNum
    }

    // User Profile
    val userProfile: Flow<UserProfileEntity?> = database.userDao().getUserProfile()

    suspend fun updateProfile(name: String, phone: String, email: String) {
        database.userDao().insertOrUpdateUser(
            UserProfileEntity(
                id = 1,
                name = name,
                phone = phone,
                email = email,
                isLoggedIn = true
            )
        )
    }

    suspend fun loginWithPhone(phone: String, name: String = "কাস্টমার (Customer)") {
        database.userDao().insertOrUpdateUser(
            UserProfileEntity(
                id = 1,
                name = name,
                phone = phone,
                email = "user_${phone.takeLast(4)}@shohojbazar.com",
                isLoggedIn = true
            )
        )
    }

    suspend fun logout() {
        database.userDao().insertOrUpdateUser(
            UserProfileEntity(
                id = 1,
                name = "গেস্ট কাস্টমার (Guest)",
                phone = "",
                email = "",
                isLoggedIn = false
            )
        )
    }

    // Catalog queries
    fun getProducts(category: ProductCategory = ProductCategory.ALL): List<Product> {
        return if (category == ProductCategory.ALL) {
            ProductCatalog.sampleProducts
        } else {
            ProductCatalog.sampleProducts.filter { it.category == category }
        }
    }

    fun getFlashSaleProducts(): List<Product> = ProductCatalog.sampleProducts.filter { it.isFlashSale }
    fun getPopularProducts(): List<Product> = ProductCatalog.sampleProducts.filter { it.isPopular }
    fun getNewArrivalProducts(): List<Product> = ProductCatalog.sampleProducts.filter { it.isNewArrival }

    fun search(query: String): List<Product> = ProductCatalog.searchProducts(query)
}
