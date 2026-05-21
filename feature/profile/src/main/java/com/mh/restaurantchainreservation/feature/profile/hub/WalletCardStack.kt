package com.mh.restaurantchainreservation.feature.profile.hub

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.HubSurfaceCardDefaults
import com.mh.restaurantchainreservation.core.designsystem.components.hubSurfaceShadow
import com.mh.restaurantchainreservation.core.designsystem.components.icons.TonightLogoMark
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.feature.profile.data.MockProfileCreditCards
import com.mh.restaurantchainreservation.feature.profile.hub.formatKrwHub
import com.mh.restaurantchainreservation.feature.profile.hub.formatUsdHub
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

private val WalletDecorCircleSize = 176.dp
private val WalletDecorCircleOffsetX = (-40).dp
private val WalletDecorCircleOffsetY = 48.dp

@Composable
fun WalletCardStack(
    showBalance: Boolean,
    onToggleBalance: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val cardShape = HubSurfaceCardDefaults.QuickActionShape
    val masked = stringResource(I18nR.string.profile_wallet_masked)

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    ) {
        // Web: pointer-events-none absolute -bottom-12 -left-10 h-44 w-44 rounded-full bg white/8
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .size(WalletDecorCircleSize)
                .offset(x = WalletDecorCircleOffsetX, y = WalletDecorCircleOffsetY)
                .background(Color.White.copy(alpha = 0.48f), CircleShape),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .hubSurfaceShadow(shape = cardShape)
                .clip(cardShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(palette.gradientStart, palette.gradientMid, palette.gradientEnd),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                    ),
                ),
        ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = topRightHighlightBrush()),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                    TonightLogoMark(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                    )
                    Text(
                        text = stringResource(I18nR.string.profile_wallet_title),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(onClick = onToggleBalance)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = if (showBalance) {
                            stringResource(I18nR.string.profile_wallet_hide)
                        } else {
                            stringResource(I18nR.string.profile_wallet_show)
                        },
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                BalanceCell(
                    title = stringResource(I18nR.string.profile_wallet_domestic_krw),
                    valueText = if (showBalance) {
                        formatKrwHub(MockProfileCreditCards.totalKrwLong())
                    } else {
                        masked
                    },
                    badge = stringResource(I18nR.string.profile_wallet_bonus),
                    modifier = Modifier.weight(1f),
                )
                BalanceCell(
                    title = stringResource(I18nR.string.profile_wallet_foreign_usd),
                    valueText = if (showBalance) {
                        formatUsdHub(MockProfileCreditCards.totalUsd())
                    } else {
                        masked
                    },
                    footnote = stringResource(I18nR.string.profile_wallet_label),
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = if (showBalance) {
                        "•••• •••• •••• ${MockProfileCreditCards.primaryLastFour()} · ${MockProfileCreditCards.HOLDER.uppercase()}"
                    } else {
                        masked
                    },
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.6.sp,
                )
                Spacer(Modifier.width(0.dp))
            }
        }
    }
    }
}

@Composable
private fun BalanceCell(
    title: String,
    valueText: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    footnote: String? = null,
) {
    val cellShape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(cellShape)
            .background(Color.White.copy(alpha = 0.15f))
            .border(1.dp, Color.White.copy(alpha = 0.20f), cellShape)
            .padding(12.dp),
    ) {
        Text(
            text = title.uppercase(),
            color = Color.White.copy(alpha = 0.80f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.9.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = valueText,
            color = Color.White,
            fontSize = 20.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.2).sp,
        )
        Spacer(Modifier.height(8.dp))
        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.25f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = badge,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        } else if (footnote != null) {
            Text(
                text = footnote,
                color = Color.White.copy(alpha = 0.80f),
                fontSize = 11.sp,
            )
        } else {
            Box(modifier = Modifier.height(0.dp))
        }
    }
}

@Composable
private fun topRightHighlightBrush(): Brush {
    return object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val maxRadius = (size.width * 1.2f).coerceAtLeast(size.height * 0.8f)
            return RadialGradientShader(
                center = Offset(size.width, 0f),
                radius = maxRadius,
                colors = listOf(
                    Color.White.copy(alpha = 0.22f),
                    Color.Transparent,
                ),
                colorStops = listOf(0f, 0.55f),
                tileMode = TileMode.Clamp,
            )
        }
    }
}
