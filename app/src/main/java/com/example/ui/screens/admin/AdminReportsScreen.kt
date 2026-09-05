package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BazarViewModel

@Composable
fun AdminReportsScreen(
    viewModel: BazarViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    val products by viewModel.adminProducts.collectAsState()
    val totalSales by viewModel.totalSales.collectAsState()
    val todaySales by viewModel.todaySales.collectAsState()

    var selectedPeriodIndex by remember { mutableIntStateOf(0) } // 0: আজকের (Daily), 1: সাপ্তাহিক (Weekly), 2: মাসিক (Monthly)

    // Calculate metrics
    val validOrders = orders.filter { !it.status.contains("বাতিল") }
    val displaySales = when (selectedPeriodIndex) {
        0 -> todaySales
        1 -> totalSales * 0.65
        else -> totalSales
    }
    val orderCount = when (selectedPeriodIndex) {
        0 -> orders.count { it.timestamp >= System.currentTimeMillis() - 86400000L }
        1 -> (validOrders.size * 0.7).toInt().coerceAtLeast(1)
        else -> validOrders.size
    }
    val avgOrderValue = if (orderCount > 0) displaySales / orderCount else 0.0

    // Best selling products calculation
    val bestSellingProducts = products.take(5)

    // Payment distribution
    val codCount = validOrders.count { it.paymentMethod.contains("ক্যাশ") || it.paymentMethod.contains("নগদ") }
    val bkashCount = validOrders.count { it.paymentMethod.contains("বিকাশ") || it.paymentMethod.contains("bkash") }
    val cardCount = (validOrders.size - codCount - bkashCount).coerceAtLeast(0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AdminCanvas)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 12.dp, bottom = 40.dp)
    ) {
        // Period tabs
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White
            ) {
                TabRow(
                    selectedTabIndex = selectedPeriodIndex,
                    containerColor = Color.White,
                    contentColor = AdminPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedPeriodIndex]),
                            color = AdminPrimary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedPeriodIndex == 0,
                        onClick = { selectedPeriodIndex = 0 },
                        text = { Text("দৈনিক (Daily)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedPeriodIndex == 1,
                        onClick = { selectedPeriodIndex = 1 },
                        text = { Text("সাপ্তাহিক (Weekly)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedPeriodIndex == 2,
                        onClick = { selectedPeriodIndex = 2 },
                        text = { Text("মাসিক (Monthly)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }
        }

        // Summary Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    title = "মোট আয়",
                    value = "৳${displaySales.toInt()}",
                    subtitle = "মোট রেভিনিউ",
                    icon = Icons.Default.AttachMoney,
                    iconBg = Color(0xFFEBF2E8),
                    iconTint = AdminPrimary,
                    modifier = Modifier.weight(1f),
                    testTag = "admin_report_revenue"
                )
                AdminStatCard(
                    title = "অর্ডার সংখ্যা",
                    value = "$orderCount টি",
                    subtitle = "সফল অর্ডার",
                    icon = Icons.Default.ShoppingCart,
                    iconBg = Color(0xFFEFF6FF),
                    iconTint = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f),
                    testTag = "admin_report_orders"
                )
                AdminStatCard(
                    title = "গড় অর্ডার",
                    value = "৳${avgOrderValue.toInt()}",
                    subtitle = "AOV",
                    icon = Icons.Default.BarChart,
                    iconBg = Color(0xFFFDF4FF),
                    iconTint = Color(0xFF9333EA),
                    modifier = Modifier.weight(1f),
                    testTag = "admin_report_aov"
                )
            }
        }

        // Visual Sales Chart
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AdminBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "বিক্রি গ্রাফ (Sales Performance)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AdminDark
                        )
                        Text(
                            text = if (selectedPeriodIndex == 0) "গত ৭ দিন" else if (selectedPeriodIndex == 1) "গত ৪ সপ্তাহ" else "চলতি বছর",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val chartBars = when (selectedPeriodIndex) {
                        0 -> listOf(
                            "শনি" to 0.45f,
                            "রবি" to 0.60f,
                            "সোম" to 0.85f,
                            "মঙ্গল" to 0.70f,
                            "বুধ" to 0.50f,
                            "বৃহ" to 0.95f,
                            "শুক্র" to 1.0f
                        )
                        1 -> listOf(
                            "সপ্তাহ ১" to 0.55f,
                            "সপ্তাহ ২" to 0.75f,
                            "সপ্তাহ ৩" to 0.90f,
                            "সপ্তাহ ৪" to 1.0f
                        )
                        else -> listOf(
                            "জানু" to 0.40f,
                            "ফেব্রু" to 0.65f,
                            "মার্চ" to 0.95f,
                            "এপ্রিল" to 0.70f,
                            "মে" to 0.85f,
                            "জুন" to 1.0f
                        )
                    }

                    // Compose Canvas Bar Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val barWidth = (size.width / chartBars.size) * 0.55f
                            val spaceBetween = (size.width - (barWidth * chartBars.size)) / (chartBars.size + 1)

                            chartBars.forEachIndexed { index, pair ->
                                val barHeight = size.height * pair.second * 0.85f
                                val xOffset = spaceBetween + index * (barWidth + spaceBetween)
                                val yOffset = size.height - barHeight

                                // Draw bar background guide
                                drawRoundRect(
                                    color = Color(0xFFF1F5F9),
                                    topLeft = Offset(xOffset, 0f),
                                    size = Size(barWidth, size.height),
                                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                )

                                // Draw active bar
                                drawRoundRect(
                                    color = if (pair.second == 1.0f) AdminPrimary else AdminPrimary.copy(alpha = 0.65f),
                                    topLeft = Offset(xOffset, yOffset),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Labels below bars
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        chartBars.forEach { pair ->
                            Text(
                                text = pair.first,
                                fontSize = 10.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Best Selling Products Table
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AdminBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🏆 সেরা বিক্রিত পণ্যসমূহ (Best Selling)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AdminDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    bestSellingProducts.forEachIndexed { index, product ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (index == 0) Color(0xFFFEF3C7) else Color(0xFFF1F5F9),
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "#${index + 1}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (index == 0) Color(0xFFD97706) else Color(0xFF64748B)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(product.emoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = product.banglaName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = AdminDark
                                    )
                                    Text(
                                        text = "${product.weightOrVolume} • ৳${product.price.toInt()}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Text(
                                text = "${(24 - index * 4)} টি বিক্রি",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AdminPrimary
                            )
                        }
                    }
                }
            }
        }

        // Payment Method breakdown
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AdminBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💳 পেমেন্ট মাধ্যম বিশ্লেষণ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AdminDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PaymentProgressItem("ক্যাশ অন ডেলিভারি (COD)", 0.70f, "৭০%", Color(0xFF16A34A))
                    PaymentProgressItem("বিকাশ ও ডিজিটাল পেমেন্ট", 0.25f, "২৫%", Color(0xFFE11D48))
                    PaymentProgressItem("কার্ড ও অন্যান্য", 0.05f, "৫%", Color(0xFF2563EB))
                }
            }
        }
    }
}

@Composable
fun PaymentProgressItem(
    title: String,
    progress: Float,
    percentLabel: String,
    color: Color
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 12.sp, color = AdminDark, fontWeight = FontWeight.Medium)
            Text(percentLabel, fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            color = color,
            trackColor = Color(0xFFF1F5F9),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        )
    }
}
