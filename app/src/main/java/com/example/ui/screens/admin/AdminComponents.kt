package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val AdminPrimary = Color(0xFF4A6741)
val AdminDark = Color(0xFF0F172A)
val AdminBorder = Color(0xFFE2E8F0)
val AdminCanvas = Color(0xFFF8FAFC)
val AdminCardBg = Color(0xFFFFFFFF)
val AdminTextDark = Color(0xFF0F172A)
val AdminTextMuted = Color(0xFF64748B)

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminCardBg),
        border = BorderStroke(1.dp, AdminBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = iconBg,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AdminDark
            )

            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Medium
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = AdminPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun StockStatusBadge(stockQuantity: Int) {
    val (bgColor, textColor, label, dotColor) = when {
        stockQuantity <= 0 -> Quadruple(
            Color(0xFFFEF2F2),
            Color(0xFFDC2626),
            "স্টক শেষ (0)",
            Color(0xFFDC2626)
        )
        stockQuantity <= 10 -> Quadruple(
            Color(0xFFFEFCE8),
            Color(0xFFD97706),
            "কম স্টক ($stockQuantity)",
            Color(0xFFD97706)
        )
        else -> Quadruple(
            Color(0xFFF0FDF4),
            Color(0xFF16A34A),
            "পর্যাপ্ত স্টক ($stockQuantity)",
            Color(0xFF16A34A)
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, dotColor.copy(alpha = 0.3f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(dotColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopHeader(
    title: String,
    onBackToCustomerApp: () -> Unit,
    onNavigateToDelivery: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AdminPrimary,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🛡️", fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminDark
                    )
                    Text(
                        text = "সহজ বাজার কন্ট্রোল সেন্টার",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        },
        actions = {
            if (onNavigateToDelivery != null) {
                Surface(
                    onClick = onNavigateToDelivery,
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFF2563EB).copy(alpha = 0.3f)),
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "🚚 রাইডার",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                    }
                }
            }

            Surface(
                onClick = onBackToCustomerApp,
                shape = RoundedCornerShape(20.dp),
                color = AdminPrimary.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, AdminPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.padding(end = if (onLogout != null) 6.dp else 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = AdminPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "গ্রাহক অ্যাপ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminPrimary
                    )
                }
            }

            if (onLogout != null) {
                Surface(
                    onClick = onLogout,
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "লগআউট",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}
