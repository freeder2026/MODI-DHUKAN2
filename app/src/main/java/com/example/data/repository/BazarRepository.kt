package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.AddressEntity
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.CouponEntity
import com.example.data.local.entity.DeliveryManEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.PasswordResetRequestEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.RegisteredUserEntity
import com.example.data.local.entity.StockLogEntity
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
            status = "নতুন অর্ডার (Pending)",
            itemsSummary = summary
        )
        database.orderDao().insertOrder(order)

        // Deduct inventory & record stock log
        for (item in cartItems) {
            val prod = database.productDao().getProductById(item.productId)
            if (prod != null) {
                val newStock = (prod.stockQuantity - item.quantity).coerceAtLeast(0)
                database.productDao().updateStock(item.productId, newStock, newStock > 0)
                database.stockLogDao().insertLog(
                    StockLogEntity(
                        productId = item.productId,
                        productName = item.banglaName,
                        changeType = "STOCK_OUT",
                        quantityChanged = -item.quantity,
                        newStock = newStock,
                        timestamp = System.currentTimeMillis(),
                        note = "অর্ডার #$orderNum সম্পন্ন"
                    )
                )
            }
        }

        database.cartDao().clearCart()
        return orderNum
    }

    suspend fun updateOrderStatus(orderId: String, status: String) {
        database.orderDao().updateOrderStatus(orderId, status)
    }

    suspend fun assignDeliveryMan(orderId: String, status: String, deliveryMan: String) {
        database.orderDao().assignDeliveryMan(orderId, status, deliveryMan)
        database.deliveryManDao().incrementActiveDeliveries(deliveryMan)
    }

    suspend fun completeDelivery(orderId: String, deliveryManName: String?) {
        database.orderDao().updateOrderStatus(orderId, "ডেলিভারি সম্পন্ন (Delivered)")
        if (!deliveryManName.isNullOrBlank()) {
            database.deliveryManDao().decrementActiveDeliveries(deliveryManName)
        }
    }

    // User Profile & Active Session
    val userProfile: Flow<UserProfileEntity?> = database.userDao().getUserProfile()

    // Registered Users & Reset Requests
    val allRegisteredUsers: Flow<List<RegisteredUserEntity>> = database.userDao().getAllRegisteredUsers()
    val allPasswordResetRequests: Flow<List<PasswordResetRequestEntity>> = database.userDao().getAllPasswordResetRequests()

    suspend fun registerCustomer(
        name: String,
        phone: String,
        email: String,
        password: String,
        presentAddress: String,
        presentPostOffice: String,
        presentUpazila: String,
        presentDistrict: String,
        presentPostCode: String,
        tempAddress: String,
        tempPostOffice: String,
        tempUpazila: String,
        tempDistrict: String,
        tempPostCode: String,
        isTempSame: Boolean,
        permanentAddress: String,
        permanentPostOffice: String,
        permanentUpazila: String,
        permanentDistrict: String,
        permanentPostCode: String,
        isPermSame: Boolean
    ): RegisteredUserEntity {
        val user = RegisteredUserEntity(
            name = name,
            phone = phone,
            email = email,
            password = password,
            presentAddress = presentAddress,
            presentPostOffice = presentPostOffice,
            presentUpazila = presentUpazila,
            presentDistrict = presentDistrict,
            presentPostCode = presentPostCode,
            tempAddress = tempAddress,
            tempPostOffice = tempPostOffice,
            tempUpazila = tempUpazila,
            tempDistrict = tempDistrict,
            tempPostCode = tempPostCode,
            isTempSameAsPresent = isTempSame,
            permanentAddress = permanentAddress,
            permanentPostOffice = permanentPostOffice,
            permanentUpazila = permanentUpazila,
            permanentDistrict = permanentDistrict,
            permanentPostCode = permanentPostCode,
            isPermanentSameAsPresent = isPermSame,
            registeredAt = System.currentTimeMillis(),
            status = "ACTIVE"
        )
        val id = database.userDao().insertUser(user)

        // Automatically create a default address for quick ordering
        database.addressDao().insertAddress(
            AddressEntity(
                title = "বর্তমান ঠিকানা",
                recipientName = name,
                phone = phone,
                addressDetails = "$presentAddress, ডাকঘর: $presentPostOffice",
                area = "$presentUpazila, $presentPostCode",
                city = presentDistrict,
                isDefault = true
            )
        )

        // Set as active logged-in user
        database.userDao().insertOrUpdateUser(
            UserProfileEntity(
                id = 1,
                name = name,
                phone = phone,
                email = email,
                isLoggedIn = true
            )
        )

        return user.copy(id = id)
    }

    suspend fun loginCustomer(phoneOrEmail: String, passwordEntered: String): Result<RegisteredUserEntity> {
        val trimmed = phoneOrEmail.trim()
        val user = database.userDao().getUserByPhoneOrEmail(trimmed)
            ?: return Result.failure(Exception("এই ফোন নম্বর বা ইমেইলে কোনো অ্যাকাউন্ট পাওয়া যায়নি। অনুগ্রহ করে রেজিস্ট্রেশন করুন।"))

        if (user.status == "BLOCKED") {
            return Result.failure(Exception("আপনার অ্যাকাউন্টটি অ্যাডমিন দ্বারা সাময়িকভাবে স্থগিত করা হয়েছে। হেল্পলাইনে যোগাযোগ করুন।"))
        }

        val isValid = user.password == passwordEntered ||
                (user.tempDemoPassword != null && user.tempDemoPassword == passwordEntered)

        if (!isValid) {
            return Result.failure(Exception("ভুল পাসওয়ার্ড! আপনি ভুলে গিয়ে থাকলে 'পাসওয়ার্ড ভুলে গেছেন?' অপশন ব্যবহার করুন।"))
        }

        // Login success - update active user session
        database.userDao().insertOrUpdateUser(
            UserProfileEntity(
                id = 1,
                name = user.name,
                phone = user.phone,
                email = user.email,
                isLoggedIn = true
            )
        )

        return Result.success(user)
    }

    suspend fun requestPasswordReset(phoneOrEmail: String): Boolean {
        val trimmed = phoneOrEmail.trim()
        val user = database.userDao().getUserByPhoneOrEmail(trimmed)
        if (user != null) {
            database.userDao().insertPasswordResetRequest(
                PasswordResetRequestEntity(
                    userPhoneOrEmail = trimmed,
                    userName = user.name,
                    requestTimestamp = System.currentTimeMillis(),
                    status = "PENDING"
                )
            )
            return true
        }
        return false
    }

    suspend fun adminSendDemoPassword(userId: Long, newDemoPassword: String) {
        database.userDao().setDemoPassword(userId, newDemoPassword, newDemoPassword)
        // Also resolve pending reset requests for this user if any
        val allUsers = database.userDao().getAllRegisteredUsers().firstOrNull()
        val targetUser = allUsers?.firstOrNull { it.id == userId }
        if (targetUser != null) {
            val pending = database.userDao().getLatestResetRequestForUser(targetUser.phone)
                ?: database.userDao().getLatestResetRequestForUser(targetUser.email)
            if (pending != null) {
                database.userDao().resolvePasswordResetRequest(pending.id, "RESOLVED", newDemoPassword)
            }
        }
    }

    suspend fun adminResolveResetRequest(requestId: Long, demoPassword: String, userPhoneOrEmail: String) {
        database.userDao().resolvePasswordResetRequest(requestId, "RESOLVED", demoPassword)
        val user = database.userDao().getUserByPhoneOrEmail(userPhoneOrEmail)
        if (user != null) {
            database.userDao().setDemoPassword(user.id, demoPassword, demoPassword)
        }
    }

    suspend fun adminUpdateUserStatus(userId: Long, status: String) {
        database.userDao().updateUserStatus(userId, status)
    }

    suspend fun adminDeleteUser(userId: Long) {
        database.userDao().deleteUser(userId)
    }

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

    // Admin Products
    val adminProducts: Flow<List<ProductEntity>> = database.productDao().getAllProducts()

    suspend fun insertProduct(product: ProductEntity) {
        database.productDao().insertProduct(product)
        // Record initial stock in log
        database.stockLogDao().insertLog(
            StockLogEntity(
                productId = product.id,
                productName = product.banglaName,
                changeType = "STOCK_IN",
                quantityChanged = product.stockQuantity,
                newStock = product.stockQuantity,
                timestamp = System.currentTimeMillis(),
                note = "নতুন পণ্য অন্তর্ভুক্তি"
            )
        )
    }

    suspend fun updateProduct(product: ProductEntity) {
        database.productDao().updateProduct(product)
    }

    suspend fun updateProductPrice(id: String, price: Double, originalPrice: Double) {
        database.productDao().updatePrice(id, price, originalPrice)
    }

    suspend fun updateProductStock(id: String, newStock: Int, note: String = "ইনভেন্টরি আপডেট") {
        val prod = database.productDao().getProductById(id)
        if (prod != null) {
            val diff = newStock - prod.stockQuantity
            database.productDao().updateStock(id, newStock, newStock > 0)
            database.stockLogDao().insertLog(
                StockLogEntity(
                    productId = id,
                    productName = prod.banglaName,
                    changeType = if (diff >= 0) "STOCK_IN" else "STOCK_OUT",
                    quantityChanged = diff,
                    newStock = newStock,
                    timestamp = System.currentTimeMillis(),
                    note = note
                )
            )
        }
    }

    suspend fun updateFlashSale(id: String, isFlashSale: Boolean) {
        database.productDao().updateFlashSale(id, isFlashSale)
    }

    suspend fun deleteProduct(id: String) {
        database.productDao().deleteProduct(id)
    }

    // Admin Categories
    val adminCategories: Flow<List<CategoryEntity>> = database.categoryDao().getAllCategories()

    suspend fun insertCategory(category: CategoryEntity) {
        database.categoryDao().insertCategory(category)
    }

    suspend fun deleteCategory(id: String) {
        database.categoryDao().deleteCategory(id)
    }

    // Admin Coupons
    val adminCoupons: Flow<List<CouponEntity>> = database.couponDao().getAllCoupons()

    suspend fun insertCoupon(coupon: CouponEntity) {
        database.couponDao().insertCoupon(coupon)
    }

    suspend fun deleteCoupon(code: String) {
        database.couponDao().deleteCoupon(code)
    }

    suspend fun updateCouponStatus(code: String, isActive: Boolean) {
        database.couponDao().updateStatus(code, isActive)
    }

    suspend fun getCouponByCode(code: String): CouponEntity? {
        return database.couponDao().getCoupon(code)
    }

    // Stock Logs
    val allStockLogs: Flow<List<StockLogEntity>> = database.stockLogDao().getAllLogs()

    fun getLogsForProduct(productId: String): Flow<List<StockLogEntity>> {
        return database.stockLogDao().getLogsForProduct(productId)
    }

    // Delivery Men
    val allDeliveryMen: Flow<List<DeliveryManEntity>> = database.deliveryManDao().getAllDeliveryMen()

    suspend fun insertDeliveryMan(man: DeliveryManEntity) {
        database.deliveryManDao().insertDeliveryMan(man)
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
