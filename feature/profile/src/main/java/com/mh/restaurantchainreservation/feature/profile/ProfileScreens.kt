package com.mh.restaurantchainreservation.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mh.restaurantchainreservation.feature.profile.hub.ProfileHubScreen
import com.mh.restaurantchainreservation.feature.profile.subpages.ContactSupportPage
import com.mh.restaurantchainreservation.feature.profile.subpages.CreditCardsPage
import com.mh.restaurantchainreservation.feature.profile.subpages.FriendsPage
import com.mh.restaurantchainreservation.feature.profile.subpages.HelpCenterPage
import com.mh.restaurantchainreservation.feature.profile.subpages.HistoryPage
import com.mh.restaurantchainreservation.feature.profile.subpages.LocationPage
import com.mh.restaurantchainreservation.feature.profile.subpages.NotificationsPage
import com.mh.restaurantchainreservation.feature.profile.subpages.ProfileEditPage
import com.mh.restaurantchainreservation.feature.profile.subpages.ReferPage
import com.mh.restaurantchainreservation.feature.profile.subpages.SendGiftPage
import com.mh.restaurantchainreservation.feature.profile.subpages.SettingsPageFull
import com.mh.restaurantchainreservation.feature.profile.subpages.SubscriptionPage
import com.mh.restaurantchainreservation.feature.profile.subpages.TopUpPage

object ProfileRoutes {
    const val Home = "profile"
    const val Settings = "profile/settings"
    const val Edit = "profile/edit"
    const val Notifications = "profile/notifications"
    const val TopUp = "profile/topup"
    const val SendGift = "profile/send-gift"
    const val Cards = "profile/cards"
    const val History = "profile/history"
    const val Refer = "profile/refer"
    const val Friends = "profile/friends"
    const val Location = "profile/location"
    const val Subscription = "profile/subscription"
    const val Help = "profile/help"
    const val ContactSupport = "profile/contact-support"

    val AllProfileSubRoutes: List<String> = listOf(
        Settings, Edit, Notifications, TopUp, SendGift, Cards, History,
        Refer, Friends, Location, Subscription, Help, ContactSupport,
    )
}

@Composable
fun ProfileHomeScreen(
    onOpenSettings: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenLocation: () -> Unit = {},
    onOpenSubscription: () -> Unit = {},
    onOpenFriends: () -> Unit = {},
    onOpenHelp: () -> Unit = {},
    onOpenContactSupport: () -> Unit = {},
    onOpenTopUp: () -> Unit = {},
    onOpenSendGift: () -> Unit = {},
    onOpenCards: () -> Unit = {},
    onOpenHistory: () -> Unit = {},
    onOpenRefer: () -> Unit = {},
    onLogOut: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    ProfileHubScreen(
        onOpenSettings = onOpenSettings,
        onOpenNotifications = onOpenNotifications,
        onOpenLocation = onOpenLocation,
        onOpenSubscription = onOpenSubscription,
        onOpenFriends = onOpenFriends,
        onOpenHelp = onOpenHelp,
        onOpenContactSupport = onOpenContactSupport,
        onOpenTopUp = onOpenTopUp,
        onOpenSendGift = onOpenSendGift,
        onOpenCards = onOpenCards,
        onOpenHistory = onOpenHistory,
        onOpenRefer = onOpenRefer,
        onLogOut = onLogOut,
        modifier = modifier,
    )
}

@Composable
fun SettingsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    SettingsPageFull(onBack = onBack, modifier = modifier)
}

@Composable
fun ProfileEditScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    ProfileEditPage(onBack = onBack, modifier = modifier)
}

@Composable
fun ProfileNotificationsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    NotificationsPage(onBack = onBack, modifier = modifier)
}

@Composable
fun TopUpScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    TopUpPage(onBack = onBack, modifier = modifier)
}

@Composable
fun SendGiftScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    SendGiftPage(onBack = onBack, modifier = modifier)
}

@Composable
fun CreditCardsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    CreditCardsPage(onBack = onBack, modifier = modifier)
}

@Composable
fun HistoryScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    HistoryPage(onBack = onBack, modifier = modifier)
}

@Composable
fun ReferScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    ReferPage(onBack = onBack, modifier = modifier)
}

@Composable
fun FriendsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    FriendsPage(onBack = onBack, modifier = modifier)
}

@Composable
fun LocationScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    LocationPage(onBack = onBack, modifier = modifier)
}

@Composable
fun SubscriptionScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    SubscriptionPage(onBack = onBack, modifier = modifier)
}

@Composable
fun HelpCenterScreen(onBack: () -> Unit, onContactSupport: () -> Unit, modifier: Modifier = Modifier) {
    HelpCenterPage(onBack = onBack, onContactSupport = onContactSupport, modifier = modifier)
}

@Composable
fun ContactSupportScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    ContactSupportPage(onBack = onBack, modifier = modifier)
}
