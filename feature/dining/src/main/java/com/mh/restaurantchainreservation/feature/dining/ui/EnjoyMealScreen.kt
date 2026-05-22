package com.mh.restaurantchainreservation.feature.dining.ui

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.trackBottomNavScroll
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking
import com.mh.restaurantchainreservation.feature.dining.data.parseBookingDateTime
import com.mh.restaurantchainreservation.feature.dining.ui.modals.MenuModal
import com.mh.restaurantchainreservation.feature.dining.ui.modals.MenuVariant
import kotlinx.coroutines.delay
import java.util.Date

enum class EnjoyMode { Live, Upcoming }

private data class EnjoyAction(
    val key: String,
    val icon: ImageVector,
    val labelRes: Int,
    val descRes: Int? = null,
    val descRaw: String? = null,
    val primary: Boolean = false,
    val onClick: () -> Unit,
)

@Composable
fun EnjoyMealScreen(
    booking: Booking,
    mode: EnjoyMode,
    onBack: () -> Unit,
    onShowQR: () -> Unit,
    onScanQR: () -> Unit,
    onScanPay: () -> Unit,
    onInvite: () -> Unit,
    onOpenDirections: () -> Unit,
    onCallServer: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val isLive = mode == EnjoyMode.Live
    val start = remember(booking.id) { parseBookingDateTime(booking) }

    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(booking.id) {
        while (true) {
            delay(1000L)
            now = Date()
        }
    }

    val timeLabel = when {
        start == null -> "--"
        isLive -> stringResource(I18nR.string.enjoy_seated_for, formatElapsed(start, now))
        else -> stringResource(I18nR.string.enjoy_starts_in, formatRemaining(start, now))
    }

    var showMenu by remember { mutableStateOf(false) }

    val actions: List<EnjoyAction> = if (isLive) listOf(
        EnjoyAction("pay", Icons.Outlined.Receipt, I18nR.string.enjoy_action_pay, I18nR.string.enjoy_action_pay_desc, primary = true, onClick = onScanPay),
        EnjoyAction("menu", Icons.Outlined.MenuBook, I18nR.string.enjoy_action_menu, I18nR.string.enjoy_action_menu_desc, onClick = { showMenu = true }),
        EnjoyAction("server", Icons.Outlined.NotificationsActive, I18nR.string.enjoy_action_server, I18nR.string.enjoy_action_server_desc, onClick = onCallServer),
    ) else listOf(
        EnjoyAction("showqr", Icons.Outlined.QrCode, I18nR.string.enjoy_action_show_qr, I18nR.string.enjoy_action_show_qr_desc, primary = true, onClick = onShowQR),
        EnjoyAction("invite", Icons.Outlined.PersonAdd, I18nR.string.enjoy_action_invite, I18nR.string.enjoy_action_invite_desc, onClick = onInvite),
        EnjoyAction("directions", Icons.Outlined.Navigation, I18nR.string.enjoy_action_directions, descRaw = booking.address, onClick = onOpenDirections),
        EnjoyAction("scanqr", Icons.Outlined.QrCodeScanner, I18nR.string.enjoy_action_scanqr, I18nR.string.enjoy_action_scanqr_desc, onClick = onScanQR),
        EnjoyAction("menu", Icons.Outlined.MenuBook, I18nR.string.enjoy_action_preview_menu, I18nR.string.enjoy_action_preview_menu_desc, onClick = { showMenu = true }),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.pageBackground),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.mutedSurface)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.ArrowBack, stringResource(I18nR.string.enjoy_back), tint = palette.foreground, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(if (isLive) I18nR.string.enjoy_now_dining else I18nR.string.enjoy_upcoming_reservation),
                    color = palette.foreground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = booking.restaurant,
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    maxLines = 1,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .trackBottomNavScroll()
                .padding(horizontal = 16.dp),
        ) {
            // Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(28.dp)),
            ) {
                AsyncImage(
                    model = booking.image,
                    contentDescription = booking.restaurant,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    RestaurantColors.Overlay.borderSubtle,
                                    RestaurantColors.Base.black.copy(alpha = 0.25f),
                                    RestaurantColors.Base.black.copy(alpha = 0.82f),
                                ),
                            ),
                        ),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusPill(
                            text = stringResource(if (isLive) I18nR.string.enjoy_status_live else I18nR.string.enjoy_status_upcoming),
                            icon = if (isLive) Icons.Outlined.RestaurantMenu else Icons.Outlined.HourglassEmpty,
                            background = if (isLive) palette.success else palette.brand,
                        )
                        StatusPill(
                            text = timeLabel,
                            icon = Icons.Outlined.AccessTime,
                            background = RestaurantColors.Base.white.copy(alpha = 0.16f),
                            translucent = true,
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = booking.restaurant,
                            color = RestaurantColors.Base.white,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        Icon(Icons.Filled.ChevronRight, null, tint = RestaurantColors.Base.white.copy(alpha = 0.75f), modifier = Modifier.size(20.dp))
                    }
                    Text(
                        text = booking.cuisine,
                        color = RestaurantColors.Base.white.copy(alpha = 0.78f),
                        fontSize = 14.sp,
                        maxLines = 1,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DetailChip(
                    icon = Icons.Outlined.Group,
                    label = "${booking.guests} ${if (booking.guests == 1) "guest" else "guests"}",
                    modifier = Modifier.weight(1f),
                )
                DetailChip(
                    icon = Icons.Outlined.AccessTime,
                    label = booking.time,
                    modifier = Modifier.weight(1f),
                )
                DetailChip(
                    icon = Icons.Outlined.LocationOn,
                    label = booking.seating,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(20.dp))
            // Session card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(24.dp))
                    .background(palette.cardSurface)
                    .padding(16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(palette.brand.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Shield, null, tint = palette.brand, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(
                        text = stringResource(if (isLive) I18nR.string.enjoy_session_active_title else I18nR.string.enjoy_ready_title),
                        color = palette.foreground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = if (isLive) stringResource(I18nR.string.enjoy_session_active_desc)
                        else stringResource(I18nR.string.enjoy_ready_desc, booking.confirmationNo),
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(if (isLive) I18nR.string.enjoy_at_the_table else I18nR.string.enjoy_before_arrive),
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 4.dp),
            )
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                actions.forEachIndexed { index, action ->
                    StaggeredActionCard(action = action, indexInGroup = index)
                }
            }

            if (!isLive) {
                Spacer(Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(palette.mutedSurface.copy(alpha = 0.7f))
                        .padding(16.dp),
                ) {
                    Text(
                        text = stringResource(I18nR.string.enjoy_checklist_title),
                        color = palette.foreground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Spacer(Modifier.height(10.dp))
                    listOf(
                        I18nR.string.enjoy_checklist_qr,
                        I18nR.string.enjoy_checklist_share,
                        I18nR.string.enjoy_checklist_early,
                    ).forEach { res ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(palette.brand),
                            )
                            Text(text = stringResource(res), color = palette.mutedForeground, fontSize = 13.sp)
                        }
                    }
                }
            }

            if (isLive) {
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(palette.brand)
                        .clickable(onClick = onScanPay),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(Icons.Outlined.Receipt, null, tint = RestaurantColors.Base.white, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = stringResource(I18nR.string.enjoy_action_pay),
                        color = RestaurantColors.Base.white,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    if (showMenu) {
        MenuModal(
            booking = booking,
            variant = if (isLive) MenuVariant.Order else MenuVariant.Preview,
            onDismiss = { showMenu = false },
        )
    }
}

@Composable
private fun StatusPill(
    text: String,
    icon: ImageVector,
    background: Color,
    translucent: Boolean = false,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(background)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icon, null, tint = RestaurantColors.Base.white, modifier = Modifier.size(14.dp))
        Text(text = text, color = RestaurantColors.Base.white, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
    }
    @Suppress("UNUSED_EXPRESSION") translucent
}

@Composable
private fun DetailChip(icon: ImageVector, label: String, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(palette.mutedSurface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, null, tint = palette.mutedForeground, modifier = Modifier.size(16.dp))
        Text(text = label, color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
    }
}

@Composable
private fun StaggeredActionCard(action: EnjoyAction, indexInGroup: Int) {
    val palette = LocalRestaurantPalette.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val translateY by animateFloatAsState(
        targetValue = if (visible) 0f else 8f,
        animationSpec = tween(durationMillis = 220, delayMillis = indexInGroup * 35),
        label = "enjoy_y",
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 220, delayMillis = indexInGroup * 35),
        label = "enjoy_alpha",
    )

    val container = if (action.primary) palette.brand.copy(alpha = 0.08f) else palette.cardSurface
    val borderColor = if (action.primary) palette.brand.copy(alpha = 0.24f) else palette.border
    val iconBg = if (action.primary) palette.brand else palette.mutedSurface
    val iconFg = if (action.primary) RestaurantColors.Base.white else palette.foreground

    Row(
        modifier = Modifier
            .graphicsLayer {
                translationY = translateY
                this.alpha = alpha
            }
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, borderColor, RoundedCornerShape(22.dp))
            .background(container)
            .clickable(onClick = action.onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(action.icon, null, tint = iconFg, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(action.labelRes),
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
            )
            val desc = action.descRaw ?: action.descRes?.let { stringResource(it) }
            if (desc != null) {
                Text(text = desc, color = palette.mutedForeground, fontSize = 12.sp, maxLines = 1)
            }
        }
        Icon(Icons.Filled.ChevronRight, null, tint = palette.mutedForeground, modifier = Modifier.size(18.dp))
    }
}

private fun formatElapsed(start: Date, now: Date): String {
    val diff = (now.time - start.time).coerceAtLeast(0L)
    val totalMin = (diff / 60_000L).toInt()
    val h = totalMin / 60
    val m = totalMin % 60
    val s = ((diff % 60_000L) / 1000L).toInt()
    return if (h > 0) "${h}h ${m}m" else String.format("%d:%02d", m, s)
}

private fun formatRemaining(target: Date, now: Date): String {
    val diff = (target.time - now.time).coerceAtLeast(0L)
    val totalMin = (diff / 60_000L).toInt()
    val days = totalMin / (60 * 24)
    val hours = (totalMin % (60 * 24)) / 60
    val minutes = totalMin % 60
    if (days > 0) return "${days}d ${hours}h"
    if (hours > 0) return String.format("%dh %02dm", hours, minutes)
    val seconds = ((diff % 60_000L) / 1000L).toInt()
    return String.format("%d:%02d", minutes, seconds)
}
