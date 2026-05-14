@file:OptIn(ExperimentalFoundationApi::class)

package com.mh.restaurantchainreservation.feature.profile.hub

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import kotlin.math.abs
import com.mh.restaurantchainreservation.core.designsystem.components.icons.TonightLogoMark
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantFontFamily
import java.text.NumberFormat
import java.util.Locale

private val HubGoldShadowTone = Color(0xFF8A5612)

private data class HubCreditCard(
    val id: String,
    val productLabel: String,
    val holder: String,
    val lastFour: String,
    val krwBalance: Long,
    val usdBalance: Double,
    val themeId: HubCardThemeId,
    val showBalance: Boolean,
    val showDualBalance: Boolean = false,
)

private fun hubGoldDotBrush(): Brush = Brush.radialGradient(
    colors = listOf(Color(0xFFFFE9A8), Color(0xFFC7892F).copy(alpha = 0.92f)),
)

@Composable
private fun LuxuryEmvChip(modifier: Modifier = Modifier) {
    val chipShape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .size(width = 40.dp, height = 28.dp)
            .clip(chipShape)
            .background(hubMetalGoldBrush())
            .border(1.dp, Color.White.copy(alpha = 0.28f), chipShape),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val grid = Color.Black.copy(alpha = 0.22f)
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
        }
        Box(
            Modifier
                .fillMaxSize()
                .padding(3.dp)
                .clip(RoundedCornerShape(3.dp))
                .border(1.dp, Color.Black.copy(alpha = 0.15f), RoundedCornerShape(3.dp)),
        )
    }
}

@Composable
private fun HubPanGoldDot(modifier: Modifier = Modifier) {
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
private fun HubMaskedExpiryGold(
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
                    color = HubGoldShadowTone.copy(alpha = 0.5f),
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
private fun HubMetallicGoldBalance(
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
                    brush = SolidColor(HubGoldShadowTone.copy(alpha = 0.62f)),
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
                            Color.White.copy(alpha = 0.72f),
                            Color.White.copy(alpha = 0.1f),
                            Color(0xFFFFE9A8).copy(alpha = 0.4f),
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
                            Color.White.copy(alpha = 0.48f),
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

private fun hubPrimaryBalanceText(krw: Long, usd: Double): String = when {
    usd > 0 && krw <= 0L -> formatUsdHub(usd)
    krw > 0 && usd <= 0.0 -> formatKrwHub(krw)
    krw.toDouble() >= usd * 1400.0 -> formatKrwHub(krw)
    usd > 0 -> formatUsdHub(usd)
    else -> formatKrwHub(krw)
}

@Composable
fun CreditCardsHubSection(
    onManageCards: () -> Unit,
    onOpenCardInfo: () -> Unit,
    onAddNewCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val cards = remember {
        listOf(
            HubCreditCard(
                id = "c-ink",
                productLabel = "Tonight Card",
                holder = "ALEX CHEN",
                lastFour = "1595",
                krwBalance = 400_000L,
                usdBalance = 0.0,
                themeId = HubCardThemeId.Ink,
                showBalance = true,
            ),
            HubCreditCard(
                id = "c-rose",
                productLabel = "Tonight Card",
                holder = "ALEX CHEN",
                lastFour = "2840",
                krwBalance = 450_000L,
                usdBalance = 120.0,
                themeId = HubCardThemeId.Rose,
                showBalance = true,
                showDualBalance = true,
            ),
            HubCreditCard(
                id = "c-amethyst",
                productLabel = "Tonight Card",
                holder = "ALEX CHEN",
                lastFour = "4412",
                krwBalance = 0L,
                usdBalance = 0.0,
                themeId = HubCardThemeId.Amethyst,
                showBalance = false,
            ),
            HubCreditCard(
                id = "c-ocean",
                productLabel = "Tonight Card",
                holder = "ALEX CHEN",
                lastFour = "7781",
                krwBalance = 0L,
                usdBalance = 0.0,
                themeId = HubCardThemeId.Ocean,
                showBalance = false,
            ),
            HubCreditCard(
                id = "c-sunset",
                productLabel = "Tonight Card",
                holder = "ALEX CHEN",
                lastFour = "9033",
                krwBalance = 0L,
                usdBalance = 0.0,
                themeId = HubCardThemeId.Sunset,
                showBalance = false,
            ),
            HubCreditCard(
                id = "c-forest",
                productLabel = "Tonight Card",
                holder = "ALEX CHEN",
                lastFour = "1129",
                krwBalance = 0L,
                usdBalance = 0.0,
                themeId = HubCardThemeId.Forest,
                showBalance = false,
            ),
        )
    }
    val totalKrw = remember(cards) { cards.sumOf { it.krwBalance } }
    val totalUsd = remember(cards) { cards.sumOf { it.usdBalance } }
    val subtitle = remember(totalKrw, totalUsd) {
        "Total · ${formatKrwHub(totalKrw)} · ${formatUsdHub(totalUsd)}"
    }

    val pagerState = rememberPagerState(pageCount = { cards.size })
    val containerShape = RoundedCornerShape(24.dp)

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = containerShape, ambientColor = Color.Black.copy(alpha = 0.12f))
            .clip(containerShape)
            .background(palette.cardSurface)
            .padding(top = 20.dp, bottom = 20.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Credit cards",
                    color = palette.foreground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                )
            }
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .border(1.dp, palette.border.copy(alpha = 0.65f), RoundedCornerShape(999.dp))
                    .clickable(role = Role.Button, onClick = onManageCards)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "Manage cards",
                    color = palette.foreground,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    tint = palette.mutedForeground,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
        ) {
            val cardWidth = remember(maxWidth) {
                val target = maxWidth * 0.84f
                val capped = if (target > 334.dp) 334.dp else target
                val floored = if (capped < 258.dp) 258.dp else capped
                if (floored > maxWidth - 10.dp) maxWidth - 10.dp else floored
            }
            val sidePad = remember(maxWidth, cardWidth) {
                ((maxWidth - cardWidth) / 2).coerceAtLeast(0.dp)
            }
            val overlap = remember(cardWidth) { cardWidth * 0.12f }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = sidePad),
                pageSize = PageSize.Fixed(cardWidth),
                pageSpacing = -overlap,
                verticalAlignment = Alignment.CenterVertically,
                beyondViewportPageCount = 3,
            ) { page ->
                val pageOffsetPages = pagerState.getOffsetDistanceInPages(page)
                Box(
                    modifier = Modifier
                        .zIndex(520f - abs(pageOffsetPages) * 200f)
                        .graphicsLayer {
                            val adj = abs(pageOffsetPages).coerceIn(0f, 1f)
                            val t = 1f - adj
                            scaleX = lerp(0.87f, 1f, t)
                            scaleY = lerp(0.87f, 1f, t)
                            translationX = pageOffsetPages * 46.dp.toPx()
                            translationY = lerp(8.dp.toPx(), 0f, t)
                            alpha = lerp(0.9f, 1f, t)
                            cameraDistance = 18f * density
                        },
                ) {
                    HubCreditCardFace(
                        card = cards[page],
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(role = Role.Button, onClick = onOpenCardInfo),
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(cards.size) { index ->
                if (index > 0) Spacer(Modifier.width(6.dp))
                val selected = index == pagerState.currentPage
                if (selected) {
                    Box(
                        modifier = Modifier
                            .width(22.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(palette.brand),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(palette.mutedForeground.copy(alpha = 0.28f)),
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${cards.size} cards · tap to manage",
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
            Text(
                text = "+ Add new card",
                color = palette.brand,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(role = Role.Button, onClick = onAddNewCard),
            )
        }
    }
}

@Composable
private fun HubCreditCardFace(card: HubCreditCard, modifier: Modifier = Modifier) {
    val cardShape = RoundedCornerShape(32.dp)
    val spec = hubCardThemeSpec(card.themeId)
    val labelMuted = hubCardLabelMuted(card.themeId)
    val balancePrimaryText = remember(card.krwBalance, card.usdBalance) {
        hubPrimaryBalanceText(card.krwBalance, card.usdBalance)
    }
    val goldBalanceStyle = remember {
        TextStyle(
            fontFamily = RestaurantFontFamily,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.05.sp,
            brush = hubMetalGoldBrush(),
            shadow = Shadow(
                color = HubGoldShadowTone.copy(alpha = 0.48f),
                offset = Offset(0f, 2.5f),
                blurRadius = 16f,
            ),
        )
    }
    val silverUsdStyle = remember {
        TextStyle(
            fontFamily = RestaurantFontFamily,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.02.sp,
            brush = hubMetalSilverBrush(),
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.35f),
                offset = Offset(0f, 1.5f),
                blurRadius = 8f,
            ),
        )
    }
    val goldDetailStyle = remember {
        TextStyle(
            fontFamily = RestaurantFontFamily,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 3.2.sp,
            brush = hubMetalGoldBrush(),
            shadow = Shadow(
                color = HubGoldShadowTone.copy(alpha = 0.55f),
                offset = Offset(0f, 1.5f),
                blurRadius = 8f,
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
                ambientColor = spec.glow.copy(alpha = 0.55f),
                spotColor = spec.shadow.copy(alpha = 0.5f),
            )
            .clip(cardShape),
    ) {
        HubThemedCardBackground(
            themeId = card.themeId,
            modifier = Modifier.fillMaxSize(),
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(cardShape),
        ) {
            val w = size.width
            val h = size.height
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.12f),
                        Color.Transparent,
                        Color.Transparent,
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w * 0.55f, h * 0.45f),
                ),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(28.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.07f)),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.48f),
                            Color.White.copy(alpha = 0.14f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                TonightLogoMark(
                    modifier = Modifier.size(26.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    contentDescription = null,
                )
                Text(
                    text = card.productLabel,
                    color = Color.White,
                    fontFamily = RestaurantFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.02.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.height(18.dp))
            if (card.showBalance) {
                Text(
                    text = "AVAILABLE BALANCE",
                    color = labelMuted,
                    fontFamily = RestaurantFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.2.sp,
                )
                Spacer(Modifier.height(6.dp))
                if (card.showDualBalance && card.krwBalance > 0L && card.usdBalance > 0.0) {
                    HubMetallicGoldBalance(
                        text = formatKrwHub(card.krwBalance),
                        baseStyle = goldBalanceStyle,
                        shimmerPhase = shimmerPhase,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = formatUsdHub(card.usdBalance),
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
                        LuxuryEmvChip()
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    HubContactlessIcon(tint = Color.White.copy(alpha = 0.88f))
                }
                Spacer(Modifier.height(18.dp))
            }
            Spacer(Modifier.weight(1f))
            HubMaskedPanRow(lastFour = card.lastFour, goldDetailStyle = goldDetailStyle)
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
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = card.holder,
                        color = Color.White,
                        fontFamily = RestaurantFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
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
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
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

@Composable
private fun HubGoldLastFourDigit(char: String, style: TextStyle) {
    Box {
        Text(
            text = char,
            modifier = Modifier.offset(0.dp, 0.85.dp),
            style = style.copy(
                brush = SolidColor(HubGoldShadowTone.copy(alpha = 0.52f)),
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
                        Color.White.copy(alpha = 0.55f),
                        Color.Transparent,
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(0f, 18f),
                ),
                shadow = Shadow(Color.Transparent, Offset.Zero, blurRadius = 0f),
            ),
        )
    }
}

@Composable
private fun HubMaskedPanRow(
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

private fun formatKrwHub(value: Long): String {
    val nf = NumberFormat.getNumberInstance(Locale.US)
    return "₩${nf.format(value)}"
}

private fun formatUsdHub(value: Double): String {
    val nf = NumberFormat.getCurrencyInstance(Locale.US)
    nf.maximumFractionDigits = 2
    nf.minimumFractionDigits = 2
    return nf.format(value)
}
