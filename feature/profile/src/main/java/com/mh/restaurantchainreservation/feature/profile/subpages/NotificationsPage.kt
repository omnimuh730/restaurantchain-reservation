package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.MarkChatRead
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SystemUpdate
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.core.model.AppNotification
import com.mh.restaurantchainreservation.core.model.NotificationKind
import com.mh.restaurantchainreservation.core.model.NotificationStore

private enum class NotifFilter { All, Unread, Read }

@Composable
fun NotificationsPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val notifications by NotificationStore.notifications.collectAsState()
    var filter by rememberSaveable { mutableStateOf(NotifFilter.All) }

    val unreadCount = remember(notifications) { notifications.count { !it.read } }
    val filtered = when (filter) {
        NotifFilter.All -> notifications
        NotifFilter.Unread -> notifications.filter { !it.read }
        NotifFilter.Read -> notifications.filter { it.read }
    }

    SubpageScaffold(
        title = stringResource(I18nR.string.title_notifications),
        onBack = onBack,
        modifier = modifier,
    ) {
        DigestHeader(unreadCount = unreadCount)
        Spacer(Modifier.height(16.dp))
        FilterTabs(filter = filter, onChange = { filter = it })
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${filtered.size}",
                color = palette.mutedForeground,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (unreadCount > 0) {
                    LinkText(stringResource(I18nR.string.notifications_mark_all_read)) {
                        NotificationStore.markAllAsRead()
                    }
                }
                LinkText(
                    when (filter) {
                        NotifFilter.All -> stringResource(I18nR.string.notifications_clear_all)
                        NotifFilter.Unread -> stringResource(I18nR.string.notifications_clear_unread)
                        NotifFilter.Read -> stringResource(I18nR.string.notifications_clear_read)
                    },
                ) {
                    when (filter) {
                        NotifFilter.All -> NotificationStore.clearAll()
                        NotifFilter.Unread -> NotificationStore.clearUnread()
                        NotifFilter.Read -> NotificationStore.clearRead()
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            EmptyNotificationsState(filter = filter)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filtered.forEach { n -> NotificationCard(n = n) }
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun DigestHeader(unreadCount: Int) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, palette.border.copy(alpha = 0.6f), shape)
            .background(palette.brandSoftSurface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(palette.brand.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.AutoAwesome, null, tint = palette.brand, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (unreadCount == 0) {
                    stringResource(I18nR.string.notifications_digest_caught_up)
                } else {
                    stringResource(I18nR.string.notifications_digest_new, unreadCount)
                },
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(I18nR.string.notifications_digest_subtitle),
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun FilterTabs(filter: NotifFilter, onChange: (NotifFilter) -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.mutedSurface)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FilterChip(stringResource(I18nR.string.notifications_tab_all), filter == NotifFilter.All) { onChange(NotifFilter.All) }
        FilterChip(stringResource(I18nR.string.notifications_tab_unread), filter == NotifFilter.Unread) { onChange(NotifFilter.Unread) }
        FilterChip(stringResource(I18nR.string.notifications_tab_read), filter == NotifFilter.Read) { onChange(NotifFilter.Read) }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.FilterChip(label: String, active: Boolean, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (active) palette.cardSurface else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (active) palette.foreground else palette.mutedForeground,
            fontSize = 13.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
private fun LinkText(text: String, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = text,
        color = palette.brand,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.clickable(onClick = onClick),
    )
}

@Composable
private fun NotificationCard(n: AppNotification) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, palette.border.copy(alpha = if (n.read) 0.4f else 0.7f), shape)
            .background(if (n.read) palette.cardSurface else palette.brandSoftSurface.copy(alpha = 0.4f))
            .clickable { NotificationStore.markAsRead(n.id) }
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val (icon, tint) = iconForKind(n.kind, palette)
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = n.title,
                    color = palette.foreground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = n.time,
                    color = palette.mutedForeground,
                    fontSize = 11.sp,
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = n.message,
                color = palette.mutedForeground,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
            if (!n.read) {
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(palette.brand),
                )
            }
        }
    }
}

private fun iconForKind(kind: NotificationKind, palette: com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantPalette): Pair<ImageVector, Color> = when (kind) {
    NotificationKind.Reservation -> Icons.Outlined.EventNote to palette.info
    NotificationKind.Promo -> Icons.Outlined.CardGiftcard to palette.brand
    NotificationKind.Reward -> Icons.Outlined.AutoAwesome to palette.gold
    NotificationKind.System -> Icons.Outlined.SystemUpdate to palette.mutedForeground
    NotificationKind.Review -> Icons.Outlined.RateReview to palette.success
    NotificationKind.Share -> Icons.Outlined.Share to palette.violetAccent.onContainer
}

@Composable
private fun EmptyNotificationsState(filter: NotifFilter) {
    val palette = LocalRestaurantPalette.current
    val (titleRes, bodyRes) = when (filter) {
        NotifFilter.All -> I18nR.string.notifications_empty_title_all to I18nR.string.notifications_empty_body_all
        NotifFilter.Unread -> I18nR.string.notifications_empty_title_unread to I18nR.string.notifications_empty_body_unread
        NotifFilter.Read -> I18nR.string.notifications_empty_title_read to I18nR.string.notifications_empty_body_read
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(64.dp).clip(CircleShape).background(palette.mutedSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (filter == NotifFilter.Unread) Icons.Outlined.MarkChatRead else if (filter == NotifFilter.Read) Icons.Outlined.ChatBubbleOutline else Icons.Outlined.NotificationsNone,
                contentDescription = null,
                tint = palette.mutedForeground,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(stringResource(titleRes), color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(bodyRes),
            color = palette.mutedForeground,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
    }
}
