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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CouponEntity
import com.example.ui.BazarViewModel

@Composable
fun AdminCouponsScreen(
    viewModel: BazarViewModel,
    modifier: Modifier = Modifier
) {
    val coupons by viewModel.adminCoupons.collectAsState()
    val products by viewModel.adminProducts.collectAsState()
    val flashSaleProducts = products.filter { it.isFlashSale }

    var showAddCouponDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(AdminCanvas)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 80.dp)
        ) {
            // Flash sale summary banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFDC2626),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "ফ্ল্যাশ সেল অফার",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF991B1B)
                                )
                                Text(
                                    text = "বর্তমানে ${flashSaleProducts.size} টি পণ্য ফ্ল্যাশ সেলে লাইভ আছে",
                                    fontSize = 12.sp,
                                    color = Color(0xFFB91C1C)
                                )
                            }
                        }
                    }
                }
            }

            // Coupon codes section header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ডিসকাউন্ট কুপন কোড (${coupons.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AdminDark
                    )
                }
            }

            // Coupon Cards
            items(coupons, key = { it.code }) { coupon ->
                CouponManagementCard(
                    coupon = coupon,
                    onToggleActive = { isActive ->
                        viewModel.toggleAdminCouponStatus(coupon.code, isActive)
                    },
                    onDelete = {
                        viewModel.deleteAdminCoupon(coupon.code)
                    }
                )
            }
        }

        // FAB to add coupon
        FloatingActionButton(
            onClick = { showAddCouponDialog = true },
            containerColor = AdminPrimary,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("admin_add_coupon_fab")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("নতুন কুপন", fontWeight = FontWeight.Bold)
            }
        }

        // Add Coupon Dialog
        if (showAddCouponDialog) {
            AddCouponDialog(
                onDismiss = { showAddCouponDialog = false },
                onAdd = { code, type, value, minOrder, desc ->
                    viewModel.addAdminCoupon(
                        code = code,
                        discountType = type,
                        discountValue = value,
                        minOrderAmount = minOrder,
                        description = desc
                    )
                    showAddCouponDialog = false
                }
            )
        }
    }
}

@Composable
fun CouponManagementCard(
    coupon: CouponEntity,
    onToggleActive: (Boolean) -> Unit,
    onDelete: () -> Unit
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (coupon.isActive) AdminPrimary.copy(alpha = 0.12f) else Color(0xFFF1F5F9),
                        modifier = Modifier.padding(end = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = coupon.code,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = if (coupon.isActive) AdminPrimary else Color(0xFF64748B)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = if (coupon.discountType == "PERCENTAGE") "${coupon.discountValue.toInt()}% ছাড়" else "৳${coupon.discountValue.toInt()} নগদ ছাড়",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AdminDark
                        )
                        Text(
                            text = "নূন্যতম অর্ডার: ৳${coupon.minOrderAmount.toInt()}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Switch(
                    checked = coupon.isActive,
                    onCheckedChange = onToggleActive,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AdminPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${coupon.description} • মেয়াদ: ${coupon.expiryDate}",
                fontSize = 11.sp,
                color = Color(0xFF475569)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(30.dp)
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

@Composable
fun AddCouponDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, Double, Double, String) -> Unit
) {
    var codeText by remember { mutableStateOf("") }
    var discountTypeText by remember { mutableStateOf("PERCENTAGE") } // PERCENTAGE or FIXED
    var valueText by remember { mutableStateOf("") }
    var minOrderText by remember { mutableStateOf("500") }
    var descText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন কুপন কোড যোগ করুন", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = codeText,
                    onValueChange = { codeText = it.uppercase() },
                    label = { Text("কুপন কোড (যেমন: EID20, BAZAR50) *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("ছাড়ের ধরণ নির্বাচন করুন:", fontSize = 12.sp, color = Color(0xFF64748B))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        onClick = { discountTypeText = "PERCENTAGE" },
                        shape = RoundedCornerShape(10.dp),
                        color = if (discountTypeText == "PERCENTAGE") AdminPrimary else Color(0xFFF1F5F9),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(
                                "শতকরা (%)",
                                color = if (discountTypeText == "PERCENTAGE") Color.White else AdminDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Surface(
                        onClick = { discountTypeText = "FIXED" },
                        shape = RoundedCornerShape(10.dp),
                        color = if (discountTypeText == "FIXED") AdminPrimary else Color(0xFFF1F5F9),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(
                                "স্থির টাকা (৳)",
                                color = if (discountTypeText == "FIXED") Color.White else AdminDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = valueText,
                    onValueChange = { valueText = it },
                    label = { Text(if (discountTypeText == "PERCENTAGE") "ছাড়ের হার (%) যেমন: 10, 15" else "ছাড়ের পরিমাণ (৳) যেমন: 50, 100") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = minOrderText,
                    onValueChange = { minOrderText = it },
                    label = { Text("নূন্যতম অর্ডার মূল্য (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descText,
                    onValueChange = { descText = it },
                    label = { Text("বিবরণ (যেমন: ঈদ স্পেশাল ১০% ছাড়)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = valueText.toDoubleOrNull() ?: 0.0
                    val minOrder = minOrderText.toDoubleOrNull() ?: 0.0
                    if (codeText.isNotBlank() && value > 0) {
                        onAdd(codeText, discountTypeText, value, minOrder, descText)
                    }
                },
                enabled = codeText.isNotBlank() && (valueText.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary)
            ) {
                Text("কুপন তৈরি করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল") }
        }
    )
}
