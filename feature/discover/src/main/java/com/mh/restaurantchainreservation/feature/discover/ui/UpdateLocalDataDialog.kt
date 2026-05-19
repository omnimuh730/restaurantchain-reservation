package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.HubSurfaceCardDefaults
import com.mh.restaurantchainreservation.core.designsystem.components.ModalGlassDialog
import com.mh.restaurantchainreservation.core.designsystem.components.ModalGlassScrimStrength
import com.mh.restaurantchainreservation.core.designsystem.components.TonightLogoBadge
import com.mh.restaurantchainreservation.core.designsystem.components.hubSurfaceCard
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.LocalDataSyncStore
import kotlin.math.roundToInt

private val SyncStepLabels = listOf(
    "Connecting to Tonight",
    "Updating restaurant catalog",
    "Syncing reservations and wallet",
    "Refreshing recommendations",
    "Finalizing local cache",
)

private enum class SyncStepState { Pending, Active, Done }

@Composable
fun UpdateLocalDataDialog(
    onDismiss: () -> Unit,
    onUpdateComplete: () -> Unit,
    mandatory: Boolean = false,
) {
    val palette = LocalRestaurantPalette.current
    var syncing by remember { mutableStateOf(false) }
    val canDismissOverlay = !syncing && !mandatory

    ModalGlassDialog(
        onDismissRequest = { if (canDismissOverlay) onDismiss() },
        dismissOnBackPress = canDismissOverlay,
        dismissOnClickOutside = canDismissOverlay,
        scrimStrength = ModalGlassScrimStrength.Strong,
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    enabled = false,
                ) {},
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .hubSurfaceCard(
                        palette = palette,
                        shape = HubSurfaceCardDefaults.Shape,
                    ),
            ) {
                if (syncing) {
                    SyncingLocalDataContent(
                        onFinished = {
                            LocalDataSyncStore.markCatalogSynced()
                            syncing = false
                            onUpdateComplete()
                        },
                    )
                } else {
                    UpdateLocalDataPromptContent(
                        onDismiss = onDismiss,
                        onUpdateNow = { syncing = true },
                        showCloseButton = !mandatory,
                    )
                }
            }
        }
    }
}

@Composable
private fun UpdateLocalDataPromptContent(
    onDismiss: () -> Unit,
    onUpdateNow: () -> Unit,
    showCloseButton: Boolean = true,
) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 22.dp, top = 22.dp, bottom = 18.dp),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                SyncAppIconBadge()
                if (showCloseButton) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(palette.mutedSurface)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = palette.mutedForeground,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(
                text = "Update local data?",
                color = palette.foreground,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Sync the latest restaurant catalog, reservations, and rewards to this device before you start browsing.",
                color = palette.mutedForeground,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
            Spacer(Modifier.height(18.dp))
            WhatsNewCard(version = LocalDataSyncStore.CURRENT_CATALOG_VERSION)
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Later",
                        color = palette.foreground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1.35f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(palette.brand)
                        .clickable(onClick = onUpdateNow),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Update now",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun SyncingLocalDataContent(onFinished: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        val durationMs = 4_800L
        val tickMs = 40L
        var elapsed = 0L
        while (elapsed < durationMs) {
            progress = (elapsed / durationMs.toFloat()) * 100f
            elapsed += tickMs
            kotlinx.coroutines.delay(tickMs)
        }
        progress = 100f
        kotlinx.coroutines.delay(320)
        onFinished()
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(120),
        label = "sync_progress",
    )
    val progressPercent = animatedProgress.roundToInt()
    val activeStepIndex = syncActiveStepIndex(animatedProgress)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SyncAppIconBadge(modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Syncing your Tonight data",
            color = palette.foreground,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Keep the app open while we prepare the freshest local experience.",
            color = palette.mutedForeground,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, palette.border.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
                .padding(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = SyncStepLabels[activeStepIndex.coerceIn(SyncStepLabels.indices)],
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "$progressPercent%",
                    color = palette.foreground,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { animatedProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(percent = 50)),
                color = palette.brand,
                trackColor = palette.mutedSurface,
            )
        }
        Spacer(Modifier.height(14.dp))
        SyncStepLabels.forEachIndexed { index, label ->
            SyncProgressRow(
                label = label,
                state = syncStepState(index, animatedProgress),
            )
            if (index < SyncStepLabels.lastIndex) {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

private fun syncActiveStepIndex(progressPercent: Float): Int {
    val index = (progressPercent / 20f).toInt()
    return index.coerceIn(0, SyncStepLabels.lastIndex)
}

private fun syncStepState(index: Int, progressPercent: Float): SyncStepState {
    val doneAt = (index + 1) * 20f - 2f
    val startAt = index * 20f
    return when {
        progressPercent >= doneAt -> SyncStepState.Done
        progressPercent >= startAt -> SyncStepState.Active
        else -> SyncStepState.Pending
    }
}

@Composable
private fun SyncProgressRow(
    label: String,
    state: SyncStepState,
) {
    val palette = LocalRestaurantPalette.current
    val background = when (state) {
        SyncStepState.Done -> palette.success.copy(alpha = 0.10f)
        SyncStepState.Active -> palette.brandSoftSurface
        SyncStepState.Pending -> palette.mutedSurface.copy(alpha = 0.65f)
    }
    val textColor = when (state) {
        SyncStepState.Done -> palette.success
        SyncStepState.Active -> palette.brand
        SyncStepState.Pending -> palette.mutedForeground
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(percent = 50))
            .background(background)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when (state) {
            SyncStepState.Done -> {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(palette.success),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            SyncStepState.Active -> {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(palette.brand),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                    )
                }
            }
            SyncStepState.Pending -> {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(palette.border),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(palette.mutedForeground),
                    )
                }
            }
        }
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (state == SyncStepState.Active) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        if (state == SyncStepState.Active) {
            Icon(
                imageVector = Icons.Outlined.Sync,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun SyncAppIconBadge(modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Box(modifier = modifier) {
        TonightLogoBadge(size = 56.dp, cornerRadius = 16.dp, logoSize = 32.dp)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 4.dp, y = 4.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(palette.cardSurface)
                .border(2.dp, palette.cardSurface, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Sync,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Composable
private fun WhatsNewCard(version: String) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, palette.border.copy(alpha = 0.65f), RoundedCornerShape(18.dp))
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "What's new in $version",
                color = palette.foreground,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp),
            )
            Text(
                text = "Release",
                color = palette.mutedForeground,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(palette.mutedSurface)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        WhatsNewRow(
            icon = Icons.Outlined.Storage,
            title = "Faster restaurant search",
            subtitle = "Fresh catalog indexes for nearby dining.",
        )
        Spacer(Modifier.height(10.dp))
        WhatsNewRow(
            icon = Icons.Outlined.CalendarMonth,
            title = "Better booking accuracy",
            subtitle = "Availability and reservation states stay current.",
        )
        Spacer(Modifier.height(10.dp))
        WhatsNewRow(
            icon = Icons.Outlined.CardGiftcard,
            title = "Rewards stay ready",
            subtitle = "Wallet and bonus data are saved locally.",
        )
    }
}

@Composable
private fun WhatsNewRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.brandSoftSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = palette.foreground,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                color = palette.mutedForeground,
                fontSize = 12.sp,
                lineHeight = 17.sp,
            )
        }
    }
}
