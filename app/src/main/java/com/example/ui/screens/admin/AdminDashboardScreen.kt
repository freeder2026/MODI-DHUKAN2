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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BazarViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: BazarViewModel,
    onNavigateToCustomerHome: () -> Unit,
    onNavigateToDelivery: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(
        "📊 ড্যাশবোর্ড",
        "👥 কাস্টমার/ইউজার",
        "📦 পণ্য ও ক্যাটাগরি",
        "📦 ইনভেন্টরি",
        "🛍️ অর্ডার",
        "🎟️ অফার ও কুপন",
        "📈 সেলস রিপোর্ট"
    )

    Scaffold(
        topBar = {
            AdminTopHeader(
                title = "সহজ বাজার - অ্যাডমিন",
                onBackToCustomerApp = onNavigateToCustomerHome,
                onNavigateToDelivery = onNavigateToDelivery,
                onLogout = {
                    viewModel.logoutAdmin()
                    onLogout()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AdminCanvas)
        ) {
            // Main Top Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = AdminPrimary,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AdminPrimary
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> AdminOverviewTab(
                    viewModel = viewModel,
                    onNavigateTab = { selectedTab = it },
                    onNavigateToDelivery = onNavigateToDelivery
                )
                1 -> AdminUsersScreen(viewModel = viewModel)
                2 -> AdminProductsScreen(viewModel = viewModel)
                3 -> AdminInventoryScreen(viewModel = viewModel)
                4 -> AdminOrdersScreen(viewModel = viewModel)
                5 -> AdminCouponsScreen(viewModel = viewModel)
                6 -> AdminReportsScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AdminOverviewTab(
    viewModel: BazarViewModel,
    onNavigateTab: (Int) -> Unit,
    onNavigateToDelivery: () -> Unit = {}
) {
    val todaySales by viewModel.todaySales.collectAsState()
    val totalSales by viewModel.totalSales.collectAsState()
    val todayOrders by viewModel.todayOrdersCount.collectAsState()
    val pendingOrders by viewModel.pendingOrdersCount.collectAsState()
    val deliveredOrders by viewModel.deliveredOrdersCount.collectAsState()
    val products by viewModel.adminProducts.collectAsState()
    val lowStockCount by viewModel.lowStockProductsCount.collectAsState()
    val orders by viewModel.orders.collectAsState()

    val totalCustomers = orders.map { it.recipientPhone }.distinct().count().coerceAtLeast(1)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 12.dp, bottom = 40.dp)
    ) {
        // Welcome & Store Status Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AdminDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF22C55E), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "স্টোর স্ট্যাটাস: অনলাইন ও চালু",
                                color = Color(0xFF22C55E),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "সহজ বাজার কন্ট্রোল প্যানেল",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "দোকানের বিক্রি, পণ্য ও অর্ডার পরিচালনা করুন",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🏪", fontSize = 22.sp)
                        }
                    }
                }
            }
        }

        // Section Title: Dashboard Metrics
        item {
            Text(
                text = "📊 ব্যবসায়িক সারসংক্ষেপ (Overview)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AdminDark
            )
        }

        // Row 1: Sales Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    title = "আজকের বিক্রি",
                    value = "৳${todaySales.toInt()}",
                    subtitle = "আজকের মোট আয়",
                    icon = Icons.Default.Paid,
                    iconBg = Color(0xFFEBF2E8),
                    iconTint = AdminPrimary,
                    modifier = Modifier.weight(1f),
                    testTag = "admin_today_sales"
                )
                AdminStatCard(
                    title = "মোট বিক্রি",
                    value = "৳${totalSales.toInt()}",
                    subtitle = "লাইফটাইম সেলস",
                    icon = Icons.Default.AttachMoney,
                    iconBg = Color(0xFFEFF6FF),
                    iconTint = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f),
                    testTag = "admin_total_sales"
                )
            }
        }

        // Row 2: Order Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    title = "আজকের অর্ডার",
                    value = "$todayOrders টি",
                    icon = Icons.Default.ShoppingBag,
                    iconBg = Color(0xFFFDF4FF),
                    iconTint = Color(0xFF9333EA),
                    modifier = Modifier.weight(1f),
                    testTag = "admin_today_orders"
                )
                AdminStatCard(
                    title = "Pending Order",
                    value = "$pendingOrders টি",
                    subtitle = if (pendingOrders > 0) "প্রসেস করুন" else "ক্লিয়ার",
                    icon = Icons.Default.HourglassTop,
                    iconBg = Color(0xFFFEFCE8),
                    iconTint = Color(0xFFD97706),
                    modifier = Modifier.weight(1f),
                    testTag = "admin_pending_orders"
                )
            }
        }

        // Row 3: Customers & Deliveries
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    title = "Delivered Order",
                    value = "$deliveredOrders টি",
                    subtitle = "সফল ডেলিভারি",
                    icon = Icons.Default.CheckCircle,
                    iconBg = Color(0xFFF0FDF4),
                    iconTint = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f),
                    testTag = "admin_delivered_orders"
                )
                AdminStatCard(
                    title = "মোট কাস্টমার",
                    value = "$totalCustomers জন",
                    subtitle = "নিবন্ধিত ক্রেতা",
                    icon = Icons.Default.People,
                    iconBg = Color(0xFFF1F5F9),
                    iconTint = Color(0xFF475569),
                    modifier = Modifier.weight(1f),
                    testTag = "admin_total_customers"
                )
            }
        }

        // Row 4: Products & Low Stock
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    title = "মোট পণ্য",
                    value = "${products.size} টি",
                    subtitle = "ক্যাটালগ আইটেম",
                    icon = Icons.Default.Inventory2,
                    iconBg = Color(0xFFEBF2E8),
                    iconTint = AdminPrimary,
                    modifier = Modifier.weight(1f),
                    testTag = "admin_total_products"
                )
                AdminStatCard(
                    title = "Low Stock Product",
                    value = "$lowStockCount টি",
                    subtitle = if (lowStockCount > 0) "রিফিল প্রয়োজন" else "সব পর্যাপ্ত",
                    icon = Icons.Default.WarningAmber,
                    iconBg = Color(0xFFFEF2F2),
                    iconTint = Color(0xFFDC2626),
                    modifier = Modifier.weight(1f),
                    testTag = "admin_low_stock"
                )
            }
        }

        // Quick Navigation Shortcuts
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AdminBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "⚡ দ্রুত একশন (Quick Actions)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AdminDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onNavigateTab(1) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, AdminBorder)
                        ) {
                            Text("➕ নতুন পণ্য", fontSize = 11.sp, color = AdminDark, fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = { onNavigateTab(2) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, AdminBorder)
                        ) {
                            Text("📦 স্টক চেক", fontSize = 11.sp, color = AdminDark, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onNavigateTab(3) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, AdminBorder)
                        ) {
                            Text("🛍️ অর্ডার লিস্ট", fontSize = 11.sp, color = AdminDark, fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = { onNavigateTab(4) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, AdminBorder)
                        ) {
                            Text("🎟️ কুপন তৈরি", fontSize = 11.sp, color = AdminDark, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = onNavigateToDelivery,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🚚 ডেলিভারি সিস্টেম ড্যাশবোর্ড খুলুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Recent Orders quick list
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সাম্প্রতিক অর্ডারসমূহ (${orders.take(3).size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AdminDark
                )
                Button(
                    onClick = { onNavigateTab(3) },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp),
                    elevation = null
                ) {
                    Text("সব দেখুন", fontSize = 11.sp, color = AdminPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(orders.take(3), key = { it.orderId }) { order ->
            AdminOrderCard(
                order = order,
                onConfirm = { viewModel.confirmOrder(order.orderId) },
                onProcess = { viewModel.processOrder(order.orderId) },
                onAssignDelivery = { onNavigateTab(3) },
                onMarkDelivered = { viewModel.markOrderDelivered(order.orderId) },
                onCancel = { viewModel.cancelOrder(order.orderId) }
            )
        }
    }
}
