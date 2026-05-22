package com.mh.restaurantchainreservation.feature.dining.ui

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking
import com.mh.restaurantchainreservation.feature.dining.data.BookingStatus
import com.mh.restaurantchainreservation.feature.dining.data.isCurrentlyDining

/* ----------------------------- StatusBadge ----------------------------- */

private data class StatusVisual(
    val labelRes: Int,
    val color: Color,
    val container: Color,
    val icon: ImageVector,
)

private fun statusVisualFor(palette: RestaurantPalette, status: BookingStatus): StatusVisual = when (status) {
    BookingStatus.Pending -> StatusVisual(
        labelRes = I18nR.string.booking_status_pending,
        color = palette.warning,
        container = palette.warning.copy(alpha = 0.10f),
        icon = Icons.Outlined.Schedule,
    )
    BookingStatus.Confirmed -> StatusVisual(
        labelRes = I18nR.string.booking_status_confirmed,
        color = palette.success,
        container = palette.success.copy(alpha = 0.10f),
        icon = Icons.Outlined.CheckCircle,
    )
    BookingStatus.Rejected -> StatusVisual(
        labelRes = I18nR.string.booking_status_rejected,
        color = palette.destructive,
        container = palette.destructive.copy(alpha = 0.10f),
        icon = Icons.Outlined.Cancel,
    )
    BookingStatus.Completed -> StatusVisual(
        labelRes = I18nR.string.booking_status_visited,
        color = palette.info,
        container = palette.info.copy(alpha = 0.10f),
        icon = Icons.Outlined.CheckCircle,
    )
    BookingStatus.Cancelled -> StatusVisual(
        labelRes = I18nR.string.booking_status_cancelled,
        color = palette.destructive,
        container = palette.destructive.copy(alpha = 0.10f),
        icon = Icons.Outlined.Cancel,
    )
    BookingStatus.NoShow -> StatusVisual(
        labelRes = I18nR.string.booking_status_no_show,
        color = palette.destructive,
        container = palette.destructive.copy(alpha = 0.10f),
        icon = Icons.Outlined.Warning,
    )
}

@Composable
fun StatusBadge(
    booking: Booking,
    checkedInIds: Set<String>?,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val isLive = booking.status == BookingStatus.Confirmed && isCurrentlyDining(booking, checkedInIds = checkedInIds)
    val shape = remember { RoundedCornerShape(percent = 50) }

    if (isLive) {
        Row(
            modifier = modifier
                .height(28.dp)
                .clip(shape)
                .background(palette.success)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        ) {
            PulsingDot(color = RestaurantColors.Base.white)
            Text(
                text = stringResource(I18nR.string.booking_status_now),
                color = RestaurantColors.Base.white,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        return
    }

    val visual = statusVisualFor(palette, booking.status)
    Row(
        modifier = modifier
            .height(28.dp)
            .clip(shape)
            .background(visual.container)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    ) {
        Icon(
            imageVector = visual.icon,
            contentDescription = null,
            tint = visual.color,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = stringResource(visual.labelRes),
            color = visual.color,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun PulsingDot(color: Color, modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "pulse")
    val scale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulseScale",
    )
    val alpha by infinite.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulseAlpha",
    )
    Box(
        modifier = modifier.size(6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .graphicsLayer {
                    this.scaleX = scale
                    this.scaleY = scale
                    this.alpha = alpha
                }
                .clip(CircleShape)
                .background(color),
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color),
        )
    }
}

/* ----------------------------- DetailPill ----------------------------- */

@Composable
fun DetailPill(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(palette.mutedSurface.copy(alpha = 0.7f))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = palette.mutedForeground,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = label,
            color = palette.mutedForeground,
            fontSize = 12.sp,
            maxLines = 1,
            fontWeight = FontWeight.Medium,
        )
    }
}

/* ----------------------------- PrimaryAction ----------------------------- */

@Composable
fun PrimaryAction(
    booking: Booking,
    isLiveDining: Boolean,
    onOpenLive: (() -> Unit)?,
    onManage: (() -> Unit)?,
    onScanQR: (() -> Unit)?,
    onBookAgain: (() -> Unit)?,
    onViewReceipt: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    when {
        booking.status == BookingStatus.Confirmed && isLiveDining && onOpenLive != null ->
            ChipButton(
                text = stringResource(I18nR.string.booking_action_enjoy),
                icon = Icons.Outlined.Restaurant,
                onClick = onOpenLive,
                variant = ChipVariant.Primary,
                modifier = modifier,
            )
        booking.status == BookingStatus.Confirmed ->
            ChipButton(
                text = stringResource(I18nR.string.booking_action_scan),
                icon = Icons.Outlined.QrCode,
                onClick = { onScanQR?.invoke() },
                variant = ChipVariant.Primary,
                modifier = modifier,
            )
        booking.status == BookingStatus.Completed && booking.receipt != null ->
            ChipButton(
                text = stringResource(I18nR.string.booking_action_receipt),
                icon = Icons.Outlined.Receipt,
                onClick = { onViewReceipt?.invoke() },
                variant = ChipVariant.Outline,
                modifier = modifier,
            )
        booking.status == BookingStatus.Pending && onOpenLive != null ->
            ChipButton(
                text = stringResource(I18nR.string.booking_action_details),
                icon = Icons.Outlined.Schedule,
                onClick = onOpenLive,
                variant = ChipVariant.Outline,
                modifier = modifier,
            )
        booking.status == BookingStatus.Rejected ->
            ChipButton(
                text = stringResource(I18nR.string.booking_action_request_again),
                icon = Icons.Outlined.Refresh,
                onClick = { (onBookAgain ?: onManage)?.invoke() },
                variant = ChipVariant.Primary,
                modifier = modifier,
            )
        else ->
            ChipButton(
                text = stringResource(I18nR.string.booking_action_book_again),
                icon = Icons.Outlined.Refresh,
                onClick = { (onBookAgain ?: onManage)?.invoke() },
                variant = ChipVariant.Primary,
                modifier = modifier,
            )
    }
}

enum class ChipVariant { Primary, Outline, BrandOutline, Destructive }

@Composable
fun ChipButton(
    text: String,
    icon: ImageVector?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ChipVariant = ChipVariant.Outline,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp),
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    val container: Color
    val content: Color
    val borderColor: Color?
    when (variant) {
        ChipVariant.Primary -> {
            container = palette.brand
            content = RestaurantColors.Base.white
            borderColor = null
        }
        ChipVariant.Outline -> {
            container = palette.cardSurface
            content = palette.foreground
            borderColor = palette.border
        }
        ChipVariant.BrandOutline -> {
            container = palette.cardSurface
            content = palette.brand
            borderColor = palette.brand.copy(alpha = 0.28f)
        }
        ChipVariant.Destructive -> {
            container = palette.cardSurface
            content = palette.destructive
            borderColor = palette.border
        }
    }
    val baseModifier = modifier
        .defaultMinSize(minHeight = 36.dp)
        .clip(shape)
        .let { if (borderColor != null) it.border(1.dp, borderColor, shape) else it }
        .background(container)
        .clickable(onClick = onClick)
        .padding(contentPadding)
    Row(
        modifier = baseModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = text,
            color = content,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
        )
    }
}

@Composable
fun IconChipButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
) {
    val palette = LocalRestaurantPalette.current
    Box(modifier = modifier.size(36.dp)) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .border(1.dp, palette.border, CircleShape)
                .background(palette.cardSurface)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = palette.foreground,
                modifier = Modifier.size(16.dp),
            )
        }
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(palette.brand),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = badgeCount.coerceAtMost(9).toString(),
                    color = RestaurantColors.Base.white,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}
