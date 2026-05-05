package com.mh.restaurantchainreservation.core.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mh.restaurantchainreservation.feature.auth.AuthRoutes
import com.mh.restaurantchainreservation.feature.auth.ForgotPasswordScreen
import com.mh.restaurantchainreservation.feature.auth.LoginScreen
import com.mh.restaurantchainreservation.feature.auth.RegisterScreen
import com.mh.restaurantchainreservation.feature.booking.BookTableScreen
import com.mh.restaurantchainreservation.feature.booking.BookingRoutes
import com.mh.restaurantchainreservation.feature.booking.RestaurantDetailScreen
import com.mh.restaurantchainreservation.feature.dining.DiningDetailScreen
import com.mh.restaurantchainreservation.feature.dining.DiningEnjoyScreen
import com.mh.restaurantchainreservation.feature.dining.DiningHomeScreen
import com.mh.restaurantchainreservation.feature.dining.DiningRoutes
import com.mh.restaurantchainreservation.feature.discover.DiscoverHomeScreen
import com.mh.restaurantchainreservation.feature.discover.DiscoverRoutes
import com.mh.restaurantchainreservation.feature.notifications.NotificationsRoutes
import com.mh.restaurantchainreservation.feature.notifications.NotificationsScreen
import com.mh.restaurantchainreservation.feature.profile.ProfileHomeScreen
import com.mh.restaurantchainreservation.feature.profile.ProfileRoutes
import com.mh.restaurantchainreservation.feature.profile.SettingsScreen
import com.mh.restaurantchainreservation.feature.qrpay.QrPayRoutes
import com.mh.restaurantchainreservation.feature.qrpay.QrPayScreen
import com.mh.restaurantchainreservation.feature.search.SearchResultsScreen
import com.mh.restaurantchainreservation.feature.search.SearchRoutes
import com.mh.restaurantchainreservation.feature.wishlist.WishlistRoutes
import com.mh.restaurantchainreservation.feature.wishlist.WishlistScreen
import com.mh.restaurantchainreservation.core.i18n.LocaleManager
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

private data class NavItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit,
)

@Composable
fun RestaurantNavHost(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val navItems = listOf(
        NavItem(DiscoverRoutes.Home, stringResource(I18nR.string.tab_discover)) { androidx.compose.material3.Icon(Icons.Outlined.TravelExplore, contentDescription = null) },
        NavItem(WishlistRoutes.Home, stringResource(I18nR.string.tab_wishlist)) { androidx.compose.material3.Icon(Icons.Outlined.BookmarkBorder, contentDescription = null) },
        NavItem(DiningRoutes.Home, stringResource(I18nR.string.tab_dining)) { androidx.compose.material3.Icon(Icons.Outlined.CalendarMonth, contentDescription = null) },
        NavItem(ProfileRoutes.Home, stringResource(I18nR.string.tab_profile)) { androidx.compose.material3.Icon(Icons.Outlined.PersonOutline, contentDescription = null) },
    )

    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (isCompact) {
                androidx.compose.material3.FloatingActionButton(onClick = { navController.navigate(QrPayRoutes.Home) }) {
                    androidx.compose.material3.Icon(Icons.Outlined.QrCodeScanner, contentDescription = stringResource(I18nR.string.tab_qrpay))
                }
            }
        },
        bottomBar = {
            if (isCompact) {
                NavigationBar {
                    navItems.forEach { item ->
                        val selected = destination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = item.icon,
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        if (isCompact) {
            AppGraph(
                navController = navController,
                context = context,
                modifier = Modifier.padding(paddingValues),
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    NavigationRail {
                        navItems.forEach { item ->
                            val selected = destination?.hierarchy?.any { it.route == item.route } == true
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navController.navigate(item.route) },
                                icon = item.icon,
                                label = { Text(item.label) },
                            )
                        }
                    }
                    AppGraph(
                        navController = navController,
                        context = context,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun AppGraph(
    navController: androidx.navigation.NavHostController,
    context: android.content.Context,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = DiscoverRoutes.Home,
        modifier = modifier,
    ) {
        composable(DiscoverRoutes.Home) {
            DiscoverHomeScreen(
                onOpenSearch = { navController.navigate(SearchRoutes.Results) },
                onOpenRestaurant = { navController.navigate("discover/restaurant/sample") },
            )
        }
        composable(SearchRoutes.Results) {
            SearchResultsScreen()
        }
        composable(BookingRoutes.RestaurantDetail) {
            RestaurantDetailScreen(onBookNow = { navController.navigate("discover/restaurant/sample/book") })
        }
        composable(BookingRoutes.BookTable) {
            BookTableScreen(onComplete = { navController.popBackStack() })
        }
        composable(DiningRoutes.Home) {
            DiningHomeScreen(onOpenDetail = { navController.navigate("dining/sample") })
        }
        composable(DiningRoutes.Detail) {
            DiningDetailScreen(onOpenEnjoy = { navController.navigate("dining/sample/enjoy") })
        }
        composable(DiningRoutes.Enjoy) {
            DiningEnjoyScreen()
        }
        composable(WishlistRoutes.Home) {
            WishlistScreen()
        }
        composable(ProfileRoutes.Home) {
            ProfileHomeScreen(
                onOpenSettings = { navController.navigate(ProfileRoutes.Settings) },
                onOpenNotifications = { navController.navigate(NotificationsRoutes.Home) },
                onSwitchKorean = { LocaleManager.setLocale(context, "ko") },
                onSwitchEnglish = { LocaleManager.setLocale(context, "en") },
            )
        }
        composable(ProfileRoutes.Settings) {
            SettingsScreen()
        }
        composable(NotificationsRoutes.Home) {
            NotificationsScreen()
        }
        composable(QrPayRoutes.Home) {
            QrPayScreen()
        }
        composable(AuthRoutes.Login) {
            LoginScreen(
                onNavigateRegister = { navController.navigate(AuthRoutes.Register) },
                onNavigateForgot = { navController.navigate(AuthRoutes.Forgot) },
            )
        }
        composable(AuthRoutes.Register) {
            RegisterScreen()
        }
        composable(AuthRoutes.Forgot) {
            ForgotPasswordScreen()
        }
        composable(AuthRoutes.Root) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Auth Root")
            }
        }
    }
}
