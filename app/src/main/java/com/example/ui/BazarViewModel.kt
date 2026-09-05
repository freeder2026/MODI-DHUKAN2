package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.data.repository.BazarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class DeliveryMethod(val id: String, val titleBangla: String, val titleEnglish: String, val fee: Double, val estimatedTime: String) {
    REGULAR("regular", "রেগুলার ডেলিভারি", "Regular Delivery", 50.0, "২৪ ঘণ্টার মধ্যে (Within 24 hours)"),
    EXPRESS("express", "এক্সপ্রেস ডেলিভারি", "Express Delivery", 80.0, "২ ঘণ্টার মধ্যে (Within 2 hours)")
}

enum class PaymentMethod(val id: String, val titleBangla: String, val subtitleBangla: String, val isAvailable: Boolean) {
    CASH_ON_DELIVERY("cod", "ক্যাশ অন ডেলিভারি (Cash on Delivery)", "পণ্য হাতে পেয়ে নগদ টাকা পরিশোধ করুন", true),
    BKASH("bkash", "বিকাশ (bKash Payment)", "শীঘ্রই আসছে / ডেমো ট্রানজেকশন", true),
    NAGAD("nagad", "নগদ (Nagad Payment)", "শীঘ্রই আসছে / ডেমো ট্রানজেকশন", true),
    CARD("card", "ক্রেডিট / ডেবিট কার্ড", "ভিসা, মাস্টারকার্ড বা অন্যান্য", true)
}

class BazarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BazarRepository
    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = BazarRepository(database)
    }

    // Database reactive streams
    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistItems: StateFlow<List<WishlistItemEntity>> = repository.wishlistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val addresses: StateFlow<List<AddressEntity>> = repository.addresses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.orders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isCustomerLoggedIn: StateFlow<Boolean> = repository.userProfile
        .combine(MutableStateFlow(Unit)) { profile, _ ->
            profile?.isLoggedIn == true && profile.phone.isNotBlank()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Admin database reactive streams
    val allRegisteredUsers: StateFlow<List<RegisteredUserEntity>> = repository.allRegisteredUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPasswordResetRequests: StateFlow<List<PasswordResetRequestEntity>> = repository.allPasswordResetRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val adminProducts: StateFlow<List<ProductEntity>> = repository.adminProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminCategories: StateFlow<List<CategoryEntity>> = repository.adminCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminCoupons: StateFlow<List<CouponEntity>> = repository.adminCoupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminStockLogs: StateFlow<List<StockLogEntity>> = repository.allStockLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminDeliveryMen: StateFlow<List<DeliveryManEntity>> = repository.allDeliveryMen
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Dashboard Computed Statistics
    val todaySales: StateFlow<Double> = orders.combine(adminProducts) { orderList, _ ->
        val oneDayAgo = System.currentTimeMillis() - 86400000L
        orderList.filter { it.timestamp >= oneDayAgo && !it.status.contains("বাতিল") }.sumOf { it.finalTotal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalSales: StateFlow<Double> = orders.combine(adminProducts) { orderList, _ ->
        orderList.filter { !it.status.contains("বাতিল") }.sumOf { it.finalTotal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayOrdersCount: StateFlow<Int> = orders.combine(adminProducts) { orderList, _ ->
        val oneDayAgo = System.currentTimeMillis() - 86400000L
        orderList.count { it.timestamp >= oneDayAgo }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingOrdersCount: StateFlow<Int> = orders.combine(adminProducts) { orderList, _ ->
        orderList.count { it.status.contains("Pending") || it.status.contains("নতুন") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val deliveredOrdersCount: StateFlow<Int> = orders.combine(adminProducts) { orderList, _ ->
        orderList.count { it.status.contains("Delivered") || it.status.contains("সম্পন্ন") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val lowStockProductsCount: StateFlow<Int> = adminProducts.combine(orders) { prodList, _ ->
        prodList.count { it.stockQuantity in 1..10 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val outOfStockProductsCount: StateFlow<Int> = adminProducts.combine(orders) { prodList, _ ->
        prodList.count { it.stockQuantity <= 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // UI state filters & search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ProductCategory.ALL)
    val selectedCategory: StateFlow<ProductCategory> = _selectedCategory.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // Checkout states
    private val _selectedDeliveryMethod = MutableStateFlow(DeliveryMethod.REGULAR)
    val selectedDeliveryMethod: StateFlow<DeliveryMethod> = _selectedDeliveryMethod.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.CASH_ON_DELIVERY)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

    private val _selectedAddressId = MutableStateFlow<Long?>(null)
    val selectedAddressId: StateFlow<Long?> = _selectedAddressId.asStateFlow()

    private val _couponCode = MutableStateFlow("")
    val couponCode: StateFlow<String> = _couponCode.asStateFlow()

    private val _appliedDiscount = MutableStateFlow(0.0)
    val appliedDiscount: StateFlow<Double> = _appliedDiscount.asStateFlow()

    private val _couponMessage = MutableStateFlow<String?>(null)
    val couponMessage: StateFlow<String?> = _couponMessage.asStateFlow()

    private val _lastPlacedOrderId = MutableStateFlow<String?>(null)
    val lastPlacedOrderId: StateFlow<String?> = _lastPlacedOrderId.asStateFlow()

    // Computed cart values
    val cartSubtotal: StateFlow<Double> = cartItems.combine(_couponCode) { items, _ ->
        items.sumOf { it.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartItemCount: StateFlow<Int> = cartItems.combine(_couponCode) { items, _ ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Flash sale countdown timer simulation (in seconds)
    private val _flashSaleTimeLeft = MutableStateFlow(13540L) // ~3h 45m
    val flashSaleTimeLeft: StateFlow<Long> = _flashSaleTimeLeft.asStateFlow()

    // Filtered products based on search and category
    val searchResults: StateFlow<List<Product>> = combine(_searchQuery, _selectedCategory) { query, category ->
        var list = ProductCatalog.sampleProducts
        if (category != ProductCategory.ALL) {
            list = list.filter { it.category == category }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter { p ->
                p.banglaName.lowercase().contains(q) ||
                        p.englishName.lowercase().contains(q) ||
                        p.category.banglaName.lowercase().contains(q) ||
                        p.category.englishName.lowercase().contains(q) ||
                        p.tags.any { it.lowercase().contains(q) } ||
                        p.brand.lowercase().contains(q)
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProductCatalog.sampleProducts)

    fun getFlashSaleProducts(): List<Product> = ProductCatalog.sampleProducts.filter { it.isFlashSale }
    fun getPopularProducts(): List<Product> = ProductCatalog.sampleProducts.filter { it.isPopular }
    fun getNewArrivalProducts(): List<Product> = ProductCatalog.sampleProducts.filter { it.isNewArrival }

    // User actions
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: ProductCategory) {
        _selectedCategory.value = category
    }

    fun selectProduct(product: Product?) {
        _selectedProduct.value = product
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product, quantity)
        }
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, quantity)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun isWishlisted(productId: String): Boolean {
        return wishlistItems.value.any { it.productId == productId }
    }

    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            repository.toggleWishlist(product)
        }
    }

    fun removeFromWishlist(productId: String) {
        viewModelScope.launch {
            repository.removeFromWishlist(productId)
        }
    }

    fun setDeliveryMethod(method: DeliveryMethod) {
        _selectedDeliveryMethod.value = method
    }

    fun setPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    fun setSelectedAddressId(id: Long) {
        _selectedAddressId.value = id
    }

    fun applyCoupon(code: String) {
        val trimmed = code.trim().uppercase()
        _couponCode.value = trimmed
        if (trimmed == "SHOHOJ50" || trimmed == "BAZAR50") {
            _appliedDiscount.value = 50.0
            _couponMessage.value = "অভিনন্দন! ৳৫০ ডিসকাউন্ট যুক্ত হয়েছে।"
        } else if (trimmed == "DESHI10" || trimmed == "EID10") {
            val subtotal = cartSubtotal.value
            _appliedDiscount.value = (subtotal * 0.10).coerceAtMost(200.0)
            _couponMessage.value = "১০% ছাড় কার্যকর হয়েছে!"
        } else {
            _appliedDiscount.value = 0.0
            _couponMessage.value = "অকার্যকর কুপন কোড! চেষ্টা করুন: SHOHOJ50"
        }
    }

    fun removeCoupon() {
        _couponCode.value = ""
        _appliedDiscount.value = 0.0
        _couponMessage.value = null
    }

    fun addAddress(title: String, name: String, phone: String, details: String, area: String, city: String) {
        viewModelScope.launch {
            repository.addAddress(
                AddressEntity(
                    title = title,
                    recipientName = name,
                    phone = phone,
                    addressDetails = details,
                    area = area,
                    city = city,
                    isDefault = addresses.value.isEmpty()
                )
            )
        }
    }

    fun deleteAddress(id: Long) {
        viewModelScope.launch {
            repository.deleteAddress(id)
        }
    }

    fun setDefaultAddress(id: Long) {
        viewModelScope.launch {
            repository.setDefaultAddress(id)
            _selectedAddressId.value = id
        }
    }

    fun placeOrder(onOrderPlaced: (String) -> Unit) {
        val items = cartItems.value
        if (items.isEmpty()) return

        val addressList = addresses.value
        val chosenAddress = addressList.firstOrNull { it.id == _selectedAddressId.value }
            ?: addressList.firstOrNull { it.isDefault }
            ?: addressList.firstOrNull()
            ?: AddressEntity(
                title = "সাধারণ ঠিকানা",
                recipientName = userProfile.value?.name ?: "সম্মানিত গ্রাহক",
                phone = userProfile.value?.phone ?: "01700000000",
                addressDetails = "ঠিকানা উল্লেখ করা হয়নি",
                area = "ঢাকা",
                city = "ঢাকা"
            )

        val subtotal = items.sumOf { it.price * it.quantity }
        val deliveryFee = if (subtotal >= 1000.0 && _selectedDeliveryMethod.value == DeliveryMethod.REGULAR) {
            0.0 // Free delivery on 1000+ BDT
        } else {
            _selectedDeliveryMethod.value.fee
        }
        val discount = _appliedDiscount.value
        val finalTotal = (subtotal + deliveryFee - discount).coerceAtLeast(0.0)

        viewModelScope.launch {
            val orderId = repository.placeOrder(
                cartItems = items,
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                discount = discount,
                finalTotal = finalTotal,
                address = chosenAddress,
                deliveryMethod = _selectedDeliveryMethod.value.titleBangla,
                paymentMethod = _selectedPaymentMethod.value.titleBangla
            )
            _lastPlacedOrderId.value = orderId
            removeCoupon()
            onOrderPlaced(orderId)
        }
    }

    fun registerCustomer(
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
        isPermSame: Boolean,
        onSuccess: (RegisteredUserEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        if (name.isBlank() || phone.isBlank() || password.isBlank()) {
            onError("দয়া করে নাম, ফোন নম্বর ও পাসওয়ার্ড পূরণ করুন")
            return
        }
        if (phone.length < 11) {
            onError("সঠিক ১১ ডিজিটের ফোন নম্বর দিন")
            return
        }
        viewModelScope.launch {
            try {
                val user = repository.registerCustomer(
                    name = name.trim(),
                    phone = phone.trim(),
                    email = email.trim(),
                    password = password.trim(),
                    presentAddress = presentAddress.trim(),
                    presentPostOffice = presentPostOffice.trim(),
                    presentUpazila = presentUpazila.trim(),
                    presentDistrict = presentDistrict.trim(),
                    presentPostCode = presentPostCode.trim(),
                    tempAddress = if (isTempSame) presentAddress.trim() else tempAddress.trim(),
                    tempPostOffice = if (isTempSame) presentPostOffice.trim() else tempPostOffice.trim(),
                    tempUpazila = if (isTempSame) presentUpazila.trim() else tempUpazila.trim(),
                    tempDistrict = if (isTempSame) presentDistrict.trim() else tempDistrict.trim(),
                    tempPostCode = if (isTempSame) presentPostCode.trim() else tempPostCode.trim(),
                    isTempSame = isTempSame,
                    permanentAddress = if (isPermSame) presentAddress.trim() else permanentAddress.trim(),
                    permanentPostOffice = if (isPermSame) presentPostOffice.trim() else permanentPostOffice.trim(),
                    permanentUpazila = if (isPermSame) presentUpazila.trim() else permanentUpazila.trim(),
                    permanentDistrict = if (isPermSame) presentDistrict.trim() else permanentDistrict.trim(),
                    permanentPostCode = if (isPermSame) presentPostCode.trim() else permanentPostCode.trim(),
                    isPermSame = isPermSame
                )
                onSuccess(user)
            } catch (e: Exception) {
                onError("রেজিস্ট্রেশনে ত্রুটি: ${e.localizedMessage}")
            }
        }
    }

    fun loginCustomer(
        phoneOrEmail: String,
        passwordEntered: String,
        onSuccess: (RegisteredUserEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        if (phoneOrEmail.isBlank() || passwordEntered.isBlank()) {
            onError("ফোন/ইমেইল এবং পাসওয়ার্ড প্রবেশ করান")
            return
        }
        viewModelScope.launch {
            val result = repository.loginCustomer(phoneOrEmail, passwordEntered)
            result.onSuccess { onSuccess(it) }
            result.onFailure { onError(it.message ?: "লগইন ব্যর্থ হয়েছে") }
        }
    }

    fun requestPasswordReset(
        phoneOrEmail: String,
        onResult: (Boolean, String) -> Unit
    ) {
        if (phoneOrEmail.isBlank()) {
            onResult(false, "ফোন নম্বর বা ইমেইল দিন")
            return
        }
        viewModelScope.launch {
            val success = repository.requestPasswordReset(phoneOrEmail)
            if (success) {
                onResult(true, "পাসওয়ার্ড রিসেট রিকোয়েস্ট সফলভাবে পাঠানো হয়েছে! অ্যাডমিন কর্তৃক নতুন ডেমো পাসওয়ার্ড অনুমোদন করা হবে।")
            } else {
                onResult(false, "এই ফোন/ইমেইল দিয়ে কোনো ইউজার পাওয়া যায়নি।")
            }
        }
    }

    fun adminSendDemoPassword(userId: Long, demoPassword: String) {
        viewModelScope.launch {
            repository.adminSendDemoPassword(userId, demoPassword)
        }
    }

    fun adminResolveResetRequest(requestId: Long, demoPassword: String, userPhoneOrEmail: String) {
        viewModelScope.launch {
            repository.adminResolveResetRequest(requestId, demoPassword, userPhoneOrEmail)
        }
    }

    fun adminUpdateUserStatus(userId: Long, status: String) {
        viewModelScope.launch {
            repository.adminUpdateUserStatus(userId, status)
        }
    }

    fun adminDeleteUser(userId: Long) {
        viewModelScope.launch {
            repository.adminDeleteUser(userId)
        }
    }

    fun login(phone: String, name: String) {
        viewModelScope.launch {
            repository.loginWithPhone(phone, name)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun logoutCustomer() {
        logout()
    }

    fun updateProfile(name: String, phone: String, email: String) {
        viewModelScope.launch {
            repository.updateProfile(name, phone, email)
        }
    }

    // Product Management
    fun addAdminProduct(
        nameBangla: String,
        nameEnglish: String,
        categoryId: String,
        categoryBangla: String,
        price: Double,
        originalPrice: Double,
        stockQuantity: Int,
        weightOrVolume: String,
        description: String,
        brand: String,
        tags: String,
        emoji: String
    ) {
        val newId = "prod_" + System.currentTimeMillis()
        val prod = ProductEntity(
            id = newId,
            banglaName = nameBangla,
            englishName = nameEnglish.ifBlank { nameBangla },
            categoryId = categoryId,
            categoryBangla = categoryBangla,
            price = price,
            originalPrice = if (originalPrice > 0) originalPrice else price,
            weightOrVolume = weightOrVolume,
            stockQuantity = stockQuantity,
            inStock = stockQuantity > 0,
            rating = 5.0f,
            reviewCount = 1,
            description = description,
            isFlashSale = false,
            isPopular = false,
            isNewArrival = true,
            brand = brand.ifBlank { "Shohoj Bazar" },
            tags = tags,
            emoji = emoji.ifBlank { "🛒" }
        )
        viewModelScope.launch {
            repository.insertProduct(prod)
        }
    }

    fun updateAdminProductPrice(id: String, price: Double, originalPrice: Double) {
        viewModelScope.launch {
            repository.updateProductPrice(id, price, originalPrice)
        }
    }

    fun updateAdminProductStock(id: String, newStock: Int, note: String = "ইনভেন্টরি আপডেট") {
        viewModelScope.launch {
            repository.updateProductStock(id, newStock, note)
        }
    }

    fun toggleAdminProductFlashSale(id: String, isFlashSale: Boolean) {
        viewModelScope.launch {
            repository.updateFlashSale(id, isFlashSale)
        }
    }

    fun deleteAdminProduct(id: String) {
        viewModelScope.launch {
            repository.deleteProduct(id)
        }
    }

    // Category Management
    fun addAdminCategory(nameBangla: String, nameEnglish: String, emoji: String) {
        val catId = "cat_" + System.currentTimeMillis()
        val cat = CategoryEntity(
            id = catId,
            banglaName = "$emoji $nameBangla",
            englishName = nameEnglish,
            emoji = emoji,
            itemCount = 0
        )
        viewModelScope.launch {
            repository.insertCategory(cat)
        }
    }

    fun deleteAdminCategory(id: String) {
        viewModelScope.launch {
            repository.deleteCategory(id)
        }
    }

    // Coupon Management
    fun addAdminCoupon(
        code: String,
        discountType: String,
        discountValue: Double,
        minOrderAmount: Double,
        description: String,
        expiryDate: String = "৩১ ডিসেম্বর ২০২৬"
    ) {
        val coupon = CouponEntity(
            code = code.trim().uppercase(),
            discountType = discountType,
            discountValue = discountValue,
            minOrderAmount = minOrderAmount,
            isActive = true,
            expiryDate = expiryDate,
            description = description
        )
        viewModelScope.launch {
            repository.insertCoupon(coupon)
        }
    }

    fun toggleAdminCouponStatus(code: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.updateCouponStatus(code, isActive)
        }
    }

    fun deleteAdminCoupon(code: String) {
        viewModelScope.launch {
            repository.deleteCoupon(code)
        }
    }

    // Order Management Actions
    fun confirmOrder(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "কনফার্মড (Confirmed)")
        }
    }

    fun processOrder(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "প্রসেসিং (Processing)")
        }
    }

    fun assignDeliveryManToOrder(orderId: String, deliveryManName: String) {
        viewModelScope.launch {
            repository.assignDeliveryMan(orderId, "ডেলিভারিম্যানের কাছে ন্যস্ত (Out for Delivery)", deliveryManName)
        }
    }

    fun markOrderDelivered(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "ডেলিভারি সম্পন্ন (Delivered)")
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "অর্ডার বাতিল (Cancelled)")
        }
    }

    // --- Admin Authentication State ---
    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    fun loginAdmin(passwordOrPin: String): Boolean {
        // Default admin password/pin: "1234" or "admin123"
        return if (passwordOrPin.trim() == "1234" || passwordOrPin.trim() == "admin123" || passwordOrPin.trim().equals("admin", ignoreCase = true)) {
            _isAdminLoggedIn.value = true
            true
        } else {
            false
        }
    }

    fun setAdminLoggedInDirect(isLoggedIn: Boolean) {
        _isAdminLoggedIn.value = isLoggedIn
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
    }

    // --- Delivery Rider System (PART 3) ---
    private val _activeDeliveryMan = MutableStateFlow<DeliveryManEntity?>(null)
    val activeDeliveryMan: StateFlow<DeliveryManEntity?> = _activeDeliveryMan.asStateFlow()

    private val _isRiderOnline = MutableStateFlow(true)
    val isRiderOnline: StateFlow<Boolean> = _isRiderOnline.asStateFlow()

    fun loginDeliveryMan(man: DeliveryManEntity) {
        _activeDeliveryMan.value = man
    }

    fun logoutDeliveryMan() {
        _activeDeliveryMan.value = null
    }

    fun toggleRiderOnlineStatus() {
        _isRiderOnline.value = !_isRiderOnline.value
    }

    fun registerNewDeliveryMan(name: String, phone: String, area: String) {
        val newMan = DeliveryManEntity(
            id = "DEL-${System.currentTimeMillis() % 10000}",
            name = name,
            phone = phone,
            area = area,
            activeDeliveries = 0
        )
        viewModelScope.launch {
            repository.insertDeliveryMan(newMan)
            _activeDeliveryMan.value = newMan
        }
    }

    // Step 9: 1️⃣ Picked Up - দোকান থেকে পণ্য নিয়েছে
    fun markOrderPickedUp(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "দোকান থেকে পিকআপ (Picked Up)")
        }
    }

    // Step 10: 2️⃣ On The Way - কাস্টমারের কাছে যাচ্ছে
    fun markOrderOnTheWay(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "কাস্টমারের ঠিকানায় রওনা (On The Way)")
        }
    }

    // Step 11: 3️⃣ Delivered - পণ্য সফলভাবে পৌঁছে দিয়েছে
    fun markOrderDeliveredByRider(orderId: String, deliveryManName: String?) {
        viewModelScope.launch {
            repository.completeDelivery(orderId, deliveryManName)
        }
    }

    // Rider Self-Assign Open Order
    fun riderAcceptOrder(orderId: String, deliveryManName: String) {
        viewModelScope.launch {
            repository.assignDeliveryMan(orderId, "দোকান থেকে পিকআপ (Picked Up)", deliveryManName)
        }
    }
}
