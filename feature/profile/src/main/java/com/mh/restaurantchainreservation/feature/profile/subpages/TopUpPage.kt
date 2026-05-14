package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.feature.profile.subpages.components.AnimatedAmountDisplay
import com.mh.restaurantchainreservation.feature.profile.subpages.components.Currency
import com.mh.restaurantchainreservation.feature.profile.subpages.components.MoneyKeypad
import com.mh.restaurantchainreservation.feature.profile.subpages.components.amountAsNumber
import com.mh.restaurantchainreservation.feature.profile.subpages.components.appendDigit
import com.mh.restaurantchainreservation.feature.profile.subpages.components.backspaceDigit
import com.mh.restaurantchainreservation.feature.profile.subpages.components.formatAmountString
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

private enum class TopUpStep { Select, Confirm, Auth, Processing, Done, Error }

private data class PaymentProvider(
    val id: String,
    val name: String,
    val desc: String,
    val icon: ImageVector,
    val tint: Color,
    val container: Color,
)

private val Providers = listOf(
    PaymentProvider("apple", "Apple Pay", "Instant", Icons.Outlined.PhoneAndroid, Color(0xFF111111), Color(0xFFEFEFEF)),
    PaymentProvider("google", "Google Pay", "Instant", Icons.Outlined.Public, Color(0xFF1976D2), Color(0xFFE3F2FD)),
    PaymentProvider("paypal", "PayPal", "1-2 min", Icons.Outlined.Bolt, Color(0xFFE39A1A), Color(0xFFFFF4E0)),
    PaymentProvider("bank", "Bank Transfer", "1-3 days", Icons.Outlined.AccountBalance, Color(0xFF0D9D63), Color(0xFFE6F5EE)),
)

private val PresetsKRW = listOf(10_000L, 30_000L, 50_000L, 100_000L, 200_000L, 500_000L)
private val PresetsUSD = listOf(10L, 25L, 50L, 100L, 200L, 500L)

@Composable
fun TopUpPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current

    var step by rememberSaveable { mutableStateOf(TopUpStep.Select) }
    var currency by rememberSaveable { mutableStateOf(Currency.KRW) }
    var amountStr by rememberSaveable(currency) { mutableStateOf(if (currency == Currency.KRW) "50000" else "50") }
    var providerId by rememberSaveable { mutableStateOf<String?>("apple") }
    var providerSheetOpen by rememberSaveable { mutableStateOf(false) }
    var otp by rememberSaveable { mutableStateOf("") }
    var otpError by rememberSaveable { mutableStateOf<String?>(null) }
    var resendIn by rememberSaveable { mutableIntStateOf(45) }
    var processingStage by rememberSaveable { mutableIntStateOf(0) }
    var receiptId by rememberSaveable { mutableStateOf("") }
    var txnId by rememberSaveable { mutableStateOf("") }
    var errorMsg by rememberSaveable { mutableStateOf<String?>(null) }

    val provider = Providers.firstOrNull { it.id == providerId }
    val activeAmount = amountAsNumber(amountStr)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface)
            .statusBarsPadding(),
    ) {
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                (slideInVertically { it / 8 } + fadeIn(tween(180))) togetherWith
                    (slideOutVertically { -it / 8 } + fadeOut(tween(120)))
            },
            label = "topup_step",
        ) { current ->
            when (current) {
                TopUpStep.Select -> SelectView(
                    currency = currency,
                    amountStr = amountStr,
                    activeAmount = activeAmount,
                    provider = provider,
                    onBack = onBack,
                    onCurrencyToggle = {
                        currency = if (currency == Currency.KRW) Currency.USD else Currency.KRW
                    },
                    onDigit = { amountStr = appendDigit(amountStr, it, currency) },
                    onBackspace = { amountStr = backspaceDigit(amountStr) },
                    onPickPreset = { amountStr = it.toString() },
                    onPickProvider = { providerSheetOpen = true },
                    onContinue = { step = TopUpStep.Confirm },
                )
                TopUpStep.Confirm -> ConfirmView(
                    currency = currency,
                    activeAmount = activeAmount,
                    provider = provider,
                    onBack = { step = TopUpStep.Select },
                    onChangeProvider = { providerSheetOpen = true },
                    onConfirm = {
                        if (provider?.id == "bank") {
                            otp = ""
                            otpError = null
                            resendIn = 45
                            step = TopUpStep.Auth
                        } else {
                            processingStage = 0
                            step = TopUpStep.Processing
                        }
                    },
                )
                TopUpStep.Auth -> AuthView(
                    provider = provider,
                    otp = otp,
                    onOtpChange = {
                        otp = it.filter { ch -> ch.isDigit() }.take(4)
                        otpError = null
                    },
                    error = otpError,
                    resendIn = resendIn,
                    onResend = { resendIn = 45 },
                    onSubmit = {
                        when {
                            otp.length < 4 -> otpError = "Enter the 4-digit code"
                            otp == "0000" -> {
                                otpError = "That code didn't match. Try again."
                            }
                            else -> {
                                processingStage = 0
                                step = TopUpStep.Processing
                            }
                        }
                    },
                    onBack = { step = TopUpStep.Confirm },
                )
                TopUpStep.Processing -> ProcessingView(
                    currency = currency,
                    activeAmount = activeAmount,
                    provider = provider,
                    stageIndex = processingStage,
                    onStageDone = {
                        processingStage += 1
                        if (processingStage >= 4) {
                            receiptId = "TOP-2026-${Random.nextInt(100000, 999999)}"
                            txnId = "TXN-${Random.nextInt(10_000_000, 99_999_999)}"
                            step = TopUpStep.Done
                        }
                    },
                )
                TopUpStep.Done -> DoneView(
                    currency = currency,
                    activeAmount = activeAmount,
                    provider = provider,
                    receiptId = receiptId,
                    txnId = txnId,
                    onAgain = {
                        step = TopUpStep.Select
                        amountStr = if (currency == Currency.KRW) "50000" else "50"
                    },
                    onDone = onBack,
                )
                TopUpStep.Error -> ErrorView(
                    message = errorMsg,
                    onRetry = { step = TopUpStep.Confirm },
                    onCancel = onBack,
                )
            }
        }

        if (providerSheetOpen) {
            ProviderSheet(
                selected = providerId,
                onDismiss = { providerSheetOpen = false },
                onSelect = {
                    providerId = it
                    providerSheetOpen = false
                },
            )
        }
    }

    // OTP resend ticker
    LaunchedEffect(step, resendIn) {
        if (step == TopUpStep.Auth && resendIn > 0) {
            delay(1000L)
            resendIn -= 1
        }
    }
}

@Composable
private fun SelectView(
    currency: Currency,
    amountStr: String,
    activeAmount: Double,
    provider: PaymentProvider?,
    onBack: () -> Unit,
    onCurrencyToggle: () -> Unit,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onPickPreset: (Long) -> Unit,
    onPickProvider: () -> Unit,
    onContinue: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
    ) {
        TopBar(title = "Top up", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        // Animated hero amount + currency toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedAmountDisplay(
                amount = formatAmountString(amountStr, currency),
                symbol = if (currency == Currency.KRW) "₩" else "$",
                symbolColor = if (currency == Currency.KRW) Color(0xFF1976D2) else palette.brand,
                valueColor = if (currency == Currency.KRW) Color(0xFF1565C0) else Color(0xFFE91E63),
                fontSize = 48,
                modifier = Modifier.weight(1f),
            )
            CurrencySwitchPill(currency = currency, onToggle = onCurrencyToggle)
        }

        Spacer(Modifier.height(24.dp))

        // Quick presets (3-grid, top row)
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val presets = if (currency == Currency.KRW) PresetsKRW else PresetsUSD
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                presets.take(3).forEach { p ->
                    PresetChip(
                        label = formatPresetLabel(p, currency),
                        selected = p == activeAmount.roundToLong(),
                        onClick = { onPickPreset(p) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        // Share vertical slack between preset block, keypad, and pay row instead of one gap above the CTA.
        Spacer(Modifier.weight(1f))

        // Numeric keypad
        MoneyKeypad(
            currency = currency,
            onDigit = onDigit,
            onBackspace = onBackspace,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.weight(0.55f))

        // Pay-with row
        PayWithRow(provider = provider, onClick = onPickProvider, modifier = Modifier.padding(horizontal = 20.dp))

        Spacer(Modifier.weight(0.45f))

        // Sticky CTA
        BrandPillButton(
            label = if (provider != null) "Topup Now · ${formatMoney(activeAmount, currency)}" else "Choose method",
            enabled = provider != null && activeAmount > 0.0,
            onClick = onContinue,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun ConfirmView(
    currency: Currency,
    activeAmount: Double,
    provider: PaymentProvider?,
    onBack: () -> Unit,
    onChangeProvider: () -> Unit,
    onConfirm: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
    ) {
        TopBar(title = "Confirm Top Up", onBack = onBack)
        Spacer(Modifier.height(20.dp))

        // Hero gradient card
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            palette.brand,
                            palette.brand.copy(red = (palette.brand.red * 0.6f), green = (palette.brand.green * 0.6f), blue = (palette.brand.blue * 0.6f)),
                        ),
                    ),
                )
                .padding(20.dp),
        ) {
            Column {
                Text(
                    text = if (currency == Currency.KRW) "TOP UP · DOMESTIC" else "TOP UP · FOREIGN",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = formatMoney(activeAmount, currency),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Payment block
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                .padding(14.dp),
        ) {
            Text(
                text = "PAYMENT METHOD",
                color = palette.mutedForeground,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (provider != null) {
                    ProviderBadge(provider)
                    Spacer(Modifier.size(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(provider.name, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(provider.desc, color = palette.mutedForeground, fontSize = 12.sp)
                    }
                }
                Text(
                    text = "Change",
                    color = palette.brand,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onChangeProvider),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Totals card
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(palette.mutedSurface.copy(alpha = 0.4f))
                .padding(16.dp),
        ) {
            TotalsRow("Top Up Amount", formatMoney(activeAmount, currency), palette.foreground, palette.foreground)
            Spacer(Modifier.height(6.dp))
            TotalsRow("Processing Fee", "FREE", palette.mutedForeground, palette.success)
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
            Spacer(Modifier.height(8.dp))
            TotalsRow(
                label = "You Pay",
                value = formatMoney(activeAmount, currency),
                labelColor = palette.foreground,
                valueColor = palette.foreground,
                bold = true,
            )
        }

        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(Icons.Outlined.Lock, null, tint = palette.success, modifier = Modifier.size(14.dp))
            Text(
                text = "Secured by ${provider?.name ?: "—"} · 256-bit encryption",
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
        }

        Spacer(Modifier.weight(1f))

        BrandPillButton(
            label = "Pay ${formatMoney(activeAmount, currency)}${provider?.let { " with ${it.name}" } ?: ""}",
            enabled = provider != null && activeAmount > 0,
            onClick = onConfirm,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun TotalsRow(label: String, value: String, labelColor: Color, valueColor: Color, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = labelColor, fontSize = 13.sp, fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium)
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.SemiBold)
    }
}

@Composable
private fun AuthView(
    provider: PaymentProvider?,
    otp: String,
    onOtpChange: (String) -> Unit,
    error: String?,
    resendIn: Int,
    onResend: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBar(title = "Verify It's You", onBack = onBack)
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(provider?.container ?: palette.mutedSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Shield, null, tint = provider?.tint ?: palette.brand, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("Enter the 4-digit code", color = palette.foreground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (provider?.id == "bank") "We sent a verification code to your registered phone (•••• 5678)" else "We sent a verification code to your ${provider?.name ?: "account"}",
            color = palette.mutedForeground,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )

        Spacer(Modifier.height(24.dp))

        // OTP cells
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            (0 until 4).forEach { i ->
                val ch = otp.getOrNull(i)?.toString() ?: ""
                val filled = ch.isNotEmpty()
                Box(
                    modifier = Modifier
                        .size(width = 52.dp, height = 60.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            width = 2.dp,
                            color = when {
                                error != null -> palette.destructive
                                filled -> palette.brand
                                else -> palette.border
                            },
                            shape = RoundedCornerShape(14.dp),
                        )
                        .background(if (filled) palette.brand.copy(alpha = 0.05f) else Color.Transparent),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(ch, color = palette.foreground, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        // Hidden text field to capture input
        BasicTextField(
            value = otp,
            onValueChange = onOtpChange,
            singleLine = true,
            cursorBrush = SolidColor(Color.Transparent),
            textStyle = TextStyle(color = Color.Transparent, fontSize = 1.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                .padding(8.dp),
        )

        AnimatedVisibility(visible = error != null, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            Text(
                text = error.orEmpty(),
                color = palette.destructive,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = if (resendIn > 0) "Resend code in ${resendIn}s" else "Resend code",
            color = if (resendIn > 0) palette.mutedForeground else palette.brand,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = if (resendIn == 0) Modifier.clickable(onClick = onResend) else Modifier,
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Tip: try 0000 to see the failure flow.",
            color = palette.mutedForeground,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 32.dp),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.weight(1f))

        BrandPillButton(
            label = "Verify & Continue",
            enabled = otp.length == 4,
            onClick = onSubmit,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun ProcessingView(
    currency: Currency,
    activeAmount: Double,
    provider: PaymentProvider?,
    stageIndex: Int,
    onStageDone: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val stages = listOf(
        "Connecting to ${provider?.name ?: "provider"}",
        "Authenticating your account",
        "Authorizing transaction",
        "Crediting your wallet",
    )

    LaunchedEffect(stageIndex) {
        delay(if (stageIndex == 0) 800L else 700L + Random.nextLong(0, 600))
        onStageDone()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 16.dp),
    ) {
        TopBar(title = "Processing", onBack = {})
        Spacer(Modifier.height(20.dp))

        // Gradient banner
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            palette.brand,
                            palette.brand.copy(red = (palette.brand.red * 0.6f), green = (palette.brand.green * 0.6f), blue = (palette.brand.blue * 0.6f)),
                        ),
                    ),
                )
                .padding(20.dp),
        ) {
            Column {
                Text("PROCESSING TOP UP", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                Spacer(Modifier.height(4.dp))
                Text(formatMoney(activeAmount, currency), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                provider?.let { Text("via ${it.name}", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp) }
            }
        }

        Spacer(Modifier.height(20.dp))
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            stages.forEachIndexed { i, label ->
                val state = when {
                    i < stageIndex -> "done"
                    i == stageIndex -> "active"
                    else -> "pending"
                }
                StageRow(index = i + 1, label = label, state = state)
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(Icons.Outlined.Lock, null, tint = palette.success, modifier = Modifier.size(14.dp))
            Text("End-to-end encrypted · do not close this screen", color = palette.mutedForeground, fontSize = 12.sp)
        }
    }
}

@Composable
private fun StageRow(index: Int, label: String, state: String) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(14.dp)
    val border = when (state) {
        "active" -> palette.brand
        "done" -> palette.success
        else -> palette.border
    }
    val container = when (state) {
        "active" -> palette.brand.copy(alpha = 0.06f)
        "done" -> palette.success.copy(alpha = 0.06f)
        else -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(container)
            .border(1.dp, border, shape)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    when (state) {
                        "active" -> palette.brand
                        "done" -> palette.success
                        else -> palette.mutedSurface
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                "active" -> {
                    val rotation by rememberInfiniteTransition(label = "stage_spin").animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(animation = tween(900, easing = LinearEasing)),
                        label = "stage_rotation",
                    )
                    Icon(
                        imageVector = Icons.Outlined.Fingerprint,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(16.dp)
                            .graphicsLayer { rotationZ = rotation },
                    )
                }
                "done" -> Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                else -> Text(index.toString(), color = palette.mutedForeground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text(label, color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DoneView(
    currency: Currency,
    activeAmount: Double,
    provider: PaymentProvider?,
    receiptId: String,
    txnId: String,
    onAgain: () -> Unit,
    onDone: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val pulse by rememberInfiniteTransition(label = "done_pulse").animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(animation = tween(1200), repeatMode = RepeatMode.Reverse),
        label = "done_pulse_anim",
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 16.dp),
    ) {
        TopBar(title = "Top Up", onBack = onDone)
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(palette.success.copy(alpha = 0.10f))
                .graphicsLayer { scaleX = pulse; scaleY = pulse }
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(palette.success.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Check, null, tint = palette.success, modifier = Modifier.size(36.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Top Up Successful!",
            color = palette.foreground,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${formatMoney(activeAmount, currency)} added to your ${if (currency == Currency.KRW) "Domestic" else "Foreign"} balance",
            color = palette.mutedForeground,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        )

        Spacer(Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("RECEIPT", color = palette.mutedForeground, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp)
            ReceiptRow("Receipt No.", receiptId)
            ReceiptRow("Transaction ID", txnId)
            ReceiptRow("Method", provider?.name ?: "—")
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
            ReceiptRow("Top Up", "+${formatMoney(activeAmount, currency)}", valueColor = palette.success, bold = true)
            ReceiptRow("Added", formatMoney(activeAmount, currency), bold = true)
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GhostPillButton(label = "Top Up Again", onClick = onAgain, modifier = Modifier.weight(1f))
            BrandPillButton(label = "Done", enabled = true, onClick = onDone, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    valueColor: Color = LocalRestaurantPalette.current.foreground,
    bold: Boolean = false,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = palette.mutedForeground, fontSize = 12.sp)
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.SemiBold)
    }
}

@Composable
private fun ErrorView(message: String?, onRetry: () -> Unit, onCancel: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 16.dp),
    ) {
        TopBar(title = "Top Up Failed", onBack = onCancel)
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(palette.destructive.copy(alpha = 0.10f))
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.ErrorOutline, null, tint = palette.destructive, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("Payment Couldn't Be Completed", color = palette.foreground, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(6.dp))
        Text(message ?: "We couldn't reach your payment provider.", color = palette.mutedForeground, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp))
        Spacer(Modifier.height(4.dp))
        Text("No funds were deducted.", color = palette.mutedForeground, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GhostPillButton(label = "Cancel", onClick = onCancel, modifier = Modifier.weight(1f))
            BrandPillButton(label = "Try Again", enabled = true, onClick = onRetry, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ProviderSheet(
    selected: String?,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(palette.cardSurface)
                .clickable(enabled = false) {}
                .padding(20.dp),
        ) {
            Text("Pay With", color = palette.foreground, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text("Choose your payment method", color = palette.mutedForeground, fontSize = 13.sp)
            Spacer(Modifier.height(16.dp))
            Providers.forEach { p ->
                val isSelected = p.id == selected
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(if (isSelected) 2.dp else 1.dp, if (isSelected) palette.brand else palette.border, RoundedCornerShape(16.dp))
                        .clickable { onSelect(p.id) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ProviderBadge(p)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(p.name, color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(p.desc, color = palette.mutedForeground, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (isSelected) palette.brand else palette.border, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isSelected) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(palette.brand))
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TopBar(title: String, onBack: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = palette.foreground,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.size(12.dp))
        Text(title, color = palette.foreground, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun CurrencySwitchPill(currency: Currency, onToggle: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    val container = if (currency == Currency.KRW) Color(0xFFFFE4E6) else Color(0xFFE0F2FE)
    val content = if (currency == Currency.KRW) Color(0xFFE91E63) else Color(0xFF1976D2)
    Row(
        modifier = Modifier
            .clip(shape)
            .background(container)
            .clickable(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = if (currency == Currency.KRW) "$" else "₩",
            color = content,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Icon(
            imageVector = Icons.Filled.SwapHoriz,
            contentDescription = "Switch currency",
            tint = content,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun PresetChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(shape)
            .background(if (selected) palette.brand else palette.cardSurface)
            .border(1.dp, if (selected) palette.brand else palette.border, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else palette.foreground,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun PayWithRow(provider: PaymentProvider?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("PAY WITH", color = palette.mutedForeground, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.6.sp)
            Text(provider?.name ?: "Choose method", color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        if (provider != null) ProviderBadge(provider)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = palette.mutedForeground,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun ProviderBadge(p: PaymentProvider) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(p.container),
        contentAlignment = Alignment.Center,
    ) {
        Icon(p.icon, null, tint = p.tint, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun BrandPillButton(label: String, enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .background(if (enabled) palette.brand else palette.brand.copy(alpha = 0.4f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
    }
}

@Composable
private fun GhostPillButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .border(1.dp, palette.border, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
    }
}

private fun formatPresetLabel(amount: Long, currency: Currency): String =
    if (currency == Currency.KRW) "₩%,d".format(amount) else "$%,d".format(amount)

private fun formatMoney(amount: Double, currency: Currency): String =
    if (currency == Currency.KRW) "₩%,d".format(amount.roundToLong()) else "$%,.2f".format(amount)

private fun Double.roundToLong(): Long = this.roundToInt().toLong()
