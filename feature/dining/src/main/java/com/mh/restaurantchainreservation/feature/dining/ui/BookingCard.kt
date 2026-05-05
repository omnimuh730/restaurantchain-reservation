package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking
import com.mh.restaurantchainreservation.feature.dining.data.BookingStatus
import com.mh.restaurantchainreservation.feature.dining.data.compactDate
import com.mh.restaurantchainreservation.feature.dining.data.fmtR
import com.mh.restaurantchainreservation.feature.dining.data.isCurrentlyDining
import kotlinx.coroutines.delay

@Composable
fun BookingCard(
    booking: Booking,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    checkedInIds: Set<String>? = null,
    onManage: (() -> Unit)? = null,
    onScanQR: (() -> Unit)? = null,
    onShowQR: (() -> Unit)? = null,
    onInvite: (() -> Unit)? = null,
    onBookAgain: (() -> Unit)? = null,
    onDeleteRequest: (() -> Unit)? = null,
    onViewReceipt: (() -> Unit)? = null,
    invitedCount: Int = 0,
) {
    val palette = LocalRestaurantPalette.current
    val isScheduled = booking.status == BookingStatus.Confirmed
    val isPending = booking.status == BookingStatus.Pending
    val isRejected = booking.status == BookingStatus.Rejected
    val isVisited = booking.status == BookingStatus.Completed
    val isCancelled = booking.status == BookingStatus.Cancelled || booking.status == BookingStatus.NoShow
    val isLive = isScheduled && isCurrentlyDining(booking, checkedInIds = checkedInIds)
    val receiptTotal = booking.receipt?.let { "$%.2f".format(it.total) }
    val cardShape = RoundedCornerShape(24.dp)

    val borderColor = if (isLive) palette.success.copy(alpha = 0.45f) else palette.border

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = cardShape, ambientColor = Color.Black.copy(alpha = 0.05f))
            .clip(cardShape)
            .let { base ->
                if (isLive) base.then(
                    Modifier.border(2.dp, palette.success.copy(alpha = 0.15f), cardShape),
                ) else base
            }
            .border(1.dp, borderColor, cardShape)
            .background(palette.cardSurface)
            .clickable(onClick = onTap)
    ) {
        // Top row: thumbnail + title + meta pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Thumbnail column (108dp wide) with cuisine overlay + status badge below
            Column(
                modifier = Modifier.width(108.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(palette.mutedSurface),
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(booking.image)
                            .crossfade(true)
                            .build(),
                        contentDescription = booking.restaurant,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                                ),
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        contentAlignment = Alignment.BottomStart,
                    ) {
                        Text(
                            text = booking.cuisine.split("·").first().trim(),
                            color = Color.White.copy(alpha = 0.92f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                        )
                    }
                }

                StatusBadge(
                    booking = booking,
                    checkedInIds = checkedInIds,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Right column: title + cuisine + 2x2 detail pills
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = booking.restaurant,
                    color = palette.foreground,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                )
                Text(
                    text = booking.cuisine,
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    maxLines = 1,
                )

                Spacer(Modifier.height(10.dp))

                // 2x2 grid of pills
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        DetailPill(
                            icon = Icons.Outlined.CalendarMonth,
                            label = compactDate(booking.date),
                            modifier = Modifier.weight(1f),
                        )
                        DetailPill(
                            icon = Icons.Outlined.AccessTime,
                            label = booking.time,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val guestsLabel = if (booking.guests == 1) {
                            stringResource(I18nR.string.booking_guests_one, booking.guests)
                        } else {
                            stringResource(I18nR.string.booking_guests_many, booking.guests)
                        }
                        DetailPill(
                            icon = Icons.Outlined.Group,
                            label = guestsLabel,
                            modifier = Modifier.weight(1f),
                        )
                        DetailPill(
                            icon = Icons.Outlined.Chair,
                            label = booking.seating,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.border.copy(alpha = 0.7f)),
        )

        // Bottom row: status detail block + action row
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when {
                isScheduled -> ConfirmationStrip(
                    code = booking.confirmationNo,
                )
                isVisited -> VisitedStatsRow(
                    rating = booking.rating,
                    paid = receiptTotal,
                    points = booking.diningPoints,
                )
                isPending -> StatusInfoBlock(
                    title = stringResource(I18nR.string.booking_waiting_title),
                    description = stringResource(I18nR.string.booking_waiting_desc),
                    accent = palette.warning,
                    container = palette.warning.copy(alpha = 0.10f),
                )
                isRejected -> StatusInfoBlock(
                    title = stringResource(I18nR.string.booking_rejected_title),
                    description = stringResource(I18nR.string.booking_rejected_desc),
                    accent = palette.destructive,
                    container = palette.destructive.copy(alpha = 0.10f),
                )
                isCancelled -> CancelledAddressRow(address = booking.address)
            }

            // Action row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PrimaryAction(
                    booking = booking,
                    isLiveDining = isLive,
                    onOpenLive = onTap,
                    onManage = onManage,
                    onScanQR = onScanQR,
                    onBookAgain = onBookAgain,
                    onViewReceipt = onViewReceipt,
                )
                if (isScheduled) {
                    ChipButton(
                        text = stringResource(I18nR.string.booking_action_manage),
                        icon = null,
                        onClick = { onManage?.invoke() },
                        variant = ChipVariant.Outline,
                    )
                    IconChipButton(
                        icon = Icons.Outlined.Group,
                        contentDescription = stringResource(I18nR.string.booking_action_invite),
                        onClick = { onInvite?.invoke() },
                        badgeCount = invitedCount,
                    )
                    IconChipButton(
                        icon = Icons.Outlined.QrCode,
                        contentDescription = stringResource(I18nR.string.booking_action_show_qr),
                        onClick = { onShowQR?.invoke() },
                    )
                }
                if (isVisited) {
                    ChipButton(
                        text = stringResource(I18nR.string.booking_action_book_again),
                        icon = null,
                        onClick = { onBookAgain?.invoke() },
                        variant = ChipVariant.Outline,
                    )
                }
                if (isRejected && onDeleteRequest != null) {
                    ChipButton(
                        text = stringResource(I18nR.string.booking_action_delete),
                        icon = null,
                        onClick = onDeleteRequest,
                        variant = ChipVariant.Destructive,
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmationStrip(
    code: String,
) {
    val palette = LocalRestaurantPalette.current
    val clipboard = LocalClipboard.current
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(1500)
            copied = false
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.brand.copy(alpha = 0.06f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = stringResource(I18nR.string.booking_confirmation_label).uppercase(),
                color = palette.brand,
                fontSize = 10.sp,
                letterSpacing = 1.6.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = code,
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
            )
        }

        // Copy button (animated swap between Copy and Check icons)
        val copyIconAlpha by animateFloatAsState(
            targetValue = if (copied) 0f else 1f,
            animationSpec = spring(),
            label = "copy_alpha",
        )
        val checkIconAlpha by animateFloatAsState(
            targetValue = if (copied) 1f else 0f,
            animationSpec = spring(),
            label = "check_alpha",
        )
        val checkScale by animateFloatAsState(
            targetValue = if (copied) 1f else 0.5f,
            animationSpec = spring(stiffness = 320f, dampingRatio = 0.55f),
            label = "check_scale",
        )

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(palette.cardSurface)
                .border(
                    width = if (copied) 2.dp else 0.dp,
                    color = if (copied) palette.brand.copy(alpha = 0.2f) else Color.Transparent,
                    shape = RoundedCornerShape(percent = 50),
                )
                .clickable {
                    clipboard.setText(AnnotatedString(code))
                    copied = true
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.ContentCopy,
                contentDescription = stringResource(I18nR.string.booking_copy_label),
                tint = palette.mutedForeground,
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer { alpha = copyIconAlpha },
            )
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = stringResource(I18nR.string.booking_copied_toast),
                tint = palette.brand,
                modifier = Modifier
                    .size(18.dp)
                    .graphicsLayer {
                        alpha = checkIconAlpha
                        scaleX = checkScale
                        scaleY = checkScale
                    },
            )
        }
    }
}

@Composable
private fun VisitedStatsRow(
    rating: Double?,
    paid: String?,
    points: Int,
) {
    val palette = LocalRestaurantPalette.current
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        VisitedStatCell(
            label = stringResource(I18nR.string.booking_rating_label),
            modifier = Modifier.weight(1f),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    tint = palette.warning,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = rating?.let { fmtR(it) } ?: "--",
                    color = palette.warning,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
        VisitedStatCell(
            label = stringResource(I18nR.string.booking_paid_label),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = paid ?: "--",
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
            )
        }
        VisitedStatCell(
            label = stringResource(I18nR.string.booking_points_label),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = "+$points",
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun VisitedStatCell(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(palette.mutedSurface.copy(alpha = 0.65f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            color = palette.mutedForeground,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
        )
        content()
    }
}

@Composable
private fun StatusInfoBlock(
    title: String,
    description: String,
    accent: Color,
    container: Color,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(container)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = title.uppercase(),
            color = accent,
            fontSize = 10.sp,
            letterSpacing = 1.6.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            text = description,
            color = palette.mutedForeground,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun CancelledAddressRow(address: String) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.mutedSurface.copy(alpha = 0.65f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = palette.mutedForeground,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp),
        )
        Text(
            text = address,
            color = palette.mutedForeground,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            maxLines = 2,
        )
    }
}
