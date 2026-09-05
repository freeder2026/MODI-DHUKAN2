package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AddressEntity
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.OrderEntity
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

    fun updateProfile(name: String, phone: String, email: String) {
        viewModelScope.launch {
            repository.updateProfile(name, phone, email)
        }
    }
}
