package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.data.local.entity.CartItemEntity
import com.example.ui.BazarViewModel
import com.example.ui.DeliveryMethod
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.GreenPrimaryContainer
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: BazarViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    onNavigateToAuth: () -> Unit = {}
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val appliedDiscount by viewModel.appliedDiscount.collectAsState()
    val couponMessage by viewModel.couponMessage.collectAsState()
    val deliveryMethod by viewModel.selectedDeliveryMethod.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isCustomerLoggedIn = userProfile?.isLoggedIn == true && !userProfile?.phone.isNullOrBlank()

    var promoInput by remember { mutableStateOf("") }
    var showGuestLoginPromptDialog by remember { mutableStateOf(false) }

    val deliveryCharge = if (subtotal >= 1000.0 && deliveryMethod == DeliveryMethod.REGULAR) 0.0 else deliveryMethod.fee
    val finalTotal = (subtotal + deliveryCharge - appliedDiscount).coerceAtLeast(0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "শপিং কার্ট",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${cartItems.sumOf { it.quantity }} টি পণ্য যোগ করা হয়েছে",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.clearCart() },
                            modifier = Modifier.testTag("clear_cart_button")
                        ) {
                            Text(
                                text = "সব মুছুন",
                                color = Color(0xFFEF4444),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBFCFF))
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "সর্বমোট প্রদেয়:",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "৳${finalTotal.toInt()}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF4A6741),
                                        fontSize = 22.sp
                                    )
                                )
                            }

                            Button(
                                onClick = {
                                    if (isCustomerLoggedIn) {
                                        onNavigateToCheckout()
                                    } else {
                                        showGuestLoginPromptDialog = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6741)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("proceed_to_checkout_btn")
                            ) {
                                Text(
                                    text = "চেকআউট করুন",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            // Empty Cart State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    color = GreenPrimaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(90.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.RemoveShoppingCart,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(46.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "আপনার কার্টটি বর্তমানে খালি!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "দৈনন্দিন বাজার সদাই, চাল, ডাল, তেল বা বিস্কুট যুক্ত করতে হোমপেজে যান।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onNavigateToHome,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("browse_products_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("পণ্য কেনাকাটা শুরু করুন", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Guest Exclusive Offer Card
                if (!isCustomerLoggedIn) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("🎁 নতুন গ্রাহক ছাড় অফার!", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFB45309))
                                    Text("লগইন বা রেজিস্ট্রেশন করলেই প্রথম অর্ডারে নিশ্চিত ছাড়!", fontSize = 11.sp, color = Color(0xFF92400E))
                                }
                                Button(
                                    onClick = onNavigateToAuth,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("লগইন / সাইনআপ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Free Delivery Progress Banner (৳১০০০ এ ফ্রি ডেলিভারি)
                item {
                    val progress = (subtotal / 1000.0).toFloat().coerceIn(0f, 1f)
                    val remaining = (1000.0 - subtotal).coerceAtLeast(0.0)

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (progress >= 1f) GreenPrimaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (progress >= 1f) Icons.Default.CheckCircle else Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (progress >= 1f) {
                                        "অভিনন্দন! আপনি ফ্রি রেগুলার ডেলিভারি পাচ্ছেন!"
                                    } else {
                                        "আর মাত্র ৳${remaining.toInt()} যোগ করলেই ফ্রি ডেলিভারি!"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (progress >= 1f) GreenPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                color = GreenPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                            )
                        }
                    }
                }

                // Cart Items List
                items(cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = { viewModel.updateCartQuantity(item.productId, item.quantity + 1) },
                        onDecrease = { viewModel.updateCartQuantity(item.productId, item.quantity - 1) },
                        onDelete = { viewModel.removeFromCart(item.productId) }
                    )
                }

                // Coupon / Promo Code Box
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "ডিসকাউন্ট কুপন (Promo Code):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = promoInput,
                                    onValueChange = { promoInput = it },
                                    placeholder = { Text("যেমন: SHOHOJ50", fontSize = 13.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GreenPrimary
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("coupon_input")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (promoInput.isNotBlank()) {
                                            viewModel.applyCoupon(promoInput)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("apply_coupon_btn")
                                ) {
                                    Text("প্রয়োগ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }

                            if (couponMessage != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = couponMessage.orEmpty(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (appliedDiscount > 0) SuccessGreen else ErrorRed
                                )
                            }
                        }
                    }
                }

                // Price Summary Breakdown (মোট দাম দেখা, Delivery Charge, Discount, Final Total)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "অর্ডার বিবরণী (Order Summary)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Subtotal
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "পণ্যের মোট দাম (Subtotal):",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "৳${subtotal.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Delivery Charge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "ডেলিভারি চার্জ (Delivery Charge):",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (deliveryCharge == 0.0) "ফ্রি (৳০)" else "৳${deliveryCharge.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (deliveryCharge == 0.0) SuccessGreen else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp
                                )
                            }

                            // Discount
                            if (appliedDiscount > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "ডিসকাউন্ট ছাড় (Discount):",
                                        color = SuccessGreen,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "-৳${appliedDiscount.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(10.dp))

                            // Final Total (সর্বমোট)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "সর্বমোট (Final Total):",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                                Text(
                                    text = "৳${finalTotal.toInt()}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = GreenPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showGuestLoginPromptDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showGuestLoginPromptDialog = false },
            title = {
                Text(
                    text = "🔒 অর্ডার করতে লগইন আবশ্যক",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "আপনি বর্তমানে গেস্ট মুডে আছেন। পণ্য ও অফার দেখার সুবিধা থাকলেও অর্ডার প্লেস করার জন্য আপনার একাউন্টে লগইন বা রেজিস্ট্রেশন করতে হবে।",
                        fontSize = 13.sp,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🎁 এখনই রেজিস্ট্রেশন বা লগইন করলে প্রথম অর্ডারে পাবেন নিশ্চিত ছাড় ও দ্রুত হোম ডেলিভারি!",
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
                        showGuestLoginPromptDialog = false
                        onNavigateToAuth()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6741))
                ) {
                    Text("লগইন / রেজিস্টার করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGuestLoginPromptDialog = false }) {
                    Text("পরে করবো")
                }
            }
        )
    }
}

@Composable
fun CartItemRow(
    item: CartItemEntity,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon thumbnail
            Surface(
                color = Color(0xFFEBF2E8),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color(0xFF4A6741),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.banglaName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF0F172A)
                    ),
                    maxLines = 1
                )
                Text(
                    text = item.weightOrVolume,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "৳${item.price.toInt()}",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4A6741),
                        fontSize = 13.sp
                    )
                    Text(
                        text = " × ${item.quantity} = ৳${(item.price * item.quantity).toInt()}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Stepper controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onDecrease() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "কমান",
                        tint = Color(0xFF4A6741),
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = item.quantity.toString(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onIncrease() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "বাড়ান",
                        tint = Color(0xFF4A6741),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Remove Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "মুছে ফেলুন",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
