package com.mh.restaurantchainreservation.feature.profile.subpages.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

enum class Currency { KRW, USD }

/**
 * Animated currency amount display with per-character pop transitions.
 * Mirrors React `TopUpSelectView` digit motion (spring 400/30/0.8) using
 * Compose `AnimatedContent` with a key derived from the formatted string.
 */
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

/**
 * 3x4 numeric keypad. Bottom row is `0` + back. Optional `.` slot for USD.
 * Triggers callbacks on each press.
 */
@Composable
fun MoneyKeypad(
    currency: Currency,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = if (currency == Currency.USD) {
        listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf(".", "0", "back"),
        )
    } else {
        listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "back"),
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
                                "" -> Unit
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
    val isEmpty = key.isBlank()
    Box(
        modifier = modifier
            .height(58.dp)
            .clip(shape)
            .background(if (isEmpty) Color.Transparent else palette.cardSurface)
            .let { if (!isEmpty) it.clickable(onClick = onClick) else it },
        contentAlignment = Alignment.Center,
    ) {
        when (key) {
            "" -> Unit
            "back" -> Icon(
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

/**
 * Format an integer/decimal amount with thousands separators.
 * KRW: integer only. USD: up to 2 decimal places.
 */
fun formatAmountString(raw: String, currency: Currency): String {
    if (raw.isEmpty() || raw == "0") return "0"
    val cleaned = raw
    return when (currency) {
        Currency.KRW -> {
            val n = cleaned.toLongOrNull() ?: return cleaned
            "%,d".format(n)
        }
        Currency.USD -> {
            val parts = cleaned.split(".")
            val intPart = parts[0].toLongOrNull() ?: return cleaned
            val intStr = "%,d".format(intPart)
            if (parts.size > 1) "$intStr.${parts[1]}" else intStr
        }
    }
}

/**
 * Append a digit/decimal to an amount string with currency-specific rules.
 *  - KRW max 9 digits; no decimal allowed.
 *  - USD max 7 chars; only one `.`; max 2 decimal places.
 *  - Stripping leading zeros except after a decimal.
 */
fun appendDigit(current: String, digit: String, currency: Currency): String {
    val raw = if (current == "0") "" else current
    return when (currency) {
        Currency.KRW -> {
            if (digit == ".") current
            else if (raw.length >= 9) current
            else (raw + digit)
        }
        Currency.USD -> {
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
