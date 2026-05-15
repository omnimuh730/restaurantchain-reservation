package com.mh.restaurantchainreservation.feature.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Description
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
import androidx.compose.ui.draw.clip
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
import com.mh.restaurantchainreservation.core.designsystem.components.RestaurantSwitch
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import kotlin.math.roundToInt

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

    DsBottomSheet(visible = visible, onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            Text(
                text = "Reservation payment",
                color = palette.foreground,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            if (paymentConfirmed) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(999.dp))
                        .background(palette.success)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
                        Text("Payment confirmed", color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }
            } else {
                PaymentMerchantCard(payTo = payTo, payToSub = payToSub)
                Spacer(Modifier.height(12.dp))
                PaymentTotalCard(totalAmount = totalAmount)
                Spacer(Modifier.height(12.dp))
                WalletPocketsSection(totalAmount = totalAmount)
                Spacer(Modifier.height(12.dp))
                PaymentBreakdownCard(guests = guests, depositAmount = depositAmount, totalAmount = totalAmount)
                Spacer(Modifier.height(12.dp))
                BonusBalanceCard()
                Spacer(Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.Shield, contentDescription = null, tint = palette.success, modifier = Modifier.size(14.dp))
                    Text(
                        "Secure payment from your saved balance",
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                    )
                }
                Spacer(Modifier.height(16.dp))
                SwipeToPayButton(
                    amountLabel = "$${fmtMoney(totalAmount)}",
                    onComplete = onPaymentComplete,
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PaymentMerchantCard(payTo: String, payToSub: String) {
    val palette = LocalRestaurantPalette.current
    BookingCard {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Verified",
                color = palette.success,
                fontSize = 11.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.emeraldAccent.container)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
            Text(
                "Ready",
                color = palette.brand,
                fontSize = 11.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.brandSoftSurface)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(palette.mutedSurface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Description, contentDescription = null, tint = palette.brand)
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                Text("Pay to", color = palette.mutedForeground, fontSize = 12.sp)
                Text(payTo, color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(payToSub, color = palette.mutedForeground, fontSize = 12.sp)
            }
            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = palette.mutedForeground)
        }
    }
}

@Composable
private fun PaymentTotalCard(totalAmount: Double) {
    val palette = LocalRestaurantPalette.current
    BookingCard {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Total payment", color = palette.mutedForeground, fontSize = 12.sp)
                Text(
                    "$${fmtMoney(totalAmount)}",
                    color = palette.foreground,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "Tonight Wallet keeps KRW and USD pockets separate, so each balance stays easy to review.",
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
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
                Icon(Icons.Outlined.Shield, contentDescription = null, tint = palette.brand)
            }
        }
    }
}

@Composable
private fun WalletPocketsSection(totalAmount: Double) {
    val palette = LocalRestaurantPalette.current
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Tonight Wallet", color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text("1 pocket(s) active", color = palette.mutedForeground, fontSize = 12.sp)
        }
        Spacer(Modifier.height(8.dp))
        WalletPocketCard(
            title = "KRW pocket",
            available = "₩13,000,000",
            payment = "₩0",
            after = "₩13,000,000",
            selected = false,
        )
        Spacer(Modifier.height(8.dp))
        WalletPocketCard(
            title = "USD pocket",
            available = "$${fmtMoney(WALLET_BALANCE_USD)}",
            payment = "$${fmtMoney(totalAmount)}",
            after = "$${fmtMoney(WALLET_BALANCE_USD - totalAmount)}",
            selected = true,
        )
    }
}

@Composable
private fun WalletPocketCard(
    title: String,
    available: String,
    payment: String,
    after: String,
    selected: Boolean,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.cardSurface)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) palette.brand else palette.border,
                shape = RoundedCornerShape(20.dp),
            )
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (selected) palette.brandSoftSurface else palette.mutedSurface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Wallet, contentDescription = null, tint = if (selected) palette.brand else palette.mutedForeground)
            }
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(title, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text("Tonight Wallet balance", color = palette.mutedForeground, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(10.dp))
        WalletBalanceRow("Available", available)
        WalletBalanceRow("This payment", payment, highlight = selected)
        WalletBalanceRow("After", after)
    }
}

@Composable
private fun WalletBalanceRow(label: String, value: String, highlight: Boolean = false) {
    val palette = LocalRestaurantPalette.current
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
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
    BookingCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Breakdown", color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Icon(Icons.Outlined.Description, contentDescription = null, tint = palette.mutedForeground, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.height(10.dp))
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
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(label, color = if (muted) palette.mutedForeground else palette.foreground, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(value, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BonusBalanceCard() {
    val palette = LocalRestaurantPalette.current
    var useBonus by remember { mutableStateOf(false) }
    BookingCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.emeraldAccent.container),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.CardGiftcard, contentDescription = null, tint = palette.success)
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                Text("Bonus balance", color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(
                    "Bonus applies to domestic wallet payments only",
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                )
            }
            RestaurantSwitch(checked = useBonus, onCheckedChange = { useBonus = it }, enabled = false)
        }
        Text(
            "You will earn $POINTS_EARN points from this payment",
            color = palette.mutedForeground,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 10.dp),
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
    val thumbSizePx = with(density) { 52.dp.toPx() }
    val maxDrag = (trackWidthPx - thumbSizePx - with(density) { 8.dp.toPx() }).coerceAtLeast(0f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .onSizeChanged { trackWidthPx = it.width.toFloat() }
            .clip(RoundedCornerShape(999.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(palette.gradientStart, palette.gradientEnd),
                ),
            ),
    ) {
        Text(
            text = "Swipe to Pay $amountLabel",
            color = Color.White.copy(alpha = 0.9f),
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
                .background(Color.White.copy(alpha = 0.95f))
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
