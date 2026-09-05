package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BazarViewModel
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.FlashBadgeRed
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.GreenPrimaryContainer

@Composable
fun AccountScreen(
    viewModel: BazarViewModel,
    onNavigateToAddresses: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToWishlist: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val addresses by viewModel.addresses.collectAsState()

    var showLoginDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    var phoneInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "কাস্টমার একাউন্ট (My Account)",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 19.sp
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Card (Mobile দিয়ে Registration / Login)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = GreenPrimaryContainer,
                                shape = CircleShape,
                                modifier = Modifier.size(60.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = GreenPrimary,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                if (userProfile != null) {
                                    Text(
                                        text = userProfile!!.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "মোবাইল: ${userProfile!!.phone}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (userProfile!!.email.isNotBlank()) {
                                        Text(
                                            text = userProfile!!.email,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "লগইন করা নেই",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "মোবাইল নম্বর দিয়ে রেজিস্ট্রেশন বা লগইন করুন",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (userProfile != null) {
                                IconButton(onClick = {
                                    nameInput = userProfile!!.name
                                    phoneInput = userProfile!!.phone
                                    emailInput = userProfile!!.email
                                    showEditProfileDialog = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "প্রোফাইল পরিবর্তন",
                                        tint = GreenPrimary
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { showLoginDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("লগইন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Quick Stats Row (Orders, Wishlist, Addresses)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AccountStatCard(
                        title = "মোট অর্ডার",
                        count = "${orders.size}",
                        icon = Icons.Default.ReceiptLong,
                        color = GreenPrimary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToOrders() }
                    )
                    AccountStatCard(
                        title = "উইশলিস্ট",
                        count = "${wishlistItems.size}",
                        icon = Icons.Default.Favorite,
                        color = FlashBadgeRed,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToWishlist() }
                    )
                    AccountStatCard(
                        title = "ঠিকানা",
                        count = "${addresses.size}",
                        icon = Icons.Default.LocationOn,
                        color = AmberSecondary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToAddresses() }
                    )
                }
            }

            // Options List (Multiple Address Save, Order History, Wishlist)
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        AccountMenuItem(
                            icon = Icons.Default.ReceiptLong,
                            title = "অর্ডার হিস্ট্রি (Order History)",
                            subtitle = "পূর্ববর্তী ও বর্তমান অর্ডারের তথ্য দেখুন",
                            badgeText = if (orders.isNotEmpty()) "${orders.size} টি" else null,
                            onClick = onNavigateToOrders,
                            testTag = "menu_order_history"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        AccountMenuItem(
                            icon = Icons.Default.Favorite,
                            title = "পছন্দের পণ্য (Wishlist)",
                            subtitle = "আপনার সেভ করা পছন্দের পণ্যের তালিকা",
                            badgeText = if (wishlistItems.isNotEmpty()) "${wishlistItems.size} টি" else null,
                            onClick = onNavigateToWishlist,
                            testTag = "menu_wishlist"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        AccountMenuItem(
                            icon = Icons.Default.LocationOn,
                            title = "সংরক্ষিত ঠিকানা (Saved Addresses)",
                            subtitle = "একাধিক ডেলিভারি ঠিকানা যোগ ও পরিবর্তন",
                            badgeText = "${addresses.size} টি",
                            onClick = onNavigateToAddresses,
                            testTag = "menu_addresses"
                        )
                    }
                }
            }

            // Customer Support & Guarantee
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        AccountMenuItem(
                            icon = Icons.Default.HeadsetMic,
                            title = "গ্রাহক সেবা (Customer Support)",
                            subtitle = "হটলাইন: 09612-000000 (সকাল ৯টা - রাত ১০টা)",
                            onClick = {}
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        AccountMenuItem(
                            icon = Icons.Default.VerifiedUser,
                            title = "শর্তাবলী ও নিরাপত্তা নীতি",
                            subtitle = "১০০% নিরাপদ ও মানসম্মত পণ্যের নিশ্চয়তা",
                            onClick = {}
                        )
                    }
                }
            }

            // Logout / Login Action
            item {
                if (userProfile != null) {
                    OutlinedButton(
                        onClick = { viewModel.logout() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("logout_button")
                    ) {
                        Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = ErrorRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("লগআউট করুন (Log out)", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { showLoginDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_trigger_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Login, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("মোবাইল দিয়ে লগইন / রেজিস্ট্রেশন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Login / Register Dialog (Mobile দিয়ে Registration / Login)
    if (showLoginDialog) {
        AlertDialog(
            onDismissRequest = { showLoginDialog = false },
            title = {
                Text(
                    text = "মোবাইল লগইন / রেজিস্ট্রেশন",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "আপনার ১১ ডিজিটের মোবাইল নম্বর ও নাম লিখুন:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("মোবাইল নম্বর (যেমন: 017xxxxxxxx)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("আপনার পূর্ণ নাম") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (phoneInput.isNotBlank()) {
                            val finalName = if (nameInput.isBlank()) "সম্মানিত গ্রাহক" else nameInput
                            viewModel.login(phoneInput, finalName)
                            showLoginDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("নিশ্চিত করুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(text = "প্রোফাইল আপডেট", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("নাম") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("মোবাইল") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("ইমেইল (ঐচ্ছিক)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(nameInput, phoneInput, emailInput)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun AccountStatCard(
    title: String,
    count: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = count, fontWeight = FontWeight.Black, fontSize = 16.sp)
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AccountMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badgeText: String? = null,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = GreenPrimaryContainer.copy(alpha = 0.5f),
            shape = CircleShape,
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (badgeText != null) {
            Surface(
                color = GreenPrimaryContainer,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = badgeText,
                    color = GreenPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
