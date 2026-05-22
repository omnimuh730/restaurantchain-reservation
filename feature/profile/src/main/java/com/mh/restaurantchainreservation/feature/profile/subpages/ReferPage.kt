package com.mh.restaurantchainreservation.feature.profile.subpages

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Share
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.profile.subpages.components.QrCanvas
import kotlinx.coroutines.delay

@Composable
fun ReferPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }
    val code = stringResource(I18nR.string.refer_code_value)

    LaunchedEffect(copied) {
        if (copied) {
            delay(2000)
            copied = false
        }
    }

    SubpageScaffold(
        title = stringResource(I18nR.string.refer_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(palette.brand.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.CardGiftcard,
                    contentDescription = null,
                    tint = palette.brand,
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(I18nR.string.refer_hero_title),
                color = palette.foreground,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(I18nR.string.refer_hero_subtitle),
                color = palette.mutedForeground,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        Spacer(Modifier.height(24.dp))

        CodeRow(
            code = code,
            copied = copied,
            onCopy = {
                copyToClipboard(context, code)
                copied = true
            },
        )

        Spacer(Modifier.height(12.dp))
        ReferQrCard(code = code)

        Spacer(Modifier.height(16.dp))
        StatsRow(palette)

        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(I18nR.string.refer_how_title),
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(12.dp))
        StepRow(1, stringResource(I18nR.string.refer_step_1))
        StepRow(2, stringResource(I18nR.string.refer_step_2))
        StepRow(3, stringResource(I18nR.string.refer_step_3))

        Spacer(Modifier.height(28.dp))
        ShareInviteButton(onClick = onBack)
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun CodeRow(code: String, copied: Boolean, onCopy: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val rowShape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(palette.cardSurface)
            .border(1.dp, palette.border, rowShape)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.padding(end = 12.dp)) {
            Text(
                text = stringResource(I18nR.string.refer_your_code).uppercase(),
                color = palette.mutedForeground,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = code,
                color = palette.foreground,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
            )
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (copied) palette.success.copy(alpha = 0.15f) else palette.mutedSurface)
                .clickable(onClick = onCopy),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (copied) Icons.Outlined.Check else Icons.Outlined.ContentCopy,
                contentDescription = stringResource(if (copied) I18nR.string.refer_copied else I18nR.string.refer_copy),
                tint = if (copied) palette.success else palette.foreground,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun StatsRow(palette: com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantPalette) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatTile(
            value = stringResource(I18nR.string.refer_stat_referred_value),
            label = stringResource(I18nR.string.refer_stats_referred),
            highlight = false,
            modifier = Modifier.weight(1f),
        )
        StatTile(
            value = stringResource(I18nR.string.refer_stat_earned_value),
            label = stringResource(I18nR.string.refer_stats_earned),
            highlight = true,
            modifier = Modifier.weight(1f),
        )
        StatTile(
            value = stringResource(I18nR.string.refer_stat_pending_value),
            label = stringResource(I18nR.string.refer_stats_pending),
            highlight = false,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatTile(value: String, label: String, highlight: Boolean, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val cardShape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(cardShape)
            .background(palette.cardSurface)
            .border(1.dp, palette.border, cardShape)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            color = if (highlight) palette.success else palette.foreground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            color = palette.mutedForeground,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun StepRow(index: Int, text: String) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(palette.brand.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = index.toString(),
                color = palette.brand,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = text,
            color = palette.foreground,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ShareInviteButton(onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(palette.brand)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Share,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.size(10.dp))
        Text(
            text = stringResource(I18nR.string.refer_share_invite),
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ReferQrCard(code: String) {
    val palette = LocalRestaurantPalette.current
    val cardShape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(palette.cardSurface)
            .border(1.dp, palette.border, cardShape)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.QrCode,
                contentDescription = null,
                tint = palette.mutedForeground,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = stringResource(I18nR.string.refer_qr_caption).uppercase(),
                color = palette.mutedForeground,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp,
            )
        }
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center,
        ) {
            QrCanvas(
                code = "catchtable://refer/$code",
                modifier = Modifier.size(168.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(I18nR.string.refer_qr_subtitle, code),
            color = palette.mutedForeground,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

private fun copyToClipboard(context: Context, value: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    cm?.setPrimaryClip(ClipData.newPlainText("refer-code", value))
}
