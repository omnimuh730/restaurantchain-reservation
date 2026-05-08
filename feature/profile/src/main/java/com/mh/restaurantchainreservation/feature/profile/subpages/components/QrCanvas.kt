package com.mh.restaurantchainreservation.feature.profile.subpages.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

/**
 * Deterministic faux QR matching the React `QRCodeSVG` placeholder pattern in
 * the rest of the app. 25 cell grid with three corner finder patterns and a
 * content area filled by a per-character hash. Used purely as a visual asset.
 */
@Composable
fun QrCanvas(code: String, modifier: Modifier = Modifier) {
    val cells = 25
    val pattern = remember(code) {
        Array(cells) { r ->
            BooleanArray(cells) { c ->
                if (code.isEmpty()) return@BooleanArray false
                if ((r < 7 && c < 7) || (r < 7 && c >= cells - 7) || (r >= cells - 7 && c < 7)) {
                    return@BooleanArray r == 0 || r == 6 || c == 0 || c == 6 || (r in 2..4 && c in 2..4) ||
                        (r < 7 && c >= cells - 7 && (c == cells - 7 || c == cells - 1 || (r in 2..4 && c >= cells - 5 && c <= cells - 3))) ||
                        (r >= cells - 7 && c < 7 && (r == cells - 7 || r == cells - 1 || (r >= cells - 5 && r <= cells - 3 && c in 2..4)))
                }
                val ch = code[r % code.length].code
                val hash = (ch * 31 + c * 17 + r * 13).mod(100)
                hash < 45
            }
        }
    }
    Canvas(modifier = modifier) {
        val cellSize = size.minDimension / cells
        for (r in 0 until cells) {
            for (c in 0 until cells) {
                if (pattern[r][c]) {
                    drawRect(
                        color = Color(0xFF222222),
                        topLeft = Offset(c * cellSize, r * cellSize),
                        size = Size(cellSize - 1f, cellSize - 1f),
                    )
                }
            }
        }
    }
}
