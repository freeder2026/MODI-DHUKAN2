package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.model.ProductCatalog
import com.example.ui.BazarViewModel
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.AddressManageScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderHistoryScreen
import com.example.ui.screens.OrderSuccessScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.CustomerAuthScreen
import com.example.ui.screens.auth.DashboardLoginScreen
import com.example.ui.screens.auth.DashboardRole
import com.example.ui.screens.delivery.DeliveryDashboardScreen
import com.example.ui.theme.FlashBadgeRed
import com.example.ui.theme.GreenOnPrimaryContainer
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.GreenPrimaryContainer
import com.example.ui.theme.MyApplicationTheme

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : Screen("home", "হোম", Icons.Filled.Home, Icons.Outlined.Home)
    object Search : Screen("search", "সার্চ", Icons.Filled.Search, Icons.Outlined.Search)
    object Cart : Screen("cart", "কার্ট", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart)
    object Account : Screen("account", "প্রোফাইল", Icons.Filled.Person, Icons.Outlined.Person)

    // Secondary routes
    object ProductDetail : Screen("product_detail/{productId}", "পণ্যের বিবরণ", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart)
    object Checkout : Screen("checkout", "চেকআউট", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart)
    object OrderSuccess : Screen("order_success/{orderId}", "অর্ডার নিশ্চিত", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart)
    object Addresses : Screen("addresses", "ঠিকানা", Icons.Filled.Home, Icons.Outlined.Home)
    object Orders : Screen("orders", "অর্ডারসমূহ", Icons.Filled.Home, Icons.Outlined.Home)
    object Wishlist : Screen("wishlist", "পছন্দের পণ্য", Icons.Filled.Home, Icons.Outlined.Home)
    object Admin : Screen("admin", "অ্যাডমিন প্যানেল", Icons.Filled.Person, Icons.Outlined.Person)
    object Delivery : Screen("delivery", "ডেলিভারি ড্যাশবোর্ড", Icons.Filled.Person, Icons.Outlined.Person)
    object DashboardLogin : Screen("dashboard_login", "ড্যাশবোর্ড লগইন", Icons.Filled.Person, Icons.Outlined.Person)
    object CustomerAuth : Screen("customer_auth", "লগইন / রেজিস্টার", Icons.Filled.Person, Icons.Outlined.Person)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BazarMainApp()
            }
        }
    }
}

@Composable
fun BazarMainApp(viewModel: BazarViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val cartItems by viewModel.cartItems.collectAsState()
    val cartCount = cartItems.sumOf { it.quantity }

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Cart,
        Screen.Account
    )

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Cart.route,
        Screen.Account.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 0.dp
                    ) {
                        bottomNavItems.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    if (screen == Screen.Cart && cartCount > 0) {
                                        BadgedBox(
                                            badge = {
                                                Badge(
                                                    containerColor = Color(0xFFEF4444),
                                                    contentColor = Color.White
                                                ) {
                                                    Text(
                                                        text = cartCount.toString(),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                                contentDescription = screen.title,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                            contentDescription = screen.title,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF4A6741),
                                    selectedTextColor = Color(0xFF4A6741),
                                    indicatorColor = Color(0xFFEBF2E8),
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF94A3B8)
                                ),
                                modifier = Modifier.testTag("nav_${screen.route}")
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Home
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                    onNavigateToProductDetail = { product ->
                        viewModel.selectProduct(product)
                        navController.navigate("product_detail/${product.id}")
                    },
                    onNavigateToAddresses = { navController.navigate(Screen.Addresses.route) },
                    onNavigateToAdmin = { navController.navigate(Screen.Admin.route) },
                    onNavigateToAuth = { navController.navigate(Screen.CustomerAuth.route) }
                )
            }

            // 2. Search
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onNavigateToProductDetail = { product ->
                        viewModel.selectProduct(product)
                        navController.navigate("product_detail/${product.id}")
                    }
                )
            }

            // 3. Cart
            composable(Screen.Cart.route) {
                CartScreen(
                    viewModel = viewModel,
                    onNavigateToHome = { navController.navigate(Screen.Home.route) },
                    onNavigateToCheckout = { navController.navigate(Screen.Checkout.route) },
                    onNavigateToAuth = { navController.navigate(Screen.CustomerAuth.route) }
                )
            }

            // 4. Account
            composable(Screen.Account.route) {
                AccountScreen(
                    viewModel = viewModel,
                    onNavigateToAddresses = { navController.navigate(Screen.Addresses.route) },
                    onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                    onNavigateToWishlist = { navController.navigate(Screen.Wishlist.route) },
                    onNavigateToAdmin = { navController.navigate(Screen.Admin.route) },
                    onNavigateToDelivery = { navController.navigate(Screen.Delivery.route) },
                    onNavigateToAuth = { navController.navigate(Screen.CustomerAuth.route) }
                )
            }

            // 5. Product Detail
            composable("product_detail/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")
                val product = ProductCatalog.sampleProducts.firstOrNull { it.id == productId }
                    ?: viewModel.selectedProduct.collectAsState().value
                    ?: ProductCatalog.sampleProducts.first()

                ProductDetailScreen(
                    product = product,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToCart = { navController.navigate(Screen.Cart.route) }
                )
            }

            // 6. Checkout
            composable(Screen.Checkout.route) {
                CheckoutScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOrderConfirmed = { orderId ->
                        navController.navigate("order_success/$orderId") {
                            popUpTo(Screen.Cart.route) { inclusive = true }
                        }
                    },
                    onNavigateToAddAddress = { navController.navigate(Screen.Addresses.route) }
                )
            }

            // 7. Order Success
            composable("order_success/{orderId}") { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: "SB-1001"
                OrderSuccessScreen(
                    orderId = orderId,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onNavigateToOrders = {
                        navController.navigate(Screen.Orders.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            // 8. Saved Addresses
            composable(Screen.Addresses.route) {
                AddressManageScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // 9. Order History
            composable(Screen.Orders.route) {
                OrderHistoryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onBrowseProducts = { navController.navigate(Screen.Home.route) }
                )
            }

            // 10. Wishlist
            composable(Screen.Wishlist.route) {
                WishlistScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToProduct = { product ->
                        viewModel.selectProduct(product)
                        navController.navigate("product_detail/${product.id}")
                    },
                    onNavigateToBrowse = { navController.navigate(Screen.Home.route) }
                )
            }

            // 11. Admin Dashboard with Auth Gate
            composable(Screen.Admin.route) {
                val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()
                if (!isAdminLoggedIn) {
                    DashboardLoginScreen(
                        viewModel = viewModel,
                        initialRole = DashboardRole.ADMIN,
                        onAdminLoginSuccess = { /* Automatically refreshes into AdminDashboardScreen */ },
                        onRiderLoginSuccess = { navController.navigate(Screen.Delivery.route) },
                        onBackToCustomerHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                } else {
                    AdminDashboardScreen(
                        viewModel = viewModel,
                        onNavigateToCustomerHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        onNavigateToDelivery = {
                            navController.navigate(Screen.Delivery.route)
                        },
                        onLogout = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                }
            }

            // 12. Delivery Dashboard with Auth Gate
            composable(Screen.Delivery.route) {
                val activeRider by viewModel.activeDeliveryMan.collectAsState()
                if (activeRider == null) {
                    DashboardLoginScreen(
                        viewModel = viewModel,
                        initialRole = DashboardRole.RIDER,
                        onAdminLoginSuccess = { navController.navigate(Screen.Admin.route) },
                        onRiderLoginSuccess = { /* Automatically refreshes into DeliveryDashboardScreen */ },
                        onBackToCustomerHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                } else {
                    DeliveryDashboardScreen(
                        viewModel = viewModel,
                        onNavigateToCustomerHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        onNavigateToAdmin = {
                            navController.navigate(Screen.Admin.route)
                        },
                        onLogout = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                }
            }

            // 13. Dedicated Dashboard Login Screen
            composable(Screen.DashboardLogin.route) {
                DashboardLoginScreen(
                    viewModel = viewModel,
                    initialRole = DashboardRole.ADMIN,
                    onAdminLoginSuccess = { navController.navigate(Screen.Admin.route) },
                    onRiderLoginSuccess = { navController.navigate(Screen.Delivery.route) },
                    onBackToCustomerHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // 14. Customer Login / Registration / Forgot Password Screen
            composable(Screen.CustomerAuth.route) {
                CustomerAuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = {
                        navController.popBackStack()
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
