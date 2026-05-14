package com.mh.restaurantchainreservation.feature.profile.hub

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.icons.TonightLogoMark
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantFontFamily
import java.text.NumberFormat
import java.util.Locale

internal val SharedHubGoldShadowTone = Color(0xFF8A5612)

internal data class SharedHubCardFaceModel(
    val productLabel: String,
    val holder: String,
    val lastFour: String,
    val krwBalance: Long,
    val usdBalance: Double,
    val themeId: HubCardThemeId,
    val pattern: HubCardPattern,
    val showBalance: Boolean,
    val showDualBalance: Boolean = false,
    val frozen: Boolean = false,
    val showFullPan: Boolean = false,
    val fullCardNumber: String = "",
)

internal fun hubPrimaryBalanceText(krw: Long, usd: Double): String = when {
    usd > 0 && krw <= 0L -> formatUsdHub(usd)
    krw > 0 && usd <= 0.0 -> formatKrwHub(krw)
    krw.toDouble() >= usd * 1400.0 -> formatKrwHub(krw)
    usd > 0 -> formatUsdHub(usd)
    else -> formatKrwHub(krw)
}

internal fun formatKrwHub(value: Long): String {
    val nf = NumberFormat.getNumberInstance(Locale.US)
    return "₩${nf.format(value)}"
}

internal fun formatUsdHub(value: Double): String {
    val nf = NumberFormat.getCurrencyInstance(Locale.US)
    nf.maximumFractionDigits = 2
    nf.minimumFractionDigits = 2
    return nf.format(value)
}

private fun hubGoldDotBrush(): Brush = Brush.radialGradient(
    colors = listOf(
        Color(0xFFFFF6E8),
        Color(0xFFFFD88A),
        Color(0xFFC7892F).copy(alpha = 0.88f),
        Color(0xFF5C3D0A).copy(alpha = 0.75f),
    ),
)

@Composable
internal fun HubLuxuryEmvChip(modifier: Modifier = Modifier) {
    val chipShape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = chipShape,
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = Color(0xFFFFE8C8).copy(alpha = 0.18f),
            )
            .size(width = 40.dp, height = 28.dp)
            .clip(chipShape)
            .background(hubChipAnodizedBrush())
            .border(1.dp, Color.White.copy(alpha = 0.26f), chipShape),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val grid = Color.Black.copy(alpha = 0.24f)
            val stepX = size.width / 4f
            var x = stepX
            repeat(3) {
                drawLine(grid, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f * density)
                x += stepX
            }
            val stepY = size.height / 3f
            var y = stepY
            repeat(2) {
                drawLine(grid, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f * density)
                y += stepY
            }
            drawLine(
                color = Color.White.copy(alpha = 0.38f),
                start = Offset(0f, 1.2f * density),
                end = Offset(size.width * 0.85f, 2.8f * density),
                strokeWidth = 1.1f * density,
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .padding(3.dp)
                .clip(RoundedCornerShape(3.dp))
                .border(1.dp, Color.Black.copy(alpha = 0.18f), RoundedCornerShape(3.dp)),
        )
    }
}

@Composable
internal fun HubPanGoldDot(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(7.dp),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(hubGoldDotBrush()),
        )
        Box(
            Modifier
                .align(Alignment.TopStart)
                .padding(start = 1.dp, top = 0.5.dp)
                .size(3.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.42f)),
        )
    }
}

@Composable
internal fun HubMaskedExpiryGold(
    goldDetailStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(2) {
            HubPanGoldDot()
        }
        Text(
            text = "/",
            style = goldDetailStyle.copy(
                fontSize = 13.sp,
                letterSpacing = 0.sp,
                brush = hubMetalGoldBrush(),
                shadow = Shadow(
                    color = SharedHubGoldShadowTone.copy(alpha = 0.5f),
                    offset = Offset(0f, 1.5f),
                    blurRadius = 5f,
                ),
            ),
        )
        repeat(2) {
            HubPanGoldDot()
        }
    }
}

@Composable
internal fun HubMetallicGoldBalance(
    text: String,
    baseStyle: TextStyle,
    shimmerPhase: Float,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { 44.dp.toPx() }
        val band = widthPx * 0.42f
        Box(Modifier.fillMaxWidth()) {
            Text(
                text = text,
                modifier = Modifier.offset(0.dp, 1.35.dp),
                style = baseStyle.copy(
                    brush = SolidColor(SharedHubGoldShadowTone.copy(alpha = 0.78f)),
                    shadow = null,
                ),
            )
            Text(
                text = text,
                style = baseStyle,
            )
            Text(
                text = text,
                modifier = Modifier.offset(0.dp, (-0.7).dp),
                style = baseStyle.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.88f),
                            Color.White.copy(alpha = 0.18f),
                            Color(0xFFFFF0C8).copy(alpha = 0.55f),
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(0f, heightPx * 0.9f),
                    ),
                    shadow = Shadow(Color.Transparent, Offset.Zero, blurRadius = 0f),
                ),
            )
            Text(
                text = text,
                style = baseStyle.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.62f),
                            Color.Transparent,
                        ),
                        start = Offset(shimmerPhase * widthPx - band, 0f),
                        end = Offset(shimmerPhase * widthPx + band, heightPx),
                    ),
                    shadow = Shadow(Color.Transparent, Offset.Zero, blurRadius = 0f),
                ),
            )
        }
    }
}

@Composable
private fun HubGoldLastFourDigit(char: String, style: TextStyle) {
    Box {
        Text(
            text = char,
            modifier = Modifier.offset(0.dp, 0.85.dp),
            style = style.copy(
                brush = SolidColor(SharedHubGoldShadowTone.copy(alpha = 0.52f)),
                shadow = null,
            ),
        )
        Text(text = char, style = style)
        Text(
            text = char,
            modifier = Modifier.offset(0.dp, (-0.35).dp),
            style = style.copy(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.78f),
                        Color(0xFFFFF8E8).copy(alpha = 0.35f),
                        Color.Transparent,
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(0f, 22f),
                ),
                shadow = Shadow(Color.Transparent, Offset.Zero, blurRadius = 0f),
            ),
        )
    }
}

@Composable
internal fun HubMaskedPanRow(
    lastFour: String,
    goldDetailStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(3) { groupIndex ->
                if (groupIndex > 0) {
                    Spacer(Modifier.width(4.dp))
                }
                repeat(4) {
                    HubPanGoldDot()
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            lastFour.forEach { ch ->
                HubGoldLastFourDigit(char = ch.toString(), style = goldDetailStyle)
            }
        }
    }
}

@Composable
internal fun SharedHubCardFace(
    model: SharedHubCardFaceModel,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(32.dp)
    val spec = hubCardThemeSpec(model.themeId)
    val labelMuted = hubCardLabelMuted(model.themeId)
    val balancePrimaryText = remember(model.krwBalance, model.usdBalance) {
        hubPrimaryBalanceText(model.krwBalance, model.usdBalance)
    }
    val goldBalanceStyle = remember {
        TextStyle(
            fontFamily = RestaurantFontFamily,
            fontSize = 29.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.12.sp,
            brush = hubMetalGoldBrush(),
            shadow = Shadow(
                color = SharedHubGoldShadowTone.copy(alpha = 0.62f),
                offset = Offset(0f, 3.5f),
                blurRadius = 22f,
            ),
        )
    }
    val silverUsdStyle = remember {
        TextStyle(
            fontFamily = RestaurantFontFamily,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.06.sp,
            brush = hubMetalSilverBrush(),
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.38f),
                offset = Offset(0f, 1.8f),
                blurRadius = 10f,
            ),
        )
    }
    val goldDetailStyle = remember {
        TextStyle(
            fontFamily = RestaurantFontFamily,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.6.sp,
            brush = hubMetalGoldBrush(),
            shadow = Shadow(
                color = SharedHubGoldShadowTone.copy(alpha = 0.68f),
                offset = Offset(0f, 2.2f),
                blurRadius = 14f,
            ),
        )
    }
    val shimmerTransition = rememberInfiniteTransition(label = "luxuryCardShimmer")
    val shimmerPhase by shimmerTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerPhase",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.58f)
            .shadow(
                elevation = 28.dp,
                shape = cardShape,
                clip = false,
                ambientColor = spec.glow.copy(alpha = 0.72f),
                spotColor = spec.shadow.copy(alpha = 0.68f),
            )
            .clip(cardShape),
    ) {
        HubThemedCardBackground(
            themeId = model.themeId,
            patternOverride = model.pattern,
            modifier = Modifier.fillMaxSize(),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    TonightLogoMark(
                        modifier = Modifier.size(26.dp),
                        color = Color.White,
                        contentDescription = null,
                    )
                    Text(
                        text = model.productLabel,
                        color = Color.White,
                        fontFamily = RestaurantFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.1.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (model.frozen) {
                    Text(
                        text = "FROZEN",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
            if (model.showBalance) {
                Text(
                    text = "AVAILABLE BALANCE",
                    color = labelMuted,
                    fontFamily = RestaurantFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.65.sp,
                )
                Spacer(Modifier.height(6.dp))
                if (model.showDualBalance && model.krwBalance > 0L && model.usdBalance > 0.0) {
                    HubMetallicGoldBalance(
                        text = formatKrwHub(model.krwBalance),
                        baseStyle = goldBalanceStyle,
                        shimmerPhase = shimmerPhase,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = formatUsdHub(model.usdBalance),
                        style = silverUsdStyle,
                    )
                } else {
                    HubMetallicGoldBalance(
                        text = balancePrimaryText,
                        baseStyle = goldBalanceStyle,
                        shimmerPhase = shimmerPhase,
                    )
                }
                Spacer(Modifier.height(16.dp))
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HubLuxuryEmvChip()
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    HubContactlessIcon(tint = Color.White)
                }
                Spacer(Modifier.height(18.dp))
            }
            Spacer(Modifier.weight(1f))
            if (model.showFullPan && model.fullCardNumber.isNotEmpty()) {
                Text(
                    text = model.fullCardNumber.chunked(4).joinToString(" "),
                    color = Color.White,
                    fontFamily = RestaurantFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            } else {
                HubMaskedPanRow(lastFour = model.lastFour, goldDetailStyle = goldDetailStyle)
            }
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = "CARD HOLDER",
                        color = labelMuted,
                        fontFamily = RestaurantFontFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.35.sp,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = model.holder,
                        color = Color.White,
                        fontFamily = RestaurantFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.35.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "EXPIRES",
                        color = labelMuted,
                        fontFamily = RestaurantFontFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.35.sp,
                    )
                    Spacer(Modifier.height(4.dp))
                    HubMaskedExpiryGold(
                        goldDetailStyle = goldDetailStyle,
                    )
                }
            }
        }
    }
}
