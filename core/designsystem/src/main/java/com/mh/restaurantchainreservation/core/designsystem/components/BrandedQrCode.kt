package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.components.icons.QrCodeIcon

/**
 * QR with brand-colored finder centers and a center logo badge (arrival / profile QR modals).
 */
@Composable
fun BrandedQrCode(
    code: String,
    modifier: Modifier = Modifier,
    foregroundColor: Color = Color(0xFF1A1A1A),
    brandColor: Color,
    cells: Int = 25,
) {
    val pattern = remember(code, cells) {
        buildQrPattern(code, cells)
    }
    val logoFraction = 0.22f

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellSize = size.minDimension / cells
            val logoRadiusCells = (cells * logoFraction / 2f).coerceAtLeast(3f)
            val centerR = cells / 2f
            val centerC = cells / 2f

            for (r in 0 until cells) {
                for (c in 0 until cells) {
                    val dr = (r - centerR).toFloat()
                    val dc = (c - centerC).toFloat()
                    if (dr * dr + dc * dc < logoRadiusCells * logoRadiusCells) continue

                    val inTopLeft = r < 7 && c < 7
                    val inTopRight = r < 7 && c >= cells - 7
                    val inBottomLeft = r >= cells - 7 && c < 7
                    val isFinderCenter = if (inTopLeft || inTopRight || inBottomLeft) {
                        val localR = if (inBottomLeft) r - (cells - 7) else r
                        val localC = if (inTopRight) c - (cells - 7) else c
                        localR in 2..4 && localC in 2..4
                    } else {
                        false
                    }

                    val filled = when {
                        isFinderCenter -> true
                        inTopLeft || inTopRight || inBottomLeft -> {
                            val localR = if (inBottomLeft) r - (cells - 7) else r
                            val localC = if (inTopRight) c - (cells - 7) else c
                            localR == 0 || localR == 6 || localC == 0 || localC == 6 ||
                                (localR in 2..4 && localC in 2..4)
                        }
                        else -> pattern[r][c]
                    }

                    if (!filled) continue

                    val color = if (isFinderCenter) brandColor else foregroundColor
                    val corner = if (inTopLeft || inTopRight || inBottomLeft) {
                        CornerRadius(cellSize * 0.15f, cellSize * 0.15f)
                    } else {
                        CornerRadius.Zero
                    }
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(c * cellSize, r * cellSize),
                        size = Size(cellSize - 1f, cellSize - 1f),
                        cornerRadius = corner,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize(logoFraction)
                .clip(CircleShape)
                .background(brandColor),
            contentAlignment = Alignment.Center,
        ) {
            QrCodeIcon(
                color = Color.White,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

private fun buildQrPattern(code: String, cells: Int): Array<BooleanArray> =
    Array(cells) { r ->
        BooleanArray(cells) { c ->
            if (code.isEmpty()) return@BooleanArray false
            val inTopLeft = r < 7 && c < 7
            val inTopRight = r < 7 && c >= cells - 7
            val inBottomLeft = r >= cells - 7 && c < 7
            if (inTopLeft || inTopRight || inBottomLeft) return@BooleanArray false
            val ch = code[r % code.length].code
            val hash = (ch * 31 + c * 17 + r * 13).mod(100)
            hash < 45
        }
    }
