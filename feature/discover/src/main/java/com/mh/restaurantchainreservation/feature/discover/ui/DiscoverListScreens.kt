package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.DiscoverData
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.RestaurantTimeSlot
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Restaurants for a quick category id. */
@Composable
fun CategoryResultsScreen(
    categoryId: String,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = DiscoverData.QUICK_CATEGORIES.firstOrNull { it.id == categoryId }
        ?.label?.replace("\n", " ")
        ?: "Restaurants"
    ListScaffold(
        title = title,
        restaurants = DiscoverData.byCategory(categoryId),
        onBack = onBack,
        onOpenRestaurant = onOpenRestaurant,
        modifier = modifier,
    )
}

/** Restaurants for a food-type id. */
@Composable
fun FoodResultsScreen(
    foodId: String,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = DiscoverData.FOOD_TYPES.firstOrNull { it.id == foodId }?.label ?: "Food"
    ListScaffold(
        title = title,
        restaurants = DiscoverData.byFoodType(foodId),
        onBack = onBack,
        onOpenRestaurant = onOpenRestaurant,
        modifier = modifier,
    )
}

/** Restaurants for a city/location id. */
@Composable
fun LocationResultsScreen(
    locationId: String,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val city = DiscoverData.CITIES.firstOrNull { it.id == locationId }
    val title = city?.let { "Best of ${it.label}" } ?: "Restaurants"
    ListScaffold(
        title = title,
        restaurants = DiscoverData.byCity(locationId),
        onBack = onBack,
        onOpenRestaurant = onOpenRestaurant,
        modifier = modifier,
    )
}

/** Restaurants for a section id (monthly-best / loved-by-locals / viral / date-night). */
@Composable
fun SectionListScreen(
    sectionId: String,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = when (sectionId) {
        "monthly-best" -> "Monthly Best"
        "loved-by-locals" -> "Loved by Locals"
        "viral" -> "Trending Now"
        "date-night" -> "Date Night Picks"
        else -> "Featured"
    }
    ListScaffold(
        title = title,
        restaurants = DiscoverData.bySection(sectionId),
        onBack = onBack,
        onOpenRestaurant = onOpenRestaurant,
        modifier = modifier,
    )
}

private data class TimeRangeOption(val id: Int, val label: String)

private val defaultTimeRanges = listOf(
    TimeRangeOption(0, "All day"),
    TimeRangeOption(1, "Lunch"),
    TimeRangeOption(2, "Afternoon"),
    TimeRangeOption(3, "Dinner"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListScaffold(
    title: String,
    restaurants: List<Restaurant>,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val locale = Locale.getDefault()
    val dayFormat = remember(locale) { SimpleDateFormat("EEE", locale) }
    val dateFormat = remember(locale) { SimpleDateFormat("MMM d", locale) }

    var selectedDay by remember {
        mutableStateOf(Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        })
    }
    var stripStart by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            },
        )
    }
    var selectedTimeRangeId by remember { mutableStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateStripDays = remember(stripStart) {
        (0 until 7).map { offset ->
            (stripStart.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, offset) }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(palette.cardSurface)
                .windowInsetsPadding(WindowInsets.statusBars),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = palette.foreground,
                    )
                }
                Spacer(Modifier.size(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = palette.foreground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                    Text(
                        text = "${restaurants.size} restaurants",
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                    )
                }
            }

            DateStripRow(
                days = dateStripDays,
                selectedDay = selectedDay,
                dayFormat = dayFormat,
                dateFormat = dateFormat,
                onSelectDay = { selectedDay = it },
                onPickDateClick = { showDatePicker = true },
                modifier = Modifier.padding(bottom = 12.dp),
            )

            TimeRangeRow(
                options = defaultTimeRanges,
                selectedId = selectedTimeRangeId,
                onSelect = { selectedTimeRangeId = it },
                modifier = Modifier.padding(bottom = 14.dp),
            )

            if (restaurants.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No restaurants to show.",
                        color = palette.mutedForeground,
                        fontSize = 14.sp,
                    )
                }
            } else {
                val selectedDayMillis = selectedDay.timeInMillis
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(restaurants, key = { it.id }) { item ->
                        val slots = remember(item.id, selectedDayMillis, selectedTimeRangeId) {
                            mockTimeSlots(
                                restaurantId = item.id,
                                dayMillis = selectedDayMillis,
                                timeRangeId = selectedTimeRangeId,
                            )
                        }
                        RestaurantListCard(
                            restaurant = item,
                            timeSlots = slots,
                            onClick = { onOpenRestaurant(item.id) },
                        )
                    }
                }
            }
        }

        if (showDatePicker) {
            val pickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDay.timeInMillis,
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            pickerState.selectedDateMillis?.let { millis ->
                                val picked = Calendar.getInstance().apply {
                                    timeInMillis = millis
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                selectedDay = picked
                                stripStart = picked.clone() as Calendar
                            }
                            showDatePicker = false
                        },
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                },
            ) {
                DatePicker(state = pickerState)
            }
        }
    }
}

@Composable
private fun DateStripRow(
    days: List<Calendar>,
    selectedDay: Calendar,
    dayFormat: SimpleDateFormat,
    dateFormat: SimpleDateFormat,
    onSelectDay: (Calendar) -> Unit,
    onPickDateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(days, key = { it.timeInMillis }) { day ->
            val selected = sameDay(day, selectedDay)
            val borderColor = if (selected) palette.brand.copy(alpha = 0.5f) else palette.border
            val bg = if (selected) palette.brandSoftSurface else palette.mutedSurface
            Column(
                modifier = Modifier
                    .width(72.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                    .background(bg)
                    .clickable { onSelectDay(day.clone() as Calendar) }
                    .padding(vertical = 10.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = dayFormat.format(day.time),
                    color = if (selected) palette.brandStrong else palette.mutedForeground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = dateFormat.format(day.time),
                    color = if (selected) palette.foreground else palette.mutedForeground,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
        item(key = "pick_date") {
            PickDateChip(
                onClick = onPickDateClick,
                modifier = Modifier.width(80.dp),
            )
        }
    }
}

@Composable
private fun PickDateChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val dashColor = palette.border
    Column(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(palette.mutedSurface)
            .pickDateDashedBorder(14.dp, dashColor)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = null,
            tint = palette.mutedForeground,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = "Pick Date",
            color = palette.mutedForeground,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

private fun Modifier.pickDateDashedBorder(cornerDp: Dp, color: Color) =
    drawBehind {
        val stroke = Stroke(
            width = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 4.dp.toPx()), 0f),
        )
        drawRoundRect(
            color = color,
            style = stroke,
            cornerRadius = CornerRadius(cornerDp.toPx(), cornerDp.toPx()),
        )
    }

@Composable
private fun TimeRangeRow(
    options: List<TimeRangeOption>,
    selectedId: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(options, key = { it.id }) { opt ->
            val selected = opt.id == selectedId
            val borderColor = if (selected) palette.brand.copy(alpha = 0.45f) else palette.border
            val bg = if (selected) palette.brandSoftSurface else palette.cardSurface
            Text(
                text = opt.label,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(999.dp))
                    .background(bg)
                    .clickable { onSelect(opt.id) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                color = if (selected) palette.brandStrong else palette.mutedForeground,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            )
        }
    }
}

private fun sameDay(a: Calendar, b: Calendar): Boolean =
    a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
        a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

private fun mockTimeSlots(
    restaurantId: String,
    dayMillis: Long,
    timeRangeId: Int,
): List<RestaurantTimeSlot> {
    val labels = listOf("11:30", "12:00", "12:30", "13:00", "13:30", "17:00")
    val dayOrdinal = (dayMillis / 86_400_000L).toInt()
    val pattern = (restaurantId.hashCode() xor dayOrdinal xor (timeRangeId * 0x2FA2F65)) and 63
    return labels.mapIndexed { i, label ->
        val available = (pattern ushr i) and 1 == 1
        RestaurantTimeSlot(label = label, available = available)
    }
}
