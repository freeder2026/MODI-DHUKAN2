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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DeliveryManEntity
import com.example.data.local.entity.OrderEntity
import com.example.ui.BazarViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminOrdersScreen(
    viewModel: BazarViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    val deliveryMen by viewModel.adminDeliveryMen.collectAsState()

    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val filterLabels = listOf("সব অর্ডার", "⏳ পেন্ডিং", "⚙️ প্রসেসিং", "🚚 ডেলিভারি", "✅ সম্পন্ন", "❌ বাতিল")

    var orderForDeliveryAssignment by remember { mutableStateOf<OrderEntity?>(null) }
    var orderToCancel by remember { mutableStateOf<OrderEntity?>(null) }

    val filteredOrders = orders.filter { order ->
        when (selectedFilterIndex) {
            1 -> order.status.contains("Pending") || order.status.contains("নতুন")
            2 -> order.status.contains("প্রসেসিং") || order.status.contains("Processing")
            3 -> order.status.contains("ডেলিভারি") || order.status.contains("Delivery")
            4 -> order.status.contains("সম্পন্ন") || order.status.contains("Delivered")
            5 -> order.status.contains("বাতিল") || order.status.contains("Cancelled")
            else -> true
        }
    }

    Box(modifier = modifier.fillMaxSize().background(AdminCanvas)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Status filters
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterLabels.size) { index ->
                    FilterChip(
                        text = filterLabels[index],
                        isSelected = selectedFilterIndex == index,
                        onClick = { selectedFilterIndex = index }
                    )
                }
            }

            // Orders list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 30.dp)
            ) {
                if (filteredOrders.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("এই ক্যাটাগরিতে কোনো অর্ডার পাওয়া যায়নি", color = Color(0xFF64748B))
                        }
                    }
                } else {
                    items(filteredOrders, key = { it.orderId }) { order ->
                        AdminOrderCard(
                            order = order,
                            onConfirm = { viewModel.confirmOrder(order.orderId) },
                            onProcess = { viewModel.processOrder(order.orderId) },
                            onAssignDelivery = { orderForDeliveryAssignment = order },
                            onMarkDelivered = { viewModel.markOrderDelivered(order.orderId) },
                            onCancel = { orderToCancel = order }
                        )
                    }
                }
            }
        }

        // Delivery assignment dialog
        orderForDeliveryAssignment?.let { order ->
            AssignDeliveryDialog(
                order = order,
                deliveryMen = deliveryMen,
                onDismiss = { orderForDeliveryAssignment = null },
                onAssign = { deliveryManName ->
                    viewModel.assignDeliveryManToOrder(order.orderId, deliveryManName)
                    orderForDeliveryAssignment = null
                }
            )
        }

        // Cancel order dialog
        orderToCancel?.let { order ->
            AlertDialog(
                onDismissRequest = { orderToCancel = null },
                title = { Text("অর্ডার বাতিল করবেন?", fontWeight = FontWeight.Bold) },
                text = { Text("অর্ডার #${order.orderId} বাতিল করা হলে কাস্টমারকে জানানো হবে।") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.cancelOrder(order.orderId)
                            orderToCancel = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("হ্যাঁ, বাতিল করুন")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { orderToCancel = null }) { Text("না") }
                }
            )
        }
    }
}

@Composable
fun AdminOrderCard(
    order: OrderEntity,
    onConfirm: () -> Unit,
    onProcess: () -> Unit,
    onAssignDelivery: () -> Unit,
    onMarkDelivered: () -> Unit,
    onCancel: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormatter.format(Date(order.timestamp))

    val (statusBg, statusTextColor) = when {
        order.status.contains("বাতিল") -> Color(0xFFFEF2F2) to Color(0xFFDC2626)
        order.status.contains("সম্পন্ন") || order.status.contains("Delivered") -> Color(0xFFF0FDF4) to Color(0xFF16A34A)
        order.status.contains("ডেলিভারি") || order.status.contains("Delivery") -> Color(0xFFEFF6FF) to Color(0xFF2563EB)
        order.status.contains("প্রসেসিং") || order.status.contains("Processing") -> Color(0xFFFDF4FF) to Color(0xFF9333EA)
        else -> Color(0xFFFEFCE8) to Color(0xFFD97706)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AdminBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_order_card_${order.orderId}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: ID & Status
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
                        color = AdminDark
                    )
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusBg,
                    border = BorderStroke(1.dp, statusTextColor.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = order.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusTextColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Customer details
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = AdminPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(order.recipientName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AdminDark)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(order.recipientPhone, fontSize = 12.sp, color = Color(0xFF059669), fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(order.deliveryAddress, fontSize = 11.sp, color = Color(0xFF475569))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Items breakdown
            Text(
                text = "আইটেম: ${order.itemsSummary}",
                fontSize = 12.sp,
                color = AdminDark,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Price & Payment Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.paymentMethod} • ${order.deliveryMethod}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "মোট: ৳${order.finalTotal.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AdminPrimary
                )
            }

            if (order.deliveryMan != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ডেলিভারিম্যান: ${order.deliveryMan}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2563EB)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions for updating status
            if (!order.status.contains("সম্পন্ন") && !order.status.contains("বাতিল")) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (order.status.contains("Pending") || order.status.contains("নতুন")) {
                        item {
                            Button(
                                onClick = onConfirm,
                                colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("কনফার্ম করুন", fontSize = 11.sp)
                            }
                        }
                    }

                    if (order.status.contains("কনফার্মড") || order.status.contains("Confirmed") || order.status.contains("Pending")) {
                        item {
                            OutlinedButton(
                                onClick = onProcess,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF9333EA)),
                                border = BorderStroke(1.dp, Color(0xFF9333EA).copy(alpha = 0.5f)),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("প্রসেসিং শুরু", fontSize = 11.sp)
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = onAssignDelivery,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.DeliveryDining, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ডেলিভারিম্যান ন্যস্ত", fontSize = 11.sp)
                        }
                    }

                    item {
                        Button(
                            onClick = onMarkDelivered,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("সম্পন্ন করুন", fontSize = 11.sp)
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = onCancel,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("বাতিল", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AssignDeliveryDialog(
    order: OrderEntity,
    deliveryMen: List<DeliveryManEntity>,
    onDismiss: () -> Unit,
    onAssign: (String) -> Unit
) {
    var selectedDeliveryMan by remember { mutableStateOf(deliveryMen.firstOrNull()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ডেলিভারিম্যান অ্যাসাইন করুন", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "অর্ডার #${order.orderId} পৌঁছে দেওয়ার জন্য রাইডার নির্বাচন করুন:",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                deliveryMen.forEach { man ->
                    Card(
                        onClick = { selectedDeliveryMan = man },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedDeliveryMan?.id == man.id) AdminPrimary.copy(alpha = 0.1f) else Color.White
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (selectedDeliveryMan?.id == man.id) AdminPrimary else AdminBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val isAvailable = man.activeDeliveries < 3
                            Column {
                                Text(man.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AdminDark)
                                Text("${man.phone} • এলাকা: ${man.area}", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isAvailable) Color(0xFFF0FDF4) else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = if (isAvailable) "ফ্রি" else "${man.activeDeliveries} টি কাজ",
                                    fontSize = 10.sp,
                                    color = if (isAvailable) Color(0xFF16A34A) else Color(0xFF64748B),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedDeliveryMan?.let { onAssign(it.name) }
                },
                enabled = selectedDeliveryMan != null,
                colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary)
            ) {
                Text("অ্যাসাইন করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল") }
        }
    )
}
