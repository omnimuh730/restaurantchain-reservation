package com.mh.restaurantchainreservation.feature.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material.icons.outlined.WineBar
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.Restaurant
import java.time.LocalDate

@Composable
internal fun BookingDateStep(
    days: List<BookingDayRow>,
    guests: Int,
    onGuestsChange: (Int) -> Unit,
    selectedDateIndex: Int,
    onSelectDate: (Int) -> Unit,
    customDate: LocalDate?,
    onCustomDateClick: () -> Unit,
    selectedTime: String?,
    onSelectTime: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        BookingCard {
            BookingSectionTitle(
                icon = Icons.Outlined.Group,
                title = "Party size",
                subtitle = "Choose how many seats you need.",
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GuestStepperButton(icon = Icons.Outlined.Remove, filled = false) {
                    onGuestsChange((guests - 1).coerceAtLeast(1))
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 28.dp),
                ) {
                    Text(
                        text = guests.toString(),
                        color = palette.foreground,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = if (guests == 1) "guest" else "guests",
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                    )
                }
                GuestStepperButton(icon = Icons.Filled.Add, filled = true) {
                    onGuestsChange((guests + 1).coerceAtMost(20))
                }
            }
        }

        Column {
            BookingSectionTitle(
                icon = Icons.Outlined.CalendarMonth,
                title = "Date",
                subtitle = "Swipe to see more available dates.",
            )
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(days) { index, day ->
                    DateChip(
                        top = day.label,
                        middle = day.date.toString(),
                        bottom = day.month,
                        selected = selectedDateIndex == index,
                        onClick = { onSelectDate(index) },
                    )
                }
                item {
                    DateChip(
                        top = customDate?.dayOfWeek?.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault()) ?: "Custom",
                        middle = if (customDate != null) customDate.dayOfMonth.toString() else "•",
                        bottom = customDate?.month?.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault()) ?: "Pick",
                        selected = selectedDateIndex == -1 && customDate != null,
                        onClick = onCustomDateClick,
                    )
                }
            }
        }

        Column {
            BookingSectionTitle(
                icon = Icons.Outlined.AccessTime,
                title = "Time",
                subtitle = "Pick the reservation start time.",
            )
            Spacer(Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(220.dp),
            ) {
                items(TIME_SLOTS) { time ->
                    TimeChip(time = time, selected = selectedTime == time, onClick = { onSelectTime(time) })
                }
            }
        }
    }
}

@Composable
private fun GuestStepperButton(icon: ImageVector, filled: Boolean, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .then(
                if (filled) {
                    Modifier.background(palette.brand)
                } else {
                    Modifier
                        .background(palette.cardSurface)
                        .border(1.dp, palette.border, CircleShape)
                },
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = if (filled) "Increase" else "Decrease",
            tint = if (filled) Color.White else palette.foreground,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun DateChip(
    top: String,
    middle: String,
    bottom: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(68.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) palette.brand else palette.cardSurface)
            .border(1.dp, if (selected) palette.brand else palette.border, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
    ) {
        Text(top, color = if (selected) Color.White else palette.foreground, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        Text(
            middle,
            color = if (selected) Color.White else palette.foreground,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 2.dp),
        )
        Text(bottom, color = if (selected) Color.White else palette.foreground, fontSize = 11.sp)
    }
}

@Composable
private fun TimeChip(time: String, selected: Boolean, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = time,
        color = if (selected) Color.White else palette.foreground,
        fontSize = 13.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) palette.brand else palette.cardSurface)
            .border(1.dp, if (selected) palette.brand else palette.border, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
    )
}

@Composable
internal fun BookingDetailsStep(
    name: String,
    phone: String,
    notes: String,
    onNotesChange: (String) -> Unit,
    occasion: String?,
    onOccasionSelect: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        BookingCard {
            BookingSectionTitle(
                icon = Icons.Outlined.AutoAwesome,
                title = "Contact",
                subtitle = "Pulled from your profile.",
            )
            Spacer(Modifier.height(12.dp))
            ContactRow("Full name", name)
            Spacer(Modifier.height(8.dp))
            ContactRow("Phone", phone)
        }

        Column {
            BookingSectionTitle(
                icon = Icons.Outlined.AutoAwesome,
                title = "Occasion",
                subtitle = "This helps the restaurant prepare.",
            )
            Spacer(Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp),
            ) {
                items(OCCASIONS) { option ->
                    OccasionChip(
                        label = option.label,
                        icon = occasionIcon(option.id),
                        selected = occasion == option.id,
                        onClick = { onOccasionSelect(option.id) },
                    )
                }
            }
        }

        Column {
            BookingSectionTitle(
                icon = Icons.Outlined.AutoAwesome,
                title = "Special requests",
                subtitle = "Allergies, seating, or celebration notes.",
            )
            Spacer(Modifier.height(12.dp))
            TextField(
                value = notes,
                onValueChange = onNotesChange,
                placeholder = {
                    Text(
                        "Allergies, dietary restrictions, celebrations…",
                        color = palette.mutedForeground,
                        fontSize = 14.sp,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = palette.cardSurface,
                    unfocusedContainerColor = palette.cardSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
        }
    }
}

@Composable
private fun ContactRow(label: String, value: String) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.mutedSurface.copy(alpha = 0.65f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = palette.mutedForeground, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Text(value, color = palette.foreground, fontSize = 14.sp)
    }
}

private fun occasionIcon(id: String): ImageVector = when (id) {
    "anniversary" -> Icons.Filled.Favorite
    "birthday" -> Icons.Outlined.Cake
    "date" -> Icons.Outlined.WineBar
    "business" -> Icons.Outlined.Work
    "casual" -> Icons.Outlined.Coffee
    else -> Icons.Outlined.Celebration
}

@Composable
private fun OccasionChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) palette.brand else palette.cardSurface)
            .border(1.dp, if (selected) palette.brand else palette.border, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 6.dp),
    ) {
        Icon(icon, contentDescription = null, tint = if (selected) Color.White else palette.foreground, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(6.dp))
        Text(label, color = if (selected) Color.White else palette.foreground, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BookingPreferencesStep(
    seating: List<String>,
    cuisine: List<String>,
    vibes: List<String>,
    amenities: List<String>,
    onToggleSeating: (String) -> Unit,
    onToggleCuisine: (String) -> Unit,
    onToggleVibe: (String) -> Unit,
    onToggleAmenity: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(palette.brand.copy(alpha = 0.08f))
                .border(1.dp, palette.brand.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(Icons.Outlined.Tune, contentDescription = null, tint = palette.brand, modifier = Modifier.size(18.dp))
            Text(
                "Customize the visit so the restaurant can prepare the right table, pace, and atmosphere.",
                color = palette.mutedForeground,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        PreferenceSectionBlock("Seating", "Where would you like to sit?", SEATING_OPTIONS, seating, onToggleSeating)
        PreferenceSectionBlock("Cuisine preferences", "What type of food do you enjoy?", CUISINE_PREFS, cuisine, onToggleCuisine)
        PreferenceSectionBlock("Vibe", "What is the mood for tonight?", VIBE_OPTIONS, vibes, onToggleVibe)
        PreferenceSectionBlock("Amenities", "Any special needs or requests?", AMENITY_OPTIONS, amenities, onToggleAmenity)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PreferenceSectionBlock(
    title: String,
    subtitle: String,
    options: List<PrefOption>,
    selected: List<String>,
    onToggle: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = palette.mutedForeground, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
            }
            if (selected.isNotEmpty()) {
                Text(
                    selected.size.toString(),
                    color = Color.White,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(palette.brand)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                PreferenceChip(
                    label = option.label,
                    selected = selected.contains(option.id),
                    onClick = { onToggle(option.id) },
                )
            }
        }
    }
}

@Composable
internal fun BookingConfirmStep(
    restaurant: Restaurant,
    dateStr: String,
    selectedTime: String?,
    guests: Int,
    occasionLabel: String,
    name: String,
    phone: String,
    notes: String,
    prefTags: List<String>,
    depositAmount: Int,
    totalAmount: Double,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(28.dp))
                .border(1.dp, palette.border, RoundedCornerShape(28.dp)),
        ) {
            AsyncImage(
                model = restaurant.image,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(0.75f)),
                            startY = 80f,
                        ),
                    ),
            )
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Text(restaurant.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "${restaurant.cuisine} · ${restaurant.distance}",
                    color = Color.White.copy(0.78f),
                    fontSize = 13.sp,
                )
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(0.18f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = palette.gold, modifier = Modifier.size(14.dp))
                    Text(
                        restaurant.rating.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }
        }

        BookingCard {
            Text("Reservation", color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            BookingDetailRow("Date", dateStr, Icons.Outlined.CalendarMonth)
            Spacer(Modifier.height(8.dp))
            BookingDetailRow("Time", selectedTime.orEmpty(), Icons.Outlined.AccessTime)
            Spacer(Modifier.height(8.dp))
            BookingDetailRow("Guests", "$guests people", Icons.Outlined.AutoAwesome)
            Spacer(Modifier.height(8.dp))
            BookingDetailRow("Occasion", occasionLabel, Icons.Outlined.AutoAwesome)
        }

        BookingCard {
            Text("Contact", color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            BookingDetailRow("Name", name)
            Spacer(Modifier.height(8.dp))
            BookingDetailRow("Phone", phone)
            if (notes.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                BookingDetailRow("Notes", notes)
            }
        }

        if (prefTags.isNotEmpty()) {
            BookingCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Preferences", color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${prefTags.size} selected",
                        color = palette.brand,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(palette.brand.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
                Spacer(Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    prefTags.forEach { tag ->
                        Text(
                            tag,
                            color = palette.foreground,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(palette.mutedSurface.copy(alpha = 0.55f))
                                .border(1.dp, palette.border, RoundedCornerShape(999.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }
        }

        BookingCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Wallet, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(18.dp))
                Text(
                    "Payment",
                    color = palette.foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            Spacer(Modifier.height(12.dp))
            PaymentSummaryLine("Deposit ($guests × $$DEPOSIT_PER_GUEST)", "$${fmtMoney(depositAmount.toDouble())}")
            PaymentSummaryLine("Service fee", "$${fmtMoney(SERVICE_FEE)}", muted = true)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.brand.copy(alpha = 0.08f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Total", color = palette.foreground, fontWeight = FontWeight.Medium)
                Text("$${fmtMoney(totalAmount)}", color = palette.brand, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(palette.info.copy(alpha = 0.08f))
                .border(1.dp, palette.info.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                .padding(16.dp),
        ) {
            Icon(Icons.Outlined.Info, contentDescription = null, tint = palette.info, modifier = Modifier.size(20.dp))
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text("Refund policy", color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(
                    "If the restaurant declines, you receive a full refund instantly. Once approved, the deposit is non-refundable.",
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun PaymentSummaryLine(label: String, value: String, muted: Boolean = false) {
    val palette = LocalRestaurantPalette.current
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = if (muted) palette.mutedForeground else palette.foreground, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(value, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
internal fun BookingAwaitingStep(
    restaurant: Restaurant,
    dateStr: String,
    selectedTime: String?,
    guests: Int,
    totalAmount: Double,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.size(96.dp),
                color = palette.brand,
                strokeWidth = 4.dp,
                trackColor = palette.brand.copy(alpha = 0.15f),
            )
        }
        Spacer(Modifier.height(24.dp))
        Text("Awaiting approval", color = palette.foreground, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
        Text(
            "${restaurant.name} is reviewing your reservation request.",
            color = palette.mutedForeground,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(palette.brand.copy(alpha = if (index == 0) 0.35f else 1f)),
                )
            }
        }
        Spacer(Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(palette.cardSurface)
                .border(1.dp, palette.border, RoundedCornerShape(24.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = restaurant.image,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
            )
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(restaurant.name, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(
                    "$dateStr · ${selectedTime.orEmpty()} · $guests guests",
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$${fmtMoney(totalAmount)}", color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text("Paid", color = palette.success, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BookingSuccessStep(
    restaurant: Restaurant,
    bookingId: String,
    dateStr: String,
    selectedTime: String?,
    guests: Int,
    occasionLabel: String,
    totalAmount: Double,
    prefTags: List<String>,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(palette.emeraldAccent.container),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = palette.success, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("Reservation request sent", color = palette.foreground, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        Text(
            "${restaurant.name} will review and approve or reject your request.",
            color = palette.mutedForeground,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
        Row(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "$${fmtMoney(totalAmount)} deposit paid",
                color = palette.success,
                fontSize = 13.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.emeraldAccent.container)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
            Text(
                "Pending",
                color = palette.warning,
                fontSize = 11.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.amberAccent.container)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
        Spacer(Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(palette.cardSurface)
                .border(1.dp, palette.border, RoundedCornerShape(28.dp)),
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(144.dp)) {
                AsyncImage(
                    model = restaurant.image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.75f)))),
                )
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                    Text(restaurant.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text("$dateStr · ${selectedTime.orEmpty()}", color = Color.White.copy(0.78f), fontSize = 13.sp)
                }
            }
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "CONFIRMATION",
                    color = palette.mutedForeground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Text(
                    bookingId,
                    color = palette.foreground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryTile("Date", dateStr, Modifier.weight(1f))
                    SummaryTile("Time", selectedTime.orEmpty(), Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryTile("Guests", guests.toString(), Modifier.weight(1f))
                    SummaryTile("Status", "Pending", Modifier.weight(1f))
                }
                if (occasionLabel != "None") {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        occasionLabel,
                        color = palette.foreground,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(999.dp))
                            .border(1.dp, palette.border, RoundedCornerShape(999.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
                if (prefTags.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        prefTags.forEach { tag ->
                            Text(
                                tag,
                                color = palette.brand,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(palette.brand.copy(alpha = 0.08f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryTile(label: String, value: String, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(palette.mutedSurface.copy(alpha = 0.65f))
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, color = palette.mutedForeground, fontSize = 11.sp)
        Text(value, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 2.dp))
    }
}
