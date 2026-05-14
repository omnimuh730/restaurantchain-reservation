package com.mh.restaurantchainreservation.feature.dining.ui

import android.content.ClipData
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.HeartDrawableIcon
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking
import com.mh.restaurantchainreservation.feature.dining.data.BookingStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookingDetailScreen(
    booking: Booking,
    onBack: () -> Unit,
    onManage: () -> Unit,
    onScanQR: () -> Unit,
    onShowQR: () -> Unit,
    onInvite: () -> Unit,
    onBookAgain: () -> Unit,
    onViewReceipt: () -> Unit,
    onDeleteRequest: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()

    var saved by remember { mutableStateOf(false) }
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(1400L)
            copied = false
        }
    }

    val isScheduled = booking.status == BookingStatus.Confirmed
    val isPending = booking.status == BookingStatus.Pending
    val isRejected = booking.status == BookingStatus.Rejected
    val isCancelled = booking.status == BookingStatus.Cancelled || booking.status == BookingStatus.NoShow
    val isCompleted = booking.status == BookingStatus.Completed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.cardSurface),
    ) {
        // Sticky header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.cardSurface.copy(alpha = 0.92f))
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            HeaderCircleButton(
                icon = Icons.Filled.ChevronLeft,
                onClick = onBack,
                contentDescription = stringResource(I18nR.string.detail_header_back),
            )
            Text(
                text = stringResource(I18nR.string.detail_header_title),
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            HeaderCircleButton(
                icon = Icons.Outlined.Share,
                onClick = onInvite,
                contentDescription = stringResource(I18nR.string.detail_header_share),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp),
        ) {
            // Hero image
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
                                    Color.Black.copy(alpha = 0.10f),
                                    Color.Black.copy(alpha = 0.20f),
                                    Color.Black.copy(alpha = 0.80f),
                                ),
                            ),
                        ),
                )
                // Save heart
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.92f))
                        .clickable { saved = !saved },
                    contentAlignment = Alignment.Center,
                ) {
                    val heartScale by animateFloatAsState(
                        targetValue = if (saved) 1.2f else 1f,
                        animationSpec = spring(stiffness = 380f, dampingRatio = 0.45f),
                        label = "heart_scale",
                    )
                    HeartDrawableIcon(
                        active = saved,
                        contentDescription = stringResource(if (saved) I18nR.string.detail_unsave else I18nR.string.detail_save),
                        modifier = Modifier.graphicsLayer { scaleX = heartScale; scaleY = heartScale },
                        iconHeight = 20.dp,
                    )
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                ) {
                    StatusBadge(booking = booking, checkedInIds = null, modifier = Modifier)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = booking.restaurant,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                    )
                    Text(
                        text = booking.cuisine,
                        color = Color.White.copy(alpha = 0.82f),
                        fontSize = 14.sp,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // 3-tile info row
            val parts = booking.date.split(",")
            val day = parts.firstOrNull()?.trim() ?: booking.date
            val date = if (parts.size > 1) parts.drop(1).joinToString(",").trim() else booking.date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InfoTile(
                    icon = Icons.Outlined.Group,
                    label = stringResource(I18nR.string.detail_party),
                    value = "${booking.guests} ${if (booking.guests == 1) "guest" else "guests"}",
                    modifier = Modifier.weight(1f),
                )
                InfoTile(
                    icon = Icons.Outlined.AccessTime,
                    label = day,
                    value = date,
                    modifier = Modifier.weight(1f),
                )
                InfoTile(
                    icon = Icons.Outlined.AccessTime,
                    label = booking.seating,
                    value = booking.time,
                    modifier = Modifier.weight(1f),
                )
            }

            // Confirmation strip + actions for confirmed bookings
            if (isScheduled) {
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, palette.brand.copy(alpha = 0.18f), RoundedCornerShape(24.dp))
                        .background(palette.brand.copy(alpha = 0.06f))
                        .padding(14.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(I18nR.string.booking_confirmation_label).uppercase(),
                                color = palette.brand,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                            )
                            Text(
                                text = booking.confirmationNo,
                                color = palette.foreground,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                            )
                        }
                        val copyAlpha by animateFloatAsState(targetValue = if (copied) 0f else 1f, label = "copy_alpha")
                        val checkAlpha by animateFloatAsState(targetValue = if (copied) 1f else 0f, label = "check_alpha")
                        val checkScale by animateFloatAsState(
                            targetValue = if (copied) 1f else 0.4f,
                            animationSpec = spring(stiffness = 380f, dampingRatio = 0.55f),
                            label = "check_scale",
                        )
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(palette.cardSurface)
                                .clickable {
                                    scope.launch {
                                        clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Confirmation Code", booking.confirmationNo)))
                                        copied = true
                                    }
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Outlined.ContentCopy,
                                null,
                                tint = palette.mutedForeground,
                                modifier = Modifier
                                    .size(16.dp)
                                    .graphicsLayer { alpha = copyAlpha },
                            )
                            Icon(
                                Icons.Filled.Check,
                                null,
                                tint = palette.brand,
                                modifier = Modifier
                                    .size(16.dp)
                                    .graphicsLayer {
                                        alpha = checkAlpha
                                        scaleX = checkScale
                                        scaleY = checkScale
                                    },
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailActionButton(
                            text = stringResource(I18nR.string.detail_show_qr),
                            icon = Icons.Outlined.QrCode,
                            primary = true,
                            onClick = onShowQR,
                            modifier = Modifier.weight(1f),
                        )
                        DetailActionButton(
                            text = stringResource(I18nR.string.manage_title),
                            icon = null,
                            primary = false,
                            onClick = onManage,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailActionButton(
                            text = stringResource(I18nR.string.scan_step_scan),
                            icon = Icons.Outlined.QrCode,
                            primary = false,
                            onClick = onScanQR,
                            modifier = Modifier.weight(1f),
                        )
                        DetailActionButton(
                            text = stringResource(I18nR.string.detail_invite),
                            icon = Icons.Outlined.PersonAdd,
                            primary = false,
                            onClick = onInvite,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Pending block
            if (isPending) {
                Spacer(Modifier.height(16.dp))
                StatusInfoBlock(
                    title = stringResource(I18nR.string.detail_pending_title),
                    description = stringResource(I18nR.string.detail_pending_desc),
                    accent = palette.warning,
                    container = palette.warning.copy(alpha = 0.10f),
                ) {
                    Text(
                        text = stringResource(I18nR.string.detail_request_code).uppercase(),
                        color = palette.mutedForeground,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = booking.confirmationNo,
                        color = palette.foreground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }

            // Rejected block with actions
            if (isRejected) {
                Spacer(Modifier.height(16.dp))
                StatusInfoBlock(
                    title = stringResource(I18nR.string.detail_rejected_title),
                    description = stringResource(I18nR.string.detail_rejected_desc),
                    accent = palette.destructive,
                    container = palette.destructive.copy(alpha = 0.10f),
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailActionButton(
                            text = "Request again",
                            icon = Icons.Outlined.Refresh,
                            primary = true,
                            onClick = onBookAgain,
                            modifier = Modifier.weight(1f),
                        )
                        DetailActionButton(
                            text = "Delete",
                            icon = null,
                            primary = false,
                            destructive = true,
                            onClick = onDeleteRequest,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Restaurant action rows
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(I18nR.string.detail_section_restaurant),
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 4.dp),
            )
            Spacer(Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailActionRow(
                    icon = Icons.Outlined.RestaurantMenu,
                    title = stringResource(I18nR.string.detail_action_browse_menu),
                    subtitle = stringResource(I18nR.string.detail_action_browse_menu_desc),
                    tone = ActionRowTone.Primary,
                    onClick = { /* could open menu modal */ },
                )
                DetailActionRow(
                    icon = Icons.Outlined.Navigation,
                    title = stringResource(I18nR.string.detail_action_directions),
                    subtitle = booking.address,
                    tone = ActionRowTone.Default,
                    onClick = {},
                )
                DetailActionRow(
                    icon = Icons.Outlined.Call,
                    title = stringResource(I18nR.string.detail_action_call),
                    subtitle = booking.phone,
                    tone = ActionRowTone.Default,
                    onClick = {},
                )
            }

            // Details (occasion + special request)
            if (booking.occasion != null || booking.specialRequest != null) {
                Spacer(Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(24.dp))
                        .background(palette.cardSurface)
                        .padding(16.dp),
                ) {
                    Text(
                        text = stringResource(I18nR.string.detail_section_details),
                        color = palette.foreground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    if (booking.occasion != null) {
                        Spacer(Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            val occasions = listOf(
                                stringResource(I18nR.string.detail_occasion_birthday),
                                stringResource(I18nR.string.detail_occasion_anniversary),
                                stringResource(I18nR.string.detail_occasion_date),
                                stringResource(I18nR.string.detail_occasion_special),
                            )
                            occasions.forEach { occ ->
                                val selected = booking.occasion.equals(occ, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(percent = 50))
                                        .background(if (selected) palette.brand.copy(alpha = 0.08f) else Color.Transparent)
                                        .border(1.dp, if (selected) palette.brand else palette.border, RoundedCornerShape(percent = 50))
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                ) {
                                    Text(
                                        text = occ,
                                        color = if (selected) palette.brand else palette.mutedForeground,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                    if (booking.specialRequest != null) {
                        Spacer(Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(palette.mutedSurface.copy(alpha = 0.7f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = booking.specialRequest,
                                color = palette.mutedForeground,
                                fontSize = 14.sp,
                                lineHeight = 19.sp,
                            )
                        }
                    }
                }
            }

            // Location
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(I18nR.string.detail_section_location),
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 4.dp),
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.padding(start = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(Icons.Outlined.LocationOn, null, tint = palette.brand, modifier = Modifier.size(16.dp))
                Text(
                    text = booking.address,
                    color = palette.mutedForeground,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                )
            }
            Spacer(Modifier.height(10.dp))
            BookingMapPreview(address = booking.address)

            // Dining points
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(24.dp))
                    .background(palette.cardSurface)
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(I18nR.string.detail_dining_points), color = palette.mutedForeground, fontSize = 12.sp)
                        Text(
                            text = when {
                                booking.diningPoints <= 0 -> stringResource(I18nR.string.detail_dining_points_none)
                                isCompleted -> stringResource(I18nR.string.detail_dining_points_earned, booking.diningPoints)
                                else -> stringResource(I18nR.string.detail_dining_points_pending, booking.diningPoints)
                            },
                            color = palette.foreground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(palette.warning.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Star, null, tint = palette.warning, modifier = Modifier.size(20.dp))
                    }
                }
                if (isCompleted && booking.rating != null) {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(palette.mutedSurface.copy(alpha = 0.7f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(Icons.Filled.Star, null, tint = palette.warning, modifier = Modifier.size(16.dp))
                        Text(text = "${booking.rating}", color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = stringResource(I18nR.string.detail_your_rating), color = palette.mutedForeground, fontSize = 13.sp)
                    }
                }
            }

            // Trailing actions
            if (isCancelled) {
                Spacer(Modifier.height(20.dp))
                DetailActionButton(
                    text = stringResource(I18nR.string.detail_book_again),
                    icon = Icons.Outlined.Refresh,
                    primary = true,
                    onClick = onBookAgain,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (isCompleted) {
                Spacer(Modifier.height(20.dp))
                if (booking.receipt != null) {
                    DetailActionButton(
                        text = stringResource(I18nR.string.detail_view_receipt),
                        icon = Icons.Outlined.Receipt,
                        primary = false,
                        onClick = onViewReceipt,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                }
                DetailActionButton(
                    text = stringResource(I18nR.string.detail_book_again),
                    icon = Icons.Outlined.Refresh,
                    primary = true,
                    onClick = onBookAgain,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (isScheduled) {
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(palette.mutedSurface.copy(alpha = 0.7f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Filled.Star, null, tint = palette.brand, modifier = Modifier.size(16.dp))
                    Text(
                        text = stringResource(I18nR.string.detail_share_hint),
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun BookingMapPreview(address: String) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(palette.mutedSurface),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val road = palette.border.copy(alpha = 0.72f)
            repeat(5) { idx ->
                val y = size.height * (0.18f + idx * 0.18f)
                drawLine(
                    color = road,
                    start = Offset(0f, y),
                    end = Offset(size.width, y + if (idx % 2 == 0) 38f else -30f),
                    strokeWidth = 18f,
                )
            }
            repeat(4) { idx ->
                val x = size.width * (0.18f + idx * 0.24f)
                drawLine(
                    color = road.copy(alpha = 0.56f),
                    start = Offset(x, 0f),
                    end = Offset(x - 54f, size.height),
                    strokeWidth = 12f,
                )
            }
            drawCircle(
                color = palette.brand.copy(alpha = 0.14f),
                radius = 42f,
                center = Offset(size.width * 0.56f, size.height * 0.45f),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(56.dp)
                .clip(CircleShape)
                .background(palette.cardSurface)
                .border(1.dp, palette.border, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(palette.brand),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.LocationOn, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(palette.cardSurface.copy(alpha = 0.94f))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.Navigation, null, tint = palette.brand, modifier = Modifier.size(16.dp))
            Text(address, color = palette.foreground, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}

@Composable
private fun HeaderCircleButton(icon: ImageVector, onClick: () -> Unit, contentDescription: String) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(palette.mutedSurface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription, tint = palette.foreground, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun InfoTile(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, palette.border, RoundedCornerShape(20.dp))
            .background(palette.cardSurface)
            .padding(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(palette.brand.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = palette.brand, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(text = label, color = palette.mutedForeground, fontSize = 11.sp, maxLines = 1)
        Text(text = value, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
    }
}

private enum class ActionRowTone { Default, Primary, Danger }

@Composable
private fun DetailActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tone: ActionRowTone,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val (bg, fg) = when (tone) {
        ActionRowTone.Primary -> palette.brand.copy(alpha = 0.10f) to palette.brand
        ActionRowTone.Danger -> palette.destructive.copy(alpha = 0.10f) to palette.destructive
        else -> palette.mutedSurface to palette.foreground
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, palette.border, RoundedCornerShape(20.dp))
            .background(palette.cardSurface)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = fg, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
            Text(text = subtitle, color = palette.mutedForeground, fontSize = 12.sp, maxLines = 1)
        }
        Icon(Icons.Filled.ChevronRight, null, tint = palette.mutedForeground, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun StatusInfoBlock(
    title: String,
    description: String,
    accent: Color,
    container: Color,
    content: @Composable () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, accent.copy(alpha = 0.20f), RoundedCornerShape(24.dp))
            .background(container)
            .padding(16.dp),
    ) {
        Text(text = title, color = accent, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = description, color = palette.mutedForeground, fontSize = 14.sp, lineHeight = 19.sp, modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(palette.cardSurface)
                .padding(12.dp),
        ) {
            Column { content() }
        }
    }
}

@Composable
private fun DetailActionButton(
    text: String,
    icon: ImageVector?,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    destructive: Boolean = false,
) {
    val palette = LocalRestaurantPalette.current
    val container = when {
        destructive && !primary -> palette.cardSurface
        primary -> palette.brand
        else -> palette.cardSurface
    }
    val content = when {
        destructive && !primary -> palette.destructive
        primary -> Color.White
        else -> palette.foreground
    }
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .let { if (!primary) it.border(1.dp, palette.border, shape) else it }
            .background(container)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = content, modifier = Modifier.size(16.dp))
            Spacer(Modifier.size(6.dp))
        }
        Text(text = text, color = content, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
    }
}
