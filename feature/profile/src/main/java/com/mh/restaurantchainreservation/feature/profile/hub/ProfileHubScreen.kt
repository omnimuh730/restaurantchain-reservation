package com.mh.restaurantchainreservation.feature.profile.hub

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingScreenTitleHeader
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingTitleHeaderMetrics
import com.mh.restaurantchainreservation.core.designsystem.components.HubSurfaceCardDefaults
import com.mh.restaurantchainreservation.core.designsystem.components.hubSurfaceCard
import com.mh.restaurantchainreservation.core.designsystem.components.ListGroup
import com.mh.restaurantchainreservation.core.designsystem.components.ListGroupItem
import com.mh.restaurantchainreservation.core.designsystem.components.ListGroupVariant
import com.mh.restaurantchainreservation.core.designsystem.components.Stagger
import com.mh.restaurantchainreservation.core.designsystem.components.StaggerItem
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.LocaleManager
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.core.model.DailyBonusStore
import com.mh.restaurantchainreservation.core.model.LocationStore
import com.mh.restaurantchainreservation.core.model.NotificationStore
import com.mh.restaurantchainreservation.core.model.PlanType
import com.mh.restaurantchainreservation.core.model.SubscriptionStore
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
    onOpenHistory: () -> Unit = {},
    onOpenRefer: () -> Unit = {},
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface),
    ) {
        val scroll = rememberScrollState()
        val density = LocalDensity.current
        val collapseRangePx = remember(density) {
            with(density) {
                (CollapsingTitleHeaderMetrics.expandedBodyHeight - CollapsingTitleHeaderMetrics.collapsedBodyHeight)
                    .toPx()
            }
                .coerceAtLeast(1f)
        }
        val collapseProgress = (scroll.value / collapseRangePx).coerceIn(0f, 1f)
        val statusBarTopDp = with(density) { WindowInsets.statusBars.getTop(this).toDp() }

        Stagger(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .zIndex(0f),
            staggerMs = 40,
            verticalArrangement = Arrangement.spacedBy(HubSurfaceCardDefaults.SectionSpacing),
        ) {
            Spacer(Modifier.height(CollapsingTitleHeaderMetrics.expandedBodyHeight + statusBarTopDp))

            StaggerItem {
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

            StaggerItem {
                WalletCardStack(
                    showBalance = showBalance,
                    onToggleBalance = { showBalance = !showBalance },
                )
            }

            StaggerItem {
                CreditCardsHubSection(
                    onManageCards = onOpenCards,
                    onOpenCardInfo = onOpenCards,
                    onAddNewCard = onOpenCards,
                )
            }

            StaggerItem {
                QuickActionsRow(
                    onTopUp = onOpenTopUp,
                    onGift = onOpenSendGift,
                    onActivity = onOpenHistory,
                )
            }

            if (!dailyClaimed) {
                StaggerItem {
                    DailyRewardCard(onClick = { bonusOpen = true })
                }
            }

            StaggerItem {
                ReferCard(onClick = onOpenRefer)
            }

            StaggerItem {
                AccountSettingsBlock(
                    locationName = currentLocation.name,
                    locationAddress = currentLocation.address,
                    isPro = plan.type == PlanType.Pro,
                    onLocationClick = onOpenLocation,
                    onSubscriptionClick = onOpenSubscription,
                    onFriendsClick = onOpenFriends,
                    onCardsClick = onOpenCards,
                    onSettingsClick = onOpenSettings,
                    onHelpClick = onOpenHelp,
                    onContactSupportClick = onOpenContactSupport,
                )
            }

            StaggerItem {
                VersionFooter()
            }

            StaggerItem {
                Spacer(Modifier.height(24.dp))
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
        onClaim = {
            DailyBonusStore.markClaimed(context)
        },
    )
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
                    .shadow(elevation = 4.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(palette.cardSurface)
                    .border(1.dp, palette.border.copy(alpha = 0.4f), CircleShape)
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
                    modifier = Modifier.size(18.dp),
                )
                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 6.dp, end = 6.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .border(2.dp, palette.cardSurface, CircleShape),
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
                tint = Color.White,
                modifier = Modifier.size(32.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(I18nR.string.profile_daily_reward_title),
                    color = palette.foreground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(palette.brand)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = stringResource(I18nR.string.profile_daily_badge).uppercase(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                    )
                }
            }
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
    onCardsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onContactSupportClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val proLabel = stringResource(I18nR.string.profile_menu_pro)
    val freeLabel = stringResource(I18nR.string.profile_menu_free)

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text(
            text = stringResource(I18nR.string.profile_account_settings),
            color = palette.foreground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
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
                        Text(
                            text = locationName,
                            color = palette.mutedForeground,
                            fontSize = 14.sp,
                            maxLines = 1,
                        )
                    },
                    onClick = onLocationClick,
                ),
                ListGroupItem(
                    id = "subscription",
                    label = stringResource(I18nR.string.profile_menu_subscription),
                    icon = { MenuIcon(Icons.Outlined.WorkspacePremium) },
                    rightContent = {
                        Text(
                            text = if (isPro) proLabel else freeLabel,
                            color = if (isPro) palette.foreground else palette.mutedForeground,
                            fontSize = 14.sp,
                            fontWeight = if (isPro) FontWeight.SemiBold else FontWeight.Normal,
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
                    id = "cards",
                    label = "Credit cards",
                    icon = { MenuIcon(Icons.Outlined.CreditCard) },
                    onClick = onCardsClick,
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
            ),
        )
    }
}

@Composable
private fun MenuIcon(icon: ImageVector) {
    val palette = LocalRestaurantPalette.current
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = palette.foreground.copy(alpha = 0.70f),
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
        Text(
            text = stringResource(I18nR.string.profile_version, APP_VERSION),
            color = palette.mutedForeground.copy(alpha = 0.85f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = stringResource(I18nR.string.profile_last_released, formattedDate),
            color = palette.mutedForeground.copy(alpha = 0.65f),
            fontSize = 11.sp,
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
