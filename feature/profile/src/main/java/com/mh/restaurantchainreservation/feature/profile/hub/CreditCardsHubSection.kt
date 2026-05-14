@file:OptIn(ExperimentalFoundationApi::class)

package com.mh.restaurantchainreservation.feature.profile.hub

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.animation.core.spring
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

private data class HubCreditCard(
    val id: String,
    val productLabel: String,
    val holder: String,
    val lastFour: String,
    val krwBalance: Long,
    val usdBalance: Double,
    val themeId: HubCardThemeId,
    val pattern: HubCardPattern? = null,
    val showBalance: Boolean,
    val showDualBalance: Boolean = false,
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
        )
    }
    val totalKrw = remember(cards) { cards.sumOf { it.krwBalance } }
    val totalUsd = remember(cards) { cards.sumOf { it.usdBalance } }
    val subtitle = remember(totalKrw, totalUsd) {
        "Total · ${formatKrwHub(totalKrw)} · ${formatUsdHub(totalUsd)}"
    }

    val pagerState = rememberPagerState(pageCount = { cards.size + 1 })
    val stackedFling = PagerDefaults.flingBehavior(
        state = pagerState,
        snapAnimationSpec = spring(
            dampingRatio = 0.82f,
            stiffness = 320f,
        ),
    )
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
                .padding(vertical = 22.dp),
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
            // Slight deck overlap + gap so back layers read as separate sheets, not one blob.
            val overlap = remember(cardWidth) { cardWidth * 0.26f }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = sidePad),
                pageSize = PageSize.Fixed(cardWidth),
                pageSpacing = -overlap,
                verticalAlignment = Alignment.CenterVertically,
                beyondViewportPageCount = 3,
                flingBehavior = stackedFling,
            ) { page ->
                val d = pagerState.getOffsetDistanceInPages(page).coerceIn(-2.5f, 2.5f)
                Box(
                    modifier = Modifier
                        .zIndex(HubStackedCarouselMotion.zIndexForPage(d))
                        .graphicsLayer {
                            transformOrigin = TransformOrigin(0.5f, 0.52f)
                            cameraDistance = 14f * density
                            rotationZ = HubStackedCarouselMotion.rotationZForPage(d)
                            val scale = HubStackedCarouselMotion.scaleForPage(d)
                            scaleX = scale
                            scaleY = scale
                            translationX = HubStackedCarouselMotion.translationX(d, density)
                            translationY = HubStackedCarouselMotion.translationY(d, density)
                            alpha = HubStackedCarouselMotion.alphaForPage(d)
                        },
                ) {
                    if (page >= cards.size) {
                        AddNewCreditCardTile(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(role = Role.Button, onClick = onAddNewCard),
                        )
                    } else {
                        HubCreditCardFace(
                            card = cards[page],
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(role = Role.Button, onClick = onOpenCardInfo),
                        )
                    }
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
            repeat(cards.size + 1) { index ->
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
    SharedHubCardFace(
        model = SharedHubCardFaceModel(
            productLabel = card.productLabel,
            holder = card.holder,
            lastFour = card.lastFour,
            krwBalance = card.krwBalance,
            usdBalance = card.usdBalance,
            themeId = card.themeId,
            pattern = card.pattern ?: hubCardThemeSpec(card.themeId).pattern,
            showBalance = card.showBalance,
            showDualBalance = card.showDualBalance,
            frozen = false,
        ),
        modifier = modifier,
    )
}
