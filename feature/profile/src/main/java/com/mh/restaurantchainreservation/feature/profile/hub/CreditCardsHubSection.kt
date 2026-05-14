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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.icons.TonightLogoMark
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantFontFamily
import java.text.NumberFormat
import java.util.Locale

private enum class HubLuxuryVariant {
    Burgundy,
    Midnight,
}

private val HubGoldLight = Color(0xFFF7D774)
private val HubGoldMid = Color(0xFFD9A441)
private val HubGoldDeep = Color(0xFFB67A1F)
private val HubGoldShadowTone = Color(0xFF8A5612)

private val HubBurgundyStops = listOf(
    Color(0xFF5B0015),
    Color(0xFF76001F),
    Color(0xFFA0002C),
    Color(0xFFB3123B),
)

private val HubMidnightStops = listOf(
    Color(0xFF0B1026),
    Color(0xFF151A35),
    Color(0xFF252047),
    Color(0xFF362F6B),
)

private data class HubCreditCard(
    val id: String,
    val productLabel: String,
    val holder: String,
    val lastFour: String,
    val krwBalance: Long,
    val usdBalance: Double,
    val variant: HubLuxuryVariant,
    val preferUsdBalance: Boolean,
)

private fun hubGoldVerticalBrush(): Brush = Brush.verticalGradient(
    listOf(HubGoldLight, HubGoldMid, HubGoldDeep, HubGoldShadowTone),
)

private fun hubGoldDotBrush(): Brush = Brush.radialGradient(
    colors = listOf(HubGoldLight, HubGoldMid.copy(alpha = 0.92f)),
)

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
                id = "card-main",
                productLabel = "Tonight Card",
                holder = "ALEX CHEN",
                lastFour = "1595",
                krwBalance = 850_000L,
                usdBalance = 0.0,
                variant = HubLuxuryVariant.Burgundy,
                preferUsdBalance = false,
            ),
            HubCreditCard(
                id = "card-travel",
                productLabel = "Travel",
                holder = "ALEX CHEN",
                lastFour = "9021",
                krwBalance = 0L,
                usdBalance = 120.0,
                variant = HubLuxuryVariant.Midnight,
                preferUsdBalance = true,
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

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(0.dp),
            pageSpacing = 0.dp,
        ) { page ->
            HubCreditCardFace(
                card = cards[page],
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Button, onClick = onOpenCardInfo),
            )
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
    val balanceText = remember(card.krwBalance, card.usdBalance, card.preferUsdBalance) {
        if (card.preferUsdBalance) formatUsdHub(card.usdBalance) else formatKrwHub(card.krwBalance)
    }
    val goldBalanceStyle = remember {
        TextStyle(
            fontFamily = RestaurantFontFamily,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.2.sp,
            brush = hubGoldVerticalBrush(),
            shadow = Shadow(
                color = HubGoldShadowTone.copy(alpha = 0.42f),
                offset = Offset(0f, 2f),
                blurRadius = 14f,
            ),
        )
    }
    val goldDetailStyle = remember {
        TextStyle(
            fontFamily = RestaurantFontFamily,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 3.2.sp,
            brush = hubGoldVerticalBrush(),
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.35f),
                offset = Offset(0f, 1f),
                blurRadius = 6f,
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
                elevation = 22.dp,
                shape = cardShape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.14f),
                spotColor = Color.Black.copy(alpha = 0.22f),
            )
            .clip(cardShape),
    ) {
        LuxuryCardBackground(
            variant = card.variant,
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
                        Color.White.copy(alpha = 0.14f),
                        Color.Transparent,
                        Color.Transparent,
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w * 0.55f, h * 0.45f),
                ),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (card.id == "card-main") {
                    TonightLogoMark(
                        modifier = Modifier.size(26.dp),
                        color = Color.White.copy(alpha = 0.95f),
                        contentDescription = null,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.12f)),
                    )
                }
                Text(
                    text = card.productLabel,
                    color = Color.White,
                    fontFamily = RestaurantFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "AVAILABLE BALANCE",
                color = Color.White.copy(alpha = 0.52f),
                fontFamily = RestaurantFontFamily,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.2.sp,
            )
            Spacer(Modifier.height(6.dp))
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val density = LocalDensity.current
                val widthPx = with(density) { maxWidth.toPx() }
                val shimmerHeightPx = with(density) { 40.dp.toPx() }
                val band = widthPx * 0.42f
                Text(text = balanceText, style = goldBalanceStyle)
                Text(
                    text = balanceText,
                    style = goldBalanceStyle.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.38f),
                                Color.Transparent,
                            ),
                            start = Offset(shimmerPhase * widthPx - band, 0f),
                            end = Offset(shimmerPhase * widthPx + band, shimmerHeightPx),
                        ),
                        shadow = Shadow(Color.Transparent, Offset.Zero, blurRadius = 0f),
                    ),
                )
            }
            Spacer(Modifier.height(12.dp))
            Spacer(Modifier.weight(1f))
            HubMaskedPanRow(lastFour = card.lastFour, goldDetailStyle = goldDetailStyle)
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column {
                    Text(
                        text = "CARD HOLDER",
                        color = Color.White.copy(alpha = 0.48f),
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
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.4.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "EXPIRES",
                        color = Color.White.copy(alpha = 0.48f),
                        fontFamily = RestaurantFontFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "09/28",
                        style = goldDetailStyle.copy(fontSize = 13.sp, letterSpacing = 2.sp),
                    )
                }
            }
        }
    }
}

@Composable
private fun LuxuryCardBackground(
    variant: HubLuxuryVariant,
    modifier: Modifier = Modifier,
) {
    val stops = when (variant) {
        HubLuxuryVariant.Burgundy -> HubBurgundyStops
        HubLuxuryVariant.Midnight -> HubMidnightStops
    }
    val transition = rememberInfiniteTransition(label = "luxuryCardAmbient")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "drift",
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val shift = drift * w * 0.12f
        drawRect(
            brush = Brush.linearGradient(
                colorStops = arrayOf(
                    0f to stops[0],
                    0.35f to stops[1],
                    0.72f to stops[2],
                    1f to stops[3],
                ),
                start = Offset(-shift, h * 0.25f),
                end = Offset(w + shift, h * 0.82f),
            ),
        )
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.07f),
                    Color.Transparent,
                ),
                start = Offset(w * 0.15f + shift * 0.5f, 0f),
                end = Offset(w * 0.95f, h),
            ),
            blendMode = BlendMode.Screen,
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent),
                center = Offset(-w * 0.12f, h * 1.08f),
                radius = w * 0.85f,
            ),
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.22f), Color.Transparent),
                center = Offset(w * 1.08f, -h * 0.12f),
                radius = w * 0.62f,
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
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(hubGoldDotBrush()),
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            lastFour.forEach { ch ->
                Text(
                    text = ch.toString(),
                    style = goldDetailStyle,
                )
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
