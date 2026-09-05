package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DeliveryManEntity
import com.example.ui.BazarViewModel
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.GreenPrimaryDark

enum class DashboardRole {
    ADMIN,
    RIDER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardLoginScreen(
    viewModel: BazarViewModel,
    initialRole: DashboardRole = DashboardRole.ADMIN,
    onAdminLoginSuccess: () -> Unit,
    onRiderLoginSuccess: () -> Unit,
    onBackToCustomerHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRole by remember { mutableStateOf(initialRole) }
    val focusManager = LocalFocusManager.current

    // Admin form states
    var adminUsername by remember { mutableStateOf("admin") }
    var adminPassword by remember { mutableStateOf("") }
    var isAdminPasswordVisible by remember { mutableStateOf(false) }
    var adminErrorMsg by remember { mutableStateOf<String?>(null) }

    // Rider form states
    val deliveryMen by viewModel.adminDeliveryMen.collectAsState()
    val activeRider by viewModel.activeDeliveryMan.collectAsState()
    var selectedRiderId by remember { mutableStateOf<String?>(activeRider?.id ?: deliveryMen.firstOrNull()?.id) }
    var riderPhoneInput by remember { mutableStateOf("") }
    var riderPinInput by remember { mutableStateOf("") }
    var riderErrorMsg by remember { mutableStateOf<String?>(null) }
    var showRegisterRiderDialog by remember { mutableStateOf(false) }

    // Synchronize selected rider if delivery list changes
    LaunchedEffect(deliveryMen) {
        if (selectedRiderId == null && deliveryMen.isNotEmpty()) {
            selectedRiderId = deliveryMen.first().id
        }
    }

    val primaryThemeColor = if (selectedRole == DashboardRole.ADMIN) GreenPrimary else Color(0xFF2563EB)
    val darkThemeColor = if (selectedRole == DashboardRole.ADMIN) Color(0xFF0F172A) else Color(0xFF0B192C)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (selectedRole == DashboardRole.ADMIN) "অ্যাডমিন লগইন" else "ডেলিভারি রাইডার লগইন",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "সহজ বাজার ড্যাশবোর্ড নিরাপত্তা পোর্টাল",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackToCustomerHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Surface(
                        onClick = onBackToCustomerHome,
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "গ্রাহক অ্যাপ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkThemeColor)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Role Toggle Tabs (Admin vs Delivery Rider)
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        // Admin Role Tab
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedRole == DashboardRole.ADMIN) GreenPrimary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedRole = DashboardRole.ADMIN
                                    adminErrorMsg = null
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = if (selectedRole == DashboardRole.ADMIN) Color.White else Color(0xFF64748B),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "🛡️ অ্যাডমিন প্যানেল",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedRole == DashboardRole.ADMIN) Color.White else Color(0xFF475569)
                                )
                            }
                        }

                        // Delivery Rider Role Tab
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedRole == DashboardRole.RIDER) Color(0xFF2563EB) else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedRole = DashboardRole.RIDER
                                    riderErrorMsg = null
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TwoWheeler,
                                    contentDescription = null,
                                    tint = if (selectedRole == DashboardRole.RIDER) Color.White else Color(0xFF64748B),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "🚚 রাইডার প্যানেল",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedRole == DashboardRole.RIDER) Color.White else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Banner with instructions
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedRole == DashboardRole.ADMIN) Color(0xFF0F172A) else Color(0xFF0F172A)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (selectedRole == DashboardRole.ADMIN) GreenPrimary else Color(0xFF2563EB),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (selectedRole == DashboardRole.ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.TwoWheeler,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (selectedRole == DashboardRole.ADMIN) "অ্যাডমিন ড্যাশবোর্ড অ্যাক্সেস" else "রাইডার ডেলিভারি ড্যাশবোর্ড",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (selectedRole == DashboardRole.ADMIN)
                                    "স্টোর পরিচালনা, ইনভেন্টরি ও সেলস রিপোর্ট দেখতে লগইন করুন।"
                                else
                                    "অর্ডার পিকআপ, লাইভ ডেলিভারি ও ক্যাশ রিপোর্ট দেখতে রাইডার আইডি সিলেক্ট করুন।",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // 3. Conditional Content based on Selected Role
            if (selectedRole == DashboardRole.ADMIN) {
                // ==========================================
                // ADMIN LOGIN FORM
                // ==========================================
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "🛡️ অ্যাডমিন ক্রেডেনশিয়াল দিন",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A)
                            )

                            // Error Message Banner
                            if (adminErrorMsg != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFEF2F2),
                                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = adminErrorMsg!!,
                                        color = Color(0xFFDC2626),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            // Admin Username
                            OutlinedTextField(
                                value = adminUsername,
                                onValueChange = {
                                    adminUsername = it
                                    adminErrorMsg = null
                                },
                                label = { Text("ইউজারনেম / ইমেইল") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_login_username"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            // Admin Password / PIN
                            OutlinedTextField(
                                value = adminPassword,
                                onValueChange = {
                                    adminPassword = it
                                    adminErrorMsg = null
                                },
                                label = { Text("অ্যাডমিন পিন / পাসওয়ার্ড (PIN)") },
                                placeholder = { Text("যেমন: 1234") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isAdminPasswordVisible = !isAdminPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isAdminPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password",
                                            tint = Color(0xFF64748B)
                                        )
                                    }
                                },
                                visualTransformation = if (isAdminPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        if (viewModel.loginAdmin(adminPassword)) {
                                            onAdminLoginSuccess()
                                        } else {
                                            adminErrorMsg = "ভুল পাসওয়ার্ড! ডেমো পিন: 1234 ব্যবহার করুন।"
                                        }
                                    }
                                ),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_login_password"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            // Demo Credentials One-Tap Chip
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF0FDF4),
                                border = BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        adminUsername = "admin"
                                        adminPassword = "1234"
                                        adminErrorMsg = null
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Key, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "দ্রুত ডেমো পিন পূরণ করুন (PIN: 1234)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GreenPrimaryDark
                                        )
                                    }
                                    Text(
                                        text = "ক্লিক করুন",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Login Button
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    if (adminPassword.isBlank()) {
                                        adminErrorMsg = "অনুগ্রহ করে অ্যাডমিন পিন বা পাসওয়ার্ড দিন।"
                                    } else if (viewModel.loginAdmin(adminPassword)) {
                                        onAdminLoginSuccess()
                                    } else {
                                        adminErrorMsg = "ভুল পাসওয়ার্ড! ডেমো পিন: 1234 ব্যবহার করুন।"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("admin_login_submit_button")
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "অ্যাডমিন ড্যাশবোর্ডে প্রবেশ করুন",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // ==========================================
                // DELIVERY RIDER LOGIN FORM
                // ==========================================
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🚚 রাইডার প্রোফাইল সিলেক্ট করুন",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A)
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEFF6FF),
                                    modifier = Modifier.clickable { showRegisterRiderDialog = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "+ নতুন রাইডার",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2563EB)
                                        )
                                    }
                                }
                            }

                            if (riderErrorMsg != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFEF2F2),
                                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = riderErrorMsg!!,
                                        color = Color(0xFFDC2626),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            // List of Registered Delivery Riders
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                deliveryMen.forEach { rider ->
                                    val isSelected = selectedRiderId == rider.id
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                                        border = BorderStroke(
                                            1.5.dp,
                                            if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedRiderId = rider.id
                                                riderErrorMsg = null
                                            }
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
                                                    shape = CircleShape,
                                                    color = if (isSelected) Color(0xFF2563EB) else Color(0xFFCBD5E1),
                                                    modifier = Modifier.size(36.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = Icons.Default.TwoWheeler,
                                                            contentDescription = null,
                                                            tint = if (isSelected) Color.White else Color(0xFF475569),
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = rider.name,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = Color(0xFF0F172A)
                                                    )
                                                    Text(
                                                        text = "📞 ${rider.phone} • ${rider.area}",
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF64748B)
                                                    )
                                                }
                                            }

                                            if (isSelected) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color(0xFF2563EB),
                                                    modifier = Modifier.size(22.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = Color.White,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Rider Login Button
                            Button(
                                onClick = {
                                    val matchedRider = deliveryMen.find { it.id == selectedRiderId } ?: deliveryMen.firstOrNull()
                                    if (matchedRider != null) {
                                        viewModel.loginDeliveryMan(matchedRider)
                                        onRiderLoginSuccess()
                                    } else {
                                        riderErrorMsg = "অনুগ্রহ করে একজন রাইডার সিলেক্ট করুন।"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("rider_login_submit_button")
                            ) {
                                Icon(Icons.Default.TwoWheeler, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "রাইডার হিসেবে লগইন করুন",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // 4. Security & Privacy Notice Card
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "এটি একটি সুরক্ষিত কন্ট্রোল পোর্টাল। শুধুমাত্র অনুমোদিত অ্যাডমিন ও ডেলিভারি রাইডারদের জন্য প্রযোজ্য।",
                            fontSize = 11.sp,
                            color = Color(0xFF475569),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }

    // Register New Rider Dialog
    if (showRegisterRiderDialog) {
        var newRiderName by remember { mutableStateOf("") }
        var newRiderPhone by remember { mutableStateOf("") }
        var newRiderArea by remember { mutableStateOf("") }
        var regError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showRegisterRiderDialog = false },
            title = {
                Text(
                    text = "➕ নতুন রাইডার রেজিস্ট্রেশন",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (regError != null) {
                        Text(text = regError!!, color = Color.Red, fontSize = 11.sp)
                    }
                    OutlinedTextField(
                        value = newRiderName,
                        onValueChange = { newRiderName = it },
                        label = { Text("রাইডারের নাম") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newRiderPhone,
                        onValueChange = { newRiderPhone = it },
                        label = { Text("মোবাইল নম্বর") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newRiderArea,
                        onValueChange = { newRiderArea = it },
                        label = { Text("ডেলিভারি এরিয়া / জোন") },
                        placeholder = { Text("যেমন: ধানমন্ডি ও কলাবাগান") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newRiderName.isBlank() || newRiderPhone.isBlank()) {
                            regError = "নাম ও মোবাইল নম্বর পূরণ করুন।"
                        } else {
                            viewModel.registerNewDeliveryMan(
                                name = newRiderName.trim(),
                                phone = newRiderPhone.trim(),
                                area = if (newRiderArea.isBlank()) "ঢাকা শহর" else newRiderArea.trim()
                            )
                            showRegisterRiderDialog = false
                            onRiderLoginSuccess()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("রেজিস্টার ও লগইন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegisterRiderDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
