package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.StockLogEntity
import com.example.ui.BazarViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminInventoryScreen(
    viewModel: BazarViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.adminProducts.collectAsState()
    val stockLogs by viewModel.adminStockLogs.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Low Stock, 2: Out of Stock, 3: History
    var productForStockEdit by remember { mutableStateOf<ProductEntity?>(null) }

    val lowStockProducts = products.filter { it.stockQuantity in 1..10 }
    val outOfStockProducts = products.filter { it.stockQuantity <= 0 }

    val displayedProducts = when (selectedTab) {
        1 -> lowStockProducts
        2 -> outOfStockProducts
        else -> products
    }

    Box(modifier = modifier.fillMaxSize().background(AdminCanvas)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Inventory Status Summary Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    title = "মোট পণ্য",
                    value = "${products.size} টি",
                    icon = Icons.Default.Inventory2,
                    iconBg = Color(0xFFEBF2E8),
                    iconTint = AdminPrimary,
                    modifier = Modifier.weight(1f),
                    testTag = "admin_total_stock_card"
                )
                AdminStatCard(
                    title = "কম স্টক",
                    value = "${lowStockProducts.size} টি",
                    subtitle = "রিফিল প্রয়োজন",
                    icon = Icons.Default.WarningAmber,
                    iconBg = Color(0xFFFEFCE8),
                    iconTint = Color(0xFFD97706),
                    modifier = Modifier.weight(1f),
                    testTag = "admin_low_stock_card"
                )
                AdminStatCard(
                    title = "স্টক শেষ",
                    value = "${outOfStockProducts.size} টি",
                    icon = Icons.Default.Remove,
                    iconBg = Color(0xFFFEF2F2),
                    iconTint = Color(0xFFDC2626),
                    modifier = Modifier.weight(1f),
                    testTag = "admin_out_stock_card"
                )
            }

            // Tab bar for inventory filters
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = AdminPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AdminPrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("সব (${products.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("⚠️ কম স্টক (${lowStockProducts.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("🚫 শেষ (${outOfStockProducts.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("📜 হিস্টোরি", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
            }

            if (selectedTab != 3) {
                // Products Inventory List
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 30.dp)
                ) {
                    items(displayedProducts, key = { it.id }) { product ->
                        InventoryProductCard(
                            product = product,
                            onQuickAdd = { qty ->
                                viewModel.updateAdminProductStock(
                                    product.id,
                                    product.stockQuantity + qty,
                                    "দ্রুত স্টক যোগ (+$qty)"
                                )
                            },
                            onManualEdit = { productForStockEdit = product }
                        )
                    }
                }
            } else {
                // Stock History Logs View
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 30.dp)
                ) {
                    if (stockLogs.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("এখনও কোনো স্টক লগ রেকর্ড হয়নি", color = Color(0xFF64748B))
                            }
                        }
                    } else {
                        items(stockLogs, key = { it.id }) { log ->
                            StockLogCard(log = log)
                        }
                    }
                }
            }
        }

        // Custom Stock Adjustment Dialog
        productForStockEdit?.let { product ->
            StockAdjustDialog(
                product = product,
                onDismiss = { productForStockEdit = null },
                onSave = { newStock, reason ->
                    viewModel.updateAdminProductStock(product.id, newStock, reason)
                    productForStockEdit = null
                }
            )
        }
    }
}

@Composable
fun InventoryProductCard(
    product: ProductEntity,
    onQuickAdd: (Int) -> Unit,
    onManualEdit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AdminBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(product.emoji, fontSize = 22.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = product.banglaName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AdminDark
                        )
                        Text(
                            text = "${product.categoryBangla} • একক: ${product.weightOrVolume}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                StockStatusBadge(product.stockQuantity)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick stock modification controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        onClick = { onQuickAdd(5) },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Text(
                            text = "+৫ যোগ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF16A34A),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }

                    Surface(
                        onClick = { onQuickAdd(10) },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Text(
                            text = "+১০ যোগ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF16A34A),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }

                    Surface(
                        onClick = { onQuickAdd(20) },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Text(
                            text = "+২০ যোগ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF16A34A),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }

                OutlinedButton(
                    onClick = onManualEdit,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AdminDark),
                    border = BorderStroke(1.dp, AdminBorder),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("কাস্টম স্টক", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun StockLogCard(log: StockLogEntity) {
    val isStockIn = log.changeType == "STOCK_IN"
    val dateFormatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormatter.format(Date(log.timestamp))

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
                    shape = RoundedCornerShape(8.dp),
                    color = if (isStockIn) Color(0xFFF0FDF4) else Color(0xFFFEF2F2),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isStockIn) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (isStockIn) Color(0xFF16A34A) else Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = log.productName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AdminDark
                    )
                    Text(
                        text = "${log.note} • $formattedDate",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (log.quantityChanged >= 0) "+${log.quantityChanged}" else "${log.quantityChanged}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = if (isStockIn) Color(0xFF16A34A) else Color(0xFFDC2626)
                )
                Text(
                    text = "অবশিষ্ট: ${log.newStock}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun StockAdjustDialog(
    product: ProductEntity,
    onDismiss: () -> Unit,
    onSave: (Int, String) -> Unit
) {
    var newStockText by remember { mutableStateOf(product.stockQuantity.toString()) }
    var noteText by remember { mutableStateOf("ইনভেন্টরি সমন্বয় (Inventory Adjustment)") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("স্টক আপডেট: ${product.banglaName}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "বর্তমান স্টক: ${product.stockQuantity} টি (${product.weightOrVolume})",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )

                OutlinedTextField(
                    value = newStockText,
                    onValueChange = { newStockText = it },
                    label = { Text("নতুন মোট স্টক সংখ্যা *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("কারণ / নোট") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val stock = newStockText.toIntOrNull() ?: product.stockQuantity
                    onSave(stock, noteText)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary)
            ) {
                Text("হালনাগাদ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল") }
        }
    )
}
