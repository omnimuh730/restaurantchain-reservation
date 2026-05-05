package com.mh.restaurantchainreservation.core.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mh.restaurantchainreservation.core.designsystem.components.BottomNavBar
import com.mh.restaurantchainreservation.core.designsystem.components.BottomNavTab
import com.mh.restaurantchainreservation.core.designsystem.components.BottomNavTabId
import com.mh.restaurantchainreservation.core.i18n.LocaleManager
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
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

@Composable
fun RestaurantNavHost(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    val bottomTabs = listOf(
        BottomNavTab(BottomNavTabId.Discover, stringResource(I18nR.string.tab_discover)),
        BottomNavTab(BottomNavTabId.Wishlist, stringResource(I18nR.string.tab_wishlist)),
        BottomNavTab(BottomNavTabId.Dining, stringResource(I18nR.string.tab_dining)),
        BottomNavTab(BottomNavTabId.Profile, stringResource(I18nR.string.tab_profile)),
    )
    val qrPayLabel = stringResource(I18nR.string.qr_pay_aria)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination
    val activeTabId = destination?.let { resolveActiveTab(it.hierarchy.mapNotNull { d -> d.route }.toList()) }
        ?: BottomNavTabId.Discover

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (isCompact) {
                BottomNavBar(
                    tabs = bottomTabs,
                    activeId = activeTabId,
                    onTabSelect = { id ->
                        navController.navigateToTab(routeForTab(id))
                    },
                    onQrPay = { navController.navigate(QrPayRoutes.Home) },
                    qrPayContentDescription = qrPayLabel,
                )
            }
        },
    ) { paddingValues ->
        if (isCompact) {
            AppGraph(
                navController = navController,
                context = context,
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Row(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                NavigationRail {
                    bottomTabs.forEach { tab ->
                        val selected = tab.id == activeTabId
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navController.navigateToTab(routeForTab(tab.id)) },
                            icon = { RailIconFor(tab.id) },
                            label = { Text(tab.label) },
                        )
                    }
                }
                AppGraph(
                    navController = navController,
                    context = context,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun RailIconFor(tab: BottomNavTabId) {
    when (tab) {
        BottomNavTabId.Discover -> Icon(Icons.Outlined.TravelExplore, contentDescription = null)
        BottomNavTabId.Wishlist -> Icon(Icons.Outlined.BookmarkBorder, contentDescription = null)
        BottomNavTabId.Dining -> Icon(Icons.Outlined.CalendarMonth, contentDescription = null)
        BottomNavTabId.Profile -> Icon(Icons.Outlined.PersonOutline, contentDescription = null)
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun routeForTab(tab: BottomNavTabId): String = when (tab) {
    BottomNavTabId.Discover -> DiscoverRoutes.Home
    BottomNavTabId.Wishlist -> WishlistRoutes.Home
    BottomNavTabId.Dining -> DiningRoutes.Home
    BottomNavTabId.Profile -> ProfileRoutes.Home
}

private fun resolveActiveTab(hierarchyRoutes: List<String>): BottomNavTabId? {
    return when {
        hierarchyRoutes.any { it == DiscoverRoutes.Home || it.startsWith("discover/") || it == SearchRoutes.Results || it.startsWith("booking/") || it == BookingRoutes.RestaurantDetail || it == BookingRoutes.BookTable } -> BottomNavTabId.Discover
        hierarchyRoutes.any { it == WishlistRoutes.Home } -> BottomNavTabId.Wishlist
        hierarchyRoutes.any { it == DiningRoutes.Home || it == DiningRoutes.Detail || it == DiningRoutes.Enjoy } -> BottomNavTabId.Dining
        hierarchyRoutes.any { it == ProfileRoutes.Home || it == ProfileRoutes.Settings || it == NotificationsRoutes.Home } -> BottomNavTabId.Profile
        else -> null
    }
}

@Composable
private fun AppGraph(
    navController: NavHostController,
    context: android.content.Context,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = DiscoverRoutes.Home,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
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
}
