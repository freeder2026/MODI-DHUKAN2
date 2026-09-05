package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AddressDao
import com.example.data.local.dao.CartDao
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.WishlistDao
import com.example.data.local.entity.AddressEntity
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.local.entity.WishlistItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CartItemEntity::class,
        WishlistItemEntity::class,
        AddressEntity::class,
        OrderEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun addressDao(): AddressDao
    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao

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
                // Seed default user
                database.userDao().insertOrUpdateUser(
                    UserProfileEntity(
                        id = 1,
                        name = "মোঃ আবরার আহমেদ",
                        phone = "01755123456",
                        email = "abrar.customer@gmail.com",
                        isLoggedIn = true
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

                // Seed initial order history
                database.orderDao().insertOrder(
                    OrderEntity(
                        orderId = "SB-74291",
                        timestamp = System.currentTimeMillis() - 86400000L * 2, // 2 days ago
                        totalItemsCount = 3,
                        subtotal = 970.0,
                        deliveryFee = 50.0,
                        discount = 50.0,
                        finalTotal = 970.0,
                        recipientName = "মোঃ আবরার আহমেদ",
                        recipientPhone = "01755123456",
                        deliveryAddress = "বাড়ি #২৪, রোড #৭, ব্লক #ডি, মিরপুর-১০, ঢাকা",
                        deliveryMethod = "রেগুলার ডেলিভারি (Regular Delivery)",
                        paymentMethod = "ক্যাশ অন ডেলিভারি (Cash on Delivery)",
                        status = "ডেলিভারি সম্পন্ন (Delivered)",
                        itemsSummary = "মিনিকেট চাল (৫ কেজি), মসুর ডাল (১ কেজি), রাধুনী সরিষার তেল (১ লিটার)"
                    )
                )
            }
        }
    }
}
