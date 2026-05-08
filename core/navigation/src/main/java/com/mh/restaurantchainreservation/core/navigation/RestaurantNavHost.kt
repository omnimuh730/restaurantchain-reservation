package com.mh.restaurantchainreservation.core.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.mh.restaurantchainreservation.feature.discover.DiscoverRoutes
import com.mh.restaurantchainreservation.feature.discover.ui.CategoryResultsScreen
import com.mh.restaurantchainreservation.feature.discover.ui.DiscoverHomeScreen
import com.mh.restaurantchainreservation.feature.discover.ui.DiscoverSearchModal
import com.mh.restaurantchainreservation.feature.discover.ui.DiscoverSearchResultsScreen
import com.mh.restaurantchainreservation.feature.discover.ui.FoodResultsScreen
import com.mh.restaurantchainreservation.feature.discover.ui.LocationResultsScreen
import com.mh.restaurantchainreservation.feature.discover.ui.SectionListScreen
import com.mh.restaurantchainreservation.feature.profile.ContactSupportScreen
import com.mh.restaurantchainreservation.feature.profile.FriendsScreen
import com.mh.restaurantchainreservation.feature.profile.HelpCenterScreen
import com.mh.restaurantchainreservation.feature.profile.HistoryScreen
import com.mh.restaurantchainreservation.feature.profile.LocationScreen
import com.mh.restaurantchainreservation.feature.profile.ProfileEditScreen
import com.mh.restaurantchainreservation.feature.profile.ProfileHomeScreen
import com.mh.restaurantchainreservation.feature.profile.ProfileNotificationsScreen
import com.mh.restaurantchainreservation.feature.profile.ProfileRoutes
import com.mh.restaurantchainreservation.feature.profile.ReferScreen
import com.mh.restaurantchainreservation.feature.profile.SendGiftScreen
import com.mh.restaurantchainreservation.feature.profile.SettingsScreen
import com.mh.restaurantchainreservation.feature.profile.SubscriptionScreen
import com.mh.restaurantchainreservation.feature.profile.TopUpScreen
import com.mh.restaurantchainreservation.feature.qrpay.QrPayRoutes
import com.mh.restaurantchainreservation.feature.qrpay.QrPayScreen
import com.mh.restaurantchainreservation.feature.wishlist.WishlistRoutes
import com.mh.restaurantchainreservation.feature.wishlist.WishlistScreen
import com.mh.restaurantchainreservation.feature.wishlist.ui.WishlistOverlayHost
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

    // Hide app chrome while auth, QR Pay, or other full-screen flows own the screen.
    val showAppChrome = shouldShowAppChrome(destination?.route)
    val showBottomBar = isCompact && showAppChrome

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
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
            Box(modifier = Modifier.fillMaxSize()) {
                AppGraph(
                    navController = navController,
                    context = context,
                    contentPadding = paddingValues,
                    modifier = Modifier.fillMaxSize(),
                )
                if (showAppChrome) {
                    // Wishlist overlay (sheet + toast) sits above app destinations.
                    // Toast is offset up by the bottom-bar inset so it floats just
                    // above the nav bar.
                    WishlistOverlayHost(bottomInset = paddingValues)
                }
            }
        } else if (showAppChrome) {
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
                Box(modifier = Modifier.fillMaxSize()) {
                    AppGraph(
                        navController = navController,
                        context = context,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.fillMaxSize(),
                    )
                    WishlistOverlayHost()
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
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
        popUpTo(DiscoverRoutes.Home) {
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
        hierarchyRoutes.any { route ->
            route == DiscoverRoutes.Home ||
                route.startsWith("discover/") ||
                route == BookingRoutes.RestaurantDetail ||
                route == BookingRoutes.BookTable
        } -> BottomNavTabId.Discover
        hierarchyRoutes.any { it == WishlistRoutes.Home } -> BottomNavTabId.Wishlist
        hierarchyRoutes.any { it == DiningRoutes.Home || it == DiningRoutes.Detail || it == DiningRoutes.Enjoy } -> BottomNavTabId.Dining
        hierarchyRoutes.any { route -> route == ProfileRoutes.Home || route in ProfileRoutes.AllProfileSubRoutes } -> BottomNavTabId.Profile
        else -> null
    }
}

/** QR Pay (and other full-screen overlays) own the entire viewport. */
private fun shouldShowAppChrome(route: String?): Boolean {
    if (route == null) return false
    return when {
        route.startsWith(AuthRoutes.Root) -> false
        route == QrPayRoutes.Home -> false
        route == DiscoverRoutes.Search -> false
        route.startsWith("discover/search?") -> false
        else -> true
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
            startDestination = AuthRoutes.Login,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            composable(DiscoverRoutes.Home) {
                DiscoverHomeScreen(
                    onOpenSearch = { navController.navigate(DiscoverRoutes.Search) },
                    onOpenRestaurant = { id -> navController.navigate(BookingRoutes.restaurantDetail(id)) },
                    onOpenCategory = { id -> navController.navigate(DiscoverRoutes.category(id)) },
                    onOpenFood = { id -> navController.navigate(DiscoverRoutes.food(id)) },
                    onOpenLocation = { id -> navController.navigate(DiscoverRoutes.location(id)) },
                    onOpenSection = { id -> navController.navigate(DiscoverRoutes.section(id)) },
                )
            }
            composable(DiscoverRoutes.Search) {
                DiscoverSearchModal(
                    onClose = { navController.popBackStack() },
                    onSubmit = { q ->
                        navController.popBackStack()
                        navController.navigate("discover/search/results?q=" + q)
                    },
                )
            }
            composable(
                route = "discover/search/results?q={q}",
                arguments = listOf(navArgument("q") { type = NavType.StringType; defaultValue = "" }),
            ) { entry ->
                val q = entry.arguments?.getString("q").orEmpty()
                DiscoverSearchResultsScreen(
                    query = q,
                    onBack = { navController.popBackStack() },
                    onOpenRestaurant = { id -> navController.navigate(BookingRoutes.restaurantDetail(id)) },
                )
            }
            composable(
                route = DiscoverRoutes.Category,
                arguments = listOf(navArgument("categoryId") { type = NavType.StringType }),
            ) { entry ->
                val id = entry.arguments?.getString("categoryId").orEmpty()
                CategoryResultsScreen(
                    categoryId = id,
                    onBack = { navController.popBackStack() },
                    onOpenRestaurant = { rid -> navController.navigate(BookingRoutes.restaurantDetail(rid)) },
                )
            }
            composable(
                route = DiscoverRoutes.Food,
                arguments = listOf(navArgument("foodId") { type = NavType.StringType }),
            ) { entry ->
                val id = entry.arguments?.getString("foodId").orEmpty()
                FoodResultsScreen(
                    foodId = id,
                    onBack = { navController.popBackStack() },
                    onOpenRestaurant = { rid -> navController.navigate(BookingRoutes.restaurantDetail(rid)) },
                )
            }
            composable(
                route = DiscoverRoutes.Location,
                arguments = listOf(navArgument("locationId") { type = NavType.StringType }),
            ) { entry ->
                val id = entry.arguments?.getString("locationId").orEmpty()
                LocationResultsScreen(
                    locationId = id,
                    onBack = { navController.popBackStack() },
                    onOpenRestaurant = { rid -> navController.navigate(BookingRoutes.restaurantDetail(rid)) },
                )
            }
            composable(
                route = DiscoverRoutes.Section,
                arguments = listOf(navArgument("sectionId") { type = NavType.StringType }),
            ) { entry ->
                val id = entry.arguments?.getString("sectionId").orEmpty()
                SectionListScreen(
                    sectionId = id,
                    onBack = { navController.popBackStack() },
                    onOpenRestaurant = { rid -> navController.navigate(BookingRoutes.restaurantDetail(rid)) },
                )
            }
            composable(
                route = BookingRoutes.RestaurantDetail,
                arguments = listOf(navArgument("restaurantId") { type = NavType.StringType }),
            ) { entry ->
                val id = entry.arguments?.getString("restaurantId").orEmpty()
                RestaurantDetailScreen(
                    restaurantId = id,
                    onBack = { navController.popBackStack() },
                    onBookNow = { navController.navigate(BookingRoutes.bookTable(id)) },
                )
            }
            composable(
                route = BookingRoutes.BookTable,
                arguments = listOf(navArgument("restaurantId") { type = NavType.StringType }),
            ) { entry ->
                val id = entry.arguments?.getString("restaurantId").orEmpty()
                BookTableScreen(
                    restaurantId = id,
                    onBack = { navController.popBackStack() },
                    onComplete = {
                        // Pop back to the discover home so the user lands somewhere sensible.
                        navController.popBackStack(DiscoverRoutes.Home, inclusive = false)
                    },
                )
            }
            composable(DiningRoutes.Home) {
                DiningHomeScreen(
                    onOpenDetail = { id -> navController.navigate(DiningRoutes.detail(id)) },
                )
            }
            composable(DiningRoutes.Detail) { entry ->
                val bookingId = entry.arguments?.getString("bookingId").orEmpty()
                DiningDetailScreen(
                    bookingId = bookingId,
                    onBack = { navController.popBackStack() },
                    onOpenEnjoy = { id -> navController.navigate(DiningRoutes.enjoy(id)) },
                )
            }
            composable(DiningRoutes.Enjoy) { entry ->
                val bookingId = entry.arguments?.getString("bookingId").orEmpty()
                DiningEnjoyScreen(
                    bookingId = bookingId,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(WishlistRoutes.Home) {
                WishlistScreen()
            }
            composable(ProfileRoutes.Home) {
                ProfileHomeScreen(
                    onOpenSettings = { navController.navigate(ProfileRoutes.Settings) },
                    onOpenNotifications = { navController.navigate(ProfileRoutes.Notifications) },
                    onOpenLocation = { navController.navigate(ProfileRoutes.Location) },
                    onOpenSubscription = { navController.navigate(ProfileRoutes.Subscription) },
                    onOpenFriends = { navController.navigate(ProfileRoutes.Friends) },
                    onOpenHelp = { navController.navigate(ProfileRoutes.Help) },
                    onOpenContactSupport = { navController.navigate(ProfileRoutes.ContactSupport) },
                    onOpenTopUp = { navController.navigate(ProfileRoutes.TopUp) },
                    onOpenSendGift = { navController.navigate(ProfileRoutes.SendGift) },
                    onOpenHistory = { navController.navigate(ProfileRoutes.History) },
                    onOpenRefer = { navController.navigate(ProfileRoutes.Refer) },
                    onSwitchKorean = { LocaleManager.setLocale(context, "ko") },
                    onSwitchEnglish = { LocaleManager.setLocale(context, "en") },
                )
            }
            profileSubComposable(ProfileRoutes.Settings) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
            profileSubComposable(ProfileRoutes.Edit) {
                ProfileEditScreen(onBack = { navController.popBackStack() })
            }
            profileSubComposable(ProfileRoutes.Notifications) {
                ProfileNotificationsScreen(onBack = { navController.popBackStack() })
            }
            profileSubComposable(ProfileRoutes.TopUp) {
                TopUpScreen(onBack = { navController.popBackStack() })
            }
            profileSubComposable(ProfileRoutes.SendGift) {
                SendGiftScreen(onBack = { navController.popBackStack() })
            }
            profileSubComposable(ProfileRoutes.History) {
                HistoryScreen(onBack = { navController.popBackStack() })
            }
            profileSubComposable(ProfileRoutes.Refer) {
                ReferScreen(onBack = { navController.popBackStack() })
            }
            profileSubComposable(ProfileRoutes.Friends) {
                FriendsScreen(onBack = { navController.popBackStack() })
            }
            profileSubComposable(ProfileRoutes.Location) {
                LocationScreen(onBack = { navController.popBackStack() })
            }
            profileSubComposable(ProfileRoutes.Subscription) {
                SubscriptionScreen(onBack = { navController.popBackStack() })
            }
            profileSubComposable(ProfileRoutes.Help) {
                HelpCenterScreen(
                    onBack = { navController.popBackStack() },
                    onContactSupport = { navController.navigate(ProfileRoutes.ContactSupport) },
                )
            }
            profileSubComposable(ProfileRoutes.ContactSupport) {
                ContactSupportScreen(onBack = { navController.popBackStack() })
            }
            composable(QrPayRoutes.Home) {
                QrPayScreen(onClose = { navController.popBackStack() })
            }
            composable(AuthRoutes.Login) {
                LoginScreen(
                    onNavigateRegister = { navController.navigate(AuthRoutes.Register) },
                    onNavigateForgot = { navController.navigate(AuthRoutes.Forgot) },
                    onAuthenticated = {
                        navController.navigate(DiscoverRoutes.Home) {
                            popUpTo(AuthRoutes.Login) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onClose = {},
                    allowDismiss = false,
                )
            }
            composable(AuthRoutes.Register) {
                RegisterScreen(
                    onGoLogin = { navController.popBackStack(AuthRoutes.Login, inclusive = false) },
                    onComplete = {
                        navController.navigate(DiscoverRoutes.Home) {
                            popUpTo(AuthRoutes.Login) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onClose = {},
                    allowDismiss = false,
                )
            }
            composable(AuthRoutes.Forgot) {
                ForgotPasswordScreen(
                    onBack = { navController.popBackStack(AuthRoutes.Login, inclusive = false) },
                    onClose = {},
                    allowDismiss = false,
                )
            }
            composable(AuthRoutes.Root) {
                LoginScreen(
                    onNavigateRegister = { navController.navigate(AuthRoutes.Register) },
                    onNavigateForgot = { navController.navigate(AuthRoutes.Forgot) },
                    onAuthenticated = {
                        navController.navigate(DiscoverRoutes.Home) {
                            popUpTo(AuthRoutes.Login) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onClose = {},
                    allowDismiss = false,
                )
            }
        }
    }
}

private fun NavGraphBuilder.profileSubComposable(
    route: String,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable(
        route = route,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(280, easing = FastOutSlowInEasing),
            ) + fadeIn(tween(220))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(220, easing = FastOutSlowInEasing),
                targetOffset = { it / 4 },
            ) + fadeOut(tween(180))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(220, easing = FastOutSlowInEasing),
                initialOffset = { it / 4 },
            ) + fadeIn(tween(180))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(280, easing = FastOutSlowInEasing),
            ) + fadeOut(tween(220))
        },
        content = content,
    )
}
