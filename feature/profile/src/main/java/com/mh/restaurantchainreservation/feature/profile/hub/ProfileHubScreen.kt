package com.mh.restaurantchainreservation.feature.profile.hub

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingScreenTitleHeader
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingTitleHeaderMetrics
import com.mh.restaurantchainreservation.core.designsystem.components.HubSurfaceCardDefaults
import com.mh.restaurantchainreservation.core.designsystem.components.LocalNavContentBottomPadding
import com.mh.restaurantchainreservation.core.designsystem.components.hubSurfaceCard
import com.mh.restaurantchainreservation.core.designsystem.components.ListGroup
import com.mh.restaurantchainreservation.core.designsystem.components.ListGroupItem
import com.mh.restaurantchainreservation.core.designsystem.components.ListGroupVariant
import com.mh.restaurantchainreservation.core.designsystem.components.RestaurantText
import com.mh.restaurantchainreservation.core.designsystem.components.hubTitleCollapseProgress
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantTextColor
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantTextRole
import com.mh.restaurantchainreservation.core.designsystem.components.trackBottomNavScroll
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.pageCanvasBackground
import com.mh.restaurantchainreservation.core.i18n.LocaleManager
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.core.model.DailyBonusStore
import com.mh.restaurantchainreservation.core.model.LocationStore
import com.mh.restaurantchainreservation.core.model.NotificationStore
import com.mh.restaurantchainreservation.core.model.PlanType
import com.mh.restaurantchainreservation.core.model.SubscriptionStore
import com.mh.restaurantchainreservation.feature.profile.data.ProfileWalletStore
import com.mh.restaurantchainreservation.feature.profile.subpages.components.Currency
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val APP_VERSION = "2.4.1"
private const val LAST_RELEASED_ISO = "2026-04-10"

@Composable
fun ProfileHubScreen(
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
    onAddNewCard: () -> Unit = onOpenCards,
    onOpenHistory: () -> Unit = {},
    onOpenRefer: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val context = LocalContext.current

    val plan by SubscriptionStore.plan.collectAsState()
    val notifications by NotificationStore.notifications.collectAsState()
    val currentLocation by LocationStore.current.collectAsState()
    val unreadCount = remember(notifications) { notifications.count { !it.read } }
    val dailyClaimed by DailyBonusStore.claimed.collectAsState()

    var showBalance by rememberSaveable { mutableStateOf(false) }
    var selectedAvatar by rememberSaveable { mutableStateOf<String?>(null) }
    var pickerOpen by rememberSaveable { mutableStateOf(false) }
    var pendingAvatar by rememberSaveable { mutableStateOf<String?>(null) }
    var tierStatusOpen by rememberSaveable { mutableStateOf(false) }
    var bonusOpen by rememberSaveable { mutableStateOf(false) }
    var showLogoutConfirm by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground),
    ) {
        val listState = rememberLazyListState()
        val density = LocalDensity.current
        val navBottomPadding = LocalNavContentBottomPadding.current
        val collapseRangePx = remember(density) {
            with(density) {
                (CollapsingTitleHeaderMetrics.expandedBodyHeight - CollapsingTitleHeaderMetrics.collapsedBodyHeight)
                    .toPx()
            }
                .coerceAtLeast(1f)
        }
        val collapseProgress by remember {
            derivedStateOf { listState.hubTitleCollapseProgress(collapseRangePx) }
        }
        val statusBarTopDp = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
        val sectionSpacing = HubSurfaceCardDefaults.SectionSpacing

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .pageCanvasBackground()
                .trackBottomNavScroll()
                .zIndex(0f),
            contentPadding = PaddingValues(bottom = 24.dp + navBottomPadding),
            verticalArrangement = Arrangement.spacedBy(sectionSpacing),
        ) {
            item(key = "profile_hub_top_spacer") {
                Spacer(Modifier.height(CollapsingTitleHeaderMetrics.expandedBodyHeight + statusBarTopDp))
            }
            item(key = "profile_top_card") {
                ProfileTopCard(
                    selectedAvatarUrl = selectedAvatar,
                    onOpenAvatarPicker = {
                        pendingAvatar = selectedAvatar
                        pickerOpen = true
                    },
                    onOpenTierDetails = { tierStatusOpen = true },
                    isPro = plan.type == PlanType.Pro,
                )
            }
            item(key = "wallet_stack") {
                WalletCardStack(
                    showBalance = showBalance,
                    onToggleBalance = { showBalance = !showBalance },
                )
            }
            item(key = "credit_cards") {
                CreditCardsHubSection(
                    onManageCards = onOpenCards,
                    onOpenCardInfo = onOpenCards,
                    onAddNewCard = onAddNewCard,
                )
            }
            item(key = "quick_actions") {
                QuickActionsRow(
                    onTopUp = onOpenTopUp,
                    onGift = onOpenSendGift,
                    onActivity = onOpenHistory,
                )
            }
            if (!dailyClaimed) {
                item(key = "daily_reward") {
                    DailyRewardCard(onClick = { bonusOpen = true })
                }
            }
            item(key = "refer") {
                ReferCard(onClick = onOpenRefer)
            }
            item(key = "account_settings") {
                AccountSettingsBlock(
                    locationName = currentLocation.name,
                    locationAddress = currentLocation.address,
                    isPro = plan.type == PlanType.Pro,
                    onLocationClick = onOpenLocation,
                    onSubscriptionClick = onOpenSubscription,
                    onFriendsClick = onOpenFriends,
                    onSettingsClick = onOpenSettings,
                    onHelpClick = onOpenHelp,
                    onContactSupportClick = onOpenContactSupport,
                    onLogoutClick = { showLogoutConfirm = true },
                )
            }
            item(key = "version_footer") {
                VersionFooter()
            }
        }

        ProfileHubCollapsingHeader(
            collapseProgress = collapseProgress,
            unreadCount = unreadCount,
            onOpenNotifications = onOpenNotifications,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(2f),
        )
    }

    AvatarPickerModal(
        visible = pickerOpen,
        pendingAvatar = pendingAvatar,
        onSelect = { pendingAvatar = it },
        onDismiss = { pickerOpen = false },
        onSave = {
            selectedAvatar = pendingAvatar
            pickerOpen = false
        },
    )

    TierStatusModal(
        visible = tierStatusOpen,
        onDismiss = { tierStatusOpen = false },
    )

    DailyBonusModal(
        visible = bonusOpen,
        onDismiss = { bonusOpen = false },
        onClaim = { reward ->
            DailyBonusStore.markClaimed(context)
            if (reward.kind == DailyRewardKind.Bonus) {
                val krw = reward.label.filter { it.isDigit() }.toDoubleOrNull()
                if (krw != null && krw > 0) {
                    ProfileWalletStore.topUpWallet(Currency.KRW, krw)
                }
            }
            bonusOpen = false
        },
    )

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = {
                Text(
                    text = stringResource(I18nR.string.profile_logout_confirm_title),
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(stringResource(I18nR.string.profile_logout_confirm_body))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirm = false
                        onLogout()
                    },
                ) {
                    Text(
                        text = stringResource(I18nR.string.profile_menu_log_out),
                        color = palette.brand,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text(stringResource(I18nR.string.common_cancel))
                }
            },
        )
    }
}

@Composable
private fun ProfileHubCollapsingHeader(
    collapseProgress: Float,
    unreadCount: Int,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    CollapsingScreenTitleHeader(
        title = stringResource(I18nR.string.profile_hub_title),
        collapseProgress = collapseProgress,
        modifier = modifier,
        trailing = {
            Box(
                modifier = Modifier
                    .size(CollapsingTitleHeaderMetrics.trailingSlotSize)
                    .clickable(
                        role = Role.Button,
                        onClickLabel = stringResource(I18nR.string.profile_hub_notifications_aria),
                        onClick = onOpenNotifications,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.NotificationsNone,
                    contentDescription = stringResource(I18nR.string.profile_hub_notifications_aria),
                    tint = palette.foreground,
                    modifier = Modifier.size(22.dp),
                )
                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .border(2.dp, palette.pageBackground, CircleShape),
                    )
                }
            }
        },
    )
}

@Composable
private fun QuickActionsRow(
    onTopUp: () -> Unit,
    onGift: () -> Unit,
    onActivity: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(HubSurfaceCardDefaults.RowCardSpacing),
    ) {
        QuickActionTile(
            label = stringResource(I18nR.string.profile_quick_topup),
            icon = Icons.Outlined.NorthEast,
            iconBg = palette.blueAccent.container,
            iconColor = palette.blueAccent.onContainer,
            onClick = onTopUp,
            modifier = Modifier.weight(1f),
        )
        QuickActionTile(
            label = stringResource(I18nR.string.profile_quick_gift),
            icon = Icons.Outlined.CardGiftcard,
            iconBg = palette.emeraldAccent.container,
            iconColor = palette.emeraldAccent.onContainer,
            onClick = onGift,
            modifier = Modifier.weight(1f),
        )
        QuickActionTile(
            label = stringResource(I18nR.string.profile_quick_activity),
            icon = Icons.Outlined.AccessTime,
            iconBg = palette.orangeAccent.container,
            iconColor = palette.orangeAccent.onContainer,
            onClick = onActivity,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun QuickActionTile(
    label: String,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier
            .hubSurfaceCard(
                palette = palette,
                shape = HubSurfaceCardDefaults.QuickActionShape,
                onClick = onClick,
            )
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp),
            )
        }
        Text(
            text = label,
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun DailyRewardCard(onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .hubSurfaceCard(palette = palette, onClick = onClick)
            .padding(HubSurfaceCardDefaults.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(palette.giftGradientStart, palette.giftGradientEnd),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.CardGiftcard,
                contentDescription = null,
                tint = RestaurantColors.Base.white,
                modifier = Modifier.size(32.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(I18nR.string.profile_daily_reward_title),
                color = palette.foreground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(I18nR.string.profile_daily_reward_subtitle),
                color = palette.mutedForeground,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = palette.mutedForeground,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun ReferCard(onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .hubSurfaceCard(palette = palette, onClick = onClick)
            .padding(HubSurfaceCardDefaults.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(palette.roseSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.PeopleOutline,
                contentDescription = null,
                tint = palette.rose,
                modifier = Modifier.size(32.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(I18nR.string.profile_refer_title),
                color = palette.foreground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(I18nR.string.profile_refer_subtitle),
                color = palette.mutedForeground,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = palette.mutedForeground,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun AccountSettingsBlock(
    locationName: String,
    locationAddress: String,
    isPro: Boolean,
    onLocationClick: () -> Unit,
    onSubscriptionClick: () -> Unit,
    onFriendsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val proLabel = stringResource(I18nR.string.profile_menu_pro)
    val freeLabel = stringResource(I18nR.string.profile_menu_free)

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        RestaurantText(
            text = stringResource(I18nR.string.profile_account_settings),
            role = RestaurantTextRole.SectionTitle,
        )
        Spacer(Modifier.height(16.dp))
        ListGroup(
            variant = ListGroupVariant.Default,
            showChevron = true,
            rowVerticalPadding = 18,
            rowHorizontalPadding = 0,
            items = listOf(
                ListGroupItem(
                    id = "location",
                    label = stringResource(I18nR.string.profile_menu_location),
                    description = locationAddress,
                    icon = { MenuIcon(Icons.Outlined.LocationOn) },
                    rightContent = {
                        RestaurantText(
                            text = locationName,
                            role = RestaurantTextRole.Body,
                            color = RestaurantTextColor.Sub,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    onClick = onLocationClick,
                ),
                ListGroupItem(
                    id = "subscription",
                    label = stringResource(I18nR.string.profile_menu_subscription),
                    icon = { MenuIcon(Icons.Outlined.WorkspacePremium) },
                    rightContent = {
                        RestaurantText(
                            text = if (isPro) proLabel else freeLabel,
                            role = RestaurantTextRole.Body,
                            color = if (isPro) RestaurantTextColor.Main else RestaurantTextColor.Sub,
                        )
                    },
                    onClick = onSubscriptionClick,
                ),
                ListGroupItem(
                    id = "friends",
                    label = stringResource(I18nR.string.profile_menu_friends),
                    icon = { MenuIcon(Icons.Outlined.PeopleOutline) },
                    onClick = onFriendsClick,
                ),
                ListGroupItem(
                    id = "settings",
                    label = stringResource(I18nR.string.profile_menu_settings),
                    icon = { MenuIcon(Icons.Outlined.Settings) },
                    onClick = onSettingsClick,
                ),
                ListGroupItem(
                    id = "help",
                    label = stringResource(I18nR.string.profile_menu_help),
                    icon = { MenuIcon(Icons.AutoMirrored.Outlined.HelpOutline) },
                    onClick = onHelpClick,
                ),
                ListGroupItem(
                    id = "contact-support",
                    label = stringResource(I18nR.string.profile_menu_contact_support),
                    icon = { MenuIcon(Icons.Outlined.ChatBubbleOutline) },
                    onClick = onContactSupportClick,
                ),
                ListGroupItem(
                    id = "log-out",
                    label = stringResource(I18nR.string.profile_menu_log_out),
                    icon = { MenuIcon(Icons.AutoMirrored.Outlined.Logout) },
                    onClick = onLogoutClick,
                ),
            ),
        )
    }
}

@Composable
private fun MenuIcon(
    icon: ImageVector,
    tint: Color? = null,
) {
    val palette = LocalRestaurantPalette.current
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint ?: palette.foreground,
        modifier = Modifier.size(24.dp),
    )
}

@Composable
private fun VersionFooter() {
    val palette = LocalRestaurantPalette.current
    val context = LocalContext.current
    val locale = remember { LocaleManager.getLocale(context) }
    val formattedDate = remember(locale) { formatMediumDate(LAST_RELEASED_ISO, locale) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RestaurantText(
            text = stringResource(I18nR.string.profile_version, APP_VERSION),
            role = RestaurantTextRole.BodySmall,
            color = RestaurantTextColor.Sub,
        )
        Spacer(Modifier.height(2.dp))
        RestaurantText(
            text = stringResource(I18nR.string.profile_last_released, formattedDate),
            role = RestaurantTextRole.Micro,
            color = RestaurantTextColor.Sub,
        )
    }
}

private fun formatMediumDate(isoYmd: String, languageTag: String): String {
    val parts = isoYmd.split("-").mapNotNull { it.toIntOrNull() }
    if (parts.size != 3) return isoYmd
    val (y, m, d) = parts
    val locale = if (languageTag.startsWith("ko")) Locale.KOREA else Locale.US
    val cal = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.YEAR, y)
        set(java.util.Calendar.MONTH, m - 1)
        set(java.util.Calendar.DAY_OF_MONTH, d)
    }
    val pattern = if (locale == Locale.KOREA) "yyyy년 M월 d일" else "MMM d, yyyy"
    return SimpleDateFormat(pattern, locale).format(Date(cal.timeInMillis))
}
