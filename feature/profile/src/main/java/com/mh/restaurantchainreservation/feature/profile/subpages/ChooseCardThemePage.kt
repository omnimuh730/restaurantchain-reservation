package com.mh.restaurantchainreservation.feature.profile.subpages

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.RestaurantModalBottomSheet
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.feature.profile.data.ProfileWalletStore
import com.mh.restaurantchainreservation.feature.profile.hub.formatKrwHub
import com.mh.restaurantchainreservation.feature.profile.hub.formatUsdHub
import kotlin.math.roundToLong
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardPattern
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardThemeId
import com.mh.restaurantchainreservation.feature.profile.hub.HubThemedCardBackground
import com.mh.restaurantchainreservation.feature.profile.hub.SharedHubCardFace
import com.mh.restaurantchainreservation.feature.profile.hub.SharedHubCardFaceModel
import com.mh.restaurantchainreservation.feature.profile.hub.hubCardThemeBackgroundBrush
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

private val ThemePickerOrder: List<HubCardThemeId> = listOf(
    HubCardThemeId.Ink,
    HubCardThemeId.Rose,
    HubCardThemeId.Amethyst,
    HubCardThemeId.Ocean,
    HubCardThemeId.Sunset,
    HubCardThemeId.Forest,
)

private val PatternPickerOrder: List<HubCardPattern> = listOf(
    HubCardPattern.Stars,
    HubCardPattern.Grid,
    HubCardPattern.Wave,
    HubCardPattern.Blob,
    HubCardPattern.Rays,
    HubCardPattern.None,
)

private fun HubCardPattern.displayLabel(): String = when (this) {
    HubCardPattern.Stars -> "Stars"
    HubCardPattern.Grid -> "Grid"
    HubCardPattern.Wave -> "Wave"
    HubCardPattern.Blob -> "Blob"
    HubCardPattern.Rays -> "Rays"
    HubCardPattern.None -> "Solid"
}

private const val ChooseCardSheetSteps = 3

private const val CardPasscodeMinDigits = 4

private fun filterCardPasscodeDigits(raw: String): String =
    raw.filter { it.isDigit() }

internal enum class NewCardOpeningCurrency { KRW, USD }

internal data class NewCardFunding(
    val openingCurrency: NewCardOpeningCurrency,
    val initialKrw: Double,
    val initialUsd: Double,
    val cardPasscode: String = "",
)

/** Width of each theme/pattern card in the horizontal picker strips. */
private val PickerStripItemWidth = 100.dp

/** Swatch area (~3:2 letterbox vs width); shorter than before per design. */
private val PickerStripSwatchHeight = 52.dp

/** Fixed label row so every slot has the same height, selected or not. */
private val PickerStripLabelRowHeight = 28.dp

private val PickerStripFrameHeight = PickerStripSwatchHeight + PickerStripLabelRowHeight

/** Black ring thickness around the selected chip. */
private val PickerSelectedRingStroke = 2.dp

/**
 * Clear space between the inner edge of the ring and the chip content (same size as [PickerSelectedRingStroke]).
 */
private val PickerSelectedRingGap = PickerSelectedRingStroke

/** Padding from slot edge to content for every item so selected/unselected sizes match. */
private val PickerStripContentPadding = PickerSelectedRingStroke + PickerSelectedRingGap

private val PickerSwatchTopCorner = 14.dp

/** Swatch: rounded top, square bottom (meets label flush). */
private val PickerSwatchShape = RoundedCornerShape(
    topStart = PickerSwatchTopCorner,
    topEnd = PickerSwatchTopCorner,
    bottomEnd = 0.dp,
    bottomStart = 0.dp,
)

private val PickerSelectedInnerShape = RoundedCornerShape(11.dp)

/** KRW lane accent (blue); USD uses [LocalRestaurantPalette.brand] (red). */
private val OpeningKrwAccentBlue = RestaurantColors.HubCard.ocean[1]

/** Horizontal inset for title, card preview, section headers, footer, and strip row ends. */
private val ChooseCardSheetContentPadding = 20.dp

/**
 * Rising bottom sheet (~78% screen height) for adding a card:
 * design → open card (currency + dual top-up) → review.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChooseCardThemeBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    previewNickname: String,
    holder: String,
    lastFour: String,
    fullCardNumber: String,
    selectedThemeId: HubCardThemeId,
    selectedPattern: HubCardPattern,
    onThemeSelected: (HubCardThemeId) -> Unit,
    onPatternSelected: (HubCardPattern) -> Unit,
    onConfirm: (NewCardFunding) -> Unit,
) {
    if (!visible) return

    val palette = LocalRestaurantPalette.current
    val configuration = LocalConfiguration.current
    val sheetMaxHeight = (configuration.screenHeightDp * 0.78f).dp
    var step by rememberSaveable { mutableIntStateOf(0) }
    var openingCurrency by rememberSaveable { mutableStateOf(NewCardOpeningCurrency.KRW.name) }
    var initialKrwText by rememberSaveable { mutableStateOf("") }
    var initialUsdText by rememberSaveable { mutableStateOf("") }
    var cardPasscode by rememberSaveable { mutableStateOf("") }
    var cardPasscodeConfirm by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(visible) {
        if (visible) {
            step = 0
            openingCurrency = NewCardOpeningCurrency.KRW.name
            initialKrwText = ""
            initialUsdText = ""
            cardPasscode = ""
            cardPasscodeConfirm = ""
        }
    }

    val opening = if (openingCurrency == NewCardOpeningCurrency.USD.name) {
        NewCardOpeningCurrency.USD
    } else {
        NewCardOpeningCurrency.KRW
    }

    val previewModel = SharedHubCardFaceModel(
        productLabel = previewNickname,
        holder = holder.uppercase(),
        lastFour = lastFour,
        krwBalance = 0L,
        usdBalance = 0.0,
        themeId = selectedThemeId,
        pattern = selectedPattern,
        showBalance = false,
        showDualBalance = false,
        frozen = false,
        showFullPan = false,
        fullCardNumber = fullCardNumber,
    )

    RestaurantModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(sheetMaxHeight),
        ) {
            ChooseCardThemeStepTabs(
                currentStep = step,
                onStepSelect = { step = it.coerceIn(0, ChooseCardSheetSteps - 1) },
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 6.dp, bottom = 6.dp),
            ) {
                val inset = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ChooseCardSheetContentPadding)
                when (step) {
                    0 -> {
                        Column(modifier = inset) {
                            Text(
                                text = "Choose your card",
                                color = palette.foreground,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Pick a theme — every card stays multi-currency.",
                                color = palette.mutedForeground,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(Modifier.height(16.dp))
                            SharedHubCardFace(
                                model = previewModel,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Color",
                            color = palette.foreground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = inset,
                        )
                        Spacer(Modifier.height(12.dp))
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = ChooseCardSheetContentPadding),
                        ) {
                            items(ThemePickerOrder, key = { it.name }) { themeId ->
                                ThemePickerStripItem(
                                    themeId = themeId,
                                    selected = themeId == selectedThemeId,
                                    onClick = { onThemeSelected(themeId) },
                                )
                            }
                        }
                        Spacer(Modifier.height(18.dp))
                        Text(
                            text = "Pattern",
                            color = palette.foreground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = inset,
                        )
                        Spacer(Modifier.height(10.dp))
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = ChooseCardSheetContentPadding),
                        ) {
                            items(PatternPickerOrder, key = { it.name }) { pattern ->
                                PatternPickerStripItem(
                                    pattern = pattern,
                                    label = pattern.displayLabel(),
                                    selected = pattern == selectedPattern,
                                    themeId = selectedThemeId,
                                    onClick = { onPatternSelected(pattern) },
                                )
                            }
                        }
                    }
                    1 -> {
                        OpenCardFundingStep(
                            modifier = Modifier.fillMaxWidth(),
                            openingCurrency = opening,
                            onOpeningCurrency = { openingCurrency = it.name },
                            initialKrwText = initialKrwText,
                            initialUsdText = initialUsdText,
                            onInitialKrwChange = { initialKrwText = filterKrwDigits(it) },
                            onInitialUsdChange = { initialUsdText = filterUsdDecimal(it) },
                            onSkipTopUp = {
                                initialKrwText = ""
                                initialUsdText = ""
                            },
                            onBack = { step = 0 },
                        )
                    }
                    else -> {
                        Column(modifier = inset) {
                            Text(
                                text = "Review and confirm",
                                color = palette.foreground,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(16.dp))
                            SharedHubCardFace(
                                model = previewModel,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(Modifier.height(16.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(1.dp, palette.border, RoundedCornerShape(20.dp))
                                    .background(palette.cardSurface)
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                            ) {
                                ReviewSummaryLine(label = "Card holder", value = holder)
                                Spacer(Modifier.height(14.dp))
                                ReviewSummaryLine(
                                    label = "Card number",
                                    value = formatCardNumberForReview(fullCardNumber),
                                )
                                Spacer(Modifier.height(14.dp))
                                ReviewSummaryLine(label = "Card theme", value = selectedThemeId.name)
                                Spacer(Modifier.height(14.dp))
                                ReviewSummaryFundingRow(
                                    initialKrwText = initialKrwText,
                                    initialUsdText = initialUsdText,
                                )
                                Spacer(Modifier.height(18.dp))
                                OutlinedTextField(
                                    value = cardPasscode,
                                    onValueChange = { cardPasscode = filterCardPasscodeDigits(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    label = {
                                        Text(
                                            text = "Card passcode",
                                            color = palette.mutedForeground,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    },
                                    placeholder = {
                                        Text(
                                            text = "At least 4 digits",
                                            color = palette.mutedForeground.copy(alpha = 0.5f),
                                        )
                                    },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = palette.border,
                                        unfocusedBorderColor = palette.border,
                                        focusedContainerColor = palette.mutedSurface.copy(alpha = 0.45f),
                                        unfocusedContainerColor = palette.mutedSurface.copy(alpha = 0.45f),
                                        cursorColor = palette.foreground,
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                )
                                Spacer(Modifier.height(10.dp))
                                val confirmMismatch =
                                    cardPasscodeConfirm.isNotEmpty() && cardPasscode != cardPasscodeConfirm
                                OutlinedTextField(
                                    value = cardPasscodeConfirm,
                                    onValueChange = { cardPasscodeConfirm = filterCardPasscodeDigits(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    label = {
                                        Text(
                                            text = "Confirm passcode",
                                            color = palette.mutedForeground,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    },
                                    placeholder = {
                                        Text(
                                            text = "Re-enter passcode",
                                            color = palette.mutedForeground.copy(alpha = 0.5f),
                                        )
                                    },
                                    isError = confirmMismatch,
                                    supportingText = {
                                        if (confirmMismatch) {
                                            Text(
                                                text = "Passcodes must match",
                                                color = palette.brand,
                                                fontSize = 12.sp,
                                            )
                                        }
                                    },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = palette.border,
                                        unfocusedBorderColor = palette.border,
                                        focusedContainerColor = palette.mutedSurface.copy(alpha = 0.45f),
                                        unfocusedContainerColor = palette.mutedSurface.copy(alpha = 0.45f),
                                        errorBorderColor = palette.brand,
                                        errorCursorColor = palette.brand,
                                        errorSupportingTextColor = palette.brand,
                                        cursorColor = palette.foreground,
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                )
                            }
                            Spacer(Modifier.height(14.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(palette.mutedSurface)
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = palette.mutedForeground,
                                    modifier = Modifier.size(22.dp),
                                )
                                Text(
                                    text = "Card details are generated when you confirm. You can manage the card any time.",
                                    color = palette.mutedForeground,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                )
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ChooseCardSheetContentPadding)
                    .padding(bottom = 16.dp, top = 2.dp),
            ) {
                val isLast = step >= ChooseCardSheetSteps - 1
                val passcodesReady =
                    cardPasscode.length >= CardPasscodeMinDigits &&
                        cardPasscode == cardPasscodeConfirm
                val fundKrw = initialKrwText.filter { it.isDigit() }.toLongOrNull()?.toDouble() ?: 0.0
                val fundUsd = initialUsdText.replace(",", "").toDoubleOrNull() ?: 0.0
                val walletKrw = ProfileWalletStore.totalKrwLong().toDouble()
                val walletUsd = ProfileWalletStore.totalUsd()
                val hasSufficientWallet = fundKrw <= walletKrw && fundUsd <= walletUsd
                val isFundingStep = step == 1
                val proceedEnabled = when {
                    isLast -> passcodesReady && hasSufficientWallet
                    isFundingStep -> hasSufficientWallet
                    else -> true
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (proceedEnabled) palette.brand else palette.brand.copy(alpha = 0.38f),
                        )
                        .clickable(
                            enabled = proceedEnabled,
                            role = Role.Button,
                            onClick = {
                                if (!proceedEnabled) return@clickable
                                if (isLast) {
                                    val krw = initialKrwText.toLongOrNull()?.toDouble() ?: 0.0
                                    val usd = initialUsdText.toDoubleOrNull() ?: 0.0
                                    onConfirm(
                                        NewCardFunding(
                                            openingCurrency = opening,
                                            initialKrw = krw,
                                            initialUsd = usd,
                                            cardPasscode = cardPasscode,
                                        ),
                                    )
                                } else {
                                    step++
                                }
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (isLast) "Open card" else "Continue",
                        color = RestaurantColors.Base.white,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
    }
}

private fun filterKrwDigits(raw: String): String =
    raw.filter { it.isDigit() }.take(12)

/** Groups digits with comma every three from the right (e.g. 100000 -> 100,000). */
private fun formatDigitsWithCommas(digitSequence: String): String {
    if (digitSequence.isEmpty()) return ""
    return digitSequence.reversed().chunked(3).joinToString(",").reversed()
}

private fun formatUsdForDisplay(raw: String): String {
    if (raw.isEmpty()) return ""
    val noComma = raw.replace(",", "")
    val dot = noComma.indexOf('.')
    if (dot == -1) {
        val intOnly = noComma.filter { it.isDigit() }.take(16)
        return formatDigitsWithCommas(intOnly)
    }
    val intPart = noComma.substring(0, dot).filter { it.isDigit() }.take(16)
    val fracPart = noComma.substring(dot + 1).filter { it.isDigit() }.take(16)
    val intFmt = formatDigitsWithCommas(intPart)
    return when {
        fracPart.isNotEmpty() -> "$intFmt.$fracPart"
        noComma.endsWith('.') -> "$intFmt."
        else -> intFmt
    }
}

/** USD amount: digits, optional single dot, at most two digits after the decimal (e.g. 1.11). */
private fun filterUsdDecimal(raw: String): String {
    val out = StringBuilder()
    var hasDot = false
    var fracDigits = 0
    for (c in raw.replace(",", "")) {
        when {
            c.isDigit() -> {
                if (!hasDot) {
                    out.append(c)
                } else if (fracDigits < 2) {
                    out.append(c)
                    fracDigits++
                }
            }
            c == '.' && !hasDot -> {
                hasDot = true
                out.append('.')
            }
        }
    }
    return out.toString().take(16)
}

/** Normalizes a filtered USD string to exactly two fraction digits (half-up). */
private fun normalizeUsdToTwoDecimals(raw: String): String {
    val cleaned = filterUsdDecimal(raw.replace(",", ""))
    if (cleaned.isEmpty()) return ""
    if (cleaned == ".") return "0.00"
    val bd = cleaned.toBigDecimalOrNull() ?: return "0.00"
    return bd.setScale(2, RoundingMode.HALF_UP).toPlainString()
}

private object KrwThousandsVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = filterKrwDigits(text.text)
        val formatted = formatDigitsWithCommas(digits)
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                formatDigitsWithCommas(digits.take(offset.coerceIn(0, digits.length))).length

            override fun transformedToOriginal(offset: Int): Int =
                formatted.take(offset.coerceIn(0, formatted.length)).count { it.isDigit() }
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}

private object UsdThousandsVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = filterUsdDecimal(text.text.replace(",", ""))
        val formatted = formatUsdForDisplay(original)
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                formatUsdForDisplay(original.take(offset.coerceIn(0, original.length))).length

            override fun transformedToOriginal(offset: Int): Int {
                val t = offset.coerceIn(0, formatted.length)
                var best = 0
                for (o in 0..original.length) {
                    if (formatUsdForDisplay(original.take(o)).length <= t) best = o
                }
                return best
            }
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}

/** Min width for KRW quick-amount chips; chips grow with content. */
private val QuickAmountChipWidth = 62.dp

private val QuickUsdChipMinWidth = 50.dp
private val QuickUsdChipHeight = 32.dp
private val QuickUsdChipFontSize = 9.sp
private val QuickUsdChipHorizontalPadding = 6.dp

private fun krwFieldValueAfterAddingPreset(currentText: String, add: Long): String {
    val base = currentText.toLongOrNull() ?: 0L
    val sum = base + add
    val capped = sum.coerceIn(0L, 999_999_999_999L)
    return filterKrwDigits(capped.toString())
}

private fun usdFieldValueAfterAddingPreset(currentText: String, add: BigDecimal): String {
    val base = currentText.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val sum = base.add(add)
    return sum.setScale(2, RoundingMode.HALF_UP).toPlainString()
}

private val QuickUsdAmountPresets = listOf(
    BigDecimal("1.00"),
    BigDecimal("5.00"),
    BigDecimal("10.00"),
    BigDecimal("20.00"),
    BigDecimal("50.00"),
    BigDecimal("100.00"),
)

private val QuickKrwAmountPresets = listOf(
    100L,
    500L,
    1_000L,
    2_000L,
    5_000L,
    10_000L,
    50_000L,
    100_000L,
    500_000L,
    1_000_000L,
)

private fun formatFundingPreview(krw: String, usd: String): String {
    val k = krw.toLongOrNull()
    val u = usd.toDoubleOrNull()
    val parts = buildList {
        if (k != null && k > 0L) add("₩%,d".format(k))
        if (u != null && u > 0.0) add("$%.2f".format(u))
    }
    return if (parts.isEmpty()) "none" else parts.joinToString(" · ")
}

/** PAN grouped as `xxxx - xxxx - xxxx - xxxx` (digits only, up to 16). */
private fun formatCardNumberForReview(pan: String): String {
    val digits = pan.filter { it.isDigit() }.take(16)
    if (digits.isEmpty()) return "—"
    return digits.chunked(4).joinToString(" - ")
}

@Composable
private fun OpenCardFundingStep(
    modifier: Modifier = Modifier,
    openingCurrency: NewCardOpeningCurrency,
    onOpeningCurrency: (NewCardOpeningCurrency) -> Unit,
    initialKrwText: String,
    initialUsdText: String,
    onInitialKrwChange: (String) -> Unit,
    onInitialUsdChange: (String) -> Unit,
    onSkipTopUp: () -> Unit,
    onBack: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val krwFocusRequester = remember { FocusRequester() }
    val usdFocusRequester = remember { FocusRequester() }
    var krwFieldFocused by remember { mutableStateOf(false) }
    var usdFieldFocused by remember { mutableStateOf(false) }
    val highlightKrw = when {
        usdFieldFocused -> false
        krwFieldFocused -> true
        else -> openingCurrency == NewCardOpeningCurrency.KRW
    }
    val highlightUsd = when {
        krwFieldFocused -> false
        usdFieldFocused -> true
        else -> openingCurrency == NewCardOpeningCurrency.USD
    }
    val usdAccent = palette.brand
    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ChooseCardSheetContentPadding),
        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(
                        role = Role.Button,
                        onClickLabel = "Back",
                        onClick = onBack,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = palette.foreground,
                    modifier = Modifier.size(22.dp),
                )
            }
            Text(
                text = "Open a credit card",
                modifier = Modifier.align(Alignment.Center),
                color = palette.foreground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Optional · you can always top up later",
            color = palette.mutedForeground,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(20.dp))
        val walletKrw = ProfileWalletStore.totalKrwLong().toDouble()
        val walletUsd = ProfileWalletStore.totalUsd()
        val fundKrw = initialKrwText.filter { it.isDigit() }.toLongOrNull()?.toDouble() ?: 0.0
        val fundUsd = initialUsdText.replace(",", "").toDoubleOrNull() ?: 0.0
        val afterKrw = (walletKrw - fundKrw).coerceAtLeast(0.0)
        val afterUsd = (walletUsd - fundUsd).coerceAtLeast(0.0)
        val insufficient = fundKrw > walletKrw || fundUsd > walletUsd
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(palette.mutedSurface)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "Wallet balance",
                color = palette.mutedForeground,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Current · ${formatKrwHub(walletKrw.roundToLong())} · ${formatUsdHub(walletUsd)}",
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "After opening · ${formatKrwHub(afterKrw.roundToLong())} · ${formatUsdHub(afterUsd)}",
                color = if (insufficient) palette.destructive else palette.mutedForeground,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
            if (insufficient) {
                Text(
                    text = "Insufficient wallet balance for this opening amount.",
                    color = palette.destructive,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Opening currency",
            color = palette.mutedForeground,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OpeningCurrencyCard(
                slot = NewCardOpeningCurrency.KRW,
                code = "KRW",
                description = "Korean won",
                symbol = "₩",
                selected = openingCurrency == NewCardOpeningCurrency.KRW,
                onClick = {
                    onOpeningCurrency(NewCardOpeningCurrency.KRW)
                    krwFocusRequester.requestFocus()
                },
                modifier = Modifier.weight(1f),
            )
            OpeningCurrencyCard(
                slot = NewCardOpeningCurrency.USD,
                code = "USD",
                description = "US dollars",
                symbol = "$",
                selected = openingCurrency == NewCardOpeningCurrency.USD,
                onClick = {
                    onOpeningCurrency(NewCardOpeningCurrency.USD)
                    usdFocusRequester.requestFocus()
                },
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(22.dp))
        Text(
            text = "Initial top-up",
            color = palette.mutedForeground,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = initialKrwText,
            onValueChange = { onInitialKrwChange(filterKrwDigits(it)) },
            visualTransformation = KrwThousandsVisualTransformation,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(krwFocusRequester)
                .onFocusChanged { fs ->
                    krwFieldFocused = fs.isFocused
                    if (fs.isFocused) onOpeningCurrency(NewCardOpeningCurrency.KRW)
                },
            singleLine = true,
            placeholder = {
                Text(
                    text = "0",
                    color = palette.mutedForeground.copy(alpha = 0.45f),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = {
                Text(
                    "W",
                    color = if (highlightKrw) OpeningKrwAccentBlue else palette.mutedForeground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            },
            textStyle = TextStyle(
                color = if (highlightKrw) OpeningKrwAccentBlue else palette.foreground,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (highlightKrw) OpeningKrwAccentBlue else palette.border,
                unfocusedBorderColor = if (highlightKrw) OpeningKrwAccentBlue else palette.border,
                focusedContainerColor = if (highlightKrw) OpeningKrwAccentBlue.copy(alpha = 0.08f) else palette.cardSurface,
                unfocusedContainerColor = if (highlightKrw) OpeningKrwAccentBlue.copy(alpha = 0.08f) else palette.cardSurface,
                cursorColor = OpeningKrwAccentBlue,
            ),
            shape = RoundedCornerShape(16.dp),
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = initialUsdText,
            onValueChange = { onInitialUsdChange(filterUsdDecimal(it)) },
            visualTransformation = UsdThousandsVisualTransformation,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(usdFocusRequester)
                .onFocusChanged { fs ->
                    if (fs.isFocused) {
                        usdFieldFocused = true
                        onOpeningCurrency(NewCardOpeningCurrency.USD)
                    } else {
                        val shouldNormalize = usdFieldFocused && initialUsdText.isNotEmpty()
                        usdFieldFocused = false
                        if (shouldNormalize) {
                            val normalized = normalizeUsdToTwoDecimals(initialUsdText)
                            if (normalized != initialUsdText) onInitialUsdChange(normalized)
                        }
                    }
                },
            singleLine = true,
            placeholder = {
                Text(
                    text = "0.00",
                    color = palette.mutedForeground.copy(alpha = 0.45f),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            leadingIcon = {
                Text(
                    "$",
                    color = if (highlightUsd) usdAccent else palette.mutedForeground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            },
            textStyle = TextStyle(
                color = if (highlightUsd) usdAccent else palette.foreground,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (highlightUsd) usdAccent else palette.border,
                unfocusedBorderColor = if (highlightUsd) usdAccent else palette.border,
                focusedContainerColor = if (highlightUsd) usdAccent.copy(alpha = 0.10f) else palette.cardSurface,
                unfocusedContainerColor = if (highlightUsd) usdAccent.copy(alpha = 0.10f) else palette.cardSurface,
                cursorColor = usdAccent,
            ),
            shape = RoundedCornerShape(16.dp),
        )
        Spacer(Modifier.height(18.dp))
        Text(
            text = "Quick amounts",
            color = palette.mutedForeground,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(8.dp))
        }
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = ChooseCardSheetContentPadding),
        ) {
            items(QuickKrwAmountPresets, key = { it }) { v ->
                QuickTopUpChip(
                    label = String.format(Locale.US, "%,d", v),
                    accent = OpeningKrwAccentBlue,
                    onClick = {
                        onOpeningCurrency(NewCardOpeningCurrency.KRW)
                        onInitialKrwChange(krwFieldValueAfterAddingPreset(initialKrwText, v))
                        krwFocusRequester.requestFocus()
                    },
                    modifier = Modifier.defaultMinSize(minWidth = QuickAmountChipWidth),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = ChooseCardSheetContentPadding),
        ) {
            items(QuickUsdAmountPresets, key = { it.toPlainString() }) { preset ->
                QuickTopUpChip(
                    label = preset.toPlainString(),
                    accent = usdAccent,
                    chipHeight = QuickUsdChipHeight,
                    fontSize = QuickUsdChipFontSize,
                    horizontalPadding = QuickUsdChipHorizontalPadding,
                    onClick = {
                        onOpeningCurrency(NewCardOpeningCurrency.USD)
                        onInitialUsdChange(usdFieldValueAfterAddingPreset(initialUsdText, preset))
                        usdFocusRequester.requestFocus()
                    },
                    modifier = Modifier.defaultMinSize(minWidth = QuickUsdChipMinWidth),
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ChooseCardSheetContentPadding),
        ) {
            Spacer(Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                QuickTopUpChip(
                    label = "Skip for now",
                    accent = null,
                    onClick = onSkipTopUp,
                    modifier = Modifier.widthIn(min = 120.dp),
                )
            }
        }
    }
}

@Composable
private fun OpeningCurrencyCard(
    slot: NewCardOpeningCurrency,
    code: String,
    description: String,
    symbol: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val usdAccent = palette.brand
    val accent = when {
        !selected -> palette.foreground
        slot == NewCardOpeningCurrency.KRW -> OpeningKrwAccentBlue
        else -> usdAccent
    }
    val borderColor = when {
        !selected -> palette.border
        slot == NewCardOpeningCurrency.KRW -> OpeningKrwAccentBlue
        else -> usdAccent
    }
    val bg = when {
        !selected -> palette.cardSurface
        slot == NewCardOpeningCurrency.KRW -> OpeningKrwAccentBlue.copy(alpha = 0.10f)
        else -> usdAccent.copy(alpha = 0.10f)
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(role = Role.Button, onClickLabel = code, onClick = onClick)
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(code, color = accent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(symbol, color = accent, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(6.dp))
        Text(description, color = accent, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun QuickTopUpChip(
    label: String,
    accent: Color?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    chipHeight: Dp = 40.dp,
    fontSize: TextUnit = 10.sp,
    horizontalPadding: Dp = 8.dp,
) {
    val palette = LocalRestaurantPalette.current
    val borderColor = accent ?: palette.border
    val textColor = accent ?: palette.foreground
    val bg = if (accent != null) accent.copy(alpha = 0.10f) else palette.cardSurface
    Box(
        modifier = modifier
            .height(chipHeight)
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, borderColor, RoundedCornerShape(999.dp))
            .background(bg)
            .clickable(role = Role.Button, onClickLabel = label, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = horizontalPadding),
        )
    }
}

@Composable
private fun PickerStripItemFrame(
    selected: Boolean,
    onClickLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    swatch: @Composable () -> Unit,
    label: String,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .width(PickerStripItemWidth)
            .height(PickerStripFrameHeight)
            .drawWithContent {
                drawContent()
                if (selected) {
                    val strokePx = PickerSelectedRingStroke.toPx()
                    val halfStroke = strokePx / 2f
                    val rw = size.width - strokePx
                    val rh = size.height - strokePx
                    if (rw > 0f && rh > 0f) {
                        val rPx = minOf(14.dp.toPx(), rw / 2f, rh / 2f)
                        drawRoundRect(
                            color = RestaurantColors.Base.black,
                            topLeft = Offset(halfStroke, halfStroke),
                            size = Size(rw, rh),
                            cornerRadius = CornerRadius(rPx, rPx),
                            style = Stroke(width = strokePx),
                        )
                    }
                }
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClickLabel = onClickLabel,
                onClick = onClick,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PickerStripContentPadding)
                .then(
                    if (selected) {
                        Modifier.background(RestaurantColors.Base.white, PickerSelectedInnerShape)
                    } else {
                        Modifier
                    },
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            swatch()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PickerStripLabelRowHeight),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = RestaurantColors.Base.black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun PickerStripCheckmark() {
    Box(
        modifier = Modifier
            .padding(5.dp)
            .size(18.dp)
            .clip(CircleShape)
            .background(RestaurantColors.Base.black),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = null,
            tint = RestaurantColors.Base.white,
            modifier = Modifier.size(11.dp),
        )
    }
}

@Composable
private fun ThemePickerStripItem(
    themeId: HubCardThemeId,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val label = themeId.name
    PickerStripItemFrame(
        selected = selected,
        onClickLabel = label,
        onClick = onClick,
        modifier = modifier,
        label = label,
        swatch = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PickerStripSwatchHeight)
                    .clip(PickerSwatchShape)
                    .drawBehind {
                        drawRect(
                            brush = hubCardThemeBackgroundBrush(
                                themeId = themeId,
                                widthPx = size.width,
                                heightPx = size.height,
                                brandColor = palette.brand,
                            ),
                        )
                    },
            ) {
                if (selected) {
                    Box(Modifier.align(Alignment.TopEnd)) {
                        PickerStripCheckmark()
                    }
                }
            }
        },
    )
}

@Composable
private fun PatternPickerStripItem(
    pattern: HubCardPattern,
    label: String,
    selected: Boolean,
    themeId: HubCardThemeId,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    PickerStripItemFrame(
        selected = selected,
        onClickLabel = label,
        onClick = onClick,
        modifier = modifier,
        label = label,
        swatch = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PickerStripSwatchHeight)
                    .clip(PickerSwatchShape),
            ) {
                HubThemedCardBackground(
                    themeId = themeId,
                    patternOverride = pattern,
                    brandColor = palette.brand,
                    modifier = Modifier.matchParentSize(),
                )
                if (selected) {
                    Box(Modifier.align(Alignment.TopEnd)) {
                        PickerStripCheckmark()
                    }
                }
            }
        },
    )
}

@Composable
private fun ReviewSummaryFundingRow(
    initialKrwText: String,
    initialUsdText: String,
) {
    val palette = LocalRestaurantPalette.current
    val usdAccent = palette.brand
    val kAmt = initialKrwText.toLongOrNull()?.takeIf { it > 0L }
    val uAmt = initialUsdText.toDoubleOrNull()?.takeIf { it > 0.0 }
    val showKrw = kAmt != null
    val showUsd = uAmt != null
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Initial top-up",
            color = palette.mutedForeground,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.42f),
        )
        Row(
            modifier = Modifier.weight(0.58f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!showKrw && !showUsd) {
                Text(
                    text = "Skip for now",
                    color = palette.foreground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            } else {
                kAmt?.let { kv ->
                    Text(
                        text = "W",
                        color = OpeningKrwAccentBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = String.format(Locale.US, "%,d", kv),
                        color = OpeningKrwAccentBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                if (showKrw && showUsd) {
                    Text(
                        text = " · ",
                        color = palette.mutedForeground,
                        fontSize = 14.sp,
                    )
                }
                uAmt?.let { uv ->
                    Text(
                        text = "$",
                        color = usdAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = String.format(Locale.US, "%.2f", uv),
                        color = usdAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewSummaryLine(
    label: String,
    value: String,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = palette.mutedForeground,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.42f),
        )
        Text(
            text = value,
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.58f),
            textAlign = TextAlign.End,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ChooseCardThemeStepTabs(
    currentStep: Int,
    onStepSelect: (Int) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ChooseCardSheetContentPadding)
            .padding(top = 2.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(ChooseCardSheetSteps) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        when {
                            index == currentStep -> palette.brand
                            index < currentStep -> palette.brand.copy(alpha = 0.42f)
                            else -> palette.mutedSurface
                        },
                    )
                    .clickable(
                        role = Role.Tab,
                        onClickLabel = "Step ${index + 1}",
                        onClick = { onStepSelect(index) },
                    ),
            )
        }
    }
}
