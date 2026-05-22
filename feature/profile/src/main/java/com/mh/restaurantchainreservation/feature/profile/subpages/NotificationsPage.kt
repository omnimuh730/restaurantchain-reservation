package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.core.model.AppNotification
import com.mh.restaurantchainreservation.core.model.NotificationKind
import com.mh.restaurantchainreservation.core.model.NotificationStore
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class NotifFilter { All, Unread, Read }

private enum class CardAction { None, Open, Mark, Delete }

private data class KindMeta(
    val icon: ImageVector,
    val tint: Color,
    val surface: Color,
    val ring: Color,
    val labelRes: Int,
)

@Composable
fun NotificationsPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val notifications by NotificationStore.notifications.collectAsState()
    var filter by rememberSaveable { mutableStateOf(NotifFilter.All) }

    val unreadCount = remember(notifications) { notifications.count { !it.read } }
    val readCount = notifications.size - unreadCount
    val filtered = remember(notifications, filter) {
        when (filter) {
            NotifFilter.All -> notifications
            NotifFilter.Unread -> notifications.filter { !it.read }
            NotifFilter.Read -> notifications.filter { it.read }
        }
    }
    val nextReservation = remember(notifications) {
        notifications.firstOrNull { it.kind == NotificationKind.Reservation }
    }

    SubpageScaffold(
        title = stringResource(I18nR.string.title_notifications),
        onBack = onBack,
        modifier = modifier,
        horizontalPadding = 16,
        headerActions = {
            AnimatedVisibility(
                visible = unreadCount > 0,
                enter = fadeIn(tween(180)),
                exit = fadeOut(tween(120)),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .border(1.dp, palette.border, RoundedCornerShape(percent = 50))
                        .clickable(onClick = { NotificationStore.markAllAsRead() })
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = stringResource(I18nR.string.notifications_mark_all_read),
                        color = palette.foreground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        },
    ) {
            FilterTabs(
                filter = filter,
                onChange = { filter = it },
                allCount = notifications.size,
                unreadCount = unreadCount,
                readCount = readCount,
            )
            Spacer(Modifier.height(12.dp))

            InboxDigest(unreadCount = unreadCount, readCount = readCount)

            if (nextReservation != null) {
                Spacer(Modifier.height(12.dp))
                FeaturedReservationCard(notification = nextReservation)
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val sectionHeading = when (filter) {
                    NotifFilter.All -> stringResource(I18nR.string.notifications_section_latest)
                    NotifFilter.Unread -> stringResource(I18nR.string.notifications_section_unread)
                    NotifFilter.Read -> stringResource(I18nR.string.notifications_section_read)
                }
                Text(
                    text = sectionHeading,
                    color = palette.foreground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                if (filtered.isNotEmpty()) {
                    val label = when (filter) {
                        NotifFilter.All -> stringResource(I18nR.string.notifications_clear_all)
                        NotifFilter.Unread -> stringResource(I18nR.string.notifications_clear_unread)
                        NotifFilter.Read -> stringResource(I18nR.string.notifications_clear_read)
                    }
                    Text(
                        text = label,
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                when (filter) {
                                    NotifFilter.All -> NotificationStore.clearAll()
                                    NotifFilter.Unread -> NotificationStore.clearUnread()
                                    NotifFilter.Read -> NotificationStore.clearRead()
                                }
                            }
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                    )
                }
            }
            Spacer(Modifier.height(10.dp))

            if (filtered.isEmpty()) {
                EmptyNotificationsState(filter = filter)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filtered.forEach { n ->
                        key(n.id) {
                            SwipeableNotificationRow(notification = n)
                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun FilterTabs(
    filter: NotifFilter,
    onChange: (NotifFilter) -> Unit,
    allCount: Int,
    unreadCount: Int,
    readCount: Int,
) {
    val palette = LocalRestaurantPalette.current
    val activeIndex = when (filter) {
        NotifFilter.All -> 0
        NotifFilter.Unread -> 1
        NotifFilter.Read -> 2
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(palette.mutedSurface)
            .padding(4.dp),
    ) {
        val density = LocalDensity.current
        val tabWidth = remember(maxWidth) { (maxWidth - 0.dp) / 3f }
        val pillOffsetX by animateFloatAsState(
            targetValue = with(density) { (tabWidth * activeIndex).toPx() },
            animationSpec = spring(dampingRatio = 0.79f, stiffness = 360f),
            label = "tab-pill-offset",
        )
        Box(
            modifier = Modifier
                .offset { androidx.compose.ui.unit.IntOffset(pillOffsetX.toInt(), 0) }
                .width(tabWidth)
                .fillMaxHeight()
                .shadow(3.dp, RoundedCornerShape(percent = 50), clip = false)
                .clip(RoundedCornerShape(percent = 50))
                .background(palette.cardSurface),
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            FilterChip(
                label = stringResource(I18nR.string.notifications_tab_all),
                count = allCount,
                active = filter == NotifFilter.All,
                onClick = { onChange(NotifFilter.All) },
            )
            FilterChip(
                label = stringResource(I18nR.string.notifications_tab_unread),
                count = unreadCount,
                active = filter == NotifFilter.Unread,
                onClick = { onChange(NotifFilter.Unread) },
            )
            FilterChip(
                label = stringResource(I18nR.string.notifications_tab_read),
                count = readCount,
                active = filter == NotifFilter.Read,
                onClick = { onChange(NotifFilter.Read) },
            )
        }
    }
}

@Composable
private fun RowScope.FilterChip(
    label: String,
    count: Int,
    active: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(percent = 50))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                color = if (active) palette.foreground else palette.mutedForeground,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (count > 0) {
                Spacer(Modifier.width(4.dp))
                Text(
                    text = count.toString(),
                    color = palette.mutedForeground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun InboxDigest(unreadCount: Int, readCount: Int) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(20.dp)
    val digestSurface = RestaurantColors.Semantic.digestWarm
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, palette.border, shape)
            .background(digestSurface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(palette.cardSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.NotificationsNone,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(18.dp),
            )
            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-4).dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(palette.brand)
                        .border(2.dp, digestSurface, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = unreadCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
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
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(I18nR.string.notifications_digest_subtitle),
                color = palette.mutedForeground,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(28.dp)
                .background(palette.border),
        )
        Spacer(Modifier.width(2.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = readCount.toString(),
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(I18nR.string.notifications_read_label),
                color = palette.mutedForeground,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun FeaturedReservationCard(notification: AppNotification) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, palette.border, shape)
            .background(palette.cardSurface)
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(palette.brandSoftSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Restaurant,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(I18nR.string.notifications_featured_update),
                color = palette.mutedForeground,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = notification.title,
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = notification.message,
                color = palette.mutedForeground,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SwipeableNotificationRow(notification: AppNotification) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val thresholdPx = with(density) { 80.dp.toPx() }
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var rowWidthPx by remember { mutableFloatStateOf(0f) }
    var dismissed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (dismissed) 0f else 1f)
            .onSizeChanged { rowWidthPx = it.width.toFloat() },
    ) {
        val absX = kotlin.math.abs(offsetX.value)
        val revealWidth = with(density) { absX.toDp() }
        if (offsetX.value > 1f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(palette.success.copy(alpha = 0.85f)),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(
                    modifier = Modifier
                        .width(revealWidth.coerceAtLeast(0.dp))
                        .fillMaxHeight()
                        .padding(start = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                    if (absX > thresholdPx * 0.8f) {
                        Text(
                            text = stringResource(I18nR.string.notifications_action_mark_read_aria),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        } else if (offsetX.value < -1f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(palette.destructive.copy(alpha = 0.92f)),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Row(
                    modifier = Modifier
                        .width(revealWidth.coerceAtLeast(0.dp))
                        .fillMaxHeight()
                        .padding(end = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (absX > thresholdPx * 0.8f) {
                        Text(
                            text = stringResource(I18nR.string.notifications_action_delete_aria),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }

        val cardAlpha = if (rowWidthPx > 0f) {
            (1f - (absX / rowWidthPx).coerceIn(0f, 1f) * 0.4f).coerceAtLeast(0.6f)
        } else {
            1f
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { androidx.compose.ui.unit.IntOffset(offsetX.value.toInt(), 0) }
                .alpha(cardAlpha)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            val newValue = (offsetX.value + delta).coerceIn(
                                -rowWidthPx.coerceAtLeast(0f),
                                rowWidthPx.coerceAtLeast(0f),
                            )
                            offsetX.snapTo(newValue)
                        }
                    },
                    onDragStopped = {
                        scope.launch {
                            val current = offsetX.value
                            when {
                                current >= thresholdPx -> {
                                    offsetX.animateTo(
                                        targetValue = thresholdPx,
                                        animationSpec = spring(dampingRatio = 0.7f, stiffness = 380f),
                                    )
                                    if (!notification.read) {
                                        NotificationStore.markAsRead(notification.id)
                                    }
                                    delay(140)
                                    offsetX.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 320f),
                                    )
                                }
                                current <= -thresholdPx -> {
                                    val target = -rowWidthPx.coerceAtLeast(thresholdPx * 4f)
                                    offsetX.animateTo(
                                        targetValue = target,
                                        animationSpec = tween(180),
                                    )
                                    dismissed = true
                                    NotificationStore.delete(notification.id)
                                }
                                else -> {
                                    offsetX.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(dampingRatio = 0.7f, stiffness = 380f),
                                    )
                                }
                            }
                        }
                    },
                ),
        ) {
            NotificationCard(notification = notification)
        }
    }
}

@Composable
private fun NotificationCard(notification: AppNotification) {
    val palette = LocalRestaurantPalette.current
    val meta = remember(notification.kind, palette) { kindMetaFor(notification.kind, palette) }
    var action by remember { mutableStateOf(CardAction.None) }

    LaunchedEffect(action) {
        when (action) {
            CardAction.Open -> {
                delay(155)
                if (!notification.read) NotificationStore.markAsRead(notification.id)
                action = CardAction.None
            }
            CardAction.Mark -> {
                delay(520)
                NotificationStore.markAsRead(notification.id)
                action = CardAction.None
            }
            CardAction.Delete -> {
                delay(180)
                NotificationStore.delete(notification.id)
            }
            CardAction.None -> Unit
        }
    }

    val flashAlpha = remember { Animatable(0f) }
    LaunchedEffect(action) {
        if (action == CardAction.Open || action == CardAction.Mark) {
            flashAlpha.snapTo(0f)
            flashAlpha.animateTo(
                1f,
                animationSpec = tween(durationMillis = 110, easing = FastOutSlowInEasing),
            )
            flashAlpha.animateTo(
                0f,
                animationSpec = tween(durationMillis = 230, easing = FastOutSlowInEasing),
            )
        } else {
            flashAlpha.snapTo(0f)
        }
    }

    val cardScale by animateFloatAsState(
        targetValue = if (action == CardAction.Delete) 0.94f else 1f,
        animationSpec = tween(180),
        label = "card-scale",
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (action == CardAction.Delete) 0f else 1f,
        animationSpec = tween(180),
        label = "card-alpha",
    )

    val shape = RoundedCornerShape(18.dp)
    val borderColor = if (notification.read) {
        palette.border
    } else {
        palette.brand.copy(alpha = 0.22f)
    }
    val flashColor: Color? = when (action) {
        CardAction.Mark -> palette.success.copy(alpha = 0.055f)
        CardAction.Open -> palette.brand.copy(alpha = 0.045f)
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardScale)
            .alpha(cardAlpha)
            .shadow(2.dp, shape, clip = false)
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .background(palette.cardSurface)
            .clickable(enabled = action == CardAction.None) {
                action = CardAction.Open
            },
    ) {
        if (!notification.read) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(palette.brand, RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)),
                )
            }
        }
        if (flashColor != null && flashAlpha.value > 0f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .alpha(flashAlpha.value)
                    .background(flashColor),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(meta.ring),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(meta.surface),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = meta.icon,
                        contentDescription = null,
                        tint = meta.tint,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(meta.labelRes).uppercase(),
                                color = palette.mutedForeground,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = palette.mutedForeground,
                                modifier = Modifier.size(11.dp),
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = notification.time,
                                color = palette.mutedForeground,
                                fontSize = 11.sp,
                            )
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = notification.title,
                            color = palette.foreground,
                            fontSize = 15.sp,
                            fontWeight = if (notification.read) FontWeight.Medium else FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        if (!notification.read) {
                            UnreadDot()
                            Spacer(Modifier.width(4.dp))
                            MarkAsReadButton(
                                action = action,
                                successColor = palette.success,
                                onClick = {
                                    if (action == CardAction.None) action = CardAction.Mark
                                },
                            )
                        }
                        DeleteButton(
                            action = action,
                            destructive = palette.brand,
                            mutedForeground = palette.mutedForeground,
                            onClick = {
                                if (action == CardAction.None) action = CardAction.Delete
                            },
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun UnreadDot() {
    val palette = LocalRestaurantPalette.current
    val transition = rememberInfiniteTransition(label = "unread-dot")
    val pulse by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "unread-dot-pulse",
    )
    Box(
        modifier = Modifier.size(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .alpha(pulse * 0.35f)
                .clip(CircleShape)
                .background(palette.brand.copy(alpha = 0.5f)),
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(palette.brand),
        )
    }
}

@Composable
private fun MarkAsReadButton(
    action: CardAction,
    successColor: Color,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val isMarking = action == CardAction.Mark

    val checkScale = remember { Animatable(1f) }
    val checkRotation = remember { Animatable(0f) }
    val ringScale = remember { Animatable(0.75f) }
    val ringAlpha = remember { Animatable(0f) }
    val confettiProgress = remember { Animatable(0f) }

    LaunchedEffect(isMarking) {
        if (isMarking) {
            checkScale.snapTo(1f)
            checkRotation.snapTo(0f)
            ringScale.snapTo(0.75f)
            ringAlpha.snapTo(0.9f)
            confettiProgress.snapTo(0f)

            coroutineScope {
                launch {
                    checkScale.animateTo(
                        1f,
                        animationSpec = keyframes {
                            durationMillis = 500
                            1f at 0
                            0.55f at 100
                            1.42f at 280
                            1f at 500
                        },
                    )
                }
                launch {
                    checkRotation.animateTo(
                        0f,
                        animationSpec = keyframes {
                            durationMillis = 500
                            0f at 0
                            -16f at 100
                            12f at 280
                            0f at 500
                        },
                    )
                }
                launch {
                    ringScale.animateTo(
                        2.2f,
                        animationSpec = tween(480, easing = FastOutSlowInEasing),
                    )
                }
                launch {
                    ringAlpha.animateTo(0f, animationSpec = tween(480, easing = FastOutSlowInEasing))
                }
                launch {
                    confettiProgress.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
                }
            }
        } else {
            checkScale.snapTo(1f)
            checkRotation.snapTo(0f)
            ringAlpha.snapTo(0f)
            confettiProgress.snapTo(0f)
        }
    }

    Box(
        modifier = Modifier.size(34.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isMarking && ringAlpha.value > 0f) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .scale(ringScale.value)
                    .alpha(ringAlpha.value)
                    .clip(CircleShape)
                    .border(1.dp, successColor.copy(alpha = 0.45f), CircleShape),
            )
        }
        if (isMarking && confettiProgress.value > 0f) {
            val progress = confettiProgress.value
            val radius = 17f * progress
            val opacity = when {
                progress < 0.2f -> progress / 0.2f
                progress > 0.8f -> (1f - progress) / 0.2f
                else -> 1f
            }
            for (i in 0 until 6) {
                val angle = (i.toFloat() / 6f) * (Math.PI.toFloat() * 2f)
                val xOffset = (cos(angle) * radius).dp
                val yOffset = (sin(angle) * radius).dp
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .offset(x = xOffset, y = yOffset)
                        .alpha(opacity.coerceIn(0f, 1f))
                        .clip(CircleShape)
                        .background(successColor),
                )
            }
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (isMarking) successColor.copy(alpha = 0.12f) else Color.Transparent)
                .clickable(enabled = action == CardAction.None) { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = stringResource(I18nR.string.notifications_action_mark_read_aria),
                tint = if (isMarking) successColor else palette.mutedForeground,
                modifier = Modifier
                    .size(14.dp)
                    .scale(checkScale.value)
                    .rotate(checkRotation.value),
            )
        }
    }
}

@Composable
private fun DeleteButton(
    action: CardAction,
    destructive: Color,
    mutedForeground: Color,
    onClick: () -> Unit,
) {
    val isDeleting = action == CardAction.Delete
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    val iconAlpha = remember { Animatable(1f) }

    LaunchedEffect(isDeleting) {
        if (isDeleting) {
            scale.snapTo(1f)
            rotation.snapTo(0f)
            iconAlpha.snapTo(1f)
            coroutineScope {
                launch {
                    scale.animateTo(
                        0.82f,
                        animationSpec = keyframes {
                            durationMillis = 180
                            1f at 0
                            1.16f at 60
                            0.82f at 180
                        },
                    )
                }
                launch {
                    rotation.animateTo(
                        8f,
                        animationSpec = keyframes {
                            durationMillis = 180
                            0f at 0
                            -10f at 60
                            8f at 180
                        },
                    )
                }
                launch {
                    iconAlpha.animateTo(
                        0f,
                        animationSpec = keyframes {
                            durationMillis = 180
                            1f at 0
                            1f at 90
                            0f at 180
                        },
                    )
                }
            }
        } else {
            scale.snapTo(1f)
            rotation.snapTo(0f)
            iconAlpha.snapTo(1f)
        }
    }

    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .clickable(enabled = action == CardAction.None) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.DeleteOutline,
            contentDescription = stringResource(I18nR.string.notifications_action_delete_aria),
            tint = if (isDeleting) destructive else mutedForeground,
            modifier = Modifier
                .size(14.dp)
                .scale(scale.value)
                .rotate(rotation.value)
                .alpha(iconAlpha.value),
        )
    }
}

private fun kindMetaFor(kind: NotificationKind, palette: RestaurantPalette): KindMeta {
    return when (kind) {
        NotificationKind.Reservation -> KindMeta(
            icon = Icons.Outlined.Restaurant,
            tint = palette.brand,
            surface = palette.brandSoftSurface,
            ring = palette.brand.copy(alpha = 0.12f),
            labelRes = I18nR.string.notifications_kind_reservation,
        )
        NotificationKind.Promo -> KindMeta(
            icon = Icons.Outlined.LocalOffer,
            tint = palette.amberAccent.onContainer,
            surface = palette.amberAccent.container,
            ring = palette.amberAccent.onContainer.copy(alpha = 0.12f),
            labelRes = I18nR.string.notifications_kind_offer,
        )
        NotificationKind.Reward -> KindMeta(
            icon = Icons.Outlined.CardGiftcard,
            tint = palette.emeraldAccent.onContainer,
            surface = palette.emeraldAccent.container,
            ring = palette.emeraldAccent.onContainer.copy(alpha = 0.12f),
            labelRes = I18nR.string.notifications_kind_reward,
        )
        NotificationKind.System -> KindMeta(
            icon = Icons.Outlined.AutoAwesome,
            tint = palette.foreground,
            surface = palette.mutedSurface,
            ring = palette.foreground.copy(alpha = 0.08f),
            labelRes = I18nR.string.notifications_kind_update,
        )
        NotificationKind.Review -> KindMeta(
            icon = Icons.Outlined.ChatBubbleOutline,
            tint = palette.violetAccent.onContainer,
            surface = palette.violetAccent.container,
            ring = palette.violetAccent.onContainer.copy(alpha = 0.12f),
            labelRes = I18nR.string.notifications_kind_review,
        )
        NotificationKind.Share -> KindMeta(
            icon = Icons.Outlined.Share,
            tint = palette.blueAccent.onContainer,
            surface = palette.blueAccent.container,
            ring = palette.blueAccent.onContainer.copy(alpha = 0.12f),
            labelRes = I18nR.string.notifications_kind_shared,
        )
    }
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
            .padding(top = 36.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        EmptyIllustration()
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(titleRes),
            color = palette.foreground,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(bodyRes),
            color = palette.mutedForeground,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
    }
}

@Composable
private fun EmptyIllustration() {
    val palette = LocalRestaurantPalette.current

    val leftOffsetX = remember { Animatable(-60f) }
    val leftOffsetY = remember { Animatable(-30f) }
    val leftAlpha = remember { Animatable(0f) }

    val rightOffsetX = remember { Animatable(60f) }
    val rightOffsetY = remember { Animatable(-30f) }
    val rightAlpha = remember { Animatable(0f) }

    val centerOffsetY = remember { Animatable(30f) }
    val centerAlpha = remember { Animatable(0f) }
    val centerScale = remember { Animatable(0.72f) }

    LaunchedEffect(Unit) {
        coroutineScope {
            launch {
                leftOffsetX.animateTo(
                    targetValue = -22f,
                    animationSpec = spring(dampingRatio = 0.62f, stiffness = 140f),
                )
            }
            launch {
                leftOffsetY.animateTo(
                    targetValue = 12f,
                    animationSpec = spring(dampingRatio = 0.62f, stiffness = 140f),
                )
            }
            launch {
                leftAlpha.animateTo(0.78f, animationSpec = tween(420))
            }
            launch {
                rightOffsetX.animateTo(
                    targetValue = 22f,
                    animationSpec = spring(dampingRatio = 0.62f, stiffness = 140f),
                )
            }
            launch {
                rightOffsetY.animateTo(
                    targetValue = 8f,
                    animationSpec = spring(dampingRatio = 0.62f, stiffness = 140f),
                )
            }
            launch {
                rightAlpha.animateTo(0.86f, animationSpec = tween(420))
            }
            launch {
                centerOffsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(dampingRatio = 0.62f, stiffness = 140f),
                )
            }
            launch { centerAlpha.animateTo(1f, animationSpec = tween(420)) }
            launch {
                centerScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = 0.62f, stiffness = 140f),
                )
            }
        }
    }

    val sway = rememberInfiniteTransition(label = "empty-sway")
    val leftRotation by sway.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "left-sway",
    )
    val rightRotation by sway.animateFloat(
        initialValue = 8f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "right-sway",
    )
    val cardScale by sway.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3000
                1f at 0
                1.04f at 1500
                1f at 3000
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "card-pulse",
    )
    val bellAlpha by sway.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bell-alpha",
    )

    Box(
        modifier = Modifier.size(width = 168.dp, height = 132.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 120.dp, height = 80.dp)
                .offset(y = 14.dp)
                .alpha(0.45f)
                .clip(RoundedCornerShape(24.dp))
                .background(palette.brand.copy(alpha = 0.10f)),
        )

        BellCard(
            offsetX = leftOffsetX.value.dp,
            offsetY = leftOffsetY.value.dp,
            rotation = leftRotation,
            scale = cardScale * 0.98f,
            alpha = leftAlpha.value,
            background = palette.mutedSurface,
            borderColor = palette.border,
            bellTint = palette.brand.copy(alpha = 0.55f),
            bellAlpha = bellAlpha,
        )

        BellCard(
            offsetX = rightOffsetX.value.dp,
            offsetY = rightOffsetY.value.dp,
            rotation = rightRotation,
            scale = cardScale * 0.98f,
            alpha = rightAlpha.value,
            background = palette.cardSurface,
            borderColor = palette.border,
            bellTint = palette.brand.copy(alpha = 0.55f),
            bellAlpha = bellAlpha,
        )

        Box(
            modifier = Modifier
                .size(88.dp)
                .offset(x = 0.dp, y = centerOffsetY.value.dp)
                .alpha(centerAlpha.value)
                .scale(centerScale.value * cardScale)
                .clip(RoundedCornerShape(22.dp))
                .background(palette.cardSurface)
                .border(1.dp, palette.border, RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, palette.brand.copy(alpha = 0.18f), RoundedCornerShape(18.dp)),
            )
            Icon(
                imageVector = Icons.Outlined.NotificationsNone,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier
                    .size(32.dp)
                    .alpha(bellAlpha),
            )
        }
    }
}

@Composable
private fun BellCard(
    offsetX: androidx.compose.ui.unit.Dp,
    offsetY: androidx.compose.ui.unit.Dp,
    rotation: Float,
    scale: Float,
    alpha: Float,
    background: Color,
    borderColor: Color,
    bellTint: Color,
    bellAlpha: Float,
) {
    Box(
        modifier = Modifier
            .size(width = 96.dp, height = 80.dp)
            .offset(x = offsetX, y = offsetY)
            .alpha(alpha)
            .scale(scale)
            .rotate(rotation)
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.NotificationsNone,
            contentDescription = null,
            tint = bellTint,
            modifier = Modifier
                .size(24.dp)
                .alpha(bellAlpha),
        )
    }
}
