package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.icons.BottomNavIconPaths
import com.mh.restaurantchainreservation.core.designsystem.components.icons.BottomNavStrokeIcon
import com.mh.restaurantchainreservation.core.designsystem.components.icons.LucidePaths
import com.mh.restaurantchainreservation.core.designsystem.components.HubSurfaceCardDefaults
import com.mh.restaurantchainreservation.core.designsystem.components.hubSurfaceShadow
import com.mh.restaurantchainreservation.core.designsystem.components.icons.QrPayNavIcon
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class BottomNavTabId { Discover, Wishlist, Dining, Profile }

data class BottomNavTab(
    val id: BottomNavTabId,
    val label: String,
)

private val TabIconSize = 24.dp
private val TabLabelFontSize = 12.sp
private val TabLabelLineHeight = 14.sp
private val TabLabelHeight = 14.dp
private val LabelTopGap = 4.dp
private val TabVerticalPadding = 6.dp
private val TabLabelBottomPadding = 12.dp
private val TabRowTopInset = 10.dp
private val TabItemMinWidth = 52.dp
private val TabContentHeight = TabIconSize + LabelTopGap + TabLabelHeight + TabVerticalPadding * 2
internal val BottomNavTabRowHeight = TabContentHeight + TabRowTopInset + TabLabelBottomPadding
private val TabRowHeight = BottomNavTabRowHeight
private val QrOuterDiameter = 68.dp
private val QrIconSize = 24.dp
private val QrFabBorderWidth = 4.dp
private val QrGlowHaloPadding = 10.dp
/** Vertical offset above the nav top edge (~30% of QR diameter + extra float). */
private val QrAboveNavBarLift = 24.dp
private val QrShadowElevationRest = 6.dp
private val QrShadowElevationActive = 10.dp

private val QrFabPressSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium,
)
private val QrFabVisualSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow,
)
private val QrFabElevationSpring = spring<Dp>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow,
)
internal val BottomNavTopBorderWidth = 1.dp
private val NavTopBorderWidth = BottomNavTopBorderWidth

/**
 * Space above the tab row reserved in the animated clip bounds for the docked QR FAB,
 * its shadow, and the center cutout (must match [QrAboveNavBarLift] + [QrOuterDiameter] layout).
 */
internal val BottomNavClipExtensionAboveTabRow =
    QrAboveNavBarLift + QrOuterDiameter + QrShadowElevationRest + 16.dp

/** Horizontal center of each nav control as a fraction of screen width. */
private const val DiscoverCenterFraction = 0.10f
private const val WishlistCenterFraction = 0.31f
private const val DiningCenterFraction = 0.69f
private const val ProfileCenterFraction = 0.89f

private val BadgeHeight = 16.dp
private val DotSize = 10.dp
private val DotBorder = 2.dp

private const val ActiveStrokeWidth = 2.5f
private const val InactiveStrokeWidth = 1.8f
private const val SurfaceTranslucentAlpha = 0.95f

@Composable
fun BottomNavBar(
    tabs: List<BottomNavTab>,
    activeId: BottomNavTabId,
    onTabSelect: (BottomNavTabId) -> Unit,
    onQrPay: () -> Unit,
    qrPayContentDescription: String,
    modifier: Modifier = Modifier,
    profileBadgeCount: Int = 0,
    showProfileDot: Boolean = false,
) {
    val cardSurface = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val discover = tabs.firstOrNull { it.id == BottomNavTabId.Discover }
    val wishlist = tabs.firstOrNull { it.id == BottomNavTabId.Wishlist }
    val dining = tabs.firstOrNull { it.id == BottomNavTabId.Dining }
    val profile = tabs.firstOrNull { it.id == BottomNavTabId.Profile }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .graphicsLayer { clip = false },
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .windowInsetsPadding(WindowInsets.navigationBars),
        ) {
            HorizontalDivider(
                color = borderColor,
                thickness = NavTopBorderWidth,
            )
            FractionalTabNavLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TabRowHeight)
                    .background(cardSurface.copy(alpha = SurfaceTranslucentAlpha)),
                discover = discover?.let { tab ->
                    {
                        TabButton(
                            tab = tab,
                            isActive = tab.id == activeId,
                            onSelect = { onTabSelect(tab.id) },
                            badgeCount = 0,
                            showDot = false,
                        )
                    }
                },
                wishlist = wishlist?.let { tab ->
                    {
                        TabButton(
                            tab = tab,
                            isActive = tab.id == activeId,
                            onSelect = { onTabSelect(tab.id) },
                            badgeCount = 0,
                            showDot = false,
                        )
                    }
                },
                dining = dining?.let { tab ->
                    {
                        TabButton(
                            tab = tab,
                            isActive = tab.id == activeId,
                            onSelect = { onTabSelect(tab.id) },
                            badgeCount = 0,
                            showDot = false,
                        )
                    }
                },
                profile = profile?.let { tab ->
                    {
                        TabButton(
                            tab = tab,
                            isActive = tab.id == activeId,
                            onSelect = { onTabSelect(tab.id) },
                            badgeCount = profileBadgeCount,
                            showDot = showProfileDot,
                        )
                    }
                },
            )
        }

        QrFloatingButton(
            onClick = onQrPay,
            contentDescription = qrPayContentDescription,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = NavTopBorderWidth)
                .graphicsLayer { translationY = -QrAboveNavBarLift.toPx() },
        )
    }
}

/**
 * Places four tab items at fixed horizontal center fractions (not equal columns).
 * Discover 10%, Wishlist 31%, Dining 69%, Profile 89% — center 50% reserved for QR FAB.
 */
@Composable
private fun FractionalTabNavLayout(
    modifier: Modifier = Modifier,
    discover: (@Composable () -> Unit)?,
    wishlist: (@Composable () -> Unit)?,
    dining: (@Composable () -> Unit)?,
    profile: (@Composable () -> Unit)?,
) {
    val slots = listOfNotNull(
        discover?.let { DiscoverCenterFraction to it },
        wishlist?.let { WishlistCenterFraction to it },
        dining?.let { DiningCenterFraction to it },
        profile?.let { ProfileCenterFraction to it },
    )

    Layout(
        content = { slots.forEach { (_, content) -> content() } },
        modifier = modifier,
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val placeables = measurables.map { measurable ->
            measurable.measure(
                Constraints(
                    minWidth = 0,
                    minHeight = 0,
                    maxWidth = constraints.maxWidth,
                    maxHeight = constraints.maxHeight,
                ),
            )
        }
        layout(width, height) {
            val topInsetPx = TabRowTopInset.roundToPx()
            val contentHeightPx = TabContentHeight.roundToPx()
            placeables.forEachIndexed { index, placeable ->
                val centerFraction = slots[index].first
                val centerX = (width * centerFraction).roundToInt()
                val x = (centerX - placeable.width / 2).coerceIn(0, (width - placeable.width).coerceAtLeast(0))
                val y = topInsetPx + (contentHeightPx - placeable.height).coerceAtLeast(0)
                placeable.placeRelative(x, y)
            }
        }
    }
}

@Composable
private fun TabButton(
    tab: BottomNavTab,
    isActive: Boolean,
    onSelect: () -> Unit,
    badgeCount: Int,
    showDot: Boolean,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    val density = LocalDensity.current

    val sparkleTrigger = remember { mutableLongStateOf(0L) }
    var prevActive by remember { mutableStateOf(isActive) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(isActive) {
        val newlyActive = isActive && !prevActive
        prevActive = isActive
        if (newlyActive) {
            sparkleTrigger.longValue = System.nanoTime()
        }
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = TabItemMinWidth, minHeight = TabContentHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onSelect,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(vertical = TabVerticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier.size(TabIconSize),
                contentAlignment = Alignment.Center,
            ) {
                SparkleEffect(triggerNanos = sparkleTrigger.longValue, color = primary)
                TabSelectionBounceBox(isActive = isActive) {
                    TabIcon(
                        tab = tab.id,
                        isActive = isActive,
                        primary = primary,
                        muted = muted,
                    )
                }
                if (badgeCount > 0) {
                    CountBadge(
                        count = badgeCount,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .graphicsLayer {
                                translationX = with(density) { 8.dp.toPx() }
                                translationY = with(density) { -6.dp.toPx() }
                            },
                    )
                } else if (showDot) {
                    AlertDot(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .graphicsLayer {
                                translationX = with(density) { 2.dp.toPx() }
                                translationY = with(density) { -2.dp.toPx() }
                            },
                    )
                }
            }
            Spacer(modifier = Modifier.height(LabelTopGap))
            Text(
                text = tab.label,
                modifier = Modifier.heightIn(min = TabLabelHeight),
                color = if (isActive) primary else muted,
                style = TextStyle(
                    fontSize = TabLabelFontSize,
                    lineHeight = TabLabelLineHeight,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None,
                    ),
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun TabIcon(
    tab: BottomNavTabId,
    isActive: Boolean,
    primary: Color,
    muted: Color,
) {
    when (tab) {
        BottomNavTabId.Discover -> BottomNavStrokeIcon(
            paths = BottomNavIconPaths.DiscoverSearch,
            isActive = isActive,
            activeColor = primary,
            inactiveColor = muted,
            iconSize = TabIconSize,
            activeStrokeWidth = 2.67f,
            inactiveStrokeWidth = 2f,
        )

        BottomNavTabId.Wishlist -> BottomNavStrokeIcon(
            paths = BottomNavIconPaths.WishlistHeart,
            isActive = isActive,
            activeColor = primary,
            inactiveColor = muted,
            iconSize = TabIconSize,
            activeStrokeWidth = ActiveStrokeWidth,
            inactiveStrokeWidth = InactiveStrokeWidth,
        )

        BottomNavTabId.Dining -> BottomNavStrokeIcon(
            paths = LucidePaths.UtensilsCrossed,
            isActive = isActive,
            activeColor = primary,
            inactiveColor = muted,
            iconSize = TabIconSize,
            activeStrokeWidth = ActiveStrokeWidth,
            inactiveStrokeWidth = InactiveStrokeWidth,
            viewportSize = 24f,
        )

        BottomNavTabId.Profile -> BottomNavStrokeIcon(
            paths = BottomNavIconPaths.ProfileInCircle,
            isActive = isActive,
            activeColor = primary,
            inactiveColor = muted,
            iconSize = TabIconSize,
            activeStrokeWidth = ActiveStrokeWidth,
            inactiveStrokeWidth = InactiveStrokeWidth,
        )
    }
}

@Composable
private fun CountBadge(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(BadgeHeight)
            .defaultMinSize(minWidth = BadgeHeight)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 10.sp,
        )
    }
}

@Composable
private fun AlertDot(modifier: Modifier = Modifier) {
    val card = MaterialTheme.colorScheme.surface
    val destructive = MaterialTheme.colorScheme.error
    Box(
        modifier = modifier
            .size(DotSize)
            .clip(CircleShape)
            .background(card),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(DotSize - DotBorder * 2)
                .clip(CircleShape)
                .background(destructive),
        )
    }
}

@Composable
private fun QrFloatingButton(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val fabGradient = remember(primary) {
        Brush.verticalGradient(
            colors = listOf(
                lerp(primary, Color.White, 0.14f),
                primary,
                lerp(primary, Color.Black, 0.07f),
            ),
        )
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isHighlighted = isPressed || isHovered

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = QrFabPressSpring,
        label = "qrPressScale",
    )
    val shadowElevation by animateDpAsState(
        targetValue = if (isHighlighted) QrShadowElevationActive else QrShadowElevationRest,
        animationSpec = QrFabElevationSpring,
        label = "qrShadowElevation",
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (isHighlighted) 0.48f else 0.30f,
        animationSpec = QrFabVisualSpring,
        label = "qrGlowAlpha",
    )
    val shadowAmbientAlpha by animateFloatAsState(
        targetValue = if (isHighlighted) {
            HubSurfaceCardDefaults.ShadowAmbientAlpha + 0.03f
        } else {
            HubSurfaceCardDefaults.ShadowAmbientAlpha
        },
        animationSpec = QrFabVisualSpring,
        label = "qrShadowAmbient",
    )
    val glowBrush = remember(primary, glowAlpha) {
        Brush.radialGradient(
            0f to primary.copy(alpha = glowAlpha * 0.85f),
            0.45f to primary.copy(alpha = glowAlpha * 0.35f),
            1f to Color.Transparent,
        )
    }

    Box(
        modifier = modifier.size(QrOuterDiameter + QrGlowHaloPadding * 2),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { clip = false }
                .background(glowBrush, CircleShape),
        )
        Box(
            modifier = Modifier
                .size(QrOuterDiameter)
                .graphicsLayer {
                    scaleX = pressScale
                    scaleY = pressScale
                    clip = false
                }
                .hubSurfaceShadow(
                    shape = CircleShape,
                    elevation = shadowElevation,
                    ambientAlpha = shadowAmbientAlpha,
                )
                .clip(CircleShape)
                .background(fabGradient)
                .border(QrFabBorderWidth, Color.White, CircleShape)
                .hoverable(interactionSource = interactionSource)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClickLabel = contentDescription,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            QrPayNavIcon(
                color = onPrimary,
                modifier = Modifier.size(QrIconSize),
            )
        }
    }
}

@Composable
private fun BoxScope.SparkleEffect(triggerNanos: Long, color: Color) {
    if (triggerNanos == 0L) return

    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
        label = "sparkleProgress",
    )

    if (progress >= 1f) return

    val particles = remember(triggerNanos) { generateParticles(triggerNanos) }
    val density = LocalDensity.current
    val pxPerDp = with(density) { 1.dp.toPx() }

    Canvas(
        modifier = Modifier
            .matchParentSize()
            .graphicsLayer { clip = false },
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        particles.forEach { p ->
            val tx = cos(p.angleRad) * p.distanceDp * pxPerDp * progress
            val ty = sin(p.angleRad) * p.distanceDp * pxPerDp * progress
            val scale = (1f - progress).coerceAtLeast(0f)
            val alpha = (1f - progress).coerceAtLeast(0f)
            val radiusPx = (p.sizeDp * pxPerDp * scale) / 2f
            if (radiusPx > 0f && alpha > 0f) {
                drawCircle(
                    color = color.copy(alpha = alpha),
                    radius = radiusPx,
                    center = center + Offset(tx, ty),
                )
            }
        }
    }
}

private data class SparkleParticle(
    val sizeDp: Float,
    val angleRad: Float,
    val distanceDp: Float,
)

private fun generateParticles(seed: Long): List<SparkleParticle> {
    val random = Random(seed)
    return List(8) { i ->
        val angleDeg = (i / 8f) * 360f + (random.nextFloat() * 20f - 10f)
        SparkleParticle(
            sizeDp = random.nextFloat() * 4f + 2f,
            angleRad = (angleDeg * Math.PI / 180f).toFloat(),
            distanceDp = random.nextFloat() * 16f + 12f,
        )
    }
}
