package com.mh.restaurantchainreservation.feature.booking

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.DsBottomSheet
import com.mh.restaurantchainreservation.core.designsystem.components.PaymentToggle
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import kotlin.math.roundToInt

private val SheetWhite = RestaurantColors.Base.white
private val MerchantCardRadius = 26.dp
private val TotalCardRadius = 28.dp
private val PocketCardRadius = 22.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookingPaymentSheet(
    visible: Boolean,
    payTo: String,
    payToSub: String,
    guests: Int,
    totalAmount: Double,
    paymentConfirmed: Boolean,
    onDismiss: () -> Unit,
    onPaymentComplete: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val depositAmount = guests * DEPOSIT_PER_GUEST
    val payKrw = 0.0
    val payUsd = totalAmount

    DsBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        title = "Reservation payment",
        containerColor = SheetWhite,
        wrapContentHeight = paymentConfirmed,
        footer = {
            if (paymentConfirmed) {
                PaymentConfirmedFooter()
            } else {
                PaymentSheetFooter(
                    amountLabel = "$${fmtMoney(totalAmount)}",
                    onComplete = onPaymentComplete,
                )
            }
        },
    ) {
        if (!paymentConfirmed) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PaymentMerchantCard(payTo = payTo, payToSub = payToSub)
                PaymentTotalCard(totalAmount = totalAmount)
                WalletPocketsSection(
                    payKrw = payKrw,
                    payUsd = payUsd,
                    finalKrw = payKrw,
                    finalUsd = payUsd,
                )
                PaymentBreakdownCard(
                    guests = guests,
                    depositAmount = depositAmount,
                    totalAmount = totalAmount,
                )
                BonusBalanceCard(
                    domesticEligible = payKrw > 0,
                )
            }
        }
    }
}

@Composable
private fun PaymentConfirmedFooter() {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(999.dp))
            .background(palette.success)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = RestaurantColors.Base.white, modifier = Modifier.size(20.dp))
            Text(
                text = "Payment confirmed",
                color = RestaurantColors.Base.white,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun PaymentSheetFooter(
    amountLabel: String,
    onComplete: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Outlined.Shield,
                contentDescription = null,
                tint = palette.success,
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Secure payment from your saved balance",
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
        }
        SwipeToPayButton(
            amountLabel = amountLabel,
            onComplete = onComplete,
        )
    }
}

@Composable
private fun PaymentMerchantCard(payTo: String, payToSub: String) {
    val palette = LocalRestaurantPalette.current
    var copied by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(MerchantCardRadius), ambientColor = RestaurantColors.Base.black.copy(0.055f))
            .clip(RoundedCornerShape(MerchantCardRadius))
            .background(SheetWhite)
            .border(1.dp, palette.border, RoundedCornerShape(MerchantCardRadius))
            .padding(16.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MiniPill(text = "Verified", tone = PillTone.Success)
            MiniPill(text = "Ready", tone = PillTone.Primary)
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(palette.mutedSurface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Description, contentDescription = null, tint = palette.brand, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text("Pay to", color = palette.mutedForeground, fontSize = 12.sp)
                Text(payTo, color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(payToSub, color = palette.mutedForeground, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.mutedSurface)
                    .clickable { copied = true },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (copied) Icons.Filled.CheckCircle else Icons.Outlined.ContentCopy,
                    contentDescription = "Copy",
                    tint = if (copied) palette.brand else palette.mutedForeground,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

private enum class PillTone { Success, Primary }

@Composable
private fun MiniPill(text: String, tone: PillTone) {
    val palette = LocalRestaurantPalette.current
    val (bg, fg, icon) = when (tone) {
        PillTone.Success -> Triple(palette.emeraldAccent.container, palette.success, Icons.Outlined.Badge)
        PillTone.Primary -> Triple(palette.brandSoftSurface, palette.brand, null)
    }
    Row(
        modifier = Modifier
            .height(28.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        icon?.let {
            Icon(it, contentDescription = null, tint = fg, modifier = Modifier.size(14.dp))
        }
        Text(text, color = fg, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PaymentTotalCard(totalAmount: Double) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(TotalCardRadius), ambientColor = RestaurantColors.Base.black.copy(0.045f))
            .clip(RoundedCornerShape(TotalCardRadius))
            .background(SheetWhite)
            .border(1.dp, palette.border, RoundedCornerShape(TotalCardRadius))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Total payment", color = palette.mutedForeground, fontSize = 12.sp)
                Text(
                    "$${fmtMoney(totalAmount)}",
                    color = palette.foreground,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    "Tonight Wallet keeps KRW and USD pockets separate, so each balance stays easy to review.",
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(palette.brand.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Shield, contentDescription = null, tint = palette.brand, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
private fun WalletPocketsSection(
    payKrw: Double,
    payUsd: Double,
    finalKrw: Double,
    finalUsd: Double,
) {
    val palette = LocalRestaurantPalette.current
    val krwActive = payKrw > 0
    val usdActive = payUsd > 0
    val activeCount = listOf(krwActive, usdActive).count { it }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Tonight Wallet", color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text("$activeCount pocket(s) active", color = palette.mutedForeground, fontSize = 12.sp)
        }
        WalletPocketCard(
            title = "KRW pocket",
            available = fmtKrw(INTERNAL_BALANCE_KRW),
            payment = fmtKrw(if (krwActive) finalKrw else 0.0),
            after = fmtKrw(INTERNAL_BALANCE_KRW - finalKrw),
            active = krwActive,
            progress = walletUsageProgress(finalKrw, INTERNAL_BALANCE_KRW),
        )
        WalletPocketCard(
            title = "USD pocket",
            available = fmtUsd(WALLET_BALANCE_USD),
            payment = fmtUsd(if (usdActive) finalUsd else 0.0),
            after = fmtUsd(WALLET_BALANCE_USD - finalUsd),
            active = usdActive,
            progress = walletUsageProgress(finalUsd, WALLET_BALANCE_USD),
        )
    }
}

@Composable
private fun WalletPocketCard(
    title: String,
    available: String,
    payment: String,
    after: String,
    active: Boolean,
    progress: Float,
) {
    val palette = LocalRestaurantPalette.current
    val borderColor = if (active) palette.brand.copy(alpha = 0.35f) else palette.border

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (active) 10.dp else 0.dp,
                shape = RoundedCornerShape(PocketCardRadius),
                ambientColor = palette.brand.copy(alpha = 0.08f),
            )
            .clip(RoundedCornerShape(PocketCardRadius))
            .background(SheetWhite)
            .border(1.dp, borderColor, RoundedCornerShape(PocketCardRadius))
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (active) palette.brand.copy(alpha = 0.1f) else palette.mutedSurface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.Wallet,
                    contentDescription = null,
                    tint = if (active) palette.brand else palette.mutedForeground,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
                Text(title, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text("Tonight Wallet balance", color = palette.mutedForeground, fontSize = 12.sp)
            }
            if (active) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(palette.brand),
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(palette.mutedSurface),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (active) palette.brand else palette.border),
            )
        }
        Spacer(Modifier.height(12.dp))
        WalletBalanceRow("Available", available)
        WalletBalanceRow("This payment", payment, highlight = active)
        WalletBalanceRow("After", after)
    }
}

@Composable
private fun WalletBalanceRow(label: String, value: String, highlight: Boolean = false) {
    val palette = LocalRestaurantPalette.current
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(label, color = palette.mutedForeground, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(
            value,
            color = if (highlight) palette.brand else palette.foreground,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun PaymentBreakdownCard(guests: Int, depositAmount: Int, totalAmount: Double) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SheetWhite)
            .border(1.dp, palette.border, RoundedCornerShape(24.dp))
            .padding(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Breakdown", color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Icon(Icons.Outlined.Description, contentDescription = null, tint = palette.mutedForeground, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.height(12.dp))
        BreakdownLine("Reservation deposit ($guests × $$DEPOSIT_PER_GUEST)", "$${fmtMoney(depositAmount.toDouble())}")
        BreakdownLine("Service fee", "$${fmtMoney(SERVICE_FEE)}", muted = true)
        HorizontalDivider(color = palette.border, modifier = Modifier.padding(vertical = 8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("USD wallet total", color = palette.mutedForeground, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Text("$${fmtMoney(totalAmount)}", color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun BreakdownLine(label: String, value: String, muted: Boolean = false) {
    val palette = LocalRestaurantPalette.current
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = if (muted) palette.mutedForeground else palette.foreground, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(value, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BonusBalanceCard(
    domesticEligible: Boolean,
) {
    val palette = LocalRestaurantPalette.current
    var useBonus by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (domesticEligible) 1f else 0.85f)
            .clip(RoundedCornerShape(24.dp))
            .background(SheetWhite)
            .border(1.dp, palette.border, RoundedCornerShape(24.dp))
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.success.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.CardGiftcard, contentDescription = null, tint = palette.success, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text("Bonus balance", color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = if (domesticEligible) {
                        "Bonus available: ${fmtKrw(BONUS_BALANCE_KRW)}"
                    } else {
                        "Bonus applies to domestic wallet payments only"
                    },
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                )
            }
            PaymentToggle(
                checked = useBonus,
                onCheckedChange = { useBonus = it },
            )
        }
        Text(
            text = "You will earn $POINTS_EARN points from this payment",
            color = palette.mutedForeground,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun SwipeToPayButton(
    amountLabel: String,
    onComplete: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    var trackWidthPx by remember { mutableFloatStateOf(0f) }
    var dragOffsetPx by remember { mutableFloatStateOf(0f) }
    var completed by remember { mutableStateOf(false) }
    val thumbSizePx = with(density) { 48.dp.toPx() }
    val maxDrag = (trackWidthPx - thumbSizePx - with(density) { 8.dp.toPx() }).coerceAtLeast(0f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .onSizeChanged { trackWidthPx = it.width.toFloat() }
            .clip(RoundedCornerShape(999.dp))
            .background(Brush.horizontalGradient(listOf(palette.gradientStart, palette.gradientEnd))),
    ) {
        Text(
            text = "Swipe to Pay $amountLabel",
            color = RestaurantColors.Base.white.copy(alpha = 0.9f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Center),
        )
        Box(
            modifier = Modifier
                .offset { IntOffset(dragOffsetPx.roundToInt(), 0) }
                .padding(4.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(RestaurantColors.Base.white)
                .pointerInput(completed) {
                    if (completed) return@pointerInput
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (dragOffsetPx >= maxDrag * 0.85f) {
                                dragOffsetPx = maxDrag
                                completed = true
                                onComplete()
                            } else {
                                dragOffsetPx = 0f
                            }
                        },
                        onHorizontalDrag = { _, delta ->
                            dragOffsetPx = (dragOffsetPx + delta).coerceIn(0f, maxDrag)
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Text("›››", color = palette.brand, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

private fun fmtKrw(amount: Double): String = "₩${String.format("%,.0f", amount)}"

private fun fmtUsd(amount: Double): String = "$${fmtMoney(amount)}"

