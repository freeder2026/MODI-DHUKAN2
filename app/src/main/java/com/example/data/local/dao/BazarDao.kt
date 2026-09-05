package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(productId: String, quantity: Int)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteCartItem(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist_items ORDER BY addedAt DESC")
    fun getAllWishlistItems(): Flow<List<WishlistItemEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE productId = :productId)")
    fun isWishlisted(productId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistItem(item: WishlistItemEntity)

    @Query("DELETE FROM wishlist_items WHERE productId = :productId")
    suspend fun deleteWishlistItem(productId: String)
}

@Dao
interface AddressDao {
    @Query("SELECT * FROM customer_addresses ORDER BY isDefault DESC, id DESC")
    fun getAllAddresses(): Flow<List<AddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity): Long

    @Update
    suspend fun updateAddress(address: AddressEntity)

    @Query("DELETE FROM customer_addresses WHERE id = :id")
    suspend fun deleteAddress(id: Long)

    @Query("UPDATE customer_addresses SET isDefault = 0")
    suspend fun clearDefaults()

    @Query("UPDATE customer_addresses SET isDefault = 1 WHERE id = :id")
    suspend fun setDefault(id: Long)

    @Transaction
    suspend fun setAsDefaultAddress(id: Long) {
        clearDefaults()
        setDefault(id)
    }
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM customer_orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE customer_orders SET status = :status WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    @Query("UPDATE customer_orders SET status = :status, deliveryMan = :deliveryMan WHERE orderId = :orderId")
    suspend fun assignDeliveryMan(orderId: String, status: String, deliveryMan: String)
}

@Dao
interface UserDao {
    // Current Active Session
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserProfileEntity)

    // Registered Users Management
    @Query("SELECT * FROM registered_users ORDER BY registeredAt DESC")
    fun getAllRegisteredUsers(): Flow<List<RegisteredUserEntity>>

    @Query("SELECT * FROM registered_users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): RegisteredUserEntity?

    @Query("SELECT * FROM registered_users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): RegisteredUserEntity?

    @Query("SELECT * FROM registered_users WHERE phone = :phoneOrEmail OR email = :phoneOrEmail LIMIT 1")
    suspend fun getUserByPhoneOrEmail(phoneOrEmail: String): RegisteredUserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: RegisteredUserEntity): Long

    @Update
    suspend fun updateUser(user: RegisteredUserEntity)

    @Query("UPDATE registered_users SET status = :status WHERE id = :userId")
    suspend fun updateUserStatus(userId: Long, status: String)

    @Query("UPDATE registered_users SET password = :newPassword, tempDemoPassword = :demoPassword WHERE id = :userId")
    suspend fun setDemoPassword(userId: Long, newPassword: String, demoPassword: String)

    @Query("DELETE FROM registered_users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)

    // Password Reset Requests
    @Query("SELECT * FROM password_reset_requests ORDER BY requestTimestamp DESC")
    fun getAllPasswordResetRequests(): Flow<List<PasswordResetRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPasswordResetRequest(request: PasswordResetRequestEntity): Long

    @Query("UPDATE password_reset_requests SET status = :status, assignedDemoPassword = :demoPassword WHERE id = :requestId")
    suspend fun resolvePasswordResetRequest(requestId: Long, status: String, demoPassword: String)

    @Query("SELECT * FROM password_reset_requests WHERE userPhoneOrEmail = :phoneOrEmail ORDER BY requestTimestamp DESC LIMIT 1")
    suspend fun getLatestResetRequestForUser(phoneOrEmail: String): PasswordResetRequestEntity?
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM admin_products ORDER BY id ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM admin_products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("UPDATE admin_products SET price = :price, originalPrice = :originalPrice WHERE id = :id")
    suspend fun updatePrice(id: String, price: Double, originalPrice: Double)

    @Query("UPDATE admin_products SET stockQuantity = :quantity, inStock = :inStock WHERE id = :id")
    suspend fun updateStock(id: String, quantity: Int, inStock: Boolean)

    @Query("UPDATE admin_products SET isFlashSale = :isFlashSale WHERE id = :id")
    suspend fun updateFlashSale(id: String, isFlashSale: Boolean)

    @Query("DELETE FROM admin_products WHERE id = :id")
    suspend fun deleteProduct(id: String)

    @Query("SELECT COUNT(*) FROM admin_products")
    suspend fun getProductCount(): Int
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM admin_categories ORDER BY id ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("DELETE FROM admin_categories WHERE id = :id")
    suspend fun deleteCategory(id: String)

    @Query("SELECT COUNT(*) FROM admin_categories")
    suspend fun getCategoryCount(): Int
}

@Dao
interface CouponDao {
    @Query("SELECT * FROM admin_coupons ORDER BY code ASC")
    fun getAllCoupons(): Flow<List<CouponEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coupons: List<CouponEntity>)

    @Query("UPDATE admin_coupons SET isActive = :isActive WHERE code = :code")
    suspend fun updateStatus(code: String, isActive: Boolean)

    @Query("DELETE FROM admin_coupons WHERE code = :code")
    suspend fun deleteCoupon(code: String)

    @Query("SELECT * FROM admin_coupons WHERE code = :code LIMIT 1")
    suspend fun getCoupon(code: String): CouponEntity?
}

@Dao
interface StockLogDao {
    @Query("SELECT * FROM stock_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<StockLogEntity>>

    @Query("SELECT * FROM stock_logs WHERE productId = :productId ORDER BY timestamp DESC")
    fun getLogsForProduct(productId: String): Flow<List<StockLogEntity>>

    @Insert
    suspend fun insertLog(log: StockLogEntity)
}

@Dao
interface DeliveryManDao {
    @Query("SELECT * FROM delivery_men ORDER BY name ASC")
    fun getAllDeliveryMen(): Flow<List<DeliveryManEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliveryMan(man: DeliveryManEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(men: List<DeliveryManEntity>)

    @Query("UPDATE delivery_men SET activeDeliveries = activeDeliveries + 1 WHERE name = :name")
    suspend fun incrementActiveDeliveries(name: String)

    @Query("UPDATE delivery_men SET activeDeliveries = MAX(0, activeDeliveries - 1) WHERE name = :name")
    suspend fun decrementActiveDeliveries(name: String)
}
