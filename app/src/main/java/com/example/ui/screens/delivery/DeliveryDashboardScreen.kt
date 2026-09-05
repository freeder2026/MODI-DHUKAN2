package com.example.ui.screens.delivery

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TakeoutDining
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DeliveryManEntity
import com.example.data.local.entity.OrderEntity
import com.example.ui.BazarViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Sporty & energetic Delivery Rider Palette
private val RiderDark = Color(0xFF0F172A)
private val RiderPrimary = Color(0xFF2563EB) // Royal Blue
private val RiderAccent = Color(0xFFF59E0B)  // Amber / Warning
private val RiderSuccess = Color(0xFF10B981) // Emerald Green
private val RiderCanvas = Color(0xFFF8FAFC)
private val RiderCard = Color(0xFFFFFFFF)
private val RiderBorder = Color(0xFFE2E8F0)

@Composable
fun DeliveryDashboardScreen(
    viewModel: BazarViewModel,
    onNavigateToCustomerHome: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeRider by viewModel.activeDeliveryMan.collectAsState()
    val isOnline by viewModel.isRiderOnline.collectAsState()
    val deliveryMen by viewModel.adminDeliveryMen.collectAsState()
    val allOrders by viewModel.orders.collectAsState()

    var showRiderSwitchDialog by remember { mutableStateOf(false) }
    var showWorkflowDialog by remember { mutableStateOf(false) }
    var selectedTabFilter by remember { mutableIntStateOf(0) } // 0: All, 1: Picked Up, 2: On The Way, 3: Delivered

    val currentRider = activeRider ?: deliveryMen.firstOrNull() ?: DeliveryManEntity(
        id = "DEL-1",
        name = "মোঃ করিম",
        phone = "01811223344",
        area = "মিরপুর ও পল্লবী জোন",
        activeDeliveries = 2
    )

    // Filter orders specifically assigned to this rider
    val riderOrders = allOrders.filter { order ->
        order.deliveryMan != null && (
            order.deliveryMan.equals(currentRider.name, ignoreCase = true) ||
            order.deliveryMan!!.contains(currentRider.name.take(5))
        )
    }

    // Orders in store waiting to be delivered (for claiming)
    val openStoreOrders = allOrders.filter { order ->
        order.deliveryMan == null && (
            order.status.contains("প্রসেসিং") ||
            order.status.contains("Processing") ||
            order.status.contains("কনফার্মড") ||
            order.status.contains("প্যাকেজিং")
        )
    }

    // Tab filtered orders
    val filteredOrders = riderOrders.filter { order ->
        when (selectedTabFilter) {
            1 -> order.status.contains("Assigned") || order.status.contains("ন্যস্ত") || order.status.contains("পিকআপ") || order.status.contains("Picked")
            2 -> order.status.contains("On The Way") || order.status.contains("রওনা") || order.status.contains("পথে")
            3 -> order.status.contains("Delivered") || order.status.contains("সম্পন্ন")
            else -> true
        }
    }

    // Statistics for this rider
    val totalAssigned = riderOrders.size
    val toPickUpCount = riderOrders.count {
        it.status.contains("Assigned") || it.status.contains("ন্যস্ত") || (it.status.contains("পিকআপ") && !it.status.contains("সম্পন্ন"))
    }
    val onTheWayCount = riderOrders.count {
        it.status.contains("On The Way") || it.status.contains("রওনা") || it.status.contains("পথে")
    }
    val completedCount = riderOrders.count {
        it.status.contains("Delivered") || it.status.contains("সম্পন্ন")
    }
    val codTotalToCollect = riderOrders
        .filter { !it.status.contains("Delivered") && !it.status.contains("সম্পন্ন") && it.paymentMethod.contains("ক্যাশ") }
        .sumOf { it.finalTotal }

    Scaffold(
        topBar = {
            RiderHeaderBar(
                rider = currentRider,
                isOnline = isOnline,
                onToggleOnline = { viewModel.toggleRiderOnlineStatus() },
                onSwitchRider = { showRiderSwitchDialog = true },
                onBackToCustomer = onNavigateToCustomerHome,
                onBackToAdmin = onNavigateToAdmin,
                onLogout = {
                    viewModel.logoutDeliveryMan()
                    onLogout()
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(RiderCanvas),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Workflow System Banner (11 Steps Visual Guide)
            item {
                DeliveryWorkflowCard(
                    onOpenDetails = { showWorkflowDialog = true }
                )
            }

            // 2. Rider Duty & Statistics Card
            item {
                RiderPerformanceSummaryCard(
                    totalAssigned = totalAssigned,
                    toPickUp = toPickUpCount,
                    onTheWay = onTheWayCount,
                    completed = completedCount,
                    codAmount = codTotalToCollect
                )
            }

            // 3. Status Tabs (All, 1. To Pick Up, 2. On The Way, 3. Delivered)
            item {
                DeliveryTabRow(
                    selectedTab = selectedTabFilter,
                    onSelectTab = { selectedTabFilter = it },
                    totalCount = riderOrders.size,
                    pickupCount = toPickUpCount,
                    onWayCount = onTheWayCount,
                    completedCount = completedCount
                )
            }

            // 4. Assigned Orders List
            if (filteredOrders.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, RiderBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = RiderPrimary.copy(alpha = 0.1f),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.DeliveryDining,
                                        contentDescription = null,
                                        tint = RiderPrimary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "এই ক্যাটাগরিতে কোনো অর্ডার নেই",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = RiderDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "অ্যাডমিন প্যানেল থেকে অর্ডার অ্যাসাইন করা হলে এখানে দেখাবে।",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredOrders, key = { it.orderId }) { order ->
                    AssignedOrderCard(
                        order = order,
                        currentRider = currentRider,
                        onPickedUp = { viewModel.markOrderPickedUp(order.orderId) },
                        onOnTheWay = { viewModel.markOrderOnTheWay(order.orderId) },
                        onDelivered = { viewModel.markOrderDeliveredByRider(order.orderId, currentRider.name) }
                    )
                }
            }

            // 5. Open Store Orders Available to Claim
            if (openStoreOrders.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "📦 দোকানে আন-অ্যাসাইনড অর্ডার (${openStoreOrders.size} টি প্রস্তুত)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = RiderDark
                    )
                    Text(
                        text = "আপনি চাইলে সরাসরি দোকান থেকে এই অর্ডারটি পিকআপ করে নিতে পারেন:",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                items(openStoreOrders, key = { "open_" + it.orderId }) { openOrder ->
                    OpenStoreOrderCard(
                        order = openOrder,
                        onAccept = {
                            viewModel.riderAcceptOrder(openOrder.orderId, currentRider.name)
                        }
                    )
                }
            }
        }
    }

    // Dialog: Switch Rider / Rider Login
    if (showRiderSwitchDialog) {
        RiderSwitchDialog(
            currentRider = currentRider,
            deliveryMen = deliveryMen,
            onDismiss = { showRiderSwitchDialog = false },
            onSelectRider = { selectedMan ->
                viewModel.loginDeliveryMan(selectedMan)
                showRiderSwitchDialog = false
            },
            onRegisterNewRider = { name, phone, area ->
                viewModel.registerNewDeliveryMan(name, phone, area)
                showRiderSwitchDialog = false
            }
        )
    }

    // Dialog: 11-Step System Workflow Guide
    if (showWorkflowDialog) {
        WorkflowGuideDialog(onDismiss = { showWorkflowDialog = false })
    }
}

// ----------------------
// 1. Rider Header Bar
// ----------------------
@Composable
private fun RiderHeaderBar(
    rider: DeliveryManEntity,
    isOnline: Boolean,
    onToggleOnline: () -> Unit,
    onSwitchRider: () -> Unit,
    onBackToCustomer: () -> Unit,
    onBackToAdmin: () -> Unit,
    onLogout: () -> Unit = {}
) {
    Surface(
        color = RiderDark,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top Navigation & Role Switchers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RiderPrimary,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.TwoWheeler,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "সহজ বাজার রাইডার",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "ডেলিভারি ড্যাশবোর্ড (Delivery Panel)",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // Switch Role buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF334155),
                        modifier = Modifier.clickable { onBackToAdmin() }
                    ) {
                        Text(
                            text = "🛡️ অ্যাডমিন",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF16A34A),
                        modifier = Modifier.clickable { onBackToCustomer() }
                    ) {
                        Text(
                            text = "🛒 গ্রাহক ভিউ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFF334155))
            Spacer(modifier = Modifier.height(10.dp))

            // Active Rider Profile Row & Online Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.5.dp, if (isOnline) RiderSuccess else Color(0xFF64748B)),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isOnline) RiderSuccess else Color(0xFF94A3B8),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = rider.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isOnline) Color(0xFF064E3B) else Color(0xFF334155)
                            ) {
                                Text(
                                    text = if (isOnline) "ডিউটিতে 🟢" else "বিশ্রামে 🔴",
                                    fontSize = 9.sp,
                                    color = if (isOnline) Color(0xFF34D399) else Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "এলাকা: ${rider.area} • 📞 ${rider.phone}",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // Actions: Switch Rider & Logout
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onSwitchRider,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("রাইডার বদল", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Surface(
                        onClick = onLogout,
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout",
                                tint = Color(0xFFFCA5A5),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "লগআউট",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFCA5A5)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------
// 2. System Workflow Card (11 Steps)
// ----------------------
@Composable
private fun DeliveryWorkflowCard(onOpenDetails: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
        border = BorderStroke(1.dp, RiderPrimary.copy(alpha = 0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = RiderPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🔄 সহজ বাজার ১১-ধাপ ডেলিভারি সিস্টেম",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = RiderDark
                    )
                }
                Text(
                    text = "বিস্তারিত দেখুন ➔",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RiderPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Visual Progress Flow of Delivery Steps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WorkflowStepBadge(num = "৮", label = "অ্যাসাইন", isActive = true)
                Text("➔", fontSize = 11.sp, color = RiderPrimary, fontWeight = FontWeight.Bold)
                WorkflowStepBadge(num = "৯", label = "1️⃣ Picked Up", isActive = true)
                Text("➔", fontSize = 11.sp, color = RiderPrimary, fontWeight = FontWeight.Bold)
                WorkflowStepBadge(num = "১০", label = "2️⃣ On The Way", isActive = true)
                Text("➔", fontSize = 11.sp, color = RiderPrimary, fontWeight = FontWeight.Bold)
                WorkflowStepBadge(num = "১১", label = "3️⃣ Delivered", isActive = true)
            }
        }
    }
}

@Composable
private fun WorkflowStepBadge(num: String, label: String, isActive: Boolean) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isActive) Color.White else Color(0xFFF1F5F9),
        border = BorderStroke(1.dp, if (isActive) RiderPrimary else Color(0xFFCBD5E1))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) RiderPrimary else Color(0xFF64748B)
            )
        }
    }
}

// ----------------------
// 3. Performance Summary Card
// ----------------------
@Composable
private fun RiderPerformanceSummaryCard(
    totalAssigned: Int,
    toPickUp: Int,
    onTheWay: Int,
    completed: Int,
    codAmount: Double
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, RiderBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 আজকের কর্মক্ষমতা ও ক্যাশ স্ট্যাটাস",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = RiderDark
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFEF3C7),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "নগদ সংগ্রহ: ৳${codAmount.toInt()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RiderStatPill(
                    count = totalAssigned.toString(),
                    label = "মোট অ্যাসাইন",
                    color = Color(0xFF6366F1),
                    modifier = Modifier.weight(1f)
                )
                RiderStatPill(
                    count = toPickUp.toString(),
                    label = "১. পিকআপ বাকি",
                    color = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f)
                )
                RiderStatPill(
                    count = onTheWay.toString(),
                    label = "২. পথে আছে",
                    color = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                )
                RiderStatPill(
                    count = completed.toString(),
                    label = "৩. সম্পন্ন",
                    color = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun RiderStatPill(
    count: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = RiderDark,
                maxLines = 1
            )
        }
    }
}

// ----------------------
// 4. Tab Row
// ----------------------
@Composable
private fun DeliveryTabRow(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    totalCount: Int,
    pickupCount: Int,
    onWayCount: Int,
    completedCount: Int
) {
    val tabs = listOf(
        "সব কাজ ($totalCount)",
        "১️⃣ পিকআপ ($pickupCount)",
        "২️⃣ পথে ($onWayCount)",
        "৩️⃣ সম্পন্ন ($completedCount)"
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tabs.size) { index ->
            val isSelected = selectedTab == index
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) RiderDark else Color.White,
                border = BorderStroke(1.dp, if (isSelected) RiderDark else RiderBorder),
                modifier = Modifier.clickable { onSelectTab(index) }
            ) {
                Text(
                    text = tabs[index],
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected) Color.White else Color(0xFF475569),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
        }
    }
}

// ----------------------
// 5. Assigned Order Card (Core Requirement)
// ----------------------
@Composable
private fun AssignedOrderCard(
    order: OrderEntity,
    currentRider: DeliveryManEntity,
    onPickedUp: () -> Unit,
    onOnTheWay: () -> Unit,
    onDelivered: () -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = dateFormatter.format(Date(order.timestamp))

    var isProductsExpanded by remember { mutableStateOf(false) }
    var showDeliveredConfirmation by remember { mutableStateOf(false) }

    // Delivery Status Progress:
    // 0: Assigned / Waiting Pickup
    // 1: Picked Up (দোকান থেকে নিয়েছে)
    // 2: On The Way (কাস্টমারের কাছে যাচ্ছে)
    // 3: Delivered (পণ্য পৌঁছে দিয়েছে)
    val stepState = when {
        order.status.contains("Delivered") || order.status.contains("সম্পন্ন") -> 3
        order.status.contains("On The Way") || order.status.contains("রওনা") || order.status.contains("পথে") -> 2
        order.status.contains("Picked Up") || order.status.contains("পিকআপ") -> 1
        else -> 0
    }

    val (badgeBg, badgeText, badgeColor) = when (stepState) {
        3 -> Triple(Color(0xFFF0FDF4), "✅ ৩. ডেলিভারি সম্পন্ন (Delivered)", Color(0xFF16A34A))
        2 -> Triple(Color(0xFFFEF3C7), "🚀 ২. কাস্টমারের পথে (On The Way)", Color(0xFFD97706))
        1 -> Triple(Color(0xFFEFF6FF), "📦 ১. দোকান থেকে পিকআপ (Picked Up)", Color(0xFF2563EB))
        else -> Triple(Color(0xFFF1F5F9), "⏳ অ্যাসাইন করা হয়েছে (Assigned)", Color(0xFF475569))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RiderCard),
        border = BorderStroke(1.2.dp, if (stepState in 1..2) RiderPrimary.copy(alpha = 0.5f) else RiderBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("delivery_order_card_${order.orderId}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Order ID, Time, Status Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "অর্ডার #${order.orderId}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = RiderDark
                    )
                    Text(
                        text = "অর্ডার সময়: $formattedTime",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = badgeBg,
                    border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Visual 3-Step Progress Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressStepIndicator(title = "1️⃣ পিকআপ", isCompleted = stepState >= 1, isCurrent = stepState == 0)
                Text("──", color = if (stepState >= 2) RiderPrimary else Color(0xFFCBD5E1), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                ProgressStepIndicator(title = "2️⃣ পথে আছে", isCompleted = stepState >= 2, isCurrent = stepState == 1)
                Text("──", color = if (stepState >= 3) RiderSuccess else Color(0xFFCBD5E1), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                ProgressStepIndicator(title = "3️⃣ ডেলিভার্ড", isCompleted = stepState >= 3, isCurrent = stepState == 2)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Customer Name & Direct Call Button
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F5F9).copy(alpha = 0.6f),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = RiderPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = RiderPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = order.recipientName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = RiderDark
                            )
                            Text(
                                text = order.recipientPhone,
                                fontSize = 12.sp,
                                color = Color(0xFF047857),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Direct Phone Call Button
                    Button(
                        onClick = {
                            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${order.recipientPhone}")
                            }
                            context.startActivity(callIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call Customer",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("কল করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Delivery Address with Map Hint
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier
                        .size(18.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ডেলিভারি ঠিকানা:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = order.deliveryAddress,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RiderDark
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.clickable {
                        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(order.deliveryAddress)}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            val browserMapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${Uri.encode(order.deliveryAddress)}"))
                            context.startActivity(browserMapIntent)
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = null, tint = RiderPrimary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("ম্যাপ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RiderPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Ordered Products (Expandable List)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isProductsExpanded = !isProductsExpanded }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TakeoutDining, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "অর্ডারকৃত পণ্য (${order.totalItemsCount} টি আইটেম)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RiderDark
                            )
                        }
                        Icon(
                            imageVector = if (isProductsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (!isProductsExpanded) {
                        Text(
                            text = order.itemsSummary,
                            fontSize = 11.sp,
                            color = Color(0xFF475569),
                            maxLines = 1,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    AnimatedVisibility(
                        visible = isProductsExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(6.dp))
                            val itemsList = order.itemsSummary.split(",")
                            itemsList.forEachIndexed { idx, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("✔️", fontSize = 10.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = item.trim(),
                                        fontSize = 12.sp,
                                        color = RiderDark,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Order Amount & Payment Collection Mode
            val isCod = order.paymentMethod.contains("ক্যাশ") || order.paymentMethod.contains("Cash")
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isCod) Color(0xFFFEF2F2) else Color(0xFFF0FDF4),
                border = BorderStroke(1.dp, if (isCod) Color(0xFFFCA5A5) else Color(0xFF86EFAC)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isCod) "⚠️ নগদ টাকা সংগ্রহ করুন (COD)" else "✅ ডিজিটাল পেমেন্ট সম্পন্ন",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCod) Color(0xFFDC2626) else Color(0xFF16A34A)
                        )
                        Text(
                            text = "পেমেন্ট: ${order.paymentMethod}",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "মোট বিল:",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "৳${order.finalTotal.toInt()}",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = if (isCod) Color(0xFFDC2626) else Color(0xFF16A34A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Lifecycle Action Buttons (Requested 3-Step Transitions)
            // 1️⃣ Picked Up ➔ 2️⃣ On The Way ➔ 3️⃣ Delivered
            when (stepState) {
                0 -> {
                    // Not picked up yet -> Step 1: Picked Up
                    Button(
                        onClick = onPickedUp,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RiderPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("action_picked_up_${order.orderId}")
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1️⃣ Picked Up (দোকান থেকে পণ্য নিয়েছি)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                1 -> {
                    // Picked up -> Step 2: On The Way
                    Button(
                        onClick = onOnTheWay,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("action_on_the_way_${order.orderId}")
                    ) {
                        Icon(Icons.Default.DirectionsRun, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2️⃣ On The Way (কাস্টমারের কাছে যাচ্ছি)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                2 -> {
                    // On The Way -> Step 3: Delivered
                    Button(
                        onClick = {
                            if (isCod) {
                                showDeliveredConfirmation = true
                            } else {
                                onDelivered()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("action_delivered_${order.orderId}")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3️⃣ Delivered (পণ্য পৌঁছে দিয়েছি)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                3 -> {
                    // Completed!
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🎉 পণ্য সফলভাবে কাস্টমারকে ডেলিভারি করা হয়েছে!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF16A34A)
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog for Cash on Delivery collection before final delivery mark
    if (showDeliveredConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeliveredConfirmation = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Paid, contentDescription = null, tint = Color(0xFF16A34A))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ক্যাশ সংগ্রহ ও ডেলিভারি সম্পন্ন", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "কাস্টমার ${order.recipientName}-এর থেকে নগদ ৳${order.finalTotal.toInt()} বুঝে পেয়েছেন?",
                        fontSize = 13.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⚠️ টাকা সংগ্রহের পর 'হ্যাঁ, ডেলিভারি সম্পন্ন' চাপুন।",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeliveredConfirmation = false
                        onDelivered()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                ) {
                    Text("হ্যাঁ, ডেলিভারি সম্পন্ন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeliveredConfirmation = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
private fun ProgressStepIndicator(title: String, isCompleted: Boolean, isCurrent: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = when {
                isCompleted -> RiderSuccess
                isCurrent -> RiderPrimary
                else -> Color(0xFFE2E8F0)
            },
            modifier = Modifier.size(16.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isCompleted) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = if (isCompleted || isCurrent) FontWeight.Bold else FontWeight.Medium,
            color = if (isCompleted || isCurrent) RiderDark else Color(0xFF94A3B8)
        )
    }
}

// ----------------------
// 6. Open Store Order Card (Claimable)
// ----------------------
@Composable
private fun OpenStoreOrderCard(
    order: OrderEntity,
    onAccept: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "অর্ডার #${order.orderId} • ${order.recipientName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = RiderDark
                )
                Text(
                    text = "${order.deliveryAddress} • ৳${order.finalTotal.toInt()}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1
                )
                Text(
                    text = "আইটেম: ${order.itemsSummary}",
                    fontSize = 10.sp,
                    color = Color(0xFF475569),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onAccept,
                colors = ButtonDefaults.buttonColors(containerColor = RiderPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text("গ্রহণ করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ----------------------
// 7. Rider Switch & Login Dialog
// ----------------------
@Composable
private fun RiderSwitchDialog(
    currentRider: DeliveryManEntity,
    deliveryMen: List<DeliveryManEntity>,
    onDismiss: () -> Unit,
    onSelectRider: (DeliveryManEntity) -> Unit,
    onRegisterNewRider: (String, String, String) -> Unit
) {
    var isRegisteringNew by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newArea by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isRegisteringNew) "নতুন রাইডার লগইন" else "ডেলিভারিম্যান নির্বাচন",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!isRegisteringNew) {
                    Text(
                        text = "ড্যাশবোর্ড ব্যবহারের জন্য সক্রিয় রাইডার নির্বাচন করুন:",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    deliveryMen.forEach { man ->
                        val isSelected = man.id == currentRider.id
                        Card(
                            onClick = { onSelectRider(man) },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) RiderPrimary.copy(alpha = 0.1f) else Color.White
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) RiderPrimary else RiderBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TwoWheeler,
                                        contentDescription = null,
                                        tint = if (isSelected) RiderPrimary else Color(0xFF64748B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = man.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = RiderDark
                                        )
                                        Text(
                                            text = "${man.phone} • ${man.area}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = RiderPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = { isRegisteringNew = true },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("+ নতুন রাইডার হিসেবে লগইন করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RiderPrimary)
                    }
                } else {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("রাইডারের পুরো নাম") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPhone,
                        onValueChange = { newPhone = it },
                        label = { Text("মোবাইল নম্বর") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newArea,
                        onValueChange = { newArea = it },
                        label = { Text("দায়িত্বপ্রাপ্ত এলাকা / জোন") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (isRegisteringNew) {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && newPhone.isNotBlank()) {
                            onRegisterNewRider(newName, newPhone, newArea.ifBlank { "ঢাকা সিটি" })
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RiderPrimary),
                    enabled = newName.isNotBlank() && newPhone.isNotBlank()
                ) {
                    Text("লগইন ও শুরু করুন")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (isRegisteringNew) isRegisteringNew = false else onDismiss()
            }) {
                Text(if (isRegisteringNew) "পূর্ববর্তী তালিকা" else "বন্ধ করুন")
            }
        }
    )
}

// ----------------------
// 8. Workflow 11 Steps Dialog
// ----------------------
@Composable
private fun WorkflowGuideDialog(onDismiss: () -> Unit) {
    val steps = listOf(
        "STEP 1 🛒" to "কাস্টমার Website / অ্যাপে আসবে।",
        "STEP 2 🔍" to "প্রয়োজনীয় পণ্য সার্চ করবে বা ব্রাউজ করবে।",
        "STEP 3 📦" to "পণ্য Cart-এ যোগ করবে এবং পরিমাণ ঠিক করবে।",
        "STEP 4 💳" to "ক্যাশ অন ডেলিভারি বা অনলাইনে Checkout করবে।",
        "STEP 5 📥" to "অর্ডার স্বয়ংক্রিয়ভাবে Admin Dashboard-এ যাবে।",
        "STEP 6 🧑💼" to "দোকানদার/অ্যাডমিন অর্ডার Confirm করবে।",
        "STEP 7 📦" to "পণ্য ইনভেন্টরি থেকে গুছিয়ে Pack করবে।",
        "STEP 8 🚚" to "Delivery Man-এর কাছে Assign করবে।",
        "STEP 9 📱" to "1️⃣ Picked Up: Delivery Man দোকান থেকে ব্যাগ গ্রহণ করবে।",
        "STEP 10 🏠" to "2️⃣ On The Way: Customer-এর ঠিকানার উদ্দেশ্যে রওনা হবে।",
        "STEP 11 ✅" to "3️⃣ Delivered: কাস্টমারকে পণ্য বুঝিয়ে দিয়ে DELIVERED মার্ক করবে।"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = RiderPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("🔄 সম্পূর্ণ ১১-ধাপের প্রক্রিয়া", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.height(380.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(steps) { (badge, desc) ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (badge.contains("8") || badge.contains("9") || badge.contains("10") || badge.contains("11")) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, if (badge.contains("8") || badge.contains("9") || badge.contains("10") || badge.contains("11")) RiderPrimary.copy(alpha = 0.3f) else Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = badge,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                color = RiderPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = desc,
                                fontSize = 11.sp,
                                color = RiderDark,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = RiderPrimary)
            ) {
                Text("বুঝেছি")
            }
        }
    )
}
