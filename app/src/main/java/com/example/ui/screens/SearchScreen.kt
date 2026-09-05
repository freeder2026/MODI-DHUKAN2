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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.components.ProductCard
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.GreenPrimaryContainer

enum class SortOption(val title: String) {
    POPULAR("জনপ্রিয়তা"),
    PRICE_LOW_HIGH("দাম: কম থেকে বেশি"),
    PRICE_HIGH_LOW("দাম: বেশি থেকে কম"),
    DISCOUNT("সর্বোচ্চ ছাড়")
}

@Composable
fun SearchScreen(
    viewModel: BazarViewModel,
    onNavigateToProductDetail: (Product) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlistItems by viewModel.wishlistItems.collectAsState()

    val cartQuantityMap = cartItems.associate { it.productId to it.quantity }
    val wishlistedIds = wishlistItems.map { it.productId }.toSet()

    var activeSort by remember { mutableStateOf(SortOption.POPULAR) }

    // Quick search tags requested by the user
    val quickSearchTags = listOf("চাল", "ডাল", "তেল", "বিস্কুট", "দুধ", "সাবান", "মসলা", "চিনি")

    // Sorted products
    val sortedProducts = remember(searchResults, activeSort) {
        when (activeSort) {
            SortOption.POPULAR -> searchResults.sortedByDescending { it.rating }
            SortOption.PRICE_LOW_HIGH -> searchResults.sortedBy { it.price }
            SortOption.PRICE_HIGH_LOW -> searchResults.sortedByDescending { it.price }
            SortOption.DISCOUNT -> searchResults.sortedByDescending { it.discountPercent }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Input Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = "চাল, ডাল, তেল, বিস্কুট, দুধ, সাবান...",
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "খুঁজুন",
                            tint = GreenPrimary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "মুছুন",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_text_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Search Keyword Chips (চাল, ডাল, তেল, বিস্কুট, দুধ, সাবান)
                Text(
                    text = "দ্রুত খুঁজুন:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickSearchTags) { tag ->
                        val isSelected = searchQuery.equals(tag, ignoreCase = true)
                        Surface(
                            color = if (isSelected) GreenPrimary else GreenPrimaryContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clickable {
                                    if (isSelected) viewModel.setSearchQuery("")
                                    else viewModel.setSearchQuery(tag)
                                }
                                .testTag("tag_chip_$tag")
                        ) {
                            Text(
                                text = tag,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else GreenPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ProductCategory.values()) { category ->
                        val isSelected = category == selectedCategory
                        Surface(
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.clickable {
                                viewModel.setSelectedCategory(category)
                            }
                        ) {
                            Text(
                                text = category.banglaName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Result Count and Sort Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${sortedProducts.size} টি পণ্য পাওয়া গেছে",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Sort cycle button
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.clickable {
                    activeSort = when (activeSort) {
                        SortOption.POPULAR -> SortOption.PRICE_LOW_HIGH
                        SortOption.PRICE_LOW_HIGH -> SortOption.PRICE_HIGH_LOW
                        SortOption.PRICE_HIGH_LOW -> SortOption.DISCOUNT
                        SortOption.DISCOUNT -> SortOption.POPULAR
                    }
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = activeSort.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Search Results List / Empty State
        if (sortedProducts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    color = GreenPrimaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "কোনো পণ্য পাওয়া যায়নি!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "দয়া করে সঠিক বানান দিয়ে আবার সার্চ করুন অথবা উপরের কি-ওয়ার্ড চিপস ব্যবহার করুন।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(sortedProducts.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
            }
        }
    }
}
