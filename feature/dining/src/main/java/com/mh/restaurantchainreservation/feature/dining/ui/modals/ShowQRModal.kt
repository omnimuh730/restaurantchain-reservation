package com.mh.restaurantchainreservation.feature.dining.ui.modals

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.BrandedQrCode
import com.mh.restaurantchainreservation.core.designsystem.components.CenterModalSheet
import com.mh.restaurantchainreservation.core.designsystem.components.CenterModalSheetSurface
import com.mh.restaurantchainreservation.core.designsystem.components.PremiumPinkSheetColors
import com.mh.restaurantchainreservation.core.designsystem.components.icons.LucideIcon
import com.mh.restaurantchainreservation.core.designsystem.components.icons.LucidePaths
import com.mh.restaurantchainreservation.core.designsystem.components.icons.QrPayNavIcon
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking

/** Matches [UpdateDataModal] action row geometry. */
private val ModalActionRowHeight = 44.dp
private val ModalActionButtonShape = RoundedCornerShape(ModalActionRowHeight / 2)

@Composable
fun ShowQRModal(
    booking: Booking,
    onDismiss: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val accent = PremiumPinkSheetColors.Primary
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }
    val confirmationNo = booking.confirmationNo
    val qrCardShape = RoundedCornerShape(20.dp)

    CenterModalSheet(
        onDismiss = onDismiss,
        cornerRadiusDp = 32,
        surface = CenterModalSheetSurface.PremiumPink,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(palette.mutedSurface)
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(I18nR.string.show_qr_close),
                    tint = palette.mutedForeground,
                    modifier = Modifier.size(16.dp),
                )
            }

            ShowQrHeroBadge(accent = accent)

            Spacer(Modifier.height(10.dp))

            Text(
                text = stringResource(I18nR.string.show_qr_your_code),
                color = palette.foreground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(I18nR.string.show_qr_subtitle),
                color = palette.mutedForeground,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
            )

            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 24.dp,
                        shape = qrCardShape,
                        ambientColor = PremiumPinkSheetColors.ShadowPink,
                        spotColor = RestaurantColors.Overlay.borderSubtle,
                    )
                    .clip(qrCardShape)
                    .background(RestaurantColors.Base.white)
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BrandedQrCode(
                        code = confirmationNo,
                        modifier = Modifier.size(200.dp),
                        brandColor = accent,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = confirmationNo,
                        color = palette.foreground,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp,
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                LucideIcon(
                    paths = LucidePaths.ShieldCheck,
                    modifier = Modifier.size(15.dp),
                    strokeColor = accent,
                    strokeWidth = 2f,
                )
                Text(
                    text = stringResource(I18nR.string.show_qr_secure),
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(palette.border),
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ModalActionRowHeight),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShowQrModalAction(
                    text = if (copied) {
                        stringResource(I18nR.string.show_qr_copied)
                    } else {
                        stringResource(I18nR.string.show_qr_copy)
                    },
                    icon = Icons.Outlined.ContentCopy,
                    primary = false,
                    accent = accent,
                    onClick = {
                        copyReservationNumber(context, confirmationNo)
                        copied = true
                    },
                    modifier = Modifier.weight(1f),
                )
                ShowQrModalAction(
                    text = stringResource(I18nR.string.show_qr_share),
                    icon = Icons.Outlined.Share,
                    primary = true,
                    accent = accent,
                    onClick = { shareReservationNumber(context, confirmationNo) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ShowQrHeroBadge(accent: Color) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .shadow(
                elevation = 10.dp,
                shape = CircleShape,
                ambientColor = PremiumPinkSheetColors.ShadowPink,
                spotColor = RestaurantColors.Base.black.copy(alpha = 0.06f),
            )
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        RestaurantColors.Base.white,
                        PremiumPinkSheetColors.LightPink,
                        PremiumPinkSheetColors.SoftPink,
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        QrPayNavIcon(
            color = accent,
            modifier = Modifier.size(26.dp),
        )
    }
}

@Composable
private fun ShowQrModalAction(
    text: String,
    icon: ImageVector,
    primary: Boolean,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val content = if (primary) RestaurantColors.Base.white else palette.foreground
    Row(
        modifier = modifier
            .fillMaxHeight()
            .clip(ModalActionButtonShape)
            .then(
                if (primary) {
                    Modifier.background(accent)
                } else {
                    Modifier
                        .border(1.dp, PremiumPinkSheetColors.Border, ModalActionButtonShape)
                        .background(RestaurantColors.Base.white.copy(alpha = 0.88f))
                },
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (primary) content else accent,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = text,
            color = if (primary) content else accent,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun copyReservationNumber(context: Context, value: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
    clipboard.setPrimaryClip(ClipData.newPlainText("reservation_number", value))
}

private fun shareReservationNumber(context: Context, value: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, value)
    }
    context.startActivity(Intent.createChooser(intent, null))
}
