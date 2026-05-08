package com.mh.restaurantchainreservation.feature.profile.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

private enum class TierKey(val minPoints: Int) {
    Silver(0),
    Gold(1000),
    Platinum(5000),
    Diamond(10000),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TierStatusModal(
    visible: Boolean,
    onDismiss: () -> Unit,
) {
    if (!visible) return
    val palette = LocalRestaurantPalette.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.cardSurface,
        contentColor = palette.foreground,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val maxSheetHeight = maxHeight * 0.85f
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxSheetHeight),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(palette.gold.copy(alpha = 0.10f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.WorkspacePremium,
                                contentDescription = null,
                                tint = palette.gold,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        Text(
                            text = stringResource(I18nR.string.profile_tier_modal_title),
                            color = palette.foreground,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(palette.mutedSurface)
                            .clickable(
                                role = Role.Button,
                                onClickLabel = stringResource(I18nR.string.common_action_close),
                                onClick = onDismiss,
                            ),
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
                HorizontalDivider(color = palette.border.copy(alpha = 0.5f))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    CurrentTierHeroCard()

                    TierKey.entries.forEach { tier ->
                        TierPerkCard(tier = tier, isCurrent = tier == TierKey.Gold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentTierHeroCard() {
    val palette = LocalRestaurantPalette.current
    val cardShape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(palette.brand)
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 0.dp, end = 0.dp)
                .size(112.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.WorkspacePremium,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.size(120.dp),
            )
        }
        Column {
            Text(
                text = stringResource(I18nR.string.profile_tier_modal_current).uppercase(),
                color = Color.White.copy(alpha = 0.80f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.6.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(I18nR.string.profile_tier_modal_gold_level),
                color = Color.White,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.30f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.67f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White),
                    )
                }
                Text(
                    text = stringResource(I18nR.string.profile_tier_modal_to_platinum),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun TierPerkCard(tier: TierKey, isCurrent: Boolean) {
    val palette = LocalRestaurantPalette.current
    val cardShape = RoundedCornerShape(16.dp)
    val borderColor = if (isCurrent) palette.gold.copy(alpha = 0.40f) else palette.border.copy(alpha = 0.6f)
    val bgColor = if (isCurrent) palette.gold.copy(alpha = 0.05f) else palette.cardSurface

    val nameRes = when (tier) {
        TierKey.Silver -> I18nR.string.profile_tier_silver_name
        TierKey.Gold -> I18nR.string.profile_tier_gold_name
        TierKey.Platinum -> I18nR.string.profile_tier_platinum_name
        TierKey.Diamond -> I18nR.string.profile_tier_diamond_name
    }
    val perks = when (tier) {
        TierKey.Silver -> listOf(
            stringResource(I18nR.string.profile_tier_silver_perk_1),
            stringResource(I18nR.string.profile_tier_silver_perk_2),
            stringResource(I18nR.string.profile_tier_silver_perk_3),
        )
        TierKey.Gold -> listOf(
            stringResource(I18nR.string.profile_tier_gold_perk_1),
            stringResource(I18nR.string.profile_tier_gold_perk_2),
            stringResource(I18nR.string.profile_tier_gold_perk_3),
            stringResource(I18nR.string.profile_tier_gold_perk_4),
        )
        TierKey.Platinum -> listOf(
            stringResource(I18nR.string.profile_tier_platinum_perk_1),
            stringResource(I18nR.string.profile_tier_platinum_perk_2),
            stringResource(I18nR.string.profile_tier_platinum_perk_3),
            stringResource(I18nR.string.profile_tier_platinum_perk_4),
        )
        TierKey.Diamond -> listOf(
            stringResource(I18nR.string.profile_tier_diamond_perk_1),
            stringResource(I18nR.string.profile_tier_diamond_perk_2),
            stringResource(I18nR.string.profile_tier_diamond_perk_3),
            stringResource(I18nR.string.profile_tier_diamond_perk_4),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(bgColor)
            .border(1.dp, borderColor, cardShape)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(nameRes),
                    color = palette.foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (isCurrent) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(palette.gold.copy(alpha = 0.10f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = stringResource(I18nR.string.profile_tier_modal_current_badge).uppercase(),
                            color = palette.gold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                    }
                }
            }
            Text(
                text = stringResource(I18nR.string.profile_tier_modal_pts_suffix, tier.minPoints),
                color = palette.mutedForeground,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            perks.forEach { perk ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = if (isCurrent) palette.gold else palette.mutedForeground.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp),
                    )
                    Text(
                        text = perk,
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}
