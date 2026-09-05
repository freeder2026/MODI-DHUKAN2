package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.OilBarrel
import androidx.compose.material.icons.filled.RiceBowl
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

enum class ProductCategory(
    val id: String,
    val banglaName: String,
    val englishName: String,
    val icon: ImageVector
) {
    ALL("all", "সব পণ্য", "All", Icons.Default.ShoppingBag),
    RICE("rice", "চাল", "Rice", Icons.Default.RiceBowl),
    DAL("dal", "ডাল", "Lentils", Icons.Default.Grass),
    OIL("oil", "তেল ও ঘি", "Oil & Ghee", Icons.Default.OilBarrel),
    BISCUIT("biscuit", "বিস্কুট ও স্ন্যাক্স", "Biscuits", Icons.Default.BreakfastDining),
    MILK("milk", "দুধ ও ডেইরি", "Milk & Dairy", Icons.Default.LocalDrink),
    SOAP("soap", "সাবান ও প্রসাধন", "Soap & Care", Icons.Default.CleaningServices),
    SPICE("spice", "মসলা ও লবণ", "Spices", Icons.Default.LocalFlorist),
    TEA("tea", "চা ও চিনি", "Tea & Sugar", Icons.Default.Egg)
}
