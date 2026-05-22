package com.mh.restaurantchainreservation.feature.discover.ui

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.model.Restaurant
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sinh

internal const val MAP_MIN_ZOOM = 10f
internal const val MAP_MAX_ZOOM = 19f
internal const val MAP_ZOOM_STEP = 0.75f
/** Zoomed in when the results sheet is at the bottom (peek). */
internal const val MAP_ZOOM_SHEET_PEEK = 15.8f
/** Zoomed out when the results sheet is at the top (full). */
internal const val MAP_ZOOM_SHEET_FULL = 14.2f

internal fun mapZoomForSheetProgress(progress: Float): Float {
    val t = progress.coerceIn(0f, 1f)
    return MAP_ZOOM_SHEET_PEEK + (MAP_ZOOM_SHEET_FULL - MAP_ZOOM_SHEET_PEEK) * t
}

/** Map zoom only interpolates between peek and half; above halfPx zoom stays at the half value. */
internal fun mapZoomForSheetHeight(
    sheetHeightPx: Float,
    peekPx: Float,
    halfPx: Float,
): Float {
    val progress = if (halfPx <= peekPx) {
        1f
    } else {
        ((sheetHeightPx - peekPx) / (halfPx - peekPx)).coerceIn(0f, 1f)
    }
    return mapZoomForSheetProgress(progress)
}

private const val TILE_SIZE = 256.0

@Composable
internal fun DiscoverSearchMapSurface(
    restaurants: List<Restaurant>,
    activeMarker: Int,
    distanceFilter: String,
    zoom: Float,
    onZoomChange: (Float) -> Unit,
    topInset: Dp,
    bottomInset: Dp,
    onMarkerSelect: (Int) -> Unit,
    recenterSignal: Int = 0,
    modifier: Modifier = Modifier,
) {
    var latitude by remember { mutableDoubleStateOf(SEARCH_USER_LOCATION.lat) }
    var longitude by remember { mutableDoubleStateOf(SEARCH_USER_LOCATION.lng) }

    val markers = remember(restaurants) {
        restaurants.mapIndexed { index, restaurant -> restaurant.searchMapLocation(index) }
    }

    LaunchedEffect(recenterSignal) {
        if (recenterSignal > 0) {
            latitude = SEARCH_USER_LOCATION.lat
            longitude = SEARCH_USER_LOCATION.lng
        }
    }

    LaunchedEffect(activeMarker) {
        markers.getOrNull(activeMarker)?.let { target ->
            latitude = target.lat
            longitude = target.lng
        }
    }

    val radiusMiles = remember(distanceFilter) { maxMilesForDistanceFilter(distanceFilter) }

    BoxWithConstraints(
        modifier = modifier
            .padding(top = topInset, bottom = bottomInset)
            .clip(RectangleShape)
            .background(RestaurantColors.Map.canvas)
            .pointerInput(onZoomChange) {
                detectTransformGestures { _, pan, zoomChange, _ ->
                    if (zoomChange != 1f) {
                        onZoomChange((zoom * zoomChange).coerceIn(MAP_MIN_ZOOM, MAP_MAX_ZOOM))
                    }
                    if (pan != Offset.Zero) {
                        val gestureZoom = zoom.toDouble()
                        val centerWorld = latLngToWorldPixel(latitude, longitude, gestureZoom)
                        val newCenter = worldPixelToLatLng(
                            worldX = (centerWorld.x - pan.x).toDouble(),
                            worldY = (centerWorld.y - pan.y).toDouble(),
                            zoom = gestureZoom,
                        )
                        latitude = newCenter.lat
                        longitude = newCenter.lng
                    }
                }
            },
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        if (widthPx <= 0f || heightPx <= 0f) return@BoxWithConstraints

        val mapZoom = zoom.toDouble()
        val tileZoom = floor(mapZoom).toInt().coerceIn(MAP_MIN_ZOOM.toInt(), MAP_MAX_ZOOM.toInt())
        val tileScale = 2.0.pow(mapZoom - tileZoom).toFloat()
        val tileSizePx = (TILE_SIZE * tileScale).toFloat()

        val topLeft = remember(latitude, longitude, mapZoom, widthPx, heightPx) {
            topLeftWorldPixel(latitude, longitude, mapZoom, widthPx, heightPx)
        }

        val startTileX = floor(topLeft.x / (TILE_SIZE * tileScale)).toInt()
        val startTileY = floor(topLeft.y / (TILE_SIZE * tileScale)).toInt()
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

            if (radiusMiles != null) {
                val radiusPx = metersToPixelsAtLat(
                    milesToMeters(radiusMiles),
                    SEARCH_USER_LOCATION.lat,
                    mapZoom,
                ).toFloat()
                val userScreen = latLngToScreen(
                    lat = SEARCH_USER_LOCATION.lat,
                    lng = SEARCH_USER_LOCATION.lng,
                    mapZoom = mapZoom,
                    topLeftWorld = topLeft,
                )
                Canvas(Modifier.fillMaxSize()) {
                    drawCircle(
                        color = RestaurantColors.Map.markerBlue.copy(alpha = 0.14f),
                        radius = radiusPx,
                        center = userScreen,
                    )
                    drawCircle(
                        color = RestaurantColors.Map.markerBlue.copy(alpha = 0.35f),
                        radius = radiusPx,
                        center = userScreen,
                        style = Stroke(width = 2.dp.toPx()),
                    )
                }
            }

            markers.forEachIndexed { index, location ->
                if (index == activeMarker) return@forEachIndexed
                RestaurantMapMarker(
                    index = index,
                    location = location,
                    restaurant = restaurants.getOrNull(index),
                    active = false,
                    mapZoom = mapZoom,
                    topLeft = topLeft,
                    onSelect = onMarkerSelect,
                )
            }
            markers.getOrNull(activeMarker)?.let { location ->
                RestaurantMapMarker(
                    index = activeMarker,
                    location = location,
                    restaurant = restaurants.getOrNull(activeMarker),
                    active = true,
                    mapZoom = mapZoom,
                    topLeft = topLeft,
                    onSelect = onMarkerSelect,
                )
            }

            val userScreen = latLngToScreen(
                lat = SEARCH_USER_LOCATION.lat,
                lng = SEARCH_USER_LOCATION.lng,
                mapZoom = mapZoom,
                topLeftWorld = topLeft,
            )
            UserLocationMarker(
                modifier = Modifier
                    .zIndex(10f)
                    .offset {
                        IntOffset(
                            (userScreen.x - 16.dp.toPx()).roundToInt(),
                            (userScreen.y - 16.dp.toPx()).roundToInt(),
                        )
                    },
            )
        }
    }
}

@Composable
private fun RestaurantMapMarker(
    index: Int,
    location: MapLatLng,
    restaurant: Restaurant?,
    active: Boolean,
    mapZoom: Double,
    topLeft: Offset,
    onSelect: (Int) -> Unit,
) {
    val markerScale by animateFloatAsState(
        targetValue = if (active) 1.12f else 1f,
        animationSpec = tween(durationMillis = 180),
        label = "markerScale",
    )
    val screen = latLngToScreen(
        lat = location.lat,
        lng = location.lng,
        mapZoom = mapZoom,
        topLeftWorld = topLeft,
    )
    MapRatingMarker(
        label = mapMarkerScore(restaurant?.rating ?: 0.0),
        active = active,
        onClick = { onSelect(index) },
        modifier = Modifier
            .zIndex(if (active) 8f else 1f)
            .graphicsLayer {
                scaleX = markerScale
                scaleY = markerScale
            }
            .offset {
                IntOffset(
                    (screen.x - 28.dp.toPx()).roundToInt(),
                    (screen.y - 36.dp.toPx()).roundToInt(),
                )
            },
    )
}

private fun topLeftWorldPixel(
    latitude: Double,
    longitude: Double,
    mapZoom: Double,
    widthPx: Float,
    heightPx: Float,
): Offset {
    val centerWorld = latLngToWorldPixel(latitude, longitude, mapZoom)
    return Offset(
        x = centerWorld.x - widthPx / 2f,
        y = centerWorld.y - heightPx / 2f,
    )
}

@Composable
private fun UserLocationMarker(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "userLocationPulse")
    val pulseScale by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulseScale",
    )
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulseAlpha",
    )
    Box(modifier = modifier.size(32.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .graphicsLayer {
                    scaleX = pulseScale
                    scaleY = pulseScale
                    alpha = pulseAlpha
                }
                .clip(CircleShape)
                .background(RestaurantColors.Map.markerBlue.copy(alpha = 0.35f)),
        )
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(RestaurantColors.Map.markerBlue.copy(alpha = 0.12f)),
        )
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(RestaurantColors.Map.markerBlue)
                .border(2.dp, RestaurantColors.Base.white, CircleShape),
        )
    }
}

@Composable
private fun MapRatingMarker(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(if (active) RestaurantColors.Text.primary else RestaurantColors.Base.white)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (active) RestaurantColors.Base.white else RestaurantColors.Text.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

private fun latLngToWorldPixel(lat: Double, lng: Double, zoom: Double): Offset {
    val scale = TILE_SIZE * 2.0.pow(zoom)
    val x = (lng + 180.0) / 360.0 * scale
    val sinLat = sin(Math.toRadians(lat))
    val y = (0.5 - ln((1 + sinLat) / (1 - sinLat)) / (4 * PI)) * scale
    return Offset(x.toFloat(), y.toFloat())
}

private fun worldPixelToLatLng(worldX: Double, worldY: Double, zoom: Double): MapLatLng {
    val scale = TILE_SIZE * 2.0.pow(zoom)
    val lng = worldX / scale * 360.0 - 180.0
    val latRadians = atan(sinh(PI - (2.0 * PI * worldY) / scale))
    return MapLatLng(lat = Math.toDegrees(latRadians), lng = lng)
}

private fun latLngToScreen(
    lat: Double,
    lng: Double,
    mapZoom: Double,
    topLeftWorld: Offset,
): Offset {
    val world = latLngToWorldPixel(lat, lng, mapZoom)
    return Offset(world.x - topLeftWorld.x, world.y - topLeftWorld.y)
}

private fun metersToPixelsAtLat(meters: Double, lat: Double, zoom: Double): Double {
    val metersPerPixel = cos(Math.toRadians(lat)) * 2 * PI * 6378137 / (TILE_SIZE * 2.0.pow(zoom))
    return meters / metersPerPixel
}

@Composable
internal fun SearchMapControlsColumn(
    mapZoom: Float,
    onRecenter: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SearchMapCurrentLocationButton(onClick = onRecenter)
        SearchMapZoomButton(
            icon = Icons.Filled.Add,
            contentDescription = "Zoom in",
            enabled = mapZoom < MAP_MAX_ZOOM,
            onClick = onZoomIn,
        )
        SearchMapZoomButton(
            icon = Icons.Filled.Remove,
            contentDescription = "Zoom out",
            enabled = mapZoom > MAP_MIN_ZOOM,
            onClick = onZoomOut,
        )
    }
}

@Composable
private fun SearchMapZoomButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .shadow(6.dp, CircleShape, ambientColor = RestaurantColors.Base.black.copy(alpha = 0.12f))
            .clip(CircleShape)
            .background(RestaurantColors.Base.white)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = RestaurantColors.Text.primary.copy(alpha = if (enabled) 1f else 0.35f),
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
internal fun SearchMapCurrentLocationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .shadow(6.dp, CircleShape, ambientColor = RestaurantColors.Base.black.copy(alpha = 0.12f))
            .clip(CircleShape)
            .background(RestaurantColors.Base.white)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Navigation,
            contentDescription = "Show current location",
            tint = RestaurantColors.Text.primary,
            modifier = Modifier
                .size(22.dp)
                .graphicsLayer { rotationZ = 45f },
        )
    }
}
