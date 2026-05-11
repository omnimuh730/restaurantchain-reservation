package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

@Composable
fun DeterministicQrCode(
    code: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF222222),
    cells: Int = 25,
) {
    val pattern = remember(code, cells) {
        Array(cells) { r ->
            BooleanArray(cells) { c ->
                if (code.isEmpty()) return@BooleanArray false
                val inTopLeft = r < 7 && c < 7
                val inTopRight = r < 7 && c >= cells - 7
                val inBottomLeft = r >= cells - 7 && c < 7
                if (inTopLeft || inTopRight || inBottomLeft) {
                    val localR = if (inBottomLeft) r - (cells - 7) else r
                    val localC = if (inTopRight) c - (cells - 7) else c
                    return@BooleanArray localR == 0 ||
                        localR == 6 ||
                        localC == 0 ||
                        localC == 6 ||
                        (localR in 2..4 && localC in 2..4)
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
                        color = color,
                        topLeft = Offset(c * cellSize, r * cellSize),
                        size = Size(cellSize - 1f, cellSize - 1f),
                    )
                }
            }
        }
    }
}
