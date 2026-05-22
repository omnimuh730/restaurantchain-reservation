package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.mh.restaurantchainreservation.core.designsystem.components.GlobalNotificationCenter
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.core.model.LocationStore
import com.mh.restaurantchainreservation.core.model.UserLocation
import kotlin.math.hypot

private enum class LocationMode { Search, Map }

private val LocationChoices = listOf(
    UserLocation("Union Square", "San Francisco, CA", 37.7879, -122.4075),
    UserLocation("Hayes Valley", "San Francisco, CA", 37.7765, -122.4242),
    UserLocation("Mission District", "San Francisco, CA", 37.7599, -122.4148),
    UserLocation("SoHo", "New York, NY", 40.7233, -74.0030),
    UserLocation("West Village", "New York, NY", 40.7358, -74.0036),
    UserLocation("Venice Beach", "Los Angeles, CA", 33.9850, -118.4695),
)

@Composable
fun LocationPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val current by LocationStore.current.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    var mode by rememberSaveable { mutableStateOf(LocationMode.Search) }
    var pickedPin by remember { mutableStateOf<UserLocation?>(null) }

    val filtered = remember(query) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) LocationChoices else LocationChoices.filter {
            it.name.lowercase().contains(q) || it.address.lowercase().contains(q)
        }
    }

    fun select(location: UserLocation) {
        LocationStore.select(location)
        GlobalNotificationCenter.success("Location updated", location.name)
        onBack()
    }

    SubpageScaffold(
        title = stringResource(I18nR.string.location_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        UseCurrentRow {
            select(
                UserLocation(
                    name = "My Location",
                    address = "Current location",
                    lat = current.lat,
                    lng = current.lng,
                ),
            )
        }
        Spacer(Modifier.height(12.dp))
        ModeSwitcher(mode = mode, onModeChange = { mode = it })
        Spacer(Modifier.height(14.dp))

        if (mode == LocationMode.Search) {
            SearchInput(value = query, onValueChange = { query = it }, onClear = { query = "" })
            Spacer(Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(18.dp))
                    .background(palette.cardSurface),
            ) {
                if (filtered.isEmpty()) {
                    EmptyLocationState()
                } else {
                    filtered.forEachIndexed { index, place ->
                        PlaceRow(
                            place = place,
                            selected = samePlace(place, current),
                            showDivider = index < filtered.lastIndex,
                            onClick = { select(place) },
                        )
                    }
                }
            }
        } else {
            MapPicker(
                current = current,
                choices = LocationChoices,
                picked = pickedPin,
                onPick = { pickedPin = it },
            )
            Spacer(Modifier.height(12.dp))
            BrandConfirmButton(
                enabled = pickedPin != null,
                label = pickedPin?.let { "Confirm ${it.name}" } ?: "Tap a pin or map area",
                onClick = { pickedPin?.let(::select) },
            )
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ModeSwitcher(mode: LocationMode, onModeChange: (LocationMode) -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.mutedSurface)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ModeButton("Search", Icons.Outlined.Search, mode == LocationMode.Search, Modifier.weight(1f)) {
            onModeChange(LocationMode.Search)
        }
        ModeButton("Pick on Map", Icons.Outlined.Map, mode == LocationMode.Map, Modifier.weight(1f)) {
            onModeChange(LocationMode.Map)
        }
    }
}

@Composable
private fun ModeButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(if (selected) palette.cardSurface else Color.Transparent)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(icon, null, tint = if (selected) palette.foreground else palette.mutedForeground, modifier = Modifier.size(16.dp))
        Spacer(Modifier.size(6.dp))
        Text(label, color = if (selected) palette.foreground else palette.mutedForeground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SearchInput(value: String, onValueChange: (String) -> Unit, onClear: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, palette.border, RoundedCornerShape(14.dp))
            .background(palette.cardSurface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(Icons.Outlined.Search, null, tint = palette.mutedForeground, modifier = Modifier.size(18.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(stringResource(I18nR.string.location_search_hint), color = palette.mutedForeground, fontSize = 14.sp)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                cursorBrush = SolidColor(palette.brand),
                textStyle = LocalTextStyle.current.merge(TextStyle(color = palette.foreground, fontSize = 14.sp)),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (value.isNotBlank()) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Clear search",
                tint = palette.mutedForeground,
                modifier = Modifier.size(18.dp).clickable(onClick = onClear),
            )
        }
    }
}

@Composable
private fun UseCurrentRow(onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, palette.brand.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .background(palette.brand.copy(alpha = 0.06f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(38.dp).clip(CircleShape).background(palette.brand.copy(alpha = 0.13f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Navigation, null, tint = palette.brand, modifier = Modifier.size(19.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(I18nR.string.location_use_current), color = palette.brand, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("Use saved GPS position", color = palette.mutedForeground, fontSize = 12.sp)
        }
    }
}

@Composable
private fun PlaceRow(place: UserLocation, selected: Boolean, showDivider: Boolean, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(Modifier.size(38.dp).clip(CircleShape).background(palette.mutedSurface), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.LocationOn, null, tint = palette.foreground, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(place.name, color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(place.address, color = palette.mutedForeground, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (selected) {
                Box(Modifier.size(26.dp).clip(CircleShape).background(palette.brand.copy(alpha = 0.10f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Check, null, tint = palette.brand, modifier = Modifier.size(16.dp))
                }
            }
        }
        if (showDivider) {
            Box(Modifier.fillMaxWidth().height(1.dp).padding(start = 64.dp).background(palette.border))
        }
    }
}

@Composable
private fun MapPicker(
    current: UserLocation,
    choices: List<UserLocation>,
    picked: UserLocation?,
    onPick: (UserLocation) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val mapShape = RoundedCornerShape(22.dp)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .clip(mapShape)
                .border(1.dp, palette.border, mapShape)
                .background(palette.mutedSurface)
                .pointerInput(choices, current) {
                    detectTapGestures { offset ->
                        onPick(locationFromTap(offset, size.toSize(), choices))
                    }
                },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawMapSurface(palette)
                val allPins = choices + current
                allPins.forEach { loc ->
                    val point = project(loc, size)
                    val isPicked = picked?.let { samePlace(it, loc) } == true
                    val isCurrent = samePlace(loc, current)
                    drawCircle(
                        color = when {
                            isPicked -> palette.brand.copy(alpha = 0.24f)
                            isCurrent -> Color(0xFF2563EB).copy(alpha = 0.20f)
                            else -> Color.Black.copy(alpha = 0.08f)
                        },
                        radius = if (isPicked) 18f else 13f,
                        center = point,
                    )
                    drawCircle(
                        color = when {
                            isPicked -> palette.brand
                            isCurrent -> Color(0xFF2563EB)
                            else -> palette.foreground
                        },
                        radius = if (isPicked) 7f else 5f,
                        center = point,
                    )
                }
                picked?.takeIf { choices.none { choice -> samePlace(choice, it) } }?.let {
                    val point = project(it, size)
                    drawCircle(palette.brand.copy(alpha = 0.18f), 22f, point)
                    drawCircle(palette.brand, 7f, point)
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.cardSurface.copy(alpha = 0.92f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Icon(Icons.Outlined.MyLocation, null, tint = palette.brand, modifier = Modifier.size(16.dp))
                    Text(picked?.name ?: "Tap map to drop a pin", color = palette.foreground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        picked?.let {
            Text(it.address, color = palette.mutedForeground, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 4.dp))
        }
    }
}

@Composable
private fun BrandConfirmButton(enabled: Boolean, label: String, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) palette.brand else palette.mutedSurface)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = if (enabled) Color.White else palette.mutedForeground, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun EmptyLocationState() {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 34.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Outlined.LocationOn, null, tint = palette.mutedForeground, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(8.dp))
        Text("No matching places", color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text("Try another neighborhood or switch to map.", color = palette.mutedForeground, fontSize = 12.sp)
    }
}

private fun samePlace(a: UserLocation, b: UserLocation): Boolean =
    a.name == b.name && a.address == b.address

private fun locationFromTap(offset: Offset, size: Size, choices: List<UserLocation>): UserLocation {
    val nearest = choices.minByOrNull { loc ->
        val p = project(loc, size)
        hypot((p.x - offset.x).toDouble(), (p.y - offset.y).toDouble())
    }
    if (nearest != null) {
        val p = project(nearest, size)
        if (hypot((p.x - offset.x).toDouble(), (p.y - offset.y).toDouble()) < 42.0) return nearest
    }
    val lat = MapMaxLat - (offset.y / size.height) * (MapMaxLat - MapMinLat)
    val lng = MapMinLng + (offset.x / size.width) * (MapMaxLng - MapMinLng)
    return UserLocation("Picked Location", "%.5f, %.5f".format(lat, lng), lat, lng)
}

private fun project(location: UserLocation, size: Size): Offset {
    val x = ((location.lng - MapMinLng) / (MapMaxLng - MapMinLng)).toFloat() * size.width
    val y = ((MapMaxLat - location.lat) / (MapMaxLat - MapMinLat)).toFloat() * size.height
    return Offset(x.coerceIn(20f, size.width - 20f), y.coerceIn(20f, size.height - 20f))
}

private fun DrawScope.drawMapSurface(palette: com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantPalette) {
    drawRect(Brush.verticalGradient(listOf(Color(0xFFF6F7F8), Color(0xFFEFF3F7))))
    val road = Color.White.copy(alpha = 0.95f)
    val arterial = palette.brand.copy(alpha = 0.20f)
    val dash = PathEffect.dashPathEffect(floatArrayOf(20f, 12f), 0f)
    repeat(7) { i ->
        val y = size.height * (0.14f + i * 0.13f)
        drawLine(road, Offset(0f, y), Offset(size.width, y + if (i % 2 == 0) 36f else -28f), strokeWidth = 18f)
    }
    repeat(5) { i ->
        val x = size.width * (0.12f + i * 0.21f)
        drawLine(road, Offset(x, 0f), Offset(x + if (i % 2 == 0) 42f else -34f, size.height), strokeWidth = 14f)
    }
    drawLine(arterial, Offset(size.width * 0.08f, size.height * 0.76f), Offset(size.width * 0.92f, size.height * 0.24f), strokeWidth = 10f, pathEffect = dash)
}

private const val MapMinLat = 37.48
private const val MapMaxLat = 37.58
private const val MapMinLng = 126.91
private const val MapMaxLng = 127.07
