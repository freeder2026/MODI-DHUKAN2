package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.ui.BazarViewModel
import com.example.ui.components.CategorySelectorRow
import com.example.ui.components.FlashSaleHeader
import com.example.ui.components.LargeBannerSection
import com.example.ui.components.ProductCard
import com.example.ui.components.TopStoreHeader
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.FlashBadgeRed
import com.example.ui.theme.GreenPrimary

@Composable
fun HomeScreen(
    viewModel: BazarViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProductDetail: (Product) -> Unit,
    onNavigateToAddresses: () -> Unit,
    onNavigateToAdmin: () -> Unit = {},
    onNavigateToAuth: () -> Unit = {}
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val addresses by viewModel.addresses.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val flashSaleTime by viewModel.flashSaleTimeLeft.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isCustomerLoggedIn = userProfile?.isLoggedIn == true && !userProfile?.phone.isNullOrBlank()

    val currentAddressTitle = addresses.firstOrNull { it.isDefault }?.let {
        "${it.area}, ${it.city.take(4)}"
    } ?: "মিরপুর-১০, ঢাকা"

    val cartQuantityMap = cartItems.associate { it.productId to it.quantity }
    val wishlistedIds = wishlistItems.map { it.productId }.toSet()

    val flashSaleProducts = viewModel.getFlashSaleProducts()
    val popularProducts = viewModel.getPopularProducts()
    val newArrivals = viewModel.getNewArrivalProducts()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sticky Top Header with Logo, Store Name, Location, Admin & Cart Badge
        TopStoreHeader(
            cartCount = cartItems.sumOf { it.quantity },
            onSearchClick = onNavigateToSearch,
            onCartClick = onNavigateToCart,
            onAddressClick = onNavigateToAddresses,
            onAdminClick = onNavigateToAdmin,
            currentAddress = currentAddressTitle
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Large Banners / Offers Carousel
            item {
                LargeBannerSection(
                    onBannerClick = { type ->
                        if (type == "flash") {
                            viewModel.setSelectedCategory(ProductCategory.ALL)
                        } else {
                            onNavigateToCart()
                        }
                    }
                )
            }

            // Guest Welcome & Discount Teaser (লোভনীয় অফার ও লগইন আমন্ত্রণ)
            if (!isCustomerLoggedIn) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🎁", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "নতুন গ্রাহক স্পেশাল অফার!",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF92400E)
                                    )
                                }
                                Text(
                                    text = "রেজিস্ট্রেশন বা লগইন করলেই পাচ্ছেন ৳৫০ ছাড় ও ফ্রি হোম ডেলিভারি!",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB45309),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onNavigateToAuth,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("লগইন / রেজিস্টার", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Popular Categories (জনপ্রিয় ক্যাটাগরি - চাল, ডাল, তেল, বিস্কুট, ইত্যাদি)
            item {
                CategorySelectorRow(
                    selectedCategory = selectedCategory,
                    onSelectCategory = { cat ->
                        viewModel.setSelectedCategory(cat)
                        onNavigateToSearch()
                    }
                )
            }

            // Flash Sale Section (Flash Sale) with live countdown timer
            item {
                FlashSaleHeader(
                    secondsRemaining = flashSaleTime,
                    onViewAll = {
                        viewModel.setSelectedCategory(ProductCategory.ALL)
                        onNavigateToSearch()
                    }
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(flashSaleProducts) { product ->
                        ProductCard(
                            product = product,
                            cartQuantity = cartQuantityMap[product.id] ?: 0,
                            isWishlisted = wishlistedIds.contains(product.id),
                            onProductClick = {
                                viewModel.selectProduct(product)
                                onNavigateToProductDetail(product)
                            },
                            onAddToCart = { viewModel.addToCart(product) },
                            onUpdateQuantity = { qty -> viewModel.updateCartQuantity(product.id, qty) },
                            onToggleWishlist = { viewModel.toggleWishlist(product) },
                            modifier = Modifier.width(165.dp)
                        )
                    }
                }
            }

            // Special Offer Discount Card (ডিসকাউন্ট ও অফার)
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFEFCE8) // soft warm pastel
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFEF08A)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .clickable { onNavigateToCart() }
                        .testTag("discount_promo_card")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFFCA8A04),
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "কুপন কোড: SHOHOJ50",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF713F12)
                                )
                            )
                            Text(
                                text = "যে কোনো অর্ডারে সাথে সাথে ৳৫০ ছাড় পান!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF854D0E),
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Surface(
                            color = Color(0xFF4A6741),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "ক্লেম",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Popular Products Section (জনপ্রিয় পণ্য)
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "জনপ্রিয় পণ্য",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = "সব দেখুন",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF4A6741),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.clickable { onNavigateToSearch() }
                    )
                }
            }

            // 2-Column Grid style for Popular Products
            items(popularProducts.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProductCard(
                        product = pair[0],
                        cartQuantity = cartQuantityMap[pair[0].id] ?: 0,
                        isWishlisted = wishlistedIds.contains(pair[0].id),
                        onProductClick = {
                            viewModel.selectProduct(pair[0])
                            onNavigateToProductDetail(pair[0])
                        },
                        onAddToCart = { viewModel.addToCart(pair[0]) },
                        onUpdateQuantity = { qty -> viewModel.updateCartQuantity(pair[0].id, qty) },
                        onToggleWishlist = { viewModel.toggleWishlist(pair[0]) },
                        modifier = Modifier.weight(1f)
                    )

                    if (pair.size > 1) {
                        ProductCard(
                            product = pair[1],
                            cartQuantity = cartQuantityMap[pair[1].id] ?: 0,
                            isWishlisted = wishlistedIds.contains(pair[1].id),
                            onProductClick = {
                                viewModel.selectProduct(pair[1])
                                onNavigateToProductDetail(pair[1])
                            },
                            onAddToCart = { viewModel.addToCart(pair[1]) },
                            onUpdateQuantity = { qty -> viewModel.updateCartQuantity(pair[1].id, qty) },
                            onToggleWishlist = { viewModel.toggleWishlist(pair[1]) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // New Products Section (নতুন পণ্য)
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "নতুন পণ্য",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = "সব দেখুন",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF4A6741),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.clickable { onNavigateToSearch() }
                    )
                }
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(newArrivals) { product ->
                        ProductCard(
                            product = product,
                            cartQuantity = cartQuantityMap[product.id] ?: 0,
                            isWishlisted = wishlistedIds.contains(product.id),
                            onProductClick = {
                                viewModel.selectProduct(product)
                                onNavigateToProductDetail(product)
                            },
                            onAddToCart = { viewModel.addToCart(product) },
                            onUpdateQuantity = { qty -> viewModel.updateCartQuantity(product.id, qty) },
                            onToggleWishlist = { viewModel.toggleWishlist(product) },
                            modifier = Modifier.width(160.dp)
                        )
                    }
                }
            }
        }
    }
}
