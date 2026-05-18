package com.mh.restaurantchainreservation.feature.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

private const val TILE_SIZE = 256.0
private const val DETAIL_MAP_ZOOM = 15.0

@Composable
internal fun RestaurantDetailLocationMap(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier,
) {
    val mapZoom = DETAIL_MAP_ZOOM
    val tileZoom = floor(mapZoom).toInt()
    val tileScale = 2.0.pow(mapZoom - tileZoom).toFloat()

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFE8E8E8)),
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        if (widthPx <= 0f || heightPx <= 0f) return@BoxWithConstraints

        val topLeft = remember(latitude, longitude, widthPx, heightPx) {
            val center = latLngToWorldPixel(latitude, longitude, mapZoom)
            androidx.compose.ui.geometry.Offset(
                x = center.x - widthPx / 2f,
                y = center.y - heightPx / 2f,
            )
        }

        val startTileX = floor(topLeft.x / (TILE_SIZE * tileScale)).toInt()
        val startTileY = floor(topLeft.y / (TILE_SIZE * tileScale)).toInt()
        val tileSizePx = (TILE_SIZE * tileScale).toFloat()
        val tilesAcrossX = (widthPx / tileSizePx).toInt() + 3
        val tilesAcrossY = (heightPx / tileSizePx).toInt() + 3
        val maxTile = (1 shl tileZoom) - 1

        Box(modifier = Modifier.fillMaxSize()) {
            for (dx in 0..tilesAcrossX) {
                for (dy in 0..tilesAcrossY) {
                    val tileX = startTileX + dx
                    val tileY = startTileY + dy
                    if (tileX < 0 || tileY < 0 || tileX > maxTile || tileY > maxTile) continue

                    val tileWorldX = tileX * TILE_SIZE * tileScale
                    val tileWorldY = tileY * TILE_SIZE * tileScale
                    val screenX = (tileWorldX - topLeft.x).roundToInt()
                    val screenY = (tileWorldY - topLeft.y).roundToInt()

                    AsyncImage(
                        model = "https://a.basemaps.cartocdn.com/light_all/$tileZoom/$tileX/$tileY.png",
                        contentDescription = null,
                        modifier = Modifier
                            .offset { IntOffset(screenX, screenY) }
                            .size(with(density) { tileSizePx.toDp() }),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(52.dp)
                    .shadow(8.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.18f))
                    .clip(CircleShape)
                    .background(Color(0xFF222222))
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

private fun latLngToWorldPixel(lat: Double, lng: Double, zoom: Double): androidx.compose.ui.geometry.Offset {
    val scale = TILE_SIZE * 2.0.pow(zoom)
    val x = (lng + 180.0) / 360.0 * scale
    val sinLat = sin(Math.toRadians(lat))
    val y = (0.5 - ln((1 + sinLat) / (1 - sinLat)) / (4 * PI)) * scale
    return androidx.compose.ui.geometry.Offset(x.toFloat(), y.toFloat())
}
