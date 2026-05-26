package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** Shared metrics for restaurant / booking detail collapsing-hero screens. */
object DetailCollapsingMetrics {
    val heroHeight = 288.dp
    val sheetTopRadius = 34.dp
    val toolbarBodyHeight = 56.dp
    /** Large title top padding inside the white sheet (restaurant detail). */
    val sheetTitleTopPadding = 28.dp
    val sheetTitleLineHeight = 38.dp

    /** Y of the white sheet's top edge in scroll content (hero overlap). */
    val sheetTopInContent: Dp get() = heroHeight - sheetTopRadius

    /** Y of the bottom edge of the large sheet title (restaurant detail). */
    val sheetTitleBottomInContent: Dp
        get() = sheetTopInContent + sheetTitleTopPadding + sheetTitleLineHeight

    fun heroScrollRangePx(density: androidx.compose.ui.unit.Density): Float =
        with(density) { sheetTopInContent.toPx().coerceAtLeast(1f) }

    fun toolbarBottomPx(density: androidx.compose.ui.unit.Density, statusBarTopPx: Float): Float =
        with(density) { toolbarBodyHeight.toPx() + statusBarTopPx }

    /** Collapse progress when the white sheet top meets the bottom of the sticky toolbar. */
    fun sheetMeetsHeaderProgress(
        density: androidx.compose.ui.unit.Density,
        statusBarTopPx: Float,
    ): Float {
        val range = heroScrollRangePx(density)
        if (range <= 0f) return 0.68f
        val scrollAtMeet = with(density) { sheetTopInContent.toPx() } - toolbarBottomPx(density, statusBarTopPx)
        return (scrollAtMeet / range).coerceIn(0.05f, 0.95f)
    }

    /**
     * Collapse progress when [titleBottomFromContentTop] has scrolled fully behind the toolbar.
     * Restaurant detail uses [sheetTitleBottomInContent]; booking passes the hero overlay title bottom.
     */
    fun contentTitleClearedProgress(
        density: androidx.compose.ui.unit.Density,
        statusBarTopPx: Float,
        titleBottomFromContentTop: Dp = sheetTitleBottomInContent,
    ): Float {
        val range = heroScrollRangePx(density)
        if (range <= 0f) return 0.88f
        val scrollAtClear =
            with(density) { titleBottomFromContentTop.toPx() } - toolbarBottomPx(density, statusBarTopPx)
        return (scrollAtClear / range).coerceIn(0.05f, 0.99f)
    }

    fun collapseProgress(
        firstVisibleItemIndex: Int,
        scrollOffsetPx: Int,
        collapseRangePx: Float,
    ): Float {
        if (collapseRangePx <= 0f) return 0f
        if (firstVisibleItemIndex > 0) return 1f
        return (scrollOffsetPx / collapseRangePx).coerceIn(0f, 1f)
    }

    fun collapseProgressFromScroll(scrollPx: Int, collapseRangePx: Float): Float {
        if (collapseRangePx <= 0f) return 0f
        return (scrollPx / collapseRangePx).coerceIn(0f, 1f)
    }

    /**
     * Hero wash-out: invisible once the sheet top reaches the toolbar (~99%+ white at that point).
     * Overlay stays at 0 until scrolling starts so the hero stays interactive at rest.
     */
    fun heroWhiteOverlayAlpha(collapseProgress: Float, sheetMeetsHeaderProgress: Float): Float {
        if (collapseProgress <= 0f) return 0f
        val meet = sheetMeetsHeaderProgress.coerceIn(0.08f, 0.92f)
        if (collapseProgress >= meet) return 1f
        val t = (collapseProgress / meet).coerceIn(0f, 1f)
        // Quartic ease — stays light early, ramps hard as the sheet approaches the header.
        val eased = t * t * t * t
        return (eased * 0.98f).coerceIn(0f, 0.98f)
    }

    fun isHeroFullyFaded(collapseProgress: Float, sheetMeetsHeaderProgress: Float): Boolean {
        val meet = sheetMeetsHeaderProgress.coerceIn(0.08f, 0.92f)
        return collapseProgress >= meet
    }

    private fun smoothstep(t: Float): Float {
        val x = t.coerceIn(0f, 1f)
        return x * x * (3f - 2f * x)
    }

    /**
     * Bottom rule appears first — before the header fills white — so sheet content tucks
     * under a visible edge as it scrolls up.
     */
    fun headerBorderAlpha(
        collapseProgress: Float,
        sheetMeetsHeaderProgress: Float,
    ): Float {
        val meet = sheetMeetsHeaderProgress.coerceIn(0.08f, 0.92f)
        val start = (meet * 0.90f).coerceAtLeast(0.04f)
        val end = meet + 0.015f
        if (collapseProgress < start) return 0f
        if (collapseProgress >= end) return 1f
        return smoothstep(((collapseProgress - start) / (end - start).coerceAtLeast(0.02f)).coerceIn(0f, 1f))
    }

    /**
     * Opaque toolbar background — only after the hero is fully white, plus a short scroll delay.
     */
    fun headerBackgroundAlpha(collapseProgress: Float, sheetMeetsHeaderProgress: Float): Float {
        val meet = sheetMeetsHeaderProgress.coerceIn(0.08f, 0.92f)
        if (!isHeroFullyFaded(collapseProgress, meet)) return 0f
        val delay = 0.035f
        val start = meet + delay
        if (collapseProgress < start) return 0f
        if (collapseProgress >= start + 0.05f) return 1f
        return smoothstep(((collapseProgress - start) / 0.05f).coerceIn(0f, 1f))
    }

    /** Sticky title — after the large content title clears, fade in quickly. */
    fun headerTitleAlpha(collapseProgress: Float, contentTitleClearedProgress: Float): Float {
        val start = contentTitleClearedProgress.coerceIn(0.05f, 0.99f)
        if (collapseProgress < start) return 0f
        if (collapseProgress >= start + 0.04f) return 1f
        val t = ((collapseProgress - start) / 0.04f).coerceIn(0f, 1f)
        return smoothstep(t)
    }

    fun floatingButtonPlateAlpha(collapseProgress: Float): Float =
        (1f - collapseProgress * 1.35f).coerceIn(0f, 1f)

    fun morphingSheetCornerRadius(collapseProgress: Float): Dp =
        lerp(sheetTopRadius, 0.dp, collapseProgress.coerceIn(0f, 1f))

    /** Counter-scroll parallax so the hero lingers briefly while the sheet rises. */
    fun heroParallaxTranslationY(scrollOffsetPx: Int): Float = scrollOffsetPx * 0.42f
}

const val DetailHeroPullScaleMax = 0.04f
const val DetailHeroMaxPullFraction = 0.12f
val DetailHeroPullReleaseSpring = spring<Float>(
    dampingRatio = 0.85f,
    stiffness = 380f,
)

/**
 * Shared pull-to-stretch motion logic for restaurant and booking details.
 * Binds to a [NestedScrollConnection] and drives [pullDistancePx].
 */
class DetailHeroPullMotion {
    val pullDistancePx = mutableFloatStateOf(0f)
    private var scope: CoroutineScope? = null
    private var releaseJob: Job? = null

    fun bind(scope: CoroutineScope) {
        this.scope = scope
    }

    private fun setPullDistance(value: Float, maxPullPx: Float) {
        releaseJob?.cancel()
        releaseJob = null
        pullDistancePx.floatValue = value.coerceIn(0f, maxPullPx)
    }

    private fun animateRelease() {
        val current = pullDistancePx.floatValue
        if (current <= 0f) return
        val s = scope ?: return
        releaseJob?.cancel()
        releaseJob = s.launch {
            animate(
                initialValue = current,
                targetValue = 0f,
                animationSpec = DetailHeroPullReleaseSpring,
            ) { value, _ ->
                pullDistancePx.floatValue = value
            }
            pullDistancePx.floatValue = 0f
            releaseJob = null
        }
    }

    fun nestedScrollConnection(
        maxPullPx: Float,
        isAtTop: () -> Boolean,
    ): NestedScrollConnection = object : NestedScrollConnection {
        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (available.y <= 0f) return Offset.Zero
            if (!isAtTop()) return Offset.Zero

            val room = maxPullPx - pullDistancePx.floatValue
            if (room <= 0f) return Offset.Zero
            val pull = min(available.y, room)
            setPullDistance(pullDistancePx.floatValue + pull, maxPullPx)
            return Offset(0f, pull)
        }

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (!isAtTop()) {
                if (pullDistancePx.floatValue > 0f) animateRelease()
                return Offset.Zero
            }
            if (available.y >= 0f || pullDistancePx.floatValue <= 0f) return Offset.Zero
            val release = min(-available.y, pullDistancePx.floatValue)
            setPullDistance(pullDistancePx.floatValue - release, maxPullPx)
            return Offset(0f, -release)
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            if (pullDistancePx.floatValue > 0f) animateRelease()
            return available
        }
    }
}

@Composable
fun rememberDetailHeroPullMotion(
    scope: CoroutineScope = rememberCoroutineScope(),
): DetailHeroPullMotion {
    val motion = remember { DetailHeroPullMotion() }
    SideEffect { motion.bind(scope) }
    return motion
}

data class DetailTransitionThresholds(
    val sheetMeetsHeader: Float,
    val contentTitleCleared: Float,
)

@Composable
fun rememberDetailTransitionThresholds(
    titleBottomFromContentTop: Dp = DetailCollapsingMetrics.sheetTitleBottomInContent,
): DetailTransitionThresholds {
    val density = LocalDensity.current
    val statusBarTopPx = WindowInsets.statusBars.getTop(density).toFloat()
    return remember(density, titleBottomFromContentTop, statusBarTopPx) {
        DetailTransitionThresholds(
            sheetMeetsHeader = DetailCollapsingMetrics.sheetMeetsHeaderProgress(
                density = density,
                statusBarTopPx = statusBarTopPx,
            ),
            contentTitleCleared = DetailCollapsingMetrics.contentTitleClearedProgress(
                density = density,
                statusBarTopPx = statusBarTopPx,
                titleBottomFromContentTop = titleBottomFromContentTop,
            ),
        )
    }
}

@Composable
fun rememberDetailCollapseProgress(
    listState: LazyListState,
    collapseRangePx: Float,
): Float {
    val progress by remember(listState, collapseRangePx) {
        derivedStateOf {
            DetailCollapsingMetrics.collapseProgress(
                firstVisibleItemIndex = listState.firstVisibleItemIndex,
                scrollOffsetPx = listState.firstVisibleItemScrollOffset,
                collapseRangePx = collapseRangePx,
            )
        }
    }
    return progress
}

@Composable
fun rememberDetailCollapseProgress(
    scrollState: ScrollState,
    collapseRangePx: Float,
): Float {
    val progress by remember(scrollState, collapseRangePx) {
        derivedStateOf {
            DetailCollapsingMetrics.collapseProgressFromScroll(
                scrollPx = scrollState.value,
                collapseRangePx = collapseRangePx,
            )
        }
    }
    return progress
}

@Composable
fun rememberDetailHeroScrollOffsetPx(listState: LazyListState, collapseRangePx: Float): Int {
    val offsetPx by remember(listState, collapseRangePx) {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) {
                listState.firstVisibleItemScrollOffset
            } else {
                collapseRangePx.toInt()
            }
        }
    }
    return offsetPx
}

fun Modifier.detailHeroParallax(scrollOffsetPx: Int): Modifier = graphicsLayer {
    translationY = DetailCollapsingMetrics.heroParallaxTranslationY(scrollOffsetPx)
}

/** White wash applied only after scrolling begins — hero stays fully interactive at rest. */
@Composable
fun DetailHeroScrollOverlay(
    collapseProgress: Float,
    modifier: Modifier = Modifier,
    transitionThresholds: DetailTransitionThresholds = rememberDetailTransitionThresholds(),
) {
    if (collapseProgress <= 0f) return
    val overlayAlpha = DetailCollapsingMetrics.heroWhiteOverlayAlpha(
        collapseProgress = collapseProgress,
        sheetMeetsHeaderProgress = transitionThresholds.sheetMeetsHeader,
    )
    if (overlayAlpha <= 0f) return
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = overlayAlpha)),
    )
}

fun Modifier.detailMorphingSheetBackground(
    color: Color,
    collapseProgress: Float,
): Modifier {
    val topRadius = DetailCollapsingMetrics.morphingSheetCornerRadius(collapseProgress)
    return drawBehind {
        val radiusPx = topRadius.toPx()
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(0f, 0f, size.width, size.height),
                    topLeft = CornerRadius(radiusPx, radiusPx),
                    topRight = CornerRadius(radiusPx, radiusPx),
                    bottomRight = CornerRadius.Zero,
                    bottomLeft = CornerRadius.Zero,
                ),
            )
        }
        drawPath(path, color = color)
    }
}

@Composable
fun detailMorphingSheetShape(collapseProgress: Float): RoundedCornerShape {
    val radius = DetailCollapsingMetrics.morphingSheetCornerRadius(collapseProgress)
    return RoundedCornerShape(topStart = radius, topEnd = radius)
}

/**
 * Airbnb-style floating toolbar: circular white chips over the hero at rest, plain icons on a
 * solid sticky bar once the content sheet reaches the header.
 */
@Composable
fun DetailFloatingToolbar(
    title: String,
    collapseProgress: Float,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    backContentDescription: String = "Back",
    transitionThresholds: DetailTransitionThresholds = rememberDetailTransitionThresholds(),
    actions: @Composable RowScope.(plateAlpha: Float) -> Unit = {},
) {
    val palette = LocalRestaurantPalette.current
    val plateAlpha = DetailCollapsingMetrics.floatingButtonPlateAlpha(collapseProgress)
    val headerBackgroundAlpha = DetailCollapsingMetrics.headerBackgroundAlpha(
        collapseProgress = collapseProgress,
        sheetMeetsHeaderProgress = transitionThresholds.sheetMeetsHeader,
    )
    val titleAlpha = DetailCollapsingMetrics.headerTitleAlpha(
        collapseProgress = collapseProgress,
        contentTitleClearedProgress = transitionThresholds.contentTitleCleared,
    )
    val borderAlpha = DetailCollapsingMetrics.headerBorderAlpha(
        collapseProgress = collapseProgress,
        sheetMeetsHeaderProgress = transitionThresholds.sheetMeetsHeader,
    )
    val headerBackground = when {
        headerBackgroundAlpha >= 1f -> palette.pageBackground
        headerBackgroundAlpha <= 0f -> Color.Transparent
        else -> palette.pageBackground.copy(alpha = headerBackgroundAlpha)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(headerBackground)
            .then(
                if (borderAlpha > 0f) {
                    Modifier.drawBehind {
                        val stroke = 1.dp.toPx()
                        drawLine(
                            color = palette.border.copy(alpha = borderAlpha),
                            start = Offset(0f, size.height - stroke / 2f),
                            end = Offset(size.width, size.height - stroke / 2f),
                            strokeWidth = stroke,
                        )
                    }
                } else {
                    Modifier
                },
            )
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(DetailCollapsingMetrics.toolbarBodyHeight)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DetailFloatingIconButton(
                onClick = onBack,
                plateAlpha = plateAlpha,
                contentDescription = backContentDescription,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = palette.foreground,
                    modifier = Modifier.size(DetailToolbarIconSize),
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (titleAlpha > 0.05f) {
                    Text(
                        text = title,
                        color = palette.foreground.copy(alpha = titleAlpha),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                actions(plateAlpha)
            }
        }
    }
}

@Composable
fun DetailFloatingIconButton(
    onClick: () -> Unit,
    plateAlpha: Float,
    contentDescription: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val plateColor = detailFloatingPlateColor(plateAlpha)
    Box(
        modifier = modifier
            .detailFloatingPlateContainer(plateAlpha)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

private val DetailFloatingPlateSize = 32.dp
private val DetailToolbarIconSize = 18.dp

/** Soft frosted grey chip over hero photos — not pure white (Airbnb-style). */
private fun detailFloatingPlateColor(plateAlpha: Float): Color {
    if (plateAlpha <= 0f) return Color.Transparent
    return Color(0xFFE9E9E9).copy(alpha = (0.90f * plateAlpha).coerceIn(0f, 1f))
}

private fun Modifier.detailFloatingPlateContainer(plateAlpha: Float): Modifier {
    val showPlate = plateAlpha > 0.04f
    return this
        .size(DetailFloatingPlateSize)
        .then(
            if (showPlate) {
                Modifier
                    .clip(CircleShape)
                    .background(detailFloatingPlateColor(plateAlpha))
            } else {
                Modifier
            },
        )
}

/**
 * Detail-toolbar wishlist control: white circular plate + black outline heart at rest;
 * after scroll collapse only the heart outline (or red fill when saved) remains.
 */
@Composable
fun DetailFloatingHeartButton(
    active: Boolean,
    plateAlpha: Float,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .detailFloatingPlateContainer(plateAlpha)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (active) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = contentDescription,
            modifier = Modifier.size(DetailToolbarIconSize),
            tint = if (active) RestaurantColors.Semantic.heart else palette.foreground,
        )
    }
}
