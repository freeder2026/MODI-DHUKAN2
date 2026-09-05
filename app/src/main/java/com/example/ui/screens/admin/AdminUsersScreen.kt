package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MarkunreadMailbox
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PasswordResetRequestEntity
import com.example.data.local.entity.RegisteredUserEntity
import com.example.ui.BazarViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminUsersScreen(viewModel: BazarViewModel) {
    val users by viewModel.allRegisteredUsers.collectAsState()
    val resetRequests by viewModel.allPasswordResetRequests.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedUserForPasswordChange by remember { mutableStateOf<RegisteredUserEntity?>(null) }
    var selectedRequestForResolution by remember { mutableStateOf<PasswordResetRequestEntity?>(null) }
    var newDemoPasswordInput by remember { mutableStateOf("Bazar@2026") }
    var userToDelete by remember { mutableStateOf<RegisteredUserEntity?>(null) }

    val filteredUsers = users.filter {
        searchQuery.isBlank() ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true) ||
                it.presentDistrict.contains(searchQuery, ignoreCase = true)
    }

    val pendingRequests = resetRequests.filter { it.status == "PENDING" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Overview Summary Statistics
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = AdminPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.People, contentDescription = null, tint = AdminPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("👥 কাস্টমার ও ইউজার কন্ট্রোল প্যানেল", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AdminTextDark)
                            Text("নিবন্ধিত সকল গ্রাহকের তথ্য, ঠিকানা ও অ্যাক্সেস নিয়ন্ত্রণ", fontSize = 11.sp, color = AdminTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AdminStatSmallCard(
                            label = "মোট ইউজার",
                            count = "${users.size} জন",
                            bgColor = Color(0xFFEFF6FF),
                            textColor = Color(0xFF1D4ED8),
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatSmallCard(
                            label = "সক্রিয় ইউজার",
                            count = "${users.count { it.status == "ACTIVE" }} জন",
                            bgColor = Color(0xFFF0FDF4),
                            textColor = Color(0xFF15803D),
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatSmallCard(
                            label = "রিসেট রিকোয়েস্ট",
                            count = "${pendingRequests.size} টি",
                            bgColor = if (pendingRequests.isNotEmpty()) Color(0xFFFEF2F2) else Color(0xFFF8FAFC),
                            textColor = if (pendingRequests.isNotEmpty()) Color(0xFFDC2626) else AdminTextDark,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Pending Password Reset Requests Section (if any)
        if (pendingRequests.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "⚠️ পাসওয়ার্ড রিসেট রিকোয়েস্ট (${pendingRequests.size} টি পেন্ডিং)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF92400E)
                            )
                        }
                        Text(
                            text = "গ্রাহক পাসওয়ার্ড ভুলে যাওয়ায় নতুন ডেমো পাসওয়ার্ড চেয়েছেন। নিচের বাটন চেপে দ্রুত নতুন পাসওয়ার্ড পাঠান।",
                            fontSize = 11.sp,
                            color = Color(0xFFB45309),
                            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                        )

                        pendingRequests.forEach { req ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFFCD34D)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(req.userName.ifBlank { "কাস্টমার" }, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AdminTextDark)
                                        Text("ফোন/ইমেইল: ${req.userPhoneOrEmail}", fontSize = 11.sp, color = AdminTextMuted)
                                        Text(
                                            "সময়: ${SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(req.requestTimestamp))}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            selectedRequestForResolution = req
                                            newDemoPasswordInput = "Bazar@" + (1000..9999).random()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("ডেমো পাসওয়ার্ড দিন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("নাম, ফোন, ইমেইল অথবা জেলা দিয়ে খুঁজুন...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AdminPrimary) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = AdminPrimary,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_admin_search_users")
            )
        }

        // Users List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "নিবন্ধিত কাস্টমার তালিকা (${filteredUsers.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AdminTextDark
                )
                Text(
                    text = "অ্যাকশন: অনুমোদন / স্থগিত / ডিলিট / পাসওয়ার্ড",
                    fontSize = 10.sp,
                    color = AdminTextMuted
                )
            }
        }

        // Users List
        if (filteredUsers.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("কোনো কাস্টমার পাওয়া যায়নি", color = AdminTextMuted, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(filteredUsers, key = { it.id }) { user ->
                AdminUserCard(
                    user = user,
                    onStatusChange = { newStatus ->
                        viewModel.adminUpdateUserStatus(user.id, newStatus)
                    },
                    onChangePasswordClick = {
                        selectedUserForPasswordChange = user
                        newDemoPasswordInput = "Bazar@" + (1000..9999).random()
                    },
                    onDeleteClick = {
                        userToDelete = user
                    }
                )
            }
        }
    }

    // Dialog: Send Demo Password for Reset Request
    if (selectedRequestForResolution != null) {
        val req = selectedRequestForResolution!!
        AlertDialog(
            onDismissRequest = { selectedRequestForResolution = null },
            title = { Text("🔑 নতুন ডেমো পাসওয়ার্ড ইস্যু", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("গ্রাহক: ${req.userName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("ফোন/ইমেইল: ${req.userPhoneOrEmail}", fontSize = 12.sp, color = AdminTextMuted)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("নতুন ডেমো পাসওয়ার্ড লিখুন বা জেনারেট করা পাসওয়ার্ড ব্যবহার করুন:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newDemoPasswordInput,
                        onValueChange = { newDemoPasswordInput = it },
                        label = { Text("ডেমো পাসওয়ার্ড") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminResolveResetRequest(req.id, newDemoPasswordInput, req.userPhoneOrEmail)
                        selectedRequestForResolution = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary)
                ) {
                    Text("পাসওয়ার্ড নিশ্চিত করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRequestForResolution = null }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Dialog: Send / Assign Demo Password for User Card
    if (selectedUserForPasswordChange != null) {
        val user = selectedUserForPasswordChange!!
        AlertDialog(
            onDismissRequest = { selectedUserForPasswordChange = null },
            title = { Text("🔑 ডেমো পাসওয়ার্ড পরিবর্তন/প্রেরণ", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("কাস্টমার: ${user.name}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("ফোন: ${user.phone}", fontSize = 12.sp, color = AdminTextMuted)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("নতুন পাসওয়ার্ড নির্ধারণ করুন (গ্রাহক এটি দিয়ে লগইন করতে পারবেন):", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newDemoPasswordInput,
                        onValueChange = { newDemoPasswordInput = it },
                        label = { Text("নতুন ডেমো পাসওয়ার্ড") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminSendDemoPassword(user.id, newDemoPasswordInput)
                        selectedUserForPasswordChange = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary)
                ) {
                    Text("আপডেট করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserForPasswordChange = null }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Dialog: Delete User Confirmation
    if (userToDelete != null) {
        val user = userToDelete!!
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("🗑️ ইউজার ডিলিট নিশ্চিতকরণ", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626)) },
            text = {
                Text("আপনি কি নিশ্চিত যে '${user.name}' (${user.phone}) ইউজারকে সম্পূর্ণভাবে ডিলিট করতে চান? এই প্রক্রিয়াটি অপরিবর্তনীয়।")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminDeleteUser(user.id)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("হ্যাঁ, ডিলিট করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("না, রাখুন")
                }
            }
        )
    }
}

@Composable
fun AdminUserCard(
    user: RegisteredUserEntity,
    onStatusChange: (String) -> Unit,
    onChangePasswordClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_user_card_${user.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: User Info & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = if (user.status == "ACTIVE") Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (user.status == "ACTIVE") Icons.Default.Person else Icons.Default.Block,
                                contentDescription = null,
                                tint = if (user.status == "ACTIVE") Color(0xFF15803D) else Color(0xFFDC2626),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AdminTextDark)
                        Text(user.phone, fontSize = 12.sp, color = AdminPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (user.status == "ACTIVE") Color(0xFFECFDF5) else Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, if (user.status == "ACTIVE") Color(0xFF86EFAC) else Color(0xFFFCA5A5))
                ) {
                    Text(
                        text = if (user.status == "ACTIVE") "🟢 সক্রিয় (Active)" else "🔴 স্থগিত (Blocked)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (user.status == "ACTIVE") Color(0xFF15803D) else Color(0xFFDC2626),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Email & Reg Date
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                if (user.email.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = AdminTextMuted, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(user.email, fontSize = 11.sp, color = AdminTextMuted)
                    }
                } else {
                    Text("ইমেইল দেওয়া হয়নি", fontSize = 11.sp, color = AdminTextMuted)
                }
                Text(
                    text = "নিবন্ধিত: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(user.registeredAt))}",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // Current Password info preview
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = AdminPrimary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("বর্তমান পাসওয়ার্ড: ${user.password}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AdminTextDark)
                    }
                    if (user.tempDemoPassword != null) {
                        Text("অ্যাডমিন ডেমো: ${user.tempDemoPassword}", fontSize = 10.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Toggle Expand Address Details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "▲ ঠিকানা বিবরণ লুকান" else "▼ ৩-ধাপের বিস্তারিত ঠিকানা দেখুন",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminPrimary
                )
                Text("ডাকঘর, থানা, জেলা ও পোস্ট কোড", fontSize = 10.sp, color = AdminTextMuted)
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    // 1. Present Address
                    AddressDisplayBlock(
                        title = "📌 বর্তমান ঠিকানা (Present Address)",
                        villageOrHouse = user.presentAddress,
                        postOffice = user.presentPostOffice,
                        upazila = user.presentUpazila,
                        district = user.presentDistrict,
                        postCode = user.presentPostCode,
                        bgColor = Color(0xFFF0FDF4),
                        borderColor = Color(0xFFBBF7D0)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // 2. Temporary Address
                    if (user.isTempSameAsPresent) {
                        SameAddressNoticeBlock("🏠 অস্থায়ী ঠিকানা: বর্তমান ঠিকানার অনুরূপ (Same as Present)")
                    } else {
                        AddressDisplayBlock(
                            title = "🏠 অস্থায়ী ঠিকানা (Temporary Address)",
                            villageOrHouse = user.tempAddress,
                            postOffice = user.tempPostOffice,
                            upazila = user.tempUpazila,
                            district = user.tempDistrict,
                            postCode = user.tempPostCode,
                            bgColor = Color(0xFFFFFBEB),
                            borderColor = Color(0xFFFDE68A)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 3. Permanent Address
                    if (user.isPermanentSameAsPresent) {
                        SameAddressNoticeBlock("🏡 স্থায়ী ঠিকানা: বর্তমান ঠিকানার অনুরূপ (Same as Present)")
                    } else {
                        AddressDisplayBlock(
                            title = "🏡 স্থায়ী ঠিকানা (Permanent Address)",
                            villageOrHouse = user.permanentAddress,
                            postOffice = user.permanentPostOffice,
                            upazila = user.permanentUpazila,
                            district = user.permanentDistrict,
                            postCode = user.permanentPostCode,
                            bgColor = Color(0xFFEFF6FF),
                            borderColor = Color(0xFFBFDBFE)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            // Admin Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Toggle Button (Keep / Suspend)
                if (user.status == "ACTIVE") {
                    OutlinedButton(
                        onClick = { onStatusChange("BLOCKED") },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("স্থগিত করুন", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { onStatusChange("ACTIVE") },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("অনুমোদন/সক্রিয়", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Send Demo Password Button
                OutlinedButton(
                    onClick = onChangePasswordClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(Icons.Default.LockReset, contentDescription = null, tint = AdminPrimary, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("ডেমো পাসওয়ার্ড", fontSize = 11.sp, color = AdminPrimary, fontWeight = FontWeight.Bold)
                }

                // Delete Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    modifier = Modifier
                        .clickable { onDeleteClick() }
                        .padding(2.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AddressDisplayBlock(
    title: String,
    villageOrHouse: String,
    postOffice: String,
    upazila: String,
    district: String,
    postCode: String,
    bgColor: Color,
    borderColor: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AdminTextDark)
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "ঠিকানা: ${villageOrHouse.ifBlank { "উল্লেখ নেই" }}",
                fontSize = 11.sp,
                color = Color(0xFF334155)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("ডাকঘর: ${postOffice.ifBlank { "-" }}", fontSize = 10.sp, color = Color(0xFF475569))
                Text("থানা/উপজেলা: ${upazila.ifBlank { "-" }}", fontSize = 10.sp, color = Color(0xFF475569))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("জেলা: ${district.ifBlank { "-" }}", fontSize = 10.sp, color = Color(0xFF475569))
                Text("পোস্ট কোড: ${postCode.ifBlank { "-" }}", fontSize = 10.sp, color = Color(0xFF475569))
            }
        }
    }
}

@Composable
fun SameAddressNoticeBlock(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun AdminStatSmallCard(
    label: String,
    count: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = textColor)
            Text(label, fontSize = 9.sp, color = textColor.copy(alpha = 0.8f))
        }
    }
}
