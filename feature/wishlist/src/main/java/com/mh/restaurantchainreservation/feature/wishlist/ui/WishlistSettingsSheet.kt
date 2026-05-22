package com.mh.restaurantchainreservation.feature.wishlist.ui

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import kotlinx.coroutines.launch

private val SettingsSheetHorizontalPadding = 20.dp

@Composable
fun WishlistSettingsSheet(
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val backdropAlpha = remember { Animatable(0f) }
    val sheetOffset = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    var dismissing by remember { mutableStateOf(false) }
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val triggerDismiss: () -> Unit = remember {
        {
            if (!dismissing) {
                dismissing = true
                scope.launch {
                    launch { backdropAlpha.animateTo(0f, tween(180)) }
                    launch { sheetOffset.animateTo(1f, tween(220)) }
                    onDismiss()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        launch { backdropAlpha.animateTo(1f, tween(220, easing = LinearOutSlowInEasing)) }
        launch {
            sheetOffset.animateTo(0f, spring(dampingRatio = 0.85f, stiffness = 270f))
        }
    }

    Dialog(
        onDismissRequest = triggerDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            decorFitsSystemWindows = false,
        ),
    ) {
        val density = LocalDensity.current
        val sheetTranslateY = with(density) { sheetOffset.value * 480.dp.toPx() }

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RestaurantColors.Overlay.scrimModal.copy(alpha = backdropAlpha.value))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) { triggerDismiss() },
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .graphicsLayer { translationY = sheetTranslateY },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(palette.cardSurface),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Settings",
                            color = palette.foreground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { triggerDismiss() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = palette.foreground,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                    SettingsSheetDivider()
                    SettingsRow(
                        icon = Icons.Outlined.Share,
                        label = "Share wishlist",
                        onClick = {
                            triggerDismiss()
                            onShare()
                        },
                    )
                    SettingsSheetDivider()
                    SettingsRow(
                        icon = Icons.Outlined.Edit,
                        label = "Rename",
                        onClick = {
                            triggerDismiss()
                            onRename()
                        },
                    )
                    SettingsSheetDivider()
                    SettingsRow(
                        icon = Icons.Outlined.Delete,
                        label = "Delete",
                        destructive = true,
                        onClick = {
                            triggerDismiss()
                            onDelete()
                        },
                    )
                    Box(Modifier.height(12.dp))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(navigationBarPadding)
                        .background(palette.cardSurface),
                )
            }
        }
    }
}

@Composable
private fun SettingsSheetDivider() {
    val palette = LocalRestaurantPalette.current
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = SettingsSheetHorizontalPadding),
        color = palette.border,
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    destructive: Boolean = false,
) {
    val palette = LocalRestaurantPalette.current
    val tint = if (destructive) palette.destructive else palette.foreground
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = SettingsSheetHorizontalPadding, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = label,
            color = tint,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = palette.mutedForeground,
            modifier = Modifier.size(22.dp),
        )
    }
}
