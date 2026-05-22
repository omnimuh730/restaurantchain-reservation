package com.mh.restaurantchainreservation.feature.profile.hub

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class DailyRewardKind { Bonus, Coupon, Points, Fx }

data class DailyReward(
    val id: String,
    val label: String,
    val description: String,
    val kind: DailyRewardKind,
)

@Composable
fun DailyBonusModal(
    visible: Boolean,
    onDismiss: () -> Unit,
    onClaim: (DailyReward) -> Unit,
) {
    if (!visible) return
    val palette = LocalRestaurantPalette.current

    val rewardsPool = remember { defaultRewards() }
    val choices = remember {
        val shuffled = rewardsPool.shuffled()
        (0 until 9).map { i -> shuffled[i % shuffled.size].copy(id = "box-$i") }.shuffled()
    }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    var showOthers by remember { mutableStateOf(false) }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0) {
            delay(600)
            showOthers = true
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = selectedIndex < 0,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(palette.cardSurface),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (selectedIndex < 0) {
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
                            contentDescription = stringResource(I18nR.string.common_action_close),
                            tint = palette.mutedForeground,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
                Header(revealed = selectedIndex >= 0)
                Spacer(Modifier.height(20.dp))
                GiftGrid(
                    choices = choices,
                    selectedIndex = selectedIndex,
                    showOthers = showOthers,
                    onPick = { i -> if (selectedIndex < 0) selectedIndex = i },
                )
                AnimatedVisibility(
                    visible = showOthers && selectedIndex >= 0,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(Modifier.height(20.dp))
                        RewardSummary(reward = choices.getOrNull(selectedIndex))
                        Spacer(Modifier.height(16.dp))
                        ClaimButton(onClick = {
                            choices.getOrNull(selectedIndex)?.let(onClaim)
                            onDismiss()
                        })
                    }
                }
                if (!showOthers) {
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun Header(revealed: Boolean) {
    val palette = LocalRestaurantPalette.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(palette.brand.copy(alpha = 0.10f))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = stringResource(I18nR.string.profile_daily_drop_label).uppercase(),
                color = palette.brand,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(
                if (revealed) I18nR.string.profile_daily_won_title else I18nR.string.profile_daily_pick_title,
            ),
            color = palette.foreground,
            fontSize = 22.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(
                if (revealed) I18nR.string.profile_daily_won_subtitle else I18nR.string.profile_daily_pick_subtitle,
            ),
            color = palette.mutedForeground,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun GiftGrid(
    choices: List<DailyReward>,
    selectedIndex: Int,
    showOthers: Boolean,
    onPick: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .widthIn(max = 320.dp)
            .fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (rowIdx in 0 until 3) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    for (colIdx in 0 until 3) {
                        val i = rowIdx * 3 + colIdx
                        val reward = choices[i]
                        val picked = selectedIndex == i
                        val flipped = picked || (selectedIndex >= 0 && showOthers)
                        Box(modifier = Modifier.weight(1f)) {
                            GiftBox(
                                reward = reward,
                                picked = picked,
                                flipped = flipped,
                                disabled = selectedIndex >= 0,
                                onClick = { onPick(i) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GiftBox(
    reward: DailyReward,
    picked: Boolean,
    flipped: Boolean,
    disabled: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val rotation = remember { Animatable(if (flipped) 180f else 0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(flipped) {
        scope.launch {
            rotation.animateTo(
                targetValue = if (flipped) 180f else 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
            )
        }
    }

    val density = LocalDensity.current
    val cameraDistancePx = with(density) { 16.dp.toPx() } * 16f

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = cameraDistancePx
            }
            .clickable(enabled = !disabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (rotation.value <= 90f) {
            GiftFront(palette = palette, modifier = Modifier.fillMaxSize())
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f },
            ) {
                GiftBack(reward = reward, picked = picked, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun GiftFront(palette: com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantPalette, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(palette.mutedSurface)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.CardGiftcard,
            contentDescription = null,
            tint = palette.mutedForeground.copy(alpha = 0.6f),
            modifier = Modifier.size(32.dp),
        )
    }
}

@Composable
private fun GiftBack(reward: DailyReward, picked: Boolean, modifier: Modifier) {
    val palette = LocalRestaurantPalette.current
    val bg = if (picked) palette.brand.copy(alpha = 0.10f) else palette.mutedSurface.copy(alpha = 0.4f)
    val borderColor = if (picked) palette.brand else palette.border
    val borderWidth: Dp = if (picked) 2.dp else 1.dp
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = rewardIcon(reward.kind),
                contentDescription = null,
                tint = if (picked) palette.brand else palette.mutedForeground,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = reward.label,
                color = if (picked) palette.brand else palette.mutedForeground,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                fontWeight = if (picked) FontWeight.Bold else FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
        }
        if (picked) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 0.dp, end = 0.dp)
                    .size(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Celebration,
                    contentDescription = null,
                    tint = palette.brand,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun RewardSummary(reward: DailyReward?) {
    if (reward == null) return
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.mutedSurface.copy(alpha = 0.5f))
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(14.dp),
    ) {
        Text(
            text = reward.label,
            color = palette.foreground,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = reward.description,
            color = palette.mutedForeground,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun ClaimButton(onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(palette.brand)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(I18nR.string.profile_daily_claim),
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun rewardIcon(kind: DailyRewardKind): ImageVector = when (kind) {
    DailyRewardKind.Bonus -> Icons.Outlined.MonetizationOn
    DailyRewardKind.Coupon -> Icons.Outlined.LocalActivity
    DailyRewardKind.Points -> Icons.Outlined.StarBorder
    DailyRewardKind.Fx -> Icons.Outlined.Public
}

private fun defaultRewards(): List<DailyReward> = listOf(
    DailyReward("bonus-1k", "₩1,000", "Bonus Wallet", DailyRewardKind.Bonus),
    DailyReward("bonus-5k", "₩5,000", "Bonus Wallet", DailyRewardKind.Bonus),
    DailyReward("bonus-10k", "₩10,000", "Bonus Wallet", DailyRewardKind.Bonus),
    DailyReward("bonus-20k", "₩20,000", "Jackpot!", DailyRewardKind.Bonus),
    DailyReward("coupon-10", "10% Off", "Dining Coupon", DailyRewardKind.Coupon),
    DailyReward("coupon-free", "Free Delivery", "Any order", DailyRewardKind.Coupon),
    DailyReward("points-50", "50 Pts", "Reward Points", DailyRewardKind.Points),
    DailyReward("points-100", "100 Pts", "Reward Points", DailyRewardKind.Points),
    DailyReward("fx-waiver", "Zero FX", "Fee Waiver", DailyRewardKind.Fx),
)
