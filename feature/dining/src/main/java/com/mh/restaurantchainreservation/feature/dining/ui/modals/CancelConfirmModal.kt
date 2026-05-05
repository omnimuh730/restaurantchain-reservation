package com.mh.restaurantchainreservation.feature.dining.ui.modals

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Warning
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

@Composable
fun CancelConfirmModal(
    restaurantName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    var pop by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { pop = true }

    val iconScale by animateFloatAsState(
        targetValue = if (pop) 1f else 0.75f,
        animationSpec = spring(stiffness = 280f, dampingRatio = 0.45f),
        label = "icon_pop",
    )
    val iconAlpha by animateFloatAsState(
        targetValue = if (pop) 1f else 0f,
        animationSpec = tween(durationMillis = 240, easing = LinearOutSlowInEasing),
        label = "icon_alpha",
    )

    CenterModalSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                        alpha = iconAlpha
                    }
                    .clip(CircleShape)
                    .background(palette.destructive.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = palette.destructive,
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(I18nR.string.cancel_confirm_title),
                color = palette.foreground,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            val body = buildAnnotatedString {
                val full = stringResource(I18nR.string.cancel_confirm_body, restaurantName)
                val nameStart = full.indexOf(restaurantName)
                if (nameStart >= 0) {
                    append(full.substring(0, nameStart))
                    withStyle(SpanStyle(color = palette.foreground, fontWeight = FontWeight.ExtraBold)) {
                        append(restaurantName)
                    }
                    append(full.substring(nameStart + restaurantName.length))
                } else {
                    append(full)
                }
            }
            Text(
                text = body,
                color = palette.mutedForeground,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(percent = 50))
                    .background(palette.warning.copy(alpha = 0.10f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null,
                    tint = palette.warning,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = stringResource(I18nR.string.cancel_confirm_warning),
                    color = palette.warning,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }

            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ConfirmActionButton(
                    text = stringResource(I18nR.string.cancel_confirm_keep),
                    primary = false,
                    destructive = false,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
                ConfirmActionButton(
                    text = stringResource(I18nR.string.cancel_confirm_cancel),
                    primary = false,
                    destructive = true,
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ConfirmActionButton(
    text: String,
    primary: Boolean,
    destructive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val container = when {
        destructive -> palette.destructive
        primary -> palette.brand
        else -> palette.cardSurface
    }
    val content = when {
        destructive || primary -> Color.White
        else -> palette.foreground
    }
    val shape = RoundedCornerShape(percent = 50)
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(shape)
            .let { if (!primary && !destructive) it.border(1.dp, palette.border, shape) else it }
            .background(container)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = content,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}
