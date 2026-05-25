package com.mh.restaurantchainreservation.feature.qrpay

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

/**
 * Currency mode for the QR Pay amount entry. Duplicated locally so qrpay does
 * not depend on profile.
 */
enum class QrCurrency { KRW, USD }

@Composable
fun AnimatedAmountDisplay(
    amount: String,
    symbol: String,
    symbolColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier,
    fontSize: Int = 52,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = symbol,
            color = symbolColor,
            fontSize = (fontSize * 0.46f).sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = (fontSize * 0.18f).dp),
        )
        Spacer(Modifier.size(2.dp))
        amount.forEachIndexed { index, ch ->
            val key = "$index-$ch-${amount.length}"
            AnimatedContent(
                targetState = key,
                transitionSpec = {
                    (scaleIn(initialScale = 0.6f, animationSpec = spring(stiffness = 400f, dampingRatio = 0.6f)) +
                        fadeIn(tween(160))) togetherWith
                        (scaleOut(targetScale = 0.6f, animationSpec = spring(stiffness = 400f, dampingRatio = 0.6f)) +
                            fadeOut(tween(120)))
                },
                label = "amount_char_$index",
            ) { _ ->
                Text(
                    text = ch.toString(),
                    color = valueColor,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.025).sp,
                )
            }
        }
    }
}

@Composable
fun MoneyKeypad(
    currency: QrCurrency,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = when (currency) {
        QrCurrency.USD -> listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("clear", ".", "0", "back"),
        )
        QrCurrency.KRW -> listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("clear", "0", "back"),
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                row.forEach { key ->
                    KeypadButton(
                        key = key,
                        onClick = {
                            when (key) {
                                "clear" -> onClear()
                                "back" -> onBackspace()
                                else -> onDigit(key)
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(key: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(18.dp)
    val isClear = key == "clear"
    val isBackspace = key == "back"
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(stiffness = 520f, dampingRatio = 0.72f),
        label = "qr_keypad_scale",
    )
    Box(
        modifier = modifier
            .height(58.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(
                when {
                    isClear -> if (pressed) palette.brand.copy(alpha = 0.16f) else palette.brand.copy(alpha = 0.07f)
                    isBackspace -> if (pressed) palette.mutedSurface else palette.cardSurface
                    pressed -> palette.brand.copy(alpha = 0.10f)
                    else -> palette.cardSurface
                },
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .then(
                if (isClear) {
                    Modifier.semantics { contentDescription = "Clear amount" }
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        when {
            isClear -> Text(
                text = "C",
                color = palette.brand,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            isBackspace -> Icon(
                imageVector = Icons.Outlined.Backspace,
                contentDescription = "Backspace",
                tint = palette.mutedForeground,
                modifier = Modifier.size(22.dp),
            )
            else -> Text(
                text = key,
                color = palette.foreground,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

fun formatAmountString(raw: String, currency: QrCurrency): String {
    if (raw.isEmpty() || raw == "0") return "0"
    return when (currency) {
        QrCurrency.KRW -> {
            val n = raw.toLongOrNull() ?: return raw
            "%,d".format(n)
        }
        QrCurrency.USD -> {
            val parts = raw.split(".")
            val intPart = parts[0].toLongOrNull() ?: return raw
            val intStr = "%,d".format(intPart)
            if (parts.size > 1) "$intStr.${parts[1]}" else intStr
        }
    }
}

fun appendDigit(current: String, digit: String, currency: QrCurrency): String {
    val raw = if (current == "0") "" else current
    return when (currency) {
        QrCurrency.KRW -> {
            if (digit == ".") current
            else if (raw.length >= 9) current
            else (raw + digit)
        }
        QrCurrency.USD -> {
            if (digit == ".") {
                if (raw.contains(".") || raw.length >= 6) current else (raw.ifEmpty { "0" }) + "."
            } else {
                if (raw.length >= 7) return current
                val parts = raw.split(".")
                if (parts.size > 1 && parts[1].length >= 2) return current
                raw + digit
            }
        }
    }
}

fun backspaceDigit(current: String): String {
    if (current.isEmpty() || current == "0") return "0"
    val next = current.dropLast(1)
    return next.ifEmpty { "0" }
}

fun amountAsNumber(raw: String): Double = raw.toDoubleOrNull() ?: 0.0
