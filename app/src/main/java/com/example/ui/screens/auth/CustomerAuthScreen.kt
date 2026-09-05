package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MarkunreadMailbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BazarViewModel
import com.example.ui.theme.FlashBadgeRed
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.GreenPrimaryContainer
import com.example.ui.theme.GreenPrimaryDark

enum class AuthMode {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerAuthScreen(
    viewModel: BazarViewModel,
    initialMode: AuthMode = AuthMode.LOGIN,
    onAuthSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var currentMode by remember { mutableStateOf(initialMode) }

    // Login Form State
    var loginPhoneOrEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var showLoginPassword by remember { mutableStateOf(false) }
    var loginErrorMsg by remember { mutableStateOf<String?>(null) }
    var isLoggingIn by remember { mutableStateOf(false) }

    // Registration Form State
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var showRegPassword by remember { mutableStateOf(false) }

    // 1. Present Address (বর্তমান ঠিকানা)
    var presAddress by remember { mutableStateOf("") }
    var presPostOffice by remember { mutableStateOf("") }
    var presUpazila by remember { mutableStateOf("") }
    var presDistrict by remember { mutableStateOf("ঢাকা") }
    var presPostCode by remember { mutableStateOf("") }

    // 2. Temporary Address (অস্থায়ী ঠিকানা)
    var isTempSameAsPres by remember { mutableStateOf(true) }
    var tempAddress by remember { mutableStateOf("") }
    var tempPostOffice by remember { mutableStateOf("") }
    var tempUpazila by remember { mutableStateOf("") }
    var tempDistrict by remember { mutableStateOf("ঢাকা") }
    var tempPostCode by remember { mutableStateOf("") }

    // 3. Permanent Address (স্থায়ী ঠিকানা)
    var isPermSameAsPres by remember { mutableStateOf(false) }
    var permAddress by remember { mutableStateOf("") }
    var permPostOffice by remember { mutableStateOf("") }
    var permUpazila by remember { mutableStateOf("") }
    var permDistrict by remember { mutableStateOf("") }
    var permPostCode by remember { mutableStateOf("") }

    var regErrorMsg by remember { mutableStateOf<String?>(null) }
    var isRegistering by remember { mutableStateOf(false) }

    // Forgot Password State
    var resetPhoneOrEmail by remember { mutableStateOf("") }
    var resetStatusMsg by remember { mutableStateOf<String?>(null) }
    var isResetSuccess by remember { mutableStateOf(false) }
    var isSubmittingReset by remember { mutableStateOf(false) }

    val allResetRequests by viewModel.allPasswordResetRequests.collectAsState()
    val allUsers by viewModel.allRegisteredUsers.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentMode) {
                            AuthMode.LOGIN -> "কাস্টমার লগইন (Customer Login)"
                            AuthMode.REGISTER -> "নতুন অ্যাকাউন্ট রেজিস্ট্রেশন"
                            AuthMode.FORGOT_PASSWORD -> "পাসওয়ার্ড পুনরুদ্ধার"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF0F172A)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("auth_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Enticing Welcome & Offers Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), GreenPrimaryDark)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = FlashBadgeRed,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.LocalOffer, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "সহজ বাজার অফার ও ডিসকাউন্ট পোর্টাল",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "লগইন করলেই পাচ্ছেন বিশেষ ছাড় ও দ্রুত অর্ডার সুবিধা",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("কুপন কোড: SHOHOJ50 (৳৫০ ছাড়)", color = Color(0xFFE2E8F0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF59E0B)
                            ) {
                                Text(
                                    text = "🔥 মেগা অফার",
                                    color = Color(0xFF78350F),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Mode Selector Tabs
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                TabRow(
                    selectedTabIndex = currentMode.ordinal,
                    containerColor = Color.White,
                    contentColor = GreenPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[currentMode.ordinal]),
                            color = GreenPrimary,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = currentMode == AuthMode.LOGIN,
                        onClick = { currentMode = AuthMode.LOGIN },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("লগইন", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        },
                        modifier = Modifier.testTag("tab_customer_login")
                    )
                    Tab(
                        selected = currentMode == AuthMode.REGISTER,
                        onClick = { currentMode = AuthMode.REGISTER },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AppRegistration, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("রেজিস্ট্রেশন", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        },
                        modifier = Modifier.testTag("tab_customer_register")
                    )
                    Tab(
                        selected = currentMode == AuthMode.FORGOT_PASSWORD,
                        onClick = { currentMode = AuthMode.FORGOT_PASSWORD },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("রিসেট", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        },
                        modifier = Modifier.testTag("tab_customer_forgot_password")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Content Area based on Mode
            when (currentMode) {
                AuthMode.LOGIN -> {
                    CustomerLoginForm(
                        phoneOrEmail = loginPhoneOrEmail,
                        onPhoneOrEmailChange = { loginPhoneOrEmail = it },
                        password = loginPassword,
                        onPasswordChange = { loginPassword = it },
                        showPassword = showLoginPassword,
                        onToggleShowPassword = { showLoginPassword = !showLoginPassword },
                        errorMsg = loginErrorMsg,
                        isLoading = isLoggingIn,
                        onLoginClick = {
                            loginErrorMsg = null
                            isLoggingIn = true
                            viewModel.loginCustomer(
                                phoneOrEmail = loginPhoneOrEmail,
                                passwordEntered = loginPassword,
                                onSuccess = {
                                    isLoggingIn = false
                                    onAuthSuccess()
                                },
                                onError = { msg ->
                                    isLoggingIn = false
                                    loginErrorMsg = msg
                                }
                            )
                        },
                        onQuickFillDemo = {
                            loginPhoneOrEmail = "01755123456"
                            loginPassword = "user1234"
                            loginErrorMsg = null
                        },
                        onForgotPasswordClick = {
                            resetPhoneOrEmail = loginPhoneOrEmail
                            currentMode = AuthMode.FORGOT_PASSWORD
                        },
                        onGoToRegister = { currentMode = AuthMode.REGISTER }
                    )
                }

                AuthMode.REGISTER -> {
                    CustomerRegistrationForm(
                        name = regName,
                        onNameChange = { regName = it },
                        phone = regPhone,
                        onPhoneChange = { regPhone = it },
                        email = regEmail,
                        onEmailChange = { regEmail = it },
                        password = regPassword,
                        onPasswordChange = { regPassword = it },
                        confirmPassword = regConfirmPassword,
                        onConfirmPasswordChange = { regConfirmPassword = it },
                        showPassword = showRegPassword,
                        onToggleShowPassword = { showRegPassword = !showRegPassword },

                        // Present
                        presAddress = presAddress,
                        onPresAddressChange = { presAddress = it },
                        presPostOffice = presPostOffice,
                        onPresPostOfficeChange = { presPostOffice = it },
                        presUpazila = presUpazila,
                        onPresUpazilaChange = { presUpazila = it },
                        presDistrict = presDistrict,
                        onPresDistrictChange = { presDistrict = it },
                        presPostCode = presPostCode,
                        onPresPostCodeChange = { presPostCode = it },

                        // Temporary
                        isTempSameAsPres = isTempSameAsPres,
                        onToggleTempSame = { isTempSameAsPres = it },
                        tempAddress = tempAddress,
                        onTempAddressChange = { tempAddress = it },
                        tempPostOffice = tempPostOffice,
                        onTempPostOfficeChange = { tempPostOffice = it },
                        tempUpazila = tempUpazila,
                        onTempUpazilaChange = { tempUpazila = it },
                        tempDistrict = tempDistrict,
                        onTempDistrictChange = { tempDistrict = it },
                        tempPostCode = tempPostCode,
                        onTempPostCodeChange = { tempPostCode = it },

                        // Permanent
                        isPermSameAsPres = isPermSameAsPres,
                        onTogglePermSame = { isPermSameAsPres = it },
                        permAddress = permAddress,
                        onPermAddressChange = { permAddress = it },
                        permPostOffice = permPostOffice,
                        onPermPostOfficeChange = { permPostOffice = it },
                        permUpazila = permUpazila,
                        onPermUpazilaChange = { permUpazila = it },
                        permDistrict = permDistrict,
                        onPermDistrictChange = { permDistrict = it },
                        permPostCode = permPostCode,
                        onPermPostCodeChange = { permPostCode = it },

                        // Shortcut auto-fill all
                        onFillAllThreeAddresses = {
                            isTempSameAsPres = true
                            isPermSameAsPres = true
                        },

                        errorMsg = regErrorMsg,
                        isLoading = isRegistering,
                        onSubmit = {
                            if (regPassword != regConfirmPassword) {
                                regErrorMsg = "পাসওয়ার্ড ও কনফার্ম পাসওয়ার্ড মিলছে না!"
                                return@CustomerRegistrationForm
                            }
                            regErrorMsg = null
                            isRegistering = true
                            viewModel.registerCustomer(
                                name = regName,
                                phone = regPhone,
                                email = regEmail,
                                password = regPassword,
                                presentAddress = presAddress,
                                presentPostOffice = presPostOffice,
                                presentUpazila = presUpazila,
                                presentDistrict = presDistrict,
                                presentPostCode = presPostCode,
                                tempAddress = if (isTempSameAsPres) presAddress else tempAddress,
                                tempPostOffice = if (isTempSameAsPres) presPostOffice else tempPostOffice,
                                tempUpazila = if (isTempSameAsPres) presUpazila else tempUpazila,
                                tempDistrict = if (isTempSameAsPres) presDistrict else tempDistrict,
                                tempPostCode = if (isTempSameAsPres) presPostCode else tempPostCode,
                                isTempSame = isTempSameAsPres,
                                permanentAddress = if (isPermSameAsPres) presAddress else permAddress,
                                permanentPostOffice = if (isPermSameAsPres) presPostOffice else permPostOffice,
                                permanentUpazila = if (isPermSameAsPres) presUpazila else permUpazila,
                                permanentDistrict = if (isPermSameAsPres) presDistrict else permDistrict,
                                permanentPostCode = if (isPermSameAsPres) presPostCode else permPostCode,
                                isPermSame = isPermSameAsPres,
                                onSuccess = {
                                    isRegistering = false
                                    onAuthSuccess()
                                },
                                onError = { msg ->
                                    isRegistering = false
                                    regErrorMsg = msg
                                }
                            )
                        },
                        onGoToLogin = { currentMode = AuthMode.LOGIN }
                    )
                }

                AuthMode.FORGOT_PASSWORD -> {
                    ForgotPasswordForm(
                        phoneOrEmail = resetPhoneOrEmail,
                        onPhoneOrEmailChange = { resetPhoneOrEmail = it },
                        statusMsg = resetStatusMsg,
                        isSuccess = isResetSuccess,
                        isLoading = isSubmittingReset,
                        allResetRequests = allResetRequests,
                        allUsers = allUsers,
                        onSubmitRequest = {
                            isSubmittingReset = true
                            viewModel.requestPasswordReset(resetPhoneOrEmail) { success, msg ->
                                isSubmittingReset = false
                                isResetSuccess = success
                                resetStatusMsg = msg
                            }
                        },
                        onQuickLoginWithDemoPass = { phone, demoPass ->
                            loginPhoneOrEmail = phone
                            loginPassword = demoPass
                            currentMode = AuthMode.LOGIN
                        },
                        onBackToLogin = { currentMode = AuthMode.LOGIN }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// -------------------------------------------------------------
// 1. Customer Login Form
// -------------------------------------------------------------
@Composable
fun CustomerLoginForm(
    phoneOrEmail: String,
    onPhoneOrEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    showPassword: Boolean,
    onToggleShowPassword: () -> Unit,
    errorMsg: String?,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onQuickFillDemo: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGoToRegister: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GreenPrimaryContainer,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "আপনার অ্যাকাউন্টে লগইন করুন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "অর্ডার করতে ও অফার পেতে আপনার তথ্য দিন",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Error Message
            AnimatedVisibility(visible = errorMsg != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = errorMsg ?: "",
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Phone / Email Field
            OutlinedTextField(
                value = phoneOrEmail,
                onValueChange = onPhoneOrEmailChange,
                label = { Text("মোবাইল নম্বর অথবা ইমেইল") },
                placeholder = { Text("যেমন: 01755123456") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GreenPrimary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = Color(0xFFCBD5E1)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_customer_phone_email")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("পাসওয়ার্ড (Password)") },
                placeholder = { Text("আপনার পাসওয়ার্ড লিখুন") },
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = GreenPrimary) },
                trailingIcon = {
                    IconButton(onClick = onToggleShowPassword) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showPassword) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onLoginClick() }),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = Color(0xFFCBD5E1)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_customer_password")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot Password Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "পাসওয়ার্ড ভুলে গেছেন? (Forgot Password?)",
                    color = Color(0xFF2563EB),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onForgotPasswordClick() }
                        .padding(vertical = 4.dp)
                        .testTag("btn_forgot_password")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Demo Auto-fill Chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF0FDF4),
                border = BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onQuickFillDemo() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "দ্রুত ডেমো কাস্টমার লগইন (01755123456 / user1234)",
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

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = onLoginClick,
                enabled = !isLoading && phoneOrEmail.isNotBlank() && password.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_submit_customer_login")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("যাচাই করা হচ্ছে...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("লগইন করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(16.dp))

            // Switch to Register
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "কোনো অ্যাকাউন্ট নেই? ",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "নতুন অ্যাকাউন্ট তৈরি করুন",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary,
                    modifier = Modifier
                        .clickable { onGoToRegister() }
                        .padding(4.dp)
                        .testTag("btn_switch_to_register")
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 2. Customer Detailed Registration Form
// -------------------------------------------------------------
@Composable
fun CustomerRegistrationForm(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    showPassword: Boolean,
    onToggleShowPassword: () -> Unit,

    // Present Address
    presAddress: String,
    onPresAddressChange: (String) -> Unit,
    presPostOffice: String,
    onPresPostOfficeChange: (String) -> Unit,
    presUpazila: String,
    onPresUpazilaChange: (String) -> Unit,
    presDistrict: String,
    onPresDistrictChange: (String) -> Unit,
    presPostCode: String,
    onPresPostCodeChange: (String) -> Unit,

    // Temporary Address
    isTempSameAsPres: Boolean,
    onToggleTempSame: (Boolean) -> Unit,
    tempAddress: String,
    onTempAddressChange: (String) -> Unit,
    tempPostOffice: String,
    onTempPostOfficeChange: (String) -> Unit,
    tempUpazila: String,
    onTempUpazilaChange: (String) -> Unit,
    tempDistrict: String,
    onTempDistrictChange: (String) -> Unit,
    tempPostCode: String,
    onTempPostCodeChange: (String) -> Unit,

    // Permanent Address
    isPermSameAsPres: Boolean,
    onTogglePermSame: (Boolean) -> Unit,
    permAddress: String,
    onPermAddressChange: (String) -> Unit,
    permPostOffice: String,
    onPermPostOfficeChange: (String) -> Unit,
    permUpazila: String,
    onPermUpazilaChange: (String) -> Unit,
    permDistrict: String,
    onPermDistrictChange: (String) -> Unit,
    permPostCode: String,
    onPermPostCodeChange: (String) -> Unit,

    onFillAllThreeAddresses: () -> Unit,

    errorMsg: String?,
    isLoading: Boolean,
    onSubmit: () -> Unit,
    onGoToLogin: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GreenPrimaryContainer,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.AppRegistration, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "নতুন কাস্টমার রেজিস্ট্রেশন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "নিচের ফর্মটি সঠিক তথ্য দিয়ে পূরণ করুন",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message
            AnimatedVisibility(visible = errorMsg != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = errorMsg ?: "",
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Section 1: Personal Info
            Text(
                text = "১. ব্যক্তিগত তথ্য ও লগইন ক্রেডেনশিয়াল",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = GreenPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("পূর্ণ নাম (Full Name) *") },
                placeholder = { Text("যেমন: মোঃ আবরার আহমেদ") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_reg_name")
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Phone
            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("মোবাইল নম্বর (১১ ডিজিট) *") },
                placeholder = { Text("যেমন: 017xxxxxxxx") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GreenPrimary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_reg_phone")
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("ইমেইল ঠিকানা (ঐচ্ছিক)") },
                placeholder = { Text("যেমন: user@example.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = GreenPrimary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_reg_email")
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Password & Confirm Password
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("পাসওয়ার্ড *") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_reg_password")
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = { Text("কনফার্ম পাসওয়ার্ড *") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_reg_confirm_password")
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onToggleShowPassword() }
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (showPassword) "পাসওয়ার্ড লুকান" else "পাসওয়ার্ড দেখুন",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(12.dp))

            // Section 2: Address Management with Shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "২. বিস্তারিত ঠিকানার বিবরণ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GreenPrimary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f)),
                    modifier = Modifier.clickable { onFillAllThreeAddresses() }
                ) {
                    Text(
                        text = "⚡ সকল ঠিকানা একই",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D4ED8),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // -------------------------------------------------------------
            // A. Present Address (বর্তমান ঠিকানা)
            // -------------------------------------------------------------
            AddressSectionCard(
                title = "📌 বর্তমান ঠিকানা (Present Address) *",
                subtitle = "যেখানে সাধারণত হোম ডেলিভারি পৌঁছে দেওয়া হবে",
                addressDetails = presAddress,
                onAddressDetailsChange = onPresAddressChange,
                postOffice = presPostOffice,
                onPostOfficeChange = onPresPostOfficeChange,
                upazila = presUpazila,
                onUpazilaChange = onPresUpazilaChange,
                district = presDistrict,
                onDistrictChange = onPresDistrictChange,
                postCode = presPostCode,
                onPostCodeChange = onPresPostCodeChange,
                isEditable = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // -------------------------------------------------------------
            // B. Temporary Address (অস্থায়ী ঠিকানা) with Checkbox Shortcut
            // -------------------------------------------------------------
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isTempSameAsPres) Color(0xFFF1F5F9) else Color(0xFFFFFBEB),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleTempSame(!isTempSameAsPres) }
                    ) {
                        Checkbox(
                            checked = isTempSameAsPres,
                            onCheckedChange = onToggleTempSame,
                            colors = CheckboxDefaults.colors(checkedColor = GreenPrimary),
                            modifier = Modifier.testTag("checkbox_temp_same_as_present")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "☑️ বর্তমান ও অস্থায়ী ঠিকানা একই (Same as Present)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "টিক দিলে বর্তমান ঠিকানার তথ্যই অস্থায়ী হিসেবে সংরক্ষিত হবে",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    AnimatedVisibility(visible = !isTempSameAsPres) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            AddressSectionCard(
                                title = "🏠 অস্থায়ী ঠিকানা (Temporary Address)",
                                subtitle = "বর্তমান অবস্থান থেকে আলাদা হলে পূরণ করুন",
                                addressDetails = tempAddress,
                                onAddressDetailsChange = onTempAddressChange,
                                postOffice = tempPostOffice,
                                onPostOfficeChange = onTempPostOfficeChange,
                                upazila = tempUpazila,
                                onUpazilaChange = onTempUpazilaChange,
                                district = tempDistrict,
                                onDistrictChange = onTempDistrictChange,
                                postCode = tempPostCode,
                                onPostCodeChange = onTempPostCodeChange,
                                isEditable = true
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // -------------------------------------------------------------
            // C. Permanent Address (স্থায়ী ঠিকানা) with Checkbox Shortcut
            // -------------------------------------------------------------
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isPermSameAsPres) Color(0xFFF1F5F9) else Color(0xFFF0FDF4),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTogglePermSame(!isPermSameAsPres) }
                    ) {
                        Checkbox(
                            checked = isPermSameAsPres,
                            onCheckedChange = onTogglePermSame,
                            colors = CheckboxDefaults.colors(checkedColor = GreenPrimary),
                            modifier = Modifier.testTag("checkbox_perm_same_as_present")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "☑️ বর্তমান ও স্থায়ী ঠিকানা একই (Same as Present)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "টিক দিলে বর্তমান ঠিকানার তথ্যই স্থায়ী হিসেবে সংরক্ষিত হবে",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    AnimatedVisibility(visible = !isPermSameAsPres) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            AddressSectionCard(
                                title = "🏡 স্থায়ী ঠিকানা (Permanent Address)",
                                subtitle = "গ্রাম, ডাকঘর, থানা ও স্থায়ী জেলার বিবরণ",
                                addressDetails = permAddress,
                                onAddressDetailsChange = onPermAddressChange,
                                postOffice = permPostOffice,
                                onPostOfficeChange = onPermPostOfficeChange,
                                upazila = permUpazila,
                                onUpazilaChange = onPermUpazilaChange,
                                district = permDistrict,
                                onDistrictChange = onPermDistrictChange,
                                postCode = permPostCode,
                                onPostCodeChange = onPermPostCodeChange,
                                isEditable = true
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Registration
            Button(
                onClick = onSubmit,
                enabled = !isLoading && name.isNotBlank() && phone.isNotBlank() && password.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_submit_registration")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("সংরক্ষণ করা হচ্ছে...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("রেজিস্ট্রেশন সম্পন্ন করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Switch to Login
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ইতিমধ্যে অ্যাকাউন্ট আছে? ",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "এখানে লগইন করুন",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary,
                    modifier = Modifier
                        .clickable { onGoToLogin() }
                        .padding(4.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Sub-component: Address Section Form Fields Card
// -------------------------------------------------------------
@Composable
fun AddressSectionCard(
    title: String,
    subtitle: String,
    addressDetails: String,
    onAddressDetailsChange: (String) -> Unit,
    postOffice: String,
    onPostOfficeChange: (String) -> Unit,
    upazila: String,
    onUpazilaChange: (String) -> Unit,
    district: String,
    onDistrictChange: (String) -> Unit,
    postCode: String,
    onPostCodeChange: (String) -> Unit,
    isEditable: Boolean = true
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
            Text(text = subtitle, fontSize = 10.sp, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(8.dp))

            // Village / Road / House
            OutlinedTextField(
                value = addressDetails,
                onValueChange = onAddressDetailsChange,
                label = { Text("রোড / বাড়ি নং / গ্রাম / ফ্ল্যাট") },
                placeholder = { Text("যেমন: বাড়ি #২৪, রোড #৭, ব্লক #ডি") },
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp)) },
                enabled = isEditable,
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Post Office & Thana/Upazila in a row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                    value = postOffice,
                    onValueChange = onPostOfficeChange,
                    label = { Text("ডাকঘর (Post Office)") },
                    placeholder = { Text("যেমন: মিরপুর") },
                    leadingIcon = { Icon(Icons.Default.MarkunreadMailbox, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp)) },
                    enabled = isEditable,
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = upazila,
                    onValueChange = onUpazilaChange,
                    label = { Text("উপজেলা / থানা") },
                    placeholder = { Text("যেমন: মিরপুর / সদর") },
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp)) },
                    enabled = isEditable,
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            // District & Post Code in a row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                    value = district,
                    onValueChange = onDistrictChange,
                    label = { Text("জেলা (District)") },
                    placeholder = { Text("যেমন: ঢাকা") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp)) },
                    enabled = isEditable,
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = postCode,
                    onValueChange = onPostCodeChange,
                    label = { Text("পোস্ট কোড") },
                    placeholder = { Text("যেমন: ১২১৬") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = isEditable,
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 3. Forgot Password Form
// -------------------------------------------------------------
@Composable
fun ForgotPasswordForm(
    phoneOrEmail: String,
    onPhoneOrEmailChange: (String) -> Unit,
    statusMsg: String?,
    isSuccess: Boolean,
    isLoading: Boolean,
    allResetRequests: List<com.example.data.local.entity.PasswordResetRequestEntity>,
    allUsers: List<com.example.data.local.entity.RegisteredUserEntity>,
    onSubmitRequest: () -> Unit,
    onQuickLoginWithDemoPass: (phone: String, demoPass: String) -> Unit,
    onBackToLogin: () -> Unit
) {
    // Check if there is an active reset request for this phone
    val latestRequest = allResetRequests.firstOrNull {
        it.userPhoneOrEmail.trim().equals(phoneOrEmail.trim(), ignoreCase = true)
    }
    val matchedUser = allUsers.firstOrNull {
        it.phone.trim().equals(phoneOrEmail.trim(), ignoreCase = true) ||
                it.email.trim().equals(phoneOrEmail.trim(), ignoreCase = true)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LockReset, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "পাসওয়ার্ড ভুলে গেছেন?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "অ্যাডমিন প্যানেল থেকে নতুন ডেমো পাসওয়ার্ড গ্রহণ করুন",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "ℹ️ পাসওয়ার্ড পুনরুদ্ধারের নিয়ম:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "১. আপনার রেজিস্টার্ড ফোন নম্বর বা ইমেইল দিয়ে রিকোয়েস্ট পাঠান।\n২. অ্যাডমিন প্যানেলে এটি জমা হবে এবং অ্যাডমিন আপনাকে একটি নতুন ডেমো পাসওয়ার্ড (যেমন: Bazar@2026) ইস্যু করবেন।\n৩. ইস্যু হওয়ার পর নিচে স্বয়ংক্রিয়ভাবে পাসওয়ার্ড দেখা যাবে এবং আপনি ১-ক্লিকেই লগইন করতে পারবেন।",
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Phone/Email Input
            OutlinedTextField(
                value = phoneOrEmail,
                onValueChange = onPhoneOrEmailChange,
                label = { Text("নিবন্ধিত ফোন নম্বর বা ইমেইল *") },
                placeholder = { Text("যেমন: 01755123456") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF2563EB)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_forgot_phone")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Status message
            AnimatedVisibility(visible = statusMsg != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSuccess) Color(0xFFF0FDF4) else Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, if (isSuccess) Color(0xFF86EFAC) else Color(0xFFFCA5A5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = statusMsg ?: "",
                        color = if (isSuccess) GreenPrimaryDark else Color(0xFFDC2626),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Live Admin-Issued Demo Password Card
            val activeDemoPass = matchedUser?.tempDemoPassword ?: latestRequest?.assignedDemoPassword
            if (activeDemoPass != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFECFDF5),
                    border = BorderStroke(1.dp, Color(0xFF10B981)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🎉 অ্যাডমিন নতুন ডেমো পাসওয়ার্ড অনুমোদন করেছেন!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF065F46)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "আপনার নতুন পাসওয়ার্ড: $activeDemoPass",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color(0xFF047857)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                onQuickLoginWithDemoPass(matchedUser?.phone ?: phoneOrEmail, activeDemoPass)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("এই পাসওয়ার্ড দিয়ে এখনই লগইন করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Submit Request Button
            Button(
                onClick = onSubmitRequest,
                enabled = !isLoading && phoneOrEmail.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_submit_forgot_request")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("রিকোয়েস্ট পাঠানো হচ্ছে...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("অ্যাডমিনের কাছে রিসেট রিকোয়েস্ট পাঠান", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Return to Login
            OutlinedButton(
                onClick = onBackToLogin,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("লগইন পেইজে ফিরে যান", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
