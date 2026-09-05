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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Star
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
    onNavigateToAddresses: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val addresses by viewModel.addresses.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val flashSaleTime by viewModel.flashSaleTimeLeft.collectAsState()

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
        // Sticky Top Header with Logo, Store Name, Location & Cart Badge
        TopStoreHeader(
            cartCount = cartItems.sumOf { it.quantity },
            onSearchClick = onNavigateToSearch,
            onCartClick = onNavigateToCart,
            onAddressClick = onNavigateToAddresses,
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
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onNavigateToCart() }
                        .testTag("discount_promo_card")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = AmberSecondary,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "কুপন কোড: SHOHOJ50",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                            Text(
                                text = "যে কোনো অর্ডারে সাথে সাথে ৳৫০ ছাড় পান!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                            )
                        }
                        Surface(
                            color = GreenPrimary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "ব্যবহার করুন",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AmberSecondary,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = "জনপ্রিয় পণ্য (Popular Products)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                    Text(
                        text = "সব দেখুন",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = GreenPrimary,
                            fontWeight = FontWeight.Bold
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
                        .padding(horizontal = 16.dp, vertical = 6.dp),
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
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NewReleases,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = "নতুন পণ্য (New Arrivals)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                    Text(
                        text = "সব দেখুন",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = GreenPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable { onNavigateToSearch() }
                    )
                }
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
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
                            modifier = Modifier.width(165.dp)
                        )
                    }
                }
            }
        }
    }
}
