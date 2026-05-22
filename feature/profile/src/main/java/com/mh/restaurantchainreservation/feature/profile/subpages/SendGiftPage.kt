package com.mh.restaurantchainreservation.feature.profile.subpages

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.GlobalNotificationCenter
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.feature.profile.data.ProfileWalletStore
import com.mh.restaurantchainreservation.feature.profile.data.WalletMutationResult
import com.mh.restaurantchainreservation.feature.profile.hub.formatKrwHub
import com.mh.restaurantchainreservation.feature.profile.hub.formatUsdHub
import com.mh.restaurantchainreservation.feature.profile.subpages.components.AnimatedAmountDisplay
import com.mh.restaurantchainreservation.feature.profile.subpages.components.Currency
import com.mh.restaurantchainreservation.feature.profile.subpages.components.MoneyKeypad
import com.mh.restaurantchainreservation.feature.profile.subpages.components.amountAsNumber
import com.mh.restaurantchainreservation.feature.profile.subpages.components.appendDigit
import com.mh.restaurantchainreservation.feature.profile.subpages.components.backspaceDigit
import com.mh.restaurantchainreservation.feature.profile.subpages.components.formatAmountString

private val GiftPresetsKRW = listOf(500_000L, 1_000_000L, 2_000_000L)
private val GiftPresetsUSD = listOf(10L, 25L, 50L)

@Composable
fun SendGiftPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    var currency by rememberSaveable { mutableStateOf(Currency.KRW) }
    var amountStr by rememberSaveable(currency) { mutableStateOf(if (currency == Currency.KRW) "500000" else "10") }
    var recipient by rememberSaveable { mutableStateOf("") }
    val activeAmount = amountAsNumber(amountStr)

    val storeCards by ProfileWalletStore.cards.collectAsState()
    val availableKrw = storeCards.sumOf { it.krwBalance.toLong() }
    val availableUsd = storeCards.sumOf { it.usdBalance }
    val availableLabel = if (currency == Currency.KRW) {
        formatKrwHub(availableKrw)
    } else {
        formatUsdHub(availableUsd)
    }
    val afterGift = if (currency == Currency.KRW) {
        availableKrw.toDouble() - activeAmount
    } else {
        availableUsd - activeAmount
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(palette.mutedSurface)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = palette.foreground, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.size(12.dp))
            Text("Send a gift", color = palette.foreground, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }

        Text(
            text = "Available ${if (currency == Currency.KRW) "domestic" else "foreign"}: $availableLabel",
            color = palette.mutedForeground,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(12.dp))

        // Recipient input
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(RestaurantColors.Neutral.inputSurface)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Outlined.PersonOutline, null, tint = RestaurantColors.Neutral.placeholder, modifier = Modifier.size(18.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (recipient.isEmpty()) {
                    Text("Who is this for? (Username)", color = RestaurantColors.Neutral.placeholder, fontSize = 14.sp)
                }
                BasicTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    singleLine = true,
                    cursorBrush = SolidColor(palette.brand),
                    textStyle = TextStyle(color = palette.foreground, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Gift card with animated amount
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GiftCard(currency = currency, amountStr = amountStr, modifier = Modifier.weight(1f))
            Spacer(Modifier.size(12.dp))
            CurrencySwitchSquare(currency = currency, onToggle = {
                currency = if (currency == Currency.KRW) Currency.USD else Currency.KRW
            })
        }

        if (activeAmount > 0) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Balance after gift: " + if (currency == Currency.KRW) {
                    formatKrwHub(afterGift.coerceAtLeast(0.0).toLong())
                } else {
                    formatUsdHub(afterGift.coerceAtLeast(0.0))
                },
                color = if (afterGift < 0) palette.destructive else palette.mutedForeground,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        Spacer(Modifier.height(24.dp))

        // Presets
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val presets = if (currency == Currency.KRW) GiftPresetsKRW else GiftPresetsUSD
            presets.forEach { p ->
                GiftPresetChip(
                    label = if (currency == Currency.KRW) "₩%,d".format(p) else "$%,d".format(p),
                    selected = p.toDouble() == activeAmount,
                    onClick = { amountStr = p.toString() },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // Keypad
        MoneyKeypad(
            currency = currency,
            onDigit = { amountStr = appendDigit(amountStr, it, currency) },
            onBackspace = {
                val next = backspaceDigit(amountStr)
                amountStr = if (next == "0") "" else next
            },
            onClear = { amountStr = "" },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.weight(1f))

        // Send CTA
        val canSend = recipient.trim().isNotEmpty() && activeAmount > 0
        Box(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp)
                .navigationBarsPadding()
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(if (canSend) palette.brand else palette.brand.copy(alpha = 0.3f))
                .clickable(enabled = canSend) {
                    when (val result = ProfileWalletStore.sendGift(currency, activeAmount, recipient.trim())) {
                        is WalletMutationResult.Success -> {
                            GlobalNotificationCenter.success("Gift sent", result.message)
                            onBack()
                        }
                        is WalletMutationResult.Error ->
                            GlobalNotificationCenter.info("Unable to send gift", result.message)
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (canSend) "Send ${if (currency == Currency.KRW) "₩" else "$"}${formatAmountString(amountStr, currency)}" else "Send Gift",
                color = RestaurantColors.Base.white,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun GiftCard(currency: Currency, amountStr: String, modifier: Modifier = Modifier) {
    val gradient = if (currency == Currency.KRW) {
        Brush.linearGradient(
            listOf(RestaurantColors.Accent.blue.second, RestaurantColors.Decoration.giftBlueEnd),
            start = androidx.compose.ui.geometry.Offset.Zero,
            end = androidx.compose.ui.geometry.Offset.Infinite,
        )
    } else {
        Brush.linearGradient(listOf(RestaurantColors.Semantic.heart, RestaurantColors.Brand.deepPink))
    }
    Box(
        modifier = modifier
            .height(168.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(gradient)
            .padding(18.dp),
    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                "Gift Card Balance",
                color = RestaurantColors.Base.white.copy(alpha = 0.72f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
            AnimatedAmountDisplay(
                amount = formatAmountString(amountStr.ifEmpty { "0" }, currency),
                symbol = if (currency == Currency.KRW) "₩" else "$",
                symbolColor = RestaurantColors.Base.white,
                valueColor = RestaurantColors.Base.white,
                fontSize = 34,
            )
        }
    }
}

@Composable
private fun CurrencySwitchSquare(currency: Currency, onToggle: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .background(palette.cardSurface)
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Filled.SwapHoriz, contentDescription = "Switch currency", tint = palette.foreground, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun GiftPresetChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) palette.foreground else palette.mutedSurface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            color = if (selected) palette.cardSurface else palette.foreground,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}
