package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AddressDao
import com.example.data.local.dao.CartDao
import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.CouponDao
import com.example.data.local.dao.DeliveryManDao
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.ProductDao
import com.example.data.local.dao.StockLogDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.WishlistDao
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
import com.example.data.model.ProductCatalog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CartItemEntity::class,
        WishlistItemEntity::class,
        AddressEntity::class,
        OrderEntity::class,
        UserProfileEntity::class,
        ProductEntity::class,
        CategoryEntity::class,
        CouponEntity::class,
        StockLogEntity::class,
        DeliveryManEntity::class,
        RegisteredUserEntity::class,
        PasswordResetRequestEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun addressDao(): AddressDao
    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun couponDao(): CouponDao
    abstract fun stockLogDao(): StockLogDao
    abstract fun deliveryManDao(): DeliveryManDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shohoj_bazar_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            suspend fun populateInitialData(database: AppDatabase) {
                // Seed default active user session
                database.userDao().insertOrUpdateUser(
                    UserProfileEntity(
                        id = 1,
                        name = "মোঃ আবরার আহমেদ",
                        phone = "01755123456",
                        email = "abrar.customer@gmail.com",
                        isLoggedIn = true
                    )
                )

                // Seed sample registered users
                val initialUsers = listOf(
                    RegisteredUserEntity(
                        id = 1,
                        name = "মোঃ আবরার আহমেদ",
                        phone = "01755123456",
                        email = "abrar.customer@gmail.com",
                        password = "user1234",
                        presentAddress = "বাড়ি #২৪, রোড #৭, ব্লক #ডি",
                        presentPostOffice = "মিরপুর",
                        presentUpazila = "মিরপুর",
                        presentDistrict = "ঢাকা",
                        presentPostCode = "১২১৬",
                        tempAddress = "লেভেল ৪, বিটিআই সেন্টার, গুলশান-২",
                        tempPostOffice = "গুলশান",
                        tempUpazila = "গুলশান",
                        tempDistrict = "ঢাকা",
                        tempPostCode = "১২১২",
                        isTempSameAsPresent = false,
                        permanentAddress = "গ্রাম: চাঁদপুর, ডাকঘর: মতলব",
                        permanentPostOffice = "মতলব",
                        permanentUpazila = "মতলব উত্তর",
                        permanentDistrict = "চাঁদপুর",
                        permanentPostCode = "৩৬৪১",
                        isPermanentSameAsPresent = false,
                        registeredAt = System.currentTimeMillis() - 86400000L * 5,
                        status = "ACTIVE"
                    ),
                    RegisteredUserEntity(
                        id = 2,
                        name = "সাদিয়া সুলতানা",
                        phone = "01733445566",
                        email = "sadia.sultana@gmail.com",
                        password = "pass1234",
                        presentAddress = "বাড়ি #১২, ধানমন্ডি ২/এ",
                        presentPostOffice = "ধানমন্ডি",
                        presentUpazila = "ধানমন্ডি",
                        presentDistrict = "ঢাকা",
                        presentPostCode = "১২০৯",
                        tempAddress = "বাড়ি #১২, ধানমন্ডি ২/এ",
                        tempPostOffice = "ধানমন্ডি",
                        tempUpazila = "ধানমন্ডি",
                        tempDistrict = "ঢাকা",
                        tempPostCode = "১২০৯",
                        isTempSameAsPresent = true,
                        permanentAddress = "গ্রাম: ইসলামপুর, ডাকঘর: টাঙ্গাইল সদর",
                        permanentPostOffice = "টাঙ্গাইল সদর",
                        permanentUpazila = "টাঙ্গাইল সদর",
                        permanentDistrict = "টাঙ্গাইল",
                        permanentPostCode = "১৯০০",
                        isPermanentSameAsPresent = false,
                        registeredAt = System.currentTimeMillis() - 86400000L * 2,
                        status = "ACTIVE"
                    )
                )
                for (u in initialUsers) {
                    database.userDao().insertUser(u)
                }

                // Seed sample password reset request
                database.userDao().insertPasswordResetRequest(
                    PasswordResetRequestEntity(
                        id = 1,
                        userPhoneOrEmail = "01733445566",
                        userName = "সাদিয়া সুলতানা",
                        requestTimestamp = System.currentTimeMillis() - 3600000L,
                        status = "PENDING",
                        assignedDemoPassword = null
                    )
                )

                // Seed default customer addresses
                database.addressDao().insertAddress(
                    AddressEntity(
                        title = "বাসা (Home)",
                        recipientName = "মোঃ আবরার আহমেদ",
                        phone = "01755123456",
                        addressDetails = "বাড়ি #২৪, রোড #৭, ব্লক #ডি",
                        area = "মিরপুর-১০, ঢাকা",
                        city = "ঢাকা (Dhaka)",
                        isDefault = true
                    )
                )
                database.addressDao().insertAddress(
                    AddressEntity(
                        title = "অফিস (Office)",
                        recipientName = "মোঃ আবরার আহমেদ",
                        phone = "01755123456",
                        addressDetails = "লেভেল ৪, বিটিআই সেন্টার, গুলশান-২",
                        area = "গুলশান, ঢাকা",
                        city = "ঢাকা (Dhaka)",
                        isDefault = false
                    )
                )

                // Seed Categories
                val categories = listOf(
                    CategoryEntity("veg", "🥬 শাকসবজি", "Fresh Vegetables", "🥬", 12),
                    CategoryEntity("rice_dal", "🍚 চাল ও ডাল", "Rice & Lentils", "🍚", 16),
                    CategoryEntity("oil_spice", "🛢️ তেল ও মসলা", "Oil & Spices", "🛢️", 18),
                    CategoryEntity("dairy", "🥛 দুধ ও ডেইরি", "Milk & Dairy", "🥛", 8),
                    CategoryEntity("egg", "🥚 ডিম", "Eggs", "🥚", 4),
                    CategoryEntity("snacks", "🍪 বিস্কুট ও স্ন্যাকস", "Biscuits & Snacks", "🍪", 14),
                    CategoryEntity("care", "🧼 সাবান ও যত্ন", "Personal Care", "🧼", 10),
                    CategoryEntity("beverage", "☕ চা ও চিনি", "Tea & Sugar", "☕", 6)
                )
                database.categoryDao().insertAll(categories)

                // Seed Products from ProductCatalog
                val initialProducts = ProductCatalog.sampleProducts.map { p ->
                    val catId = when {
                        p.category.name == "RICE" || p.category.name == "DAL" -> "rice_dal"
                        p.category.name == "OIL" || p.category.name == "SPICE" -> "oil_spice"
                        p.category.name == "MILK" -> "dairy"
                        p.category.name == "BISCUIT" -> "snacks"
                        p.category.name == "SOAP" -> "care"
                        else -> "beverage"
                    }
                    val emoji = when (p.category.name) {
                        "RICE" -> "🍚"
                        "DAL" -> "🫘"
                        "OIL" -> "🧴"
                        "BISCUIT" -> "🍪"
                        "MILK" -> "🥛"
                        "SOAP" -> "🧼"
                        "SPICE" -> "🌶️"
                        else -> "☕"
                    }
                    ProductEntity(
                        id = p.id,
                        banglaName = p.banglaName,
                        englishName = p.englishName,
                        categoryId = catId,
                        categoryBangla = p.category.banglaName,
                        price = p.price,
                        originalPrice = p.originalPrice,
                        weightOrVolume = p.weightOrVolume,
                        stockQuantity = p.stockQuantity,
                        inStock = p.inStock,
                        rating = p.rating,
                        reviewCount = p.reviewCount,
                        description = p.description,
                        isFlashSale = p.isFlashSale,
                        isPopular = p.isPopular,
                        isNewArrival = p.isNewArrival,
                        brand = p.brand,
                        tags = p.tags.joinToString(","),
                        emoji = emoji
                    )
                }
                database.productDao().insertAll(initialProducts)

                // Add vegetable & egg products explicitly
                val additionalProducts = listOf(
                    ProductEntity(
                        id = "veg_1",
                        banglaName = "তাজা দেশি গোল আলু",
                        englishName = "Fresh Native Potato",
                        categoryId = "veg",
                        categoryBangla = "🥬 শাকসবজি",
                        price = 60.0,
                        originalPrice = 70.0,
                        weightOrVolume = "১ কেজি (1 Kg)",
                        stockQuantity = 8, // Low stock demo!
                        inStock = true,
                        rating = 4.8f,
                        reviewCount = 94,
                        description = "বগুড়ার সেরা ফলন, পরিষ্কার ও ফ্রেশ নতুন দেশি গোল আলু।",
                        isFlashSale = false,
                        isPopular = true,
                        isNewArrival = false,
                        brand = "দেশি বাজার",
                        tags = "আলু,সবজি,potato",
                        emoji = "🥔"
                    ),
                    ProductEntity(
                        id = "veg_2",
                        banglaName = "দেশি লাল টমেটো",
                        englishName = "Fresh Red Tomato",
                        categoryId = "veg",
                        categoryBangla = "🥬 শাকসবজি",
                        price = 90.0,
                        originalPrice = 110.0,
                        weightOrVolume = "১ কেজি (1 Kg)",
                        stockQuantity = 0, // Out of stock demo!
                        inStock = false,
                        rating = 4.7f,
                        reviewCount = 68,
                        description = "গাছপাকা ফ্রেশ লাল টমেটো। সালাদ ও রান্নার স্বাদ বাড়ায়।",
                        isFlashSale = true,
                        isPopular = true,
                        isNewArrival = true,
                        brand = "দেশি বাজার",
                        tags = "টমেটো,সবজি,tomato",
                        emoji = "🍅"
                    ),
                    ProductEntity(
                        id = "egg_1",
                        banglaName = "ফার্মের তাজা লাল ডিম",
                        englishName = "Fresh Farm Brown Eggs",
                        categoryId = "egg",
                        categoryBangla = "🥚 ডিম",
                        price = 145.0,
                        originalPrice = 160.0,
                        weightOrVolume = "১২ টি (1 Dozen)",
                        stockQuantity = 35,
                        inStock = true,
                        rating = 4.9f,
                        reviewCount = 230,
                        description = "প্রতিদিনের ফ্রেশ ও বাছাইকৃত লাল ডিম। পুষ্টিগুণে ভরপুর।",
                        isFlashSale = false,
                        isPopular = true,
                        isNewArrival = false,
                        brand = "সিপি (CP Bangladesh)",
                        tags = "ডিম,egg,লেয়ার",
                        emoji = "🥚"
                    )
                )
                database.productDao().insertAll(additionalProducts)

                // Seed Coupons
                val coupons = listOf(
                    CouponEntity(
                        code = "SHOHOJ50",
                        discountType = "FIXED",
                        discountValue = 50.0,
                        minOrderAmount = 500.0,
                        isActive = true,
                        description = "৳৫০০ বা তার বেশি অর্ডারে ৳৫০ ফ্ল্যাট ছাড়"
                    ),
                    CouponEntity(
                        code = "EID100",
                        discountType = "FIXED",
                        discountValue = 100.0,
                        minOrderAmount = 1000.0,
                        isActive = true,
                        description = "উৎসবের বিশেষ অফার! ৳১০০০ এ ৳১০০ ডিসকাউন্ট"
                    ),
                    CouponEntity(
                        code = "SUPER10",
                        discountType = "PERCENTAGE",
                        discountValue = 10.0,
                        minOrderAmount = 400.0,
                        isActive = true,
                        description = "যেকোনো অর্ডারে ১০% বিশেষ ছাড় (সর্বোচ্চ ৳১৫০)"
                    )
                )
                database.couponDao().insertAll(coupons)

                // Seed Delivery Men
                val deliveryMen = listOf(
                    DeliveryManEntity("DEL-1", "মোঃ করিম", "01811223344", "মিরপুর ও পল্লবী জোন", 2),
                    DeliveryManEntity("DEL-2", "রফিক হোসেন", "01722334455", "গুলশান, বনানী ও বাড্ডা জোন", 1),
                    DeliveryManEntity("DEL-3", "তানভীর আহমেদ", "01933445566", "ধানমন্ডি, কলাবাগান ও লালমাটিয়া", 0)
                )
                database.deliveryManDao().insertAll(deliveryMen)

                // Seed Initial Orders (Today, Yesterday, Last week)
                val currentTime = System.currentTimeMillis()
                val initialOrders = listOf(
                    OrderEntity(
                        orderId = "SB-84912",
                        timestamp = currentTime - 1800000L, // 30 mins ago (TODAY)
                        totalItemsCount = 4,
                        subtotal = 1420.0,
                        deliveryFee = 0.0,
                        discount = 50.0,
                        finalTotal = 1370.0,
                        recipientName = "নাসির উদ্দিন",
                        recipientPhone = "01822334455",
                        deliveryAddress = "ফ্ল্যাট ৩বি, বাড়ি ১৮, রোড ৪, সেক্টর ৯, উত্তরা, ঢাকা",
                        deliveryMethod = "রেগুলার ডেলিভারি",
                        paymentMethod = "ক্যাশ অন ডেলিভারি (Cash on Delivery)",
                        status = "নতুন অর্ডার (Pending)",
                        itemsSummary = "নাজিরশাইল চাল (৫ কেজি), মসুর ডাল (১ কেজি), সয়াবিন তেল (২ লিটার)"
                    ),
                    OrderEntity(
                        orderId = "SB-83540",
                        timestamp = currentTime - 7200000L, // 2 hours ago (TODAY)
                        totalItemsCount = 2,
                        subtotal = 650.0,
                        deliveryFee = 50.0,
                        discount = 0.0,
                        finalTotal = 700.0,
                        recipientName = "সাদিয়া সুলতানা",
                        recipientPhone = "01733445566",
                        deliveryAddress = "বাড়ি #১২, ধানমন্ডি ২/এ, ঢাকা",
                        deliveryMethod = "এক্সপ্রেস ডেলিভারি",
                        paymentMethod = "বিকাশ (bKash Payment)",
                        status = "প্রসেসিং (Processing)",
                        itemsSummary = "আড়ং খাঁটি গাওয়া ঘি (৪০০ গ্রাম), মিল্ক ভিটা তরল দুধ (১ লিটার)"
                    ),
                    OrderEntity(
                        orderId = "SB-82109",
                        timestamp = currentTime - 86400000L, // 1 day ago
                        totalItemsCount = 3,
                        subtotal = 920.0,
                        deliveryFee = 50.0,
                        discount = 50.0,
                        finalTotal = 920.0,
                        recipientName = "ফারুক হোসেন",
                        recipientPhone = "01944556677",
                        deliveryAddress = "রোড #১০, বনানী, ঢাকা",
                        deliveryMethod = "রেগুলার ডেলিভারি",
                        paymentMethod = "ক্যাশ অন ডেলিভারি",
                        status = "ডেলিভারি ম্যানের কাছে ন্যস্ত (Out for Delivery)",
                        itemsSummary = "রূপচাঁদা সয়াবিন তেল (৫ লিটার), ইস্পাহানি চা (৪০০ গ্রাম)",
                        deliveryMan = "রফিক হোসেন"
                    ),
                    OrderEntity(
                        orderId = "SB-74291",
                        timestamp = currentTime - 86400000L * 2, // 2 days ago
                        totalItemsCount = 3,
                        subtotal = 970.0,
                        deliveryFee = 50.0,
                        discount = 50.0,
                        finalTotal = 970.0,
                        recipientName = "মোঃ আবরার আহমেদ",
                        recipientPhone = "01755123456",
                        deliveryAddress = "বাড়ি #২৪, রোড #৭, ব্লক #ডি, মিরপুর-১০, ঢাকা",
                        deliveryMethod = "রেগুলার ডেলিভারি",
                        paymentMethod = "ক্যাশ অন ডেলিভারি",
                        status = "ডেলিভারি সম্পন্ন (Delivered)",
                        itemsSummary = "মিনিকেট চাল (৫ কেজি), মসুর ডাল (১ কেজি), রাধুনী সরিষার তেল (১ লিটার)",
                        deliveryMan = "মোঃ করিম"
                    )
                )
                for (order in initialOrders) {
                    database.orderDao().insertOrder(order)
                }

                // Seed initial stock logs
                val initialLogs = listOf(
                    StockLogEntity(
                        productId = "rice_1",
                        productName = "প্রিমিয়াম নাজিরশাইল চাল",
                        changeType = "STOCK_IN",
                        quantityChanged = 50,
                        newStock = 50,
                        timestamp = currentTime - 86400000L * 3,
                        note = "নয়া চালান গ্রহণ - তীর অফিসিয়াল"
                    ),
                    StockLogEntity(
                        productId = "rice_1",
                        productName = "প্রিমিয়াম নাজিরশাইল চাল",
                        changeType = "STOCK_OUT",
                        quantityChanged = -5,
                        newStock = 45,
                        timestamp = currentTime - 86400000L,
                        note = "কাস্টমার অর্ডার #SB-84912 ও কাউন্টার সেল"
                    ),
                    StockLogEntity(
                        productId = "veg_1",
                        productName = "তাজা দেশি গোল আলু",
                        changeType = "ADJUSTMENT",
                        quantityChanged = -12,
                        newStock = 8,
                        timestamp = currentTime - 3600000L,
                        note = "দোকানের ইনভেন্টরি যাচাই ও সমন্বয়"
                    ),
                    StockLogEntity(
                        productId = "veg_2",
                        productName = "দেশি লাল টমেটো",
                        changeType = "STOCK_OUT",
                        quantityChanged = -25,
                        newStock = 0,
                        timestamp = currentTime - 7200000L,
                        note = "ফ্ল্যাশ সেলে দ্রুত স্টক শেষ"
                    )
                )
                for (log in initialLogs) {
                    database.stockLogDao().insertLog(log)
                }
            }
        }
    }
}
