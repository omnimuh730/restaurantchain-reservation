package com.mh.restaurantchainreservation.core.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
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
import com.mh.restaurantchainreservation.core.designsystem.transition.LocalAnimatedContentScope
import com.mh.restaurantchainreservation.core.designsystem.transition.LocalRestaurantSharedTransitionScope
import com.mh.restaurantchainreservation.core.designsystem.components.icons.BottomNavIconPaths
import com.mh.restaurantchainreservation.core.designsystem.components.icons.BottomNavStrokeIcon
import com.mh.restaurantchainreservation.core.designsystem.components.icons.LucidePaths
import com.mh.restaurantchainreservation.core.designsystem.components.BottomNavBar
import com.mh.restaurantchainreservation.core.designsystem.components.LocalBottomNavScrollBehavior
import com.mh.restaurantchainreservation.core.designsystem.components.rememberBottomNavScrollBehavior
import com.mh.restaurantchainreservation.core.designsystem.components.BottomNavTab
import com.mh.restaurantchainreservation.core.designsystem.components.BottomNavTabId
import com.mh.restaurantchainreservation.core.designsystem.components.GlobalNotificationHost
import com.mh.restaurantchainreservation.core.model.AuthSessionStore
import com.mh.restaurantchainreservation.core.model.DiscoverData
import com.mh.restaurantchainreservation.core.model.LocalDataSyncStore
import com.mh.restaurantchainreservation.core.model.LocationStore
import com.mh.restaurantchainreservation.feature.auth.AuthRoutes
import com.mh.restaurantchainreservation.feature.auth.ForgotPasswordScreen
import com.mh.restaurantchainreservation.feature.auth.LoginScreen
import com.mh.restaurantchainreservation.feature.auth.RegisterScreen
import com.mh.restaurantchainreservation.feature.auth.SignInRequiredDialog
import com.mh.restaurantchainreservation.feature.auth.SignInRequiredReason
import com.mh.restaurantchainreservation.feature.booking.BookTableScreen
import com.mh.restaurantchainreservation.feature.booking.BookingRoutes
import com.mh.restaurantchainreservation.feature.booking.RestaurantDetailScreen
import com.mh.restaurantchainreservation.feature.dining.DiningDetailScreen
import com.mh.restaurantchainreservation.feature.dining.DiningEnjoyScreen
import com.mh.restaurantchainreservation.feature.dining.DiningHomeScreen
import com.mh.restaurantchainreservation.feature.dining.DiningRoutes
import com.mh.restaurantchainreservation.feature.discover.DiscoverRoutes
import com.mh.restaurantchainreservation.feature.discover.ui.AllPromotionsScreen
import com.mh.restaurantchainreservation.feature.discover.ui.CategoryResultsScreen
import com.mh.restaurantchainreservation.feature.discover.ui.DiscoverHazeRegistry
import com.mh.restaurantchainreservation.feature.discover.ui.DiscoverHomeScreen
import com.mh.restaurantchainreservation.feature.discover.ui.DiscoverUpdateModalHost
import com.mh.restaurantchainreservation.feature.discover.ui.DiscoverSearchModal
import com.mh.restaurantchainreservation.feature.discover.ui.DiscoverSearchResultsScreen
import com.mh.restaurantchainreservation.feature.discover.ui.FoodTypeCuisineListScreen
import com.mh.restaurantchainreservation.feature.discover.ui.FoodResultsScreen
import com.mh.restaurantchainreservation.feature.discover.ui.LocationResultsScreen
import com.mh.restaurantchainreservation.feature.discover.ui.NewsDetailScreen
import com.mh.restaurantchainreservation.feature.discover.ui.NewsListScreen
import com.mh.restaurantchainreservation.feature.discover.ui.SectionListScreen
import com.mh.restaurantchainreservation.feature.discover.ui.WhereToEatAreaListScreen
import com.mh.restaurantchainreservation.feature.profile.ContactSupportScreen
import com.mh.restaurantchainreservation.feature.profile.CreditCardsScreen
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
    val authenticated by AuthSessionStore.isAuthenticated.collectAsState()
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
    var signInRequiredReason by remember { mutableStateOf<SignInRequiredReason?>(null) }

    fun promptSignIn(reason: SignInRequiredReason) {
        signInRequiredReason = reason
    }

    fun navigateToLogin() {
        signInRequiredReason = null
        navController.navigate(AuthRoutes.Login) {
            launchSingleTop = true
        }
    }

    // Hide app chrome while auth, QR Pay, or other full-screen flows own the screen.
    val showAppChrome = shouldShowAppChrome(destination?.route)
    val onDiscoverHome = destination?.route == DiscoverRoutes.Home
    val shouldShowUpdatePrompt by LocalDataSyncStore.shouldShowUpdatePrompt.collectAsState()
    // Keep bottom nav hidden until the post-login update flow finishes so Discover paints first.
    val hideBottomBarForUpdateFlow = authenticated && onDiscoverHome && shouldShowUpdatePrompt
    val bottomNavScrollBehavior = rememberBottomNavScrollBehavior()
    val showBottomBarSlot = isCompact &&
        showAppChrome &&
        shouldShowBottomNavBar(destination?.route) &&
        !hideBottomBarForUpdateFlow
    val showBottomBar = showBottomBarSlot && bottomNavScrollBehavior.isVisible

    LaunchedEffect(destination?.route) {
        bottomNavScrollBehavior.show()
    }

    LaunchedEffect(authenticated, destination?.route) {
        val route = destination?.route ?: return@LaunchedEffect
        if (!authenticated && requiresAuthRoute(route)) {
            promptSignIn(signInReasonForRoute(route))
            navController.navigateToDiscoverHome()
        } else if (authenticated && route.startsWith(AuthRoutes.Root)) {
            navController.navigate(DiscoverRoutes.Home) {
                popUpTo(AuthRoutes.Login) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    var bottomBarHeightPx by remember { mutableIntStateOf(0) }
    val bottomNavHideFraction by animateFloatAsState(
        targetValue = if (showBottomBar) 0f else 1f,
        animationSpec = spring(
            dampingRatio = 0.86f,
            stiffness = 340f,
        ),
        label = "bottomNavHideFraction",
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        // Phone bottom nav is drawn as an overlay so hide/show can animate in sync with content inset.
        bottomBar = {},
    ) { paddingValues ->
        val compactBottomInset = with(density) {
            (bottomBarHeightPx * (1f - bottomNavHideFraction)).toDp().coerceAtLeast(0.dp)
        }
        val compactContentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding(),
            start = paddingValues.calculateStartPadding(layoutDirection),
            end = paddingValues.calculateEndPadding(layoutDirection),
            bottom = compactBottomInset,
        )
        if (isCompact) {
            Box(modifier = Modifier.fillMaxSize()) {
                CompositionLocalProvider(
                    LocalBottomNavScrollBehavior provides
                        bottomNavScrollBehavior.takeIf { showBottomBarSlot },
                ) {
                    AppGraph(
                        navController = navController,
                        contentPadding = compactContentPadding,
                        authenticated = authenticated,
                        onAuthenticated = { AuthSessionStore.markAuthenticated(context.applicationContext) },
                        onRequireSignIn = { promptSignIn(it) },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                if (showAppChrome) {
                    // Wishlist overlay (sheet + toast) sits above app destinations.
                    // Toast is offset up by the bottom-bar inset so it floats just
                    // above the nav bar.
                    WishlistOverlayHost(bottomInset = compactContentPadding)
                }
                GlobalNotificationHost(
                    bottomInset = if (showAppChrome) compactContentPadding else PaddingValues(0.dp),
                )
                signInRequiredReason?.let { reason ->
                    SignInRequiredDialog(
                        message = stringResource(reason.messageRes),
                        onSignIn = { navigateToLogin() },
                        onDismiss = { signInRequiredReason = null },
                    )
                }
                if (showBottomBarSlot) {
                    BottomNavBar(
                        tabs = bottomTabs,
                        activeId = activeTabId,
                        onTabSelect = { id ->
                            bottomNavScrollBehavior.show()
                            val route = routeForTab(id)
                            if (!authenticated && requiresAuthRoute(route)) {
                                promptSignIn(signInReasonForTab(id))
                            } else if (id == BottomNavTabId.Discover) {
                                navController.navigateToDiscoverHome()
                            } else {
                                navController.navigateToTab(route)
                            }
                        },
                        onQrPay = {
                            if (authenticated) {
                                navController.navigate(QrPayRoutes.Home)
                            } else {
                                promptSignIn(SignInRequiredReason.QrPay)
                            }
                        },
                        qrPayContentDescription = qrPayLabel,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .graphicsLayer {
                                translationY = bottomBarHeightPx * bottomNavHideFraction
                            }
                            .onSizeChanged { size ->
                                if (size.height > 0) {
                                    bottomBarHeightPx = size.height
                                }
                            },
                    )
                }
            }
        } else if (showAppChrome) {
            Row(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                NavigationRail {
                    bottomTabs.forEach { tab ->
                        val selected = tab.id == activeTabId
                        NavigationRailItem(
                            selected = selected,
                            onClick = {
                                val route = routeForTab(tab.id)
                                if (!authenticated && requiresAuthRoute(route)) {
                                    promptSignIn(signInReasonForTab(tab.id))
                                } else if (tab.id == BottomNavTabId.Discover) {
                                    navController.navigateToDiscoverHome()
                                } else {
                                    navController.navigateToTab(route)
                                }
                            },
                            icon = { RailIconFor(tab.id, selected = selected) },
                            label = { Text(tab.label) },
                        )
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    AppGraph(
                        navController = navController,
                        contentPadding = PaddingValues(0.dp),
                        authenticated = authenticated,
                        onAuthenticated = { AuthSessionStore.markAuthenticated(context.applicationContext) },
                        onRequireSignIn = { promptSignIn(it) },
                        modifier = Modifier.fillMaxSize(),
                    )
                    WishlistOverlayHost()
                    GlobalNotificationHost()
                    signInRequiredReason?.let { reason ->
                        SignInRequiredDialog(
                            message = stringResource(reason.messageRes),
                            onSignIn = { navigateToLogin() },
                            onDismiss = { signInRequiredReason = null },
                        )
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                AppGraph(
                    navController = navController,
                    contentPadding = PaddingValues(0.dp),
                    authenticated = authenticated,
                    onAuthenticated = { AuthSessionStore.markAuthenticated(context.applicationContext) },
                    onRequireSignIn = { promptSignIn(it) },
                    modifier = Modifier.fillMaxSize(),
                )
                GlobalNotificationHost()
                signInRequiredReason?.let { reason ->
                    SignInRequiredDialog(
                        message = stringResource(reason.messageRes),
                        onSignIn = { navigateToLogin() },
                        onDismiss = { signInRequiredReason = null },
                    )
                }
            }
        }
    }

        if (isCompact && onDiscoverHome) {
            DiscoverUpdateModalHost(onDiscoverHome = onDiscoverHome)
        }
    }
}

@Composable
private fun RailIconFor(tab: BottomNavTabId, selected: Boolean) {
    val primary = MaterialTheme.colorScheme.primary
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    when (tab) {
        BottomNavTabId.Discover -> BottomNavStrokeIcon(
            paths = BottomNavIconPaths.DiscoverSearch,
            isActive = selected,
            activeColor = primary,
            inactiveColor = muted,
            activeStrokeWidth = 2.67f,
            inactiveStrokeWidth = 2f,
        )
        BottomNavTabId.Wishlist -> BottomNavStrokeIcon(
            paths = BottomNavIconPaths.WishlistHeart,
            isActive = selected,
            activeColor = primary,
            inactiveColor = muted,
        )
        BottomNavTabId.Dining -> BottomNavStrokeIcon(
            paths = LucidePaths.UtensilsCrossed,
            isActive = selected,
            activeColor = primary,
            inactiveColor = muted,
            viewportSize = 24f,
        )
        BottomNavTabId.Profile -> BottomNavStrokeIcon(
            paths = BottomNavIconPaths.ProfileInCircle,
            isActive = selected,
            activeColor = primary,
            inactiveColor = muted,
        )
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

/** Pops booking/dining overlays and returns to the discover home feed. */
private fun NavHostController.navigateToDiscoverHome() {
    if (popBackStack(DiscoverRoutes.Home, inclusive = false)) {
        return
    }
    navigate(DiscoverRoutes.Home) {
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

private fun shouldShowBottomNavBar(route: String?): Boolean {
    if (route == null) return true
    return !route.startsWith("discover/restaurant/")
}

/** QR Pay (and other full-screen overlays) own the entire viewport. */
private fun shouldShowAppChrome(route: String?): Boolean {
    if (route == null) return false
    return when {
        route.startsWith(AuthRoutes.Root) -> false
        route == QrPayRoutes.Home -> false
        route == DiscoverRoutes.Search -> false
        route.startsWith("discover/search?") -> false
        route == DiscoverRoutes.AllPromotions -> false
        else -> true
    }
}

private fun requiresAuthRoute(route: String?): Boolean {
    if (route == null) return false
    return route == WishlistRoutes.Home ||
        route == QrPayRoutes.Home ||
        route == BookingRoutes.BookTable ||
        route == DiningRoutes.Home ||
        route == DiningRoutes.Detail ||
        route == DiningRoutes.Enjoy ||
        route == ProfileRoutes.Home ||
        route in ProfileRoutes.AllProfileSubRoutes
}

private fun signInReasonForTab(tab: BottomNavTabId): SignInRequiredReason = when (tab) {
    BottomNavTabId.Wishlist -> SignInRequiredReason.Wishlist
    BottomNavTabId.Dining -> SignInRequiredReason.Dining
    BottomNavTabId.Profile -> SignInRequiredReason.Profile
    BottomNavTabId.Discover -> SignInRequiredReason.Generic
}

private fun signInReasonForRoute(route: String): SignInRequiredReason = when {
    route == WishlistRoutes.Home -> SignInRequiredReason.Wishlist
    route == QrPayRoutes.Home -> SignInRequiredReason.QrPay
    route == BookingRoutes.BookTable -> SignInRequiredReason.Booking
    route == DiningRoutes.Home || route == DiningRoutes.Detail || route == DiningRoutes.Enjoy ->
        SignInRequiredReason.Dining
    route == ProfileRoutes.Home || route in ProfileRoutes.AllProfileSubRoutes ->
        SignInRequiredReason.Profile
    else -> SignInRequiredReason.Generic
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AppGraph(
    navController: NavHostController,
    contentPadding: PaddingValues,
    authenticated: Boolean,
    onAuthenticated: () -> Unit,
    onRequireSignIn: (SignInRequiredReason) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val initialStartDestination = remember { DiscoverRoutes.Home }

    Box(modifier = modifier) {
        SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
            CompositionLocalProvider(LocalRestaurantSharedTransitionScope provides this) {
                NavHost(
                    navController = navController,
                    startDestination = initialStartDestination,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                ) {
            composable(DiscoverRoutes.Home) {
                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    val currentLocation by LocationStore.current.collectAsState()
                    DiscoverHomeScreen(
                        onOpenSearch = { navController.navigate(DiscoverRoutes.Search) },
                        onOpenMap = {
                            navController.navigateDiscoverSearchResults(
                                q = "All restaurants",
                                summary = "Tonight, 7:00 PM, 2 people",
                            )
                        },
                        onOpenRestaurant = { id -> navController.navigateToRestaurantDetail(id) },
                        onOpenCategory = { id ->
                            when (id) {
                                "nearby-me" -> {
                                    navController.navigateDiscoverSearchResults(
                                        q = currentLocation.name,
                                        summary = "Tonight, 7:00 PM, 2 people",
                                    )
                                }
                                "local-fav" -> {
                                    val city = DiscoverData.cityForLocation(currentLocation)
                                    navController.navigateDiscoverSearchResults(
                                        q = DiscoverData.localFavoritesSearchQuery,
                                        summary = "Tonight, 7:00 PM, 2 people",
                                        locationId = city.id,
                                    )
                                }
                                else -> navController.navigate(DiscoverRoutes.category(id))
                            }
                        },
                        onOpenFood = { id -> navController.navigate(DiscoverRoutes.food(id)) },
                        onOpenLocation = { id -> navController.navigate(DiscoverRoutes.location(id)) },
                        onOpenNewsList = { navController.navigate(DiscoverRoutes.NewsList) },
                        onOpenNewsArticle = { id -> navController.navigate(DiscoverRoutes.newsDetail(id)) },
                        onOpenSection = { id ->
                            when (id) {
                                "banners" -> navController.navigate(DiscoverRoutes.AllPromotions)
                                "where-to-eat" -> navController.navigate(DiscoverRoutes.WhereToEatAreas)
                                "top-picks-food" -> navController.navigate(DiscoverRoutes.FoodTypes)
                                else -> navController.navigate(DiscoverRoutes.section(id))
                            }
                        },
                    )
                }
            }
            composable(DiscoverRoutes.NewsList) {
                NewsListScreen(
                    onBack = { navController.popBackStack() },
                    onOpenArticle = { id -> navController.navigate(DiscoverRoutes.newsDetail(id)) },
                )
            }
            composable(
                route = DiscoverRoutes.NewsDetail,
                arguments = listOf(navArgument("articleId") { type = NavType.StringType }),
            ) { entry ->
                val articleId = entry.arguments?.getString("articleId").orEmpty()
                NewsDetailScreen(
                    articleId = articleId,
                    onBack = { navController.popBackStack() },
                    onOpenArticle = { id -> navController.navigate(DiscoverRoutes.newsDetail(id)) },
                )
            }
            composable(DiscoverRoutes.AllPromotions) {
                AllPromotionsScreen(
                    onClose = { navController.popBackStack() },
                    onBannerClick = { bannerId ->
                        navController.navigate(DiscoverRoutes.section(bannerId))
                    },
                )
            }
            composable(DiscoverRoutes.WhereToEatAreas) {
                WhereToEatAreaListScreen(
                    onBack = { navController.popBackStack() },
                    onSelectArea = { locationId, areaLabel ->
                        navController.navigateDiscoverSearchResults(
                            q = "Best of $areaLabel",
                            summary = "Tonight, 7:00 PM, 2 people",
                            locationId = locationId,
                        )
                    },
                )
            }
            composable(DiscoverRoutes.FoodTypes) {
                FoodTypeCuisineListScreen(
                    onBack = { navController.popBackStack() },
                    onSelectCuisine = { foodId -> navController.navigate(DiscoverRoutes.food(foodId)) },
                )
            }
            composable(DiscoverRoutes.Search) {
                DiscoverSearchModal(
                    onClose = { navController.popBackStack() },
                    onSubmit = { q, summary ->
                        navController.popBackStack()
                        navController.navigateDiscoverSearchResults(q = q, summary = summary)
                    },
                )
            }
            composable(
                route = DiscoverRoutes.SearchResults,
                arguments = listOf(
                    navArgument("q") { type = NavType.StringType; defaultValue = "" },
                    navArgument("summary") { type = NavType.StringType; defaultValue = "" },
                    navArgument("locationId") { type = NavType.StringType; defaultValue = "" },
                ),
            ) { entry ->
                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    val q = entry.arguments?.getString("q").orEmpty()
                    val summary = entry.arguments?.getString("summary").orEmpty()
                    val locationId = entry.arguments?.getString("locationId").orEmpty()
                    DiscoverSearchResultsScreen(
                        query = q,
                        planSummary = summary.ifBlank { "Tonight, 7:00 PM, 2 people" },
                        locationId = locationId,
                        onBack = { navController.popBackStack() },
                        onOpenSearch = { navController.navigate(DiscoverRoutes.Search) },
                        onOpenRestaurant = { id -> navController.navigateToRestaurantDetail(id) },
                    )
                }
            }
            composable(
                route = DiscoverRoutes.Category,
                arguments = listOf(navArgument("categoryId") { type = NavType.StringType }),
            ) { entry ->
                val id = entry.arguments?.getString("categoryId").orEmpty()
                CategoryResultsScreen(
                    categoryId = id,
                    onBack = { navController.popBackStack() },
                    onOpenRestaurant = { rid -> navController.navigateToRestaurantDetail(rid) },
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
                    onOpenRestaurant = { rid -> navController.navigateToRestaurantDetail(rid) },
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
                    onOpenRestaurant = { rid -> navController.navigateToRestaurantDetail(rid) },
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
                    onOpenRestaurant = { rid -> navController.navigateToRestaurantDetail(rid) },
                )
            }
            composable(
                route = BookingRoutes.RestaurantDetail,
                arguments = listOf(navArgument("restaurantId") { type = NavType.StringType }),
            ) { entry ->
                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    val id = entry.arguments?.getString("restaurantId").orEmpty()
                    RestaurantDetailScreen(
                        restaurantId = id,
                        onBack = { navController.popBackStack() },
                        onBookNow = {
                            if (authenticated) {
                                navController.navigate(BookingRoutes.bookTable(id))
                            } else {
                                onRequireSignIn(SignInRequiredReason.Booking)
                            }
                        },
                    )
                }
            }
            composable(
                route = BookingRoutes.BookTable,
                arguments = listOf(navArgument("restaurantId") { type = NavType.StringType }),
            ) { entry ->
                val id = entry.arguments?.getString("restaurantId").orEmpty()
                BookTableScreen(
                    restaurantId = id,
                    onBack = { navController.popBackStack() },
                    onNavigateToDining = {
                        navController.navigate(DiningRoutes.Home) {
                            popUpTo(DiscoverRoutes.Home) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToDiscover = {
                        navController.navigateToDiscoverHome()
                    },
                )
            }
            composable(DiningRoutes.Home) {
                DiningHomeScreen(
                    onOpenDetail = { id -> navController.navigate(DiningRoutes.detail(id)) },
                    onExploreRestaurants = {
                        navController.navigateToTab(DiscoverRoutes.Home)
                    },
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
                WishlistScreen(
                    onOpenRestaurant = { id -> navController.navigateToRestaurantDetail(id) },
                )
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
                    onOpenCards = { navController.navigate(ProfileRoutes.Cards) },
                    onOpenHistory = { navController.navigate(ProfileRoutes.History) },
                    onOpenRefer = { navController.navigate(ProfileRoutes.Refer) },
                    onLogout = {
                        AuthSessionStore.signOut(context)
                        navController.navigate(AuthRoutes.Login) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
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
            profileSubComposable(ProfileRoutes.Cards) {
                CreditCardsScreen(onBack = { navController.popBackStack() })
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
                        onAuthenticated()
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
                        onAuthenticated()
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
                LaunchedEffect(authenticated) {
                    navController.navigate(if (authenticated) DiscoverRoutes.Home else AuthRoutes.Login) {
                        popUpTo(AuthRoutes.Root) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
            }
        }
    }
}

private fun NavHostController.navigateToRestaurantDetail(restaurantId: String) {
    navigate(BookingRoutes.restaurantDetail(restaurantId)) {
        launchSingleTop = true
    }
}

private fun NavHostController.navigateDiscoverSearchResults(
    q: String,
    summary: String = "Tonight, 7:00 PM, 2 people",
    locationId: String = "",
) {
    navigate(
        "discover/search/results?q=" + Uri.encode(q) +
            "&summary=" + Uri.encode(summary) +
            "&locationId=" + Uri.encode(locationId),
    )
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
