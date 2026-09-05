package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.ui.theme.CoralTertiary
import com.example.ui.theme.FlashBadgeRed
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.GreenPrimaryContainer
import com.example.ui.theme.StarGold
import com.example.ui.theme.SuccessGreen

@Composable
fun TopStoreHeader(
    cartCount: Int,
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onAddressClick: () -> Unit,
    onAdminClick: (() -> Unit)? = null,
    currentAddress: String = "মিরপুর-১০, ঢাকা"
) {
    Surface(
        color = Color(0xFFFBFCFF),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            // Store Logo, Name and Action Circular Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = Color(0xFF4A6741),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "SB",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "সহজ বাজার",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                fontSize = 18.sp,
                                lineHeight = 22.sp
                            )
                        )
                        Text(
                            text = "GROCERY & MORE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Admin Dashboard Shortcut Button
                    if (onAdminClick != null) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFEBF2E8),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4A6741).copy(alpha = 0.3f)),
                            modifier = Modifier
                                .size(42.dp)
                                .clickable { onAdminClick() }
                                .testTag("top_admin_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🛡️", fontSize = 16.sp)
                            }
                        }
                    }

                    // Delivery Location Button
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .size(42.dp)
                            .clickable { onAddressClick() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "ডেলিভারি ঠিকানা",
                                tint = Color(0xFF4A6741),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Cart Icon with Sleek Badge
                    Box {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .size(42.dp)
                                .clickable { onCartClick() }
                                .testTag("top_cart_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "শপিং কার্ট",
                                    tint = Color(0xFF334155),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        if (cartCount > 0) {
                            Surface(
                                color = Color(0xFFEF4444),
                                shape = CircleShape,
                                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFBFCFF)),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = cartCount.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sleek Search Bar (bg-slate-100, rounded-2xl)
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { onSearchClick() }
                    .testTag("home_search_bar")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "সার্চ",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "চাল, ডাল, তেল খুঁজুন...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun LargeBannerSection(
    onBannerClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .width(320.dp)
                    .height(140.dp)
                    .clickable { onBannerClick("flash") }
                    .testTag("banner_flash_sale")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF4A6741),
                                    Color(0xFF6A8E5C)
                                )
                            )
                        )
                ) {
                    // Subtle background pattern decoration
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier
                            .size(110.dp)
                            .align(Alignment.BottomEnd)
                            .padding(end = 6.dp, bottom = 4.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            color = Color(0xFFFACC15), // yellow-400
                            shape = CircleShape
                        ) {
                            Text(
                                text = "FLASH SALE",
                                color = Color(0xFF4A6741),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "সেরা পণ্যে ১৫% ছাড়!",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "সীমিত সময়ের জন্য অফার",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .width(320.dp)
                    .height(140.dp)
                    .clickable { onBannerClick("mega") }
                    .testTag("banner_grocery_mega")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF2E4029),
                                    Color(0xFF4A6741)
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            color = Color(0xFFFACC15),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "FREE DELIVERY",
                                color = Color(0xFF4A6741),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "৳১০০০+ অর্ডারে ফ্রি ডেলিভারি",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "কুপন কোড: SHOHOJ50",
                            color = Color(0xFFFEF08A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySelectorRow(
    selectedCategory: ProductCategory,
    onSelectCategory: (ProductCategory) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ক্যাটাগরি",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
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
                modifier = Modifier.clickable { onSelectCategory(ProductCategory.ALL) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(ProductCategory.values()) { category ->
                val isSelected = category == selectedCategory

                val (pastelBg, emoji) = when (category) {
                    ProductCategory.ALL -> Pair(Color(0xFFF1F5F9), "🛒")
                    ProductCategory.RICE -> Pair(Color(0xFFFFF7ED), "🍚") // Orange-50
                    ProductCategory.DAL -> Pair(Color(0xFFFEFCE8), "🫘") // Yellow-50
                    ProductCategory.OIL -> Pair(Color(0xFFEFF6FF), "🧴") // Blue-50
                    ProductCategory.BISCUIT -> Pair(Color(0xFFFFF7ED), "🍪")
                    ProductCategory.MILK -> Pair(Color(0xFFF0FDF4), "🥛") // Green-50
                    ProductCategory.SOAP -> Pair(Color(0xFFFDF2F8), "🧼") // Pink-50
                    ProductCategory.SPICE -> Pair(Color(0xFFFEFCE8), "🌶️")
                    ProductCategory.TEA -> Pair(Color(0xFFFAF5FF), "☕")
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onSelectCategory(category) }
                        .testTag("category_${category.id}")
                ) {
                    Surface(
                        color = if (isSelected) Color(0xFF4A6741) else pastelBg,
                        shape = RoundedCornerShape(18.dp),
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        shadowElevation = if (isSelected) 2.dp else 0.dp,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isSelected) {
                                Icon(
                                    imageVector = category.icon,
                                    contentDescription = category.banglaName,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = emoji,
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = category.banglaName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF4A6741) else Color(0xFF475569),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FlashSaleHeader(
    secondsRemaining: Long,
    onViewAll: () -> Unit
) {
    val hours = secondsRemaining / 3600
    val minutes = (secondsRemaining % 3600) / 60
    val seconds = secondsRemaining % 60
    val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = FlashBadgeRed,
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "FLASH SALE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "⏳ $timeFormatted",
                    color = Color(0xFFFDE047),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }
        }

        Text(
            text = "সব দেখুন ➔",
            style = MaterialTheme.typography.labelMedium.copy(
                color = CoralTertiary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            ),
            modifier = Modifier.clickable { onViewAll() }
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    cartQuantity: Int = 0,
    isWishlisted: Boolean = false,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    onUpdateQuantity: (Int) -> Unit,
    onToggleWishlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = modifier
            .clickable { onProductClick() }
            .testTag("product_card_${product.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Top Image Box with Category Art
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF8FAFC))
            ) {
                // Category Icon Illustration in Center
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .align(Alignment.Center)
                        .background(Color(0xFFEBF2E8), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = product.category.icon,
                        contentDescription = null,
                        tint = Color(0xFF4A6741),
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Wishlist Toggle (Top-Right)
                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .testTag("wishlist_btn_${product.id}")
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "পছন্দের তালিকায় রাখুন",
                        tint = if (isWishlisted) Color(0xFFEF4444) else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sleek Status Pills (Emerald / Red)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (product.inStock) {
                    Surface(
                        color = Color(0xFFECFDF5), // emerald-50
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "স্টকে আছে",
                            color = Color(0xFF059669), // emerald-600
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
                if (product.discountPercent > 0) {
                    Surface(
                        color = Color(0xFFFEF2F2), // red-50
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${product.discountPercent}% ছাড়",
                            color = Color(0xFFEF4444), // red-500
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Product Bangla Name
            Text(
                text = product.banglaName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Weight & Price
            Text(
                text = "৳${product.price.toInt()} • ${product.weightOrVolume}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )

            // Add to Cart Button (w-full bg-[#4A6741] text-white text-[11px] font-bold py-2 rounded-lg)
            if (cartQuantity == 0) {
                Surface(
                    color = Color(0xFF4A6741),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .clickable { onAddToCart() }
                        .testTag("add_cart_${product.id}")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "কার্টে যোগ",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .background(Color(0xFFEBF2E8), RoundedCornerShape(10.dp))
                        .padding(horizontal = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onUpdateQuantity(cartQuantity - 1) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "কমান",
                            tint = Color(0xFF4A6741),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = cartQuantity.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A6741)
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onUpdateQuantity(cartQuantity + 1) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "বাড়ান",
                            tint = Color(0xFF4A6741),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
