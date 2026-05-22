package com.mh.restaurantchainreservation.feature.qrpay

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object QrPayRoutes {
    const val Home = "qrpay"
}

private enum class QrStage { Scan, Pay, Success }

/**
 * Full-screen QR Pay overlay. Mirrors React `QRPayPage.tsx` (scan → pay → success).
 * Renders edge-to-edge (own header, fills system bars) so the host nav graph
 * should hide its bottom bar while this destination is active.
 */
@Composable
fun QrPayScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    var stage by remember { mutableStateOf(QrStage.Scan) }
    var address by remember { mutableStateOf("") }
    var paidUsd by remember { mutableStateOf(0.0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground)
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        QrPayHeader(
            stage = stage,
            onClose = {
                when (stage) {
                    QrStage.Pay -> stage = QrStage.Scan
                    QrStage.Scan,
                    QrStage.Success -> onClose()
                }
            },
        )

        AnimatedContent(
            targetState = stage,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f, fill = true),
            transitionSpec = {
                (fadeIn(tween(200)) + slideInHorizontally { it / 6 }) togetherWith
                    (fadeOut(tween(160)) + slideOutHorizontally { -it / 6 })
            },
            label = "qrpay_stage",
        ) { current ->
            when (current) {
                QrStage.Scan -> ScannerView(
                    onScanned = { detected ->
                        address = detected
                        stage = QrStage.Pay
                    },
                )
                QrStage.Pay -> PayView(
                    payTo = address,
                    onComplete = { usd ->
                        paidUsd = usd
                        stage = QrStage.Success
                    },
                )
                QrStage.Success -> SuccessView(
                    address = address,
                    amount = paidUsd,
                    onDone = onClose,
                )
            }
        }
    }
}

/* ── Header ─────────────────────────────────────── */

@Composable
private fun QrPayHeader(
    stage: QrStage,
    onClose: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val title = when (stage) {
        QrStage.Scan -> "QR Pay"
        QrStage.Pay -> "Confirm Payment"
        QrStage.Success -> "Complete"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.cardSurface)
            .border(width = 1.dp, color = palette.border)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            val icon = when (stage) {
                QrStage.Scan,
                QrStage.Success -> Icons.Filled.Close
                QrStage.Pay -> Icons.AutoMirrored.Filled.ArrowBack
            }
            Icon(
                imageVector = icon,
                contentDescription = if (stage == QrStage.Pay) "Back" else "Close",
                tint = palette.foreground,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = title,
            color = palette.foreground,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.weight(1f))
        Box(modifier = Modifier.size(40.dp))
    }
}

/* ── Stage 1: Scanner ───────────────────────────── */

@Composable
private fun ScannerView(onScanned: (String) -> Unit) {
    val palette = LocalRestaurantPalette.current
    var scanning by remember { mutableStateOf(false) }

    LaunchedEffect(scanning) {
        if (scanning) {
            delay(1800L)
            onScanned("0xA7c3...F92d - Sakura Omakase")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ScannerSquare(scanning = scanning)
        Spacer(Modifier.height(28.dp))
        Text(
            text = if (scanning) "Scanning QR code..." else "Position the QR code within the frame",
            color = palette.mutedForeground,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        if (!scanning) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.brand)
                    .clickable { scanning = true }
                    .padding(horizontal = 28.dp, vertical = 14.dp),
            ) {
                Text(
                    text = "Scan QR Code",
                    color = RestaurantColors.Base.white,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        } else {
            ScanningSpinnerRow()
        }
    }
}

@Composable
private fun ScannerSquare(scanning: Boolean) {
    val palette = LocalRestaurantPalette.current
    val side = 256.dp
    val corner = 28.dp
    val bracketLen = 36.dp
    val bracketStroke = 4.dp
    val brand = palette.brand
    val dashColor = brand.copy(alpha = 0.32f)

    Box(
        modifier = Modifier.size(side),
        contentAlignment = Alignment.Center,
    ) {
        // Dashed border square.
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(corner))
                .drawBehind {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                    drawRoundRect(
                        color = dashColor,
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner.toPx(), corner.toPx()),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 2f,
                            pathEffect = pathEffect,
                        ),
                    )
                },
        )

        // 4 corner brackets.
        CornerBracket(Alignment.TopStart, corner, bracketLen, bracketStroke, brand, top = true, leading = true)
        CornerBracket(Alignment.TopEnd, corner, bracketLen, bracketStroke, brand, top = true, leading = false)
        CornerBracket(Alignment.BottomStart, corner, bracketLen, bracketStroke, brand, top = false, leading = true)
        CornerBracket(Alignment.BottomEnd, corner, bracketLen, bracketStroke, brand, top = false, leading = false)

        // Animated scan line.
        if (scanning) {
            BoxWithConstraints(modifier = Modifier.matchParentSize()) {
                val density = LocalDensity.current
                val heightPx = with(density) { maxHeight.toPx() }
                val transition = rememberInfiniteTransition(label = "scanline")
                val pos by transition.animateFloat(
                    initialValue = 0.10f,
                    targetValue = 0.85f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse,
                    ),
                    label = "scanline_pos",
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(2.dp)
                        .offset {
                            androidx.compose.ui.unit.IntOffset(
                                x = 0,
                                y = (pos * heightPx).toInt(),
                            )
                        }
                        .background(brand, RoundedCornerShape(2.dp)),
                )
            }
        }

        // Center scan icon.
        Icon(
            imageVector = Icons.Outlined.QrCodeScanner,
            contentDescription = null,
            tint = if (scanning) brand else palette.mutedForeground.copy(alpha = 0.45f),
            modifier = Modifier.size(64.dp),
        )
    }
}

@Composable
private fun BoxScopeBracketShim() = Unit // placeholder to keep the file simple

@Composable
private fun androidx.compose.foundation.layout.BoxScope.CornerBracket(
    alignment: Alignment,
    corner: androidx.compose.ui.unit.Dp,
    length: androidx.compose.ui.unit.Dp,
    stroke: androidx.compose.ui.unit.Dp,
    color: Color,
    top: Boolean,
    leading: Boolean,
) {
    Box(
        modifier = Modifier
            .align(alignment)
            .size(length)
            .drawBehind {
                val s = stroke.toPx()
                val w = size.width
                val h = size.height
                if (top) {
                    drawRect(color = color, topLeft = Offset(0f, 0f), size = androidx.compose.ui.geometry.Size(w, s))
                } else {
                    drawRect(color = color, topLeft = Offset(0f, h - s), size = androidx.compose.ui.geometry.Size(w, s))
                }
                if (leading) {
                    drawRect(color = color, topLeft = Offset(0f, 0f), size = androidx.compose.ui.geometry.Size(s, h))
                } else {
                    drawRect(color = color, topLeft = Offset(w - s, 0f), size = androidx.compose.ui.geometry.Size(s, h))
                }
            },
    )
}

@Composable
private fun ScanningSpinnerRow() {
    val palette = LocalRestaurantPalette.current
    val transition = rememberInfiniteTransition(label = "spin")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "spin_angle",
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer { rotationZ = angle }
                .drawBehind {
                    drawCircle(
                        color = palette.brand,
                        radius = size.minDimension / 2f - 2f,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f),
                    )
                    drawArc(
                        color = palette.cardSurface,
                        startAngle = -90f,
                        sweepAngle = 90f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f),
                    )
                },
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = "Detecting...",
            color = palette.brand,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

/* ── Stage 2: Pay ───────────────────────────────── */

@Composable
private fun PayView(
    payTo: String,
    onComplete: (Double) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val scope = rememberCoroutineScope()
    var currency by remember { mutableStateOf(QrCurrency.KRW) }
    var raw by remember { mutableStateOf("0") }

    val displayAmount = formatAmountString(raw, currency)
    val numeric = amountAsNumber(raw)
    val krwToUsd = 1300.0
    val usdEquivalent = if (currency == QrCurrency.KRW) numeric / krwToUsd else numeric
    val canPay = numeric > 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.mutedSurface.copy(alpha = 0.4f))
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        PayToCard(payTo = payTo)
        AmountCard(
            currency = currency,
            displayAmount = displayAmount,
            onSwitch = { next ->
                if (next != currency) {
                    raw = "0"
                    currency = next
                }
            },
        )
        MoneyKeypad(
            currency = currency,
            onDigit = { raw = appendDigit(raw, it, currency) },
            onBackspace = { raw = backspaceDigit(raw) },
        )
        Spacer(Modifier.height(8.dp))
        SlideToPayButton(
            label = if (canPay) "Slide to pay" else "Enter an amount",
            enabled = canPay,
            disabledLabel = "Enter an amount",
            onComplete = {
                scope.launch {
                    delay(1200L)
                    onComplete(usdEquivalent)
                }
            },
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun PayToCard(payTo: String) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.cardSurface)
            .border(1.dp, palette.border, RoundedCornerShape(20.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(palette.brandSoftSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Receipt,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Pay to",
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
            Text(
                text = payTo.ifBlank { "Unknown recipient" },
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun AmountCard(
    currency: QrCurrency,
    displayAmount: String,
    onSwitch: (QrCurrency) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(palette.cardSurface)
            .border(1.dp, palette.border, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(palette.mutedSurface)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CurrencyChip(label = "KRW", active = currency == QrCurrency.KRW) { onSwitch(QrCurrency.KRW) }
            CurrencyChip(label = "USD", active = currency == QrCurrency.USD) { onSwitch(QrCurrency.USD) }
        }
        Spacer(Modifier.height(10.dp))
        AnimatedAmountDisplay(
            amount = displayAmount,
            symbol = if (currency == QrCurrency.KRW) "₩" else "$",
            symbolColor = palette.mutedForeground,
            valueColor = palette.foreground,
            fontSize = 44,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (currency == QrCurrency.KRW) "Tonight Wallet · KRW pocket" else "Tonight Wallet · USD pocket",
            color = palette.mutedForeground,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun CurrencyChip(label: String, active: Boolean, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (active) palette.cardSurface else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (active) palette.foreground else palette.mutedForeground,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp,
        )
    }
}

/* ── Stage 3: Success ───────────────────────────── */

@Composable
private fun SuccessView(
    address: String,
    amount: Double,
    onDone: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(stiffness = 200f, dampingRatio = 0.55f),
        label = "success_scale",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(CircleShape)
                .background(palette.success.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = palette.success,
                modifier = Modifier.size(56.dp),
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Payment Sent",
            color = palette.foreground,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "$%.2f paid to".format(amount),
            color = palette.mutedForeground,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = address.ifBlank { "Unknown" },
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(palette.brand)
                .clickable(onClick = onDone)
                .padding(horizontal = 36.dp, vertical = 14.dp),
        ) {
            Text(
                text = "Done",
                color = RestaurantColors.Base.white,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
