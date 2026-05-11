package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.icons.LucideIcon
import com.mh.restaurantchainreservation.core.designsystem.components.icons.LucidePaths
import com.mh.restaurantchainreservation.core.designsystem.components.icons.QrCodeIcon
import com.mh.restaurantchainreservation.core.designsystem.components.icons.TonightLogoMark
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.launch

enum class BottomNavTabId { Discover, Wishlist, Dining, Profile }

data class BottomNavTab(
    val id: BottomNavTabId,
    val label: String,
)

private val TabVerticalPadding = 10.dp
private val IconSizeStandard = 22.dp
private val IconSizeLogo = 26.dp
private val LabelTopGap = 2.dp
private val QrButtonSize: Dp = 60.dp
private val QrIconSize = 26.dp
private val QrBorderWidth = 4.dp
private val QrLift = 20.dp
private val BadgeHeight = 16.dp
private val DotSize = 10.dp
private val DotBorder = 2.dp
private val NavTopBorderWidth = 1.dp

private const val ActiveBackgroundAlpha = 0.08f
private const val ActiveIconFillAlpha = 0.15f
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            HorizontalDivider(
                color = borderColor,
                thickness = NavTopBorderWidth,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardSurface.copy(alpha = SurfaceTranslucentAlpha))
                    .windowInsetsPadding(WindowInsets.navigationBars),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEach { tab ->
                    TabButton(
                        tab = tab,
                        isActive = tab.id == activeId,
                        onSelect = { onTabSelect(tab.id) },
                        badgeCount = if (tab.id == BottomNavTabId.Profile) profileBadgeCount else 0,
                        showDot = tab.id == BottomNavTabId.Profile && showProfileDot,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        QrFloatingButton(
            onClick = onQrPay,
            contentDescription = qrPayContentDescription,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = NavTopBorderWidth)
                .graphicsLayer { translationY = -QrLift.toPx() },
        )
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

    val scale = remember { Animatable(1f) }
    val translateYDp = remember { Animatable(0f) }
    val sparkleTrigger = remember { mutableLongStateOf(0L) }
    var prevActive by remember { mutableStateOf(isActive) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(isActive) {
        val newlyActive = isActive && !prevActive
        prevActive = isActive
        if (newlyActive) {
            sparkleTrigger.longValue = System.nanoTime()
            scope.launch {
                scale.snapTo(1f)
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = keyframes {
                        durationMillis = 350
                        1f at 0
                        0.8f at 88
                        1.15f at 263
                        1f at 350
                    },
                )
            }
            scope.launch {
                translateYDp.snapTo(0f)
                translateYDp.animateTo(
                    targetValue = 0f,
                    animationSpec = keyframes {
                        durationMillis = 350
                        0f at 0
                        2f at 88
                        -2f at 263
                        0f at 350
                    },
                )
            }
        }
    }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onSelect,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(primary.copy(alpha = ActiveBackgroundAlpha)),
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = TabVerticalPadding),
        ) {
            Box(contentAlignment = Alignment.Center) {
                SparkleEffect(triggerNanos = sparkleTrigger.longValue, color = primary)
                Box(
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        translationY = with(density) { translateYDp.value.dp.toPx() }
                    },
                ) {
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
                color = if (isActive) primary else muted,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 14.sp,
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
        BottomNavTabId.Discover -> TonightLogoMark(
            modifier = Modifier.size(IconSizeLogo),
            color = if (isActive) primary else muted,
        )

        BottomNavTabId.Wishlist -> LucideIcon(
            paths = LucidePaths.Heart,
            modifier = Modifier.size(IconSizeStandard),
            strokeColor = if (isActive) primary else muted,
            fillColor = if (isActive) primary.copy(alpha = ActiveIconFillAlpha) else Color.Transparent,
            strokeWidth = if (isActive) ActiveStrokeWidth else InactiveStrokeWidth,
        )

        BottomNavTabId.Dining -> LucideIcon(
            paths = LucidePaths.UtensilsCrossed,
            modifier = Modifier.size(IconSizeStandard),
            strokeColor = if (isActive) primary else muted,
            fillColor = if (isActive) primary.copy(alpha = ActiveIconFillAlpha) else Color.Transparent,
            strokeWidth = if (isActive) ActiveStrokeWidth else InactiveStrokeWidth,
        )

        BottomNavTabId.Profile -> LucideIcon(
            paths = LucidePaths.User,
            modifier = Modifier.size(IconSizeStandard),
            strokeColor = if (isActive) primary else muted,
            fillColor = if (isActive) primary.copy(alpha = ActiveIconFillAlpha) else Color.Transparent,
            strokeWidth = if (isActive) ActiveStrokeWidth else InactiveStrokeWidth,
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
    val card = MaterialTheme.colorScheme.surface
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(durationMillis = 120, easing = LinearOutSlowInEasing),
        label = "qrPressScale",
    )

    Box(
        modifier = modifier
            .size(QrButtonSize)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .shadow(elevation = 6.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(card)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClickLabel = contentDescription,
                onClick = onClick,
            )
            .padding(QrBorderWidth)
            .clip(CircleShape)
            .background(primary),
        contentAlignment = Alignment.Center,
    ) {
        QrCodeIcon(
            color = onPrimary,
            modifier = Modifier.size(QrIconSize),
        )
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
