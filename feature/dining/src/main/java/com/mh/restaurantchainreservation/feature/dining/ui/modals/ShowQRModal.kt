package com.mh.restaurantchainreservation.feature.dining.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.DeterministicQrCode
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking

@Composable
fun ShowQRModal(
    booking: Booking,
    onDismiss: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    CenterModalSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = stringResource(I18nR.string.show_qr_title),
                        color = palette.foreground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = stringResource(I18nR.string.show_qr_subtitle),
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(palette.mutedSurface)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(I18nR.string.show_qr_close),
                        tint = palette.foreground,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            // QR card frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(palette.mutedSurface.copy(alpha = 0.5f))
                    .padding(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(16.dp),
                ) {
                    QrCanvas(code = booking.confirmationNo, modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f))
                }
            }

            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(palette.mutedSurface.copy(alpha = 0.65f))
                    .padding(12.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = booking.restaurant,
                        color = palette.foreground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                    )
                    Text(
                        text = "${booking.date} · ${booking.time} · ${booking.guests} ${if (booking.guests == 1) "guest" else "guests"}",
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                        maxLines = 1,
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(palette.cardSurface)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.QrCode,
                            contentDescription = null,
                            tint = palette.brand,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = booking.confirmationNo,
                            color = palette.brand,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ShowQrAction(
                    text = stringResource(I18nR.string.show_qr_save),
                    icon = Icons.Outlined.Download,
                    primary = false,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
                ShowQrAction(
                    text = stringResource(I18nR.string.show_qr_share),
                    icon = Icons.Outlined.Share,
                    primary = true,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ShowQrAction(
    text: String,
    icon: ImageVector,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val container = if (primary) palette.brand else palette.cardSurface
    val content = if (primary) Color.White else palette.foreground
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
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = content,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = text,
            color = content,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

/**
 * Deterministic faux QR identical to React's pattern (same seed math).
 * 25-cell grid with three corner finders + content cells.
 */
@Composable
fun QrCanvas(code: String, modifier: Modifier = Modifier) {
    DeterministicQrCode(code = code, modifier = modifier)
}
