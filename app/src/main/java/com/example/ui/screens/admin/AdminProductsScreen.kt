package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.ProductEntity
import com.example.ui.BazarViewModel

@Composable
fun AdminProductsScreen(
    viewModel: BazarViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.adminProducts.collectAsState()
    val categories by viewModel.adminCategories.collectAsState()

    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Products, 1: Categories
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }

    // Dialog states
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    val filteredProducts = products.filter { prod ->
        val matchesCategory = selectedCategoryId == null || prod.categoryId == selectedCategoryId
        val matchesSearch = searchQuery.isBlank() ||
                prod.banglaName.contains(searchQuery, ignoreCase = true) ||
                prod.englishName.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Box(modifier = modifier.fillMaxSize().background(AdminCanvas)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Sub-tabs: Products vs Categories
            TabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = Color.White,
                contentColor = AdminPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                        color = AdminPrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { selectedSubTab = 0 },
                    text = {
                        Text(
                            text = "📦 পণ্যসমূহ (${products.size})",
                            fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { selectedSubTab = 1 },
                    text = {
                        Text(
                            text = "📂 ক্যাটাগরি (${categories.size})",
                            fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }

            if (selectedSubTab == 0) {
                // PRODUCTS VIEW
                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("পণ্য খুঁজুন (চাল, ডাল, তেল, বিস্কুট...)", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("admin_search_product"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = AdminPrimary,
                        unfocusedBorderColor = AdminBorder
                    ),
                    singleLine = true
                )

                // Category Filter Pills
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            text = "সব পণ্য",
                            isSelected = selectedCategoryId == null,
                            onClick = { selectedCategoryId = null }
                        )
                    }
                    items(categories) { cat ->
                        FilterChip(
                            text = cat.banglaName,
                            isSelected = selectedCategoryId == cat.id,
                            onClick = { selectedCategoryId = cat.id }
                        )
                    }
                }

                // Products List
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        AdminProductCard(
                            product = product,
                            onEdit = { editingProduct = product },
                            onDelete = { productToDelete = product },
                            onToggleFlashSale = {
                                viewModel.toggleAdminProductFlashSale(product.id, !product.isFlashSale)
                            }
                        )
                    }
                }
            } else {
                // CATEGORIES VIEW
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "মোট ক্যাটাগরি (${categories.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = AdminDark
                        )
                        Button(
                            onClick = { showAddCategoryDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("নতুন ক্যাটাগরি", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 40.dp)
                    ) {
                        items(categories, key = { it.id }) { cat ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, AdminBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = Color(0xFFF1F5F9),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(cat.emoji, fontSize = 20.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = cat.banglaName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = AdminDark
                                            )
                                            Text(
                                                text = "${cat.englishName} • ${products.count { it.categoryId == cat.id }} টি পণ্য",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteAdminCategory(cat.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "মুছুন",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to Add Product
        if (selectedSubTab == 0) {
            FloatingActionButton(
                onClick = { showAddProductDialog = true },
                containerColor = AdminPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .testTag("admin_add_product_fab")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("নতুন পণ্য", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Add Product Dialog
        if (showAddProductDialog) {
            AddProductDialog(
                categories = categories,
                onDismiss = { showAddProductDialog = false },
                onAdd = { bangla, eng, catId, catBangla, price, origPrice, stock, weight, desc, brand, tags, emoji ->
                    viewModel.addAdminProduct(
                        nameBangla = bangla,
                        nameEnglish = eng,
                        categoryId = catId,
                        categoryBangla = catBangla,
                        price = price,
                        originalPrice = origPrice,
                        stockQuantity = stock,
                        weightOrVolume = weight,
                        description = desc,
                        brand = brand,
                        tags = tags,
                        emoji = emoji
                    )
                    showAddProductDialog = false
                }
            )
        }

        // Edit Product Dialog
        editingProduct?.let { product ->
            EditProductDialog(
                product = product,
                onDismiss = { editingProduct = null },
                onSave = { price, origPrice, stock ->
                    viewModel.updateAdminProductPrice(product.id, price, origPrice)
                    viewModel.updateAdminProductStock(product.id, stock, "ম্যানুয়াল প্রাইস ও স্টক আপডেট")
                    editingProduct = null
                }
            )
        }

        // Delete Confirmation Dialog
        productToDelete?.let { product ->
            AlertDialog(
                onDismissRequest = { productToDelete = null },
                title = { Text("পণ্য ডিলিট করবেন?", fontWeight = FontWeight.Bold) },
                text = { Text("\"${product.banglaName}\" পণ্যটি সিস্টেম থেকে মুছে ফেলা হবে।") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteAdminProduct(product.id)
                            productToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("ডিলিট করুন")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { productToDelete = null }) {
                        Text("বাতিল")
                    }
                }
            )
        }

        // Add Category Dialog
        if (showAddCategoryDialog) {
            AddCategoryDialog(
                onDismiss = { showAddCategoryDialog = false },
                onAdd = { name, eng, emoji ->
                    viewModel.addAdminCategory(name, eng, emoji)
                    showAddCategoryDialog = false
                }
            )
        }
    }
}

@Composable
fun AdminProductCard(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleFlashSale: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AdminBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail emoji
                Surface(
                    color = Color(0xFFEBF2E8),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(product.emoji, fontSize = 26.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.banglaName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AdminDark,
                        maxLines = 1
                    )
                    Text(
                        text = "${product.englishName} • ${product.weightOrVolume}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "৳${product.price.toInt()}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = AdminPrimary
                        )
                        if (product.originalPrice > product.price) {
                            Text(
                                text = "৳${product.originalPrice.toInt()}",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        }
                        StockStatusBadge(product.stockQuantity)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flash Sale Toggle Chip
                Surface(
                    onClick = onToggleFlashSale,
                    shape = RoundedCornerShape(8.dp),
                    color = if (product.isFlashSale) Color(0xFFFEF2F2) else Color(0xFFF1F5F9),
                    border = BorderStroke(
                        1.dp,
                        if (product.isFlashSale) Color(0xFFFCA5A5) else Color(0xFFCBD5E1)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = null,
                            tint = if (product.isFlashSale) Color(0xFFDC2626) else Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (product.isFlashSale) "ফ্ল্যাশ সেল: চালু" else "ফ্ল্যাশ সেল: বন্ধ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (product.isFlashSale) Color(0xFFDC2626) else Color(0xFF64748B)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Edit Price & Stock
                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AdminPrimary),
                        border = BorderStroke(1.dp, AdminPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("এডিট", fontSize = 11.sp)
                    }

                    // Delete
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "ডিলিট",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) AdminPrimary else Color.White,
        border = BorderStroke(1.dp, if (isSelected) AdminPrimary else AdminBorder)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else AdminDark,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun EditProductDialog(
    product: ProductEntity,
    onDismiss: () -> Unit,
    onSave: (Double, Double, Int) -> Unit
) {
    var priceText by remember { mutableStateOf(product.price.toInt().toString()) }
    var origPriceText by remember { mutableStateOf(product.originalPrice.toInt().toString()) }
    var stockText by remember { mutableStateOf(product.stockQuantity.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("পণ্য এডিট: ${product.banglaName}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("বিক্রয় মূল্য (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = origPriceText,
                    onValueChange = { origPriceText = it },
                    label = { Text("মূল/আগের মূল্য (৳) - ডিসকাউন্টের জন্য") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text("স্টক পরিমাণ (Stock Quantity)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: product.price
                    val orig = origPriceText.toDoubleOrNull() ?: product.originalPrice
                    val stock = stockText.toIntOrNull() ?: product.stockQuantity
                    onSave(price, orig, stock)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary)
            ) {
                Text("সংরক্ষণ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল") }
        }
    )
}

@Composable
fun AddProductDialog(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, Double, Double, Int, String, String, String, String, String) -> Unit
) {
    var banglaName by remember { mutableStateOf("") }
    var englishName by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf(categories.firstOrNull()) }
    var priceText by remember { mutableStateOf("") }
    var originalPriceText by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("50") }
    var weightText by remember { mutableStateOf("১ কেজি") }
    var descText by remember { mutableStateOf("") }
    var brandText by remember { mutableStateOf("Shohoj Bazar") }
    var selectedEmoji by remember { mutableStateOf("🛒") }

    val emojis = listOf("🥬", "🥔", "🍅", "🍚", "🫘", "🛢️", "🥛", "🥚", "🍪", "🧼", "🌶️", "☕", "🧃", "🍼")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন পণ্য যোগ করুন", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("আইকন/ইমোজি বাছাই করুন:", fontSize = 12.sp, color = Color(0xFF64748B))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        items(emojis) { emoji ->
                            Surface(
                                onClick = { selectedEmoji = emoji },
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedEmoji == emoji) AdminPrimary.copy(alpha = 0.2f) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (selectedEmoji == emoji) AdminPrimary else Color.Transparent),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(emoji, fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = banglaName,
                        onValueChange = { banglaName = it },
                        label = { Text("পণ্যের বাংলা নাম *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = englishName,
                        onValueChange = { englishName = it },
                        label = { Text("ইংরেজি নাম") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text("ক্যাটাগরি নির্বাচন করুন:", fontSize = 12.sp, color = Color(0xFF64748B))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        items(categories) { cat ->
                            Surface(
                                onClick = { selectedCat = cat },
                                shape = RoundedCornerShape(16.dp),
                                color = if (selectedCat?.id == cat.id) AdminPrimary else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = cat.banglaName,
                                    fontSize = 11.sp,
                                    color = if (selectedCat?.id == cat.id) Color.White else AdminDark,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("দাম (৳) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = originalPriceText,
                            onValueChange = { originalPriceText = it },
                            label = { Text("আগের দাম (৳)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = stockText,
                            onValueChange = { stockText = it },
                            label = { Text("স্টক পরিমাণ *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = weightText,
                            onValueChange = { weightText = it },
                            label = { Text("ওজন/পরিমাণ *") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = brandText,
                        onValueChange = { brandText = it },
                        label = { Text("ব্র্যান্ডের নাম") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = descText,
                        onValueChange = { descText = it },
                        label = { Text("পণ্যের বিস্তারিত বিবরণ") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (banglaName.isNotBlank() && priceText.toDoubleOrNull() != null) {
                        val cat = selectedCat ?: categories.first()
                        val price = priceText.toDouble()
                        val orig = originalPriceText.toDoubleOrNull() ?: price
                        val stock = stockText.toIntOrNull() ?: 50
                        onAdd(
                            banglaName,
                            englishName,
                            cat.id,
                            cat.banglaName,
                            price,
                            orig,
                            stock,
                            weightText,
                            descText,
                            brandText,
                            "",
                            selectedEmoji
                        )
                    }
                },
                enabled = banglaName.isNotBlank() && priceText.toDoubleOrNull() != null,
                colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary)
            ) {
                Text("যোগ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল") }
        }
    )
}

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var nameBangla by remember { mutableStateOf("") }
    var nameEnglish by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🥬") }

    val emojis = listOf("🥬", "🍚", "🛢️", "🥛", "🥚", "🍪", "🧼", "☕", "🥩", "🐟", "🥖", "🍎")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন ক্যাটাগরি তৈরি করুন", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("ইমোজি আইকন বাছাই করুন:", fontSize = 12.sp, color = Color(0xFF64748B))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(emojis) { emoji ->
                        Surface(
                            onClick = { selectedEmoji = emoji },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedEmoji == emoji) AdminPrimary.copy(alpha = 0.2f) else Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, if (selectedEmoji == emoji) AdminPrimary else Color.Transparent),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(emoji, fontSize = 18.sp)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = nameBangla,
                    onValueChange = { nameBangla = it },
                    label = { Text("ক্যাটাগরি নাম (বাংলা) *") },
                    placeholder = { Text("যেমন: শাকসবজি, ডিম, মাংস") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = nameEnglish,
                    onValueChange = { nameEnglish = it },
                    label = { Text("ইংরেজি নাম") },
                    placeholder = { Text("e.g. Fresh Vegetables") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameBangla.isNotBlank()) {
                        onAdd(nameBangla, nameEnglish, selectedEmoji)
                    }
                },
                enabled = nameBangla.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary)
            ) {
                Text("তৈরি করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল") }
        }
    )
}
