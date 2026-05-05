package com.mh.restaurantchainreservation.feature.dining.ui.scanqr

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.ScanStep

data class StepDescriptor(val id: ScanStep, val labelRes: Int)

val ScanSteps = listOf(
    StepDescriptor(ScanStep.Scan, I18nR.string.scan_step_scan),
    StepDescriptor(ScanStep.Arrived, I18nR.string.scan_step_arrived),
    StepDescriptor(ScanStep.Dining, I18nR.string.scan_step_dining),
    StepDescriptor(ScanStep.Bill, I18nR.string.scan_step_bill),
    StepDescriptor(ScanStep.Pay, I18nR.string.scan_step_pay),
    StepDescriptor(ScanStep.Review, I18nR.string.scan_step_review),
)

/**
 * StepProgressBar: row of segments that fill as steps complete.
 */
@Composable
fun StepProgressBar(currentStep: ScanStep, complete: Boolean = false) {
    val palette = LocalRestaurantPalette.current
    val currentIdx = ScanSteps.indexOfFirst { it.id == currentStep }.coerceAtLeast(0)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(I18nR.string.scan_progress_label),
                color = palette.mutedForeground,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = if (complete) stringResource(I18nR.string.scan_progress_complete) else stringResource(ScanSteps[currentIdx].labelRes),
                color = palette.brand,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            ScanSteps.forEachIndexed { i, step ->
                val completed = complete || i < currentIdx
                val active = !complete && i == currentIdx
                val color = when {
                    completed -> palette.success
                    active -> palette.brand
                    else -> palette.border
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(color),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(step.labelRes),
                        color = when {
                            completed -> palette.success
                            active -> palette.brand
                            else -> palette.mutedForeground.copy(alpha = 0.45f)
                        },
                        fontSize = 10.sp,
                        fontWeight = if (active || completed) FontWeight.ExtraBold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/**
 * QRCodeVisual: faux scanning QR with corner indicators + animated scan beam.
 */
@Composable
fun QRCodeVisual(active: Boolean = true) {
    val palette = LocalRestaurantPalette.current
    val cells = 21
    val pattern = remember {
        Array(cells) { r ->
            BooleanArray(cells) { c ->
                if ((r < 7 && c < 7) || (r < 7 && c >= cells - 7) || (r >= cells - 7 && c < 7)) {
                    return@BooleanArray r == 0 || r == 6 || c == 0 || c == 6 || (r in 2..4 && c in 2..4) ||
                        (r < 7 && c >= cells - 7 && (c == cells - 7 || c == cells - 1)) ||
                        (r >= cells - 7 && c < 7 && (r == cells - 7 || r == cells - 1))
                }
                ((r * 7 + c * 13 + r * c) % 3) == 0
            }
        }
    }

    val density = LocalDensity.current
    val transition = rememberInfiniteTransition(label = "scan_beam")
    val beamY by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scan_beam_y",
    )

    BoxWithConstraints(
        modifier = Modifier
            .size(220.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(16.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellSize = size.minDimension / cells
            for (r in 0 until cells) {
                for (c in 0 until cells) {
                    if (pattern[r][c]) {
                        drawRect(
                            color = Color(0xFF222222).copy(alpha = 0.8f),
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize - 1f, cellSize - 1f),
                        )
                    }
                }
            }
            // Corner finders
            val cornerLen = 32f
            val stroke = 4f
            val brand = palette.brand
            // top-left
            drawRect(brand, Offset(0f, 0f), Size(cornerLen, stroke))
            drawRect(brand, Offset(0f, 0f), Size(stroke, cornerLen))
            // top-right
            drawRect(brand, Offset(size.width - cornerLen, 0f), Size(cornerLen, stroke))
            drawRect(brand, Offset(size.width - stroke, 0f), Size(stroke, cornerLen))
            // bottom-left
            drawRect(brand, Offset(0f, size.height - stroke), Size(cornerLen, stroke))
            drawRect(brand, Offset(0f, size.height - cornerLen), Size(stroke, cornerLen))
            // bottom-right
            drawRect(brand, Offset(size.width - cornerLen, size.height - stroke), Size(cornerLen, stroke))
            drawRect(brand, Offset(size.width - stroke, size.height - cornerLen), Size(stroke, cornerLen))

            if (active) {
                val beamHeight = 36f
                val travel = size.height - beamHeight
                drawRect(
                    color = brand.copy(alpha = 0.18f),
                    topLeft = Offset(8f, beamY * travel),
                    size = Size(size.width - 16f, beamHeight),
                )
            }
        }
    }
    @Suppress("UNUSED_EXPRESSION") density
}

/**
 * StepIntro: large rounded icon + title + description.
 */
@Composable
fun StepIntro(
    icon: ImageVector,
    title: String,
    desc: String,
    tone: IntroTone = IntroTone.Primary,
) {
    val palette = LocalRestaurantPalette.current
    val (bg, fg) = when (tone) {
        IntroTone.Primary -> palette.brand.copy(alpha = 0.10f) to palette.brand
        IntroTone.Success -> palette.success.copy(alpha = 0.10f) to palette.success
        IntroTone.Warning -> palette.warning.copy(alpha = 0.10f) to palette.warning
        IntroTone.Info -> palette.info.copy(alpha = 0.10f) to palette.info
    }
    var pop by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { pop = true }
    val scale by animateFloatAsState(
        targetValue = if (pop) 1f else 0.8f,
        animationSpec = androidx.compose.animation.core.spring(stiffness = 280f, dampingRatio = 0.45f),
        label = "intro_scale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (pop) 1f else 0f,
        animationSpec = tween(220),
        label = "intro_alpha",
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale; scaleY = scale; this.alpha = alpha
                }
                .size(56.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = fg, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = title,
            color = palette.foreground,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = desc,
            color = palette.mutedForeground,
            fontSize = 14.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

enum class IntroTone { Primary, Success, Warning, Info }

/**
 * BookingMiniCard: 1-line summary card used inside scan flow.
 */
@Composable
fun BookingMiniCard(
    image: String,
    restaurant: String,
    date: String,
    time: String,
    guests: Int,
    seating: String,
    right: @Composable (() -> Unit)? = null,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, palette.border, RoundedCornerShape(22.dp))
            .background(palette.cardSurface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        coil.compose.AsyncImage(
            model = image,
            contentDescription = restaurant,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp)),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = restaurant, color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
            Text(
                text = "$date · $time · $guests ${if (guests == 1) "guest" else "guests"}",
                color = palette.mutedForeground,
                fontSize = 12.sp,
                maxLines = 1,
            )
            Text(text = "Table P1 · $seating", color = palette.brand, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        }
        right?.invoke()
    }
}

/** SubRatingRow used in the review step. */
@Composable
fun SubRatingRow(label: String, value: Int?, onChange: (Int?) -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.mutedSurface.copy(alpha = 0.65f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            for (i in 1..5) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (value != null && i <= value) palette.warning else palette.border,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onChange(i) },
                    )
                }
            }
        }
    }
}

