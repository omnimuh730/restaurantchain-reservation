package com.mh.restaurantchainreservation.feature.booking

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.DiscoverData
import com.mh.restaurantchainreservation.core.model.Restaurant

object BookingRoutes {
    const val RestaurantDetail: String = "discover/restaurant/{restaurantId}"
    const val BookTable: String = "discover/restaurant/{restaurantId}/book"

    fun restaurantDetail(id: String): String = "discover/restaurant/$id"
    fun bookTable(id: String): String = "discover/restaurant/$id/book"
}

private enum class DetailTab { About, Menu, Reviews, Map }

/**
 * Restaurant detail screen — collapsing hero, title row, tabbed content, and a
 * sticky `Book a table` bottom bar. Mirrors React `RestaurantDetailView.tsx`
 * with menu / reviews / about / map sub-sections.
 */
@Composable
fun RestaurantDetailScreen(
    restaurantId: String,
    onBack: () -> Unit,
    onBookNow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val restaurant = remember(restaurantId) {
        DiscoverData.findById(restaurantId) ?: DiscoverData.MONTHLY_BEST.first()
    }
    var tab by remember { mutableStateOf(DetailTab.About) }
    var saved by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(palette.cardSurface)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp),
        ) {
            item { Hero(restaurant = restaurant) }
            item { TitleBlock(restaurant = restaurant) }
            item { TabsRow(active = tab, onSelect = { tab = it }) }
            when (tab) {
                DetailTab.About -> {
                    item { AboutSection(restaurant = restaurant) }
                    item { HoursGrid() }
                    item { AddressCard(restaurant = restaurant) }
                }
                DetailTab.Menu -> {
                    item { Spacer(Modifier.height(8.dp)) }
                    items(SAMPLE_MENU, key = { it.name }) { dish -> MenuRow(dish) }
                }
                DetailTab.Reviews -> {
                    item { ReviewStats(restaurant = restaurant) }
                    items(SAMPLE_REVIEWS, key = { it.id }) { review -> ReviewCard(review) }
                }
                DetailTab.Map -> {
                    item { MapPlaceholder(restaurant = restaurant) }
                }
            }
            item { Spacer(Modifier.height(40.dp)) }
        }

        // Top overlay buttons (back + heart) sit in the safe area regardless of scroll.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleButton(
                onClick = onBack,
                content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = palette.foreground,
                    )
                },
            )
            Spacer(Modifier.weight(1f))
            CircleButton(
                onClick = { saved = !saved },
                content = {
                    Icon(
                        imageVector = if (saved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (saved) "Remove from saved" else "Save",
                        tint = if (saved) palette.brand else palette.foreground,
                    )
                },
            )
        }

        // Sticky bottom bar.
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(palette.cardSurface)
                .border(1.dp, palette.borderSoft)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "From " + restaurant.price,
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = "Tonight, 7:30 PM",
                        color = palette.foreground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(palette.brand)
                        .clickable(onClick = onBookNow)
                        .padding(horizontal = 22.dp, vertical = 14.dp),
                ) {
                    Text(
                        text = "Book a table",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun CircleButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(palette.cardSurface.copy(alpha = 0.92f))
            .border(1.dp, palette.borderSoft, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun Hero(restaurant: Restaurant) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f),
    ) {
        AsyncImage(
            model = restaurant.image,
            contentDescription = restaurant.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent, Color.Transparent),
                    ),
                ),
        )
    }
}

@Composable
private fun TitleBlock(restaurant: Restaurant) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = restaurant.name,
            color = palette.foreground,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = "%.1f".format(restaurant.rating),
                color = palette.foreground,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Dot()
            Text("${restaurant.reviews} reviews", color = palette.mutedForeground, fontSize = 12.sp)
            Dot()
            Text(restaurant.cuisine, color = palette.mutedForeground, fontSize = 12.sp, maxLines = 1)
        }
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(restaurant.price, color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Dot()
            OpenStatusChip()
            Dot()
            Icon(Icons.Outlined.Place, contentDescription = null, tint = palette.mutedForeground, modifier = Modifier.size(13.dp))
            Spacer(Modifier.size(2.dp))
            Text(restaurant.distance, color = palette.mutedForeground, fontSize = 12.sp)
        }
    }
}

@Composable
private fun OpenStatusChip() {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(palette.success.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(
            text = "Open now",
            color = palette.success,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun TabsRow(active: DetailTab, onSelect: (DetailTab) -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DetailTab.values().forEach { entry ->
            val isActive = entry == active
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (isActive) palette.foreground else palette.mutedSurface)
                    .clickable { onSelect(entry) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = entry.name,
                    color = if (isActive) palette.cardSurface else palette.foreground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun AboutSection(restaurant: Restaurant) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "About",
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "${restaurant.name} is a ${restaurant.cuisine.lowercase()} spot loved for its relaxed " +
                "atmosphere, friendly service, and a menu that balances classic favorites with seasonal " +
                "specials. The room seats around 60, with a chef's counter for solo diners.",
            color = palette.mutedForeground,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun HoursGrid() {
    val palette = LocalRestaurantPalette.current
    val rows = listOf(
        "Mon" to "11:30–22:00",
        "Tue" to "11:30–22:00",
        "Wed" to "11:30–22:00",
        "Thu" to "11:30–23:00",
        "Fri" to "11:30–23:30",
        "Sat" to "12:00–23:30",
        "Sun" to "12:00–22:00",
    )
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.mutedSurface)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = palette.foreground,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.size(6.dp))
            Text(
                text = "Hours",
                color = palette.foreground,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        rows.forEach { (day, hours) ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(day, color = palette.foreground, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text(hours, color = palette.mutedForeground, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun AddressCard(restaurant: Restaurant) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.mutedSurface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Place,
            contentDescription = null,
            tint = palette.brand,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.size(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = restaurant.area ?: "Downtown",
                color = palette.foreground,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "243 Main Street · ${restaurant.distance} from you",
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
        }
    }
}

private data class Dish(val name: String, val description: String, val price: String, val image: String)

private val SAMPLE_MENU = listOf(
    Dish(
        name = "Signature Tasting",
        description = "Five-course chef's selection rotating with the season.",
        price = "$78",
        image = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400&h=300&fit=crop",
    ),
    Dish(
        name = "House Charcuterie",
        description = "Cured meats, aged cheeses, pickles, and fresh focaccia.",
        price = "$26",
        image = "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=400&h=300&fit=crop",
    ),
    Dish(
        name = "Wood-fired Steak",
        description = "12oz prime ribeye, rosemary butter, and crispy potatoes.",
        price = "$48",
        image = "https://images.unsplash.com/photo-1546964124-0cce460f38ef?w=400&h=300&fit=crop",
    ),
    Dish(
        name = "Garden Greens",
        description = "Heirloom tomatoes, burrata, basil oil, sourdough crumbs.",
        price = "$18",
        image = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop",
    ),
)

@Composable
private fun MenuRow(dish: Dish) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.cardSurface)
            .border(1.dp, palette.borderSoft, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp)),
        ) {
            AsyncImage(
                model = dish.image,
                contentDescription = dish.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(dish.name, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(
                text = dish.description,
                color = palette.mutedForeground,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2,
            )
        }
        Spacer(Modifier.size(10.dp))
        Text(
            text = dish.price,
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

private data class Review(val id: String, val author: String, val rating: Int, val date: String, val body: String)

private val SAMPLE_REVIEWS = listOf(
    Review("r1", "Hannah", 5, "2 weeks ago", "Hands down the best meal I've had this year. The chef came over to chat and the wine pairings were spot-on."),
    Review("r2", "Marcus", 4, "1 month ago", "Great vibe and the staff knows the menu cold. The room can get loud after 8pm but the food more than makes up for it."),
    Review("r3", "Priya", 5, "1 month ago", "We came for an anniversary and they made it really special. Tasting menu is the way to go."),
)

@Composable
private fun ReviewStats(restaurant: Restaurant) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.mutedSurface)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "%.1f".format(restaurant.rating),
                color = palette.foreground,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(Modifier.size(10.dp))
            Column {
                Row {
                    repeat(5) { idx ->
                        val filled = idx < restaurant.rating.toInt()
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (filled) palette.brand else palette.borderSoft,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
                Text(
                    text = "${restaurant.reviews} reviews",
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.cardSurface)
            .border(1.dp, palette.borderSoft, RoundedCornerShape(14.dp))
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(palette.brandSoftSurface),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = review.author.take(1),
                    color = palette.brand,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.size(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(review.author, color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(review.date, color = palette.mutedForeground, fontSize = 11.sp)
            }
            Row {
                repeat(5) { idx ->
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (idx < review.rating) palette.brand else palette.borderSoft,
                        modifier = Modifier.size(13.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(review.body, color = palette.mutedForeground, fontSize = 13.sp, lineHeight = 18.sp)
    }
}

@Composable
private fun MapPlaceholder(restaurant: Restaurant) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.mutedSurface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(palette.borderSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(40.dp),
            )
        }
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = restaurant.area ?: "Downtown",
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Map preview · pinch to zoom on the live build.",
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun Dot() {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .size(3.dp)
            .clip(CircleShape)
            .background(palette.mutedForeground.copy(alpha = 0.6f)),
    )
}

/* ── BookTable ───────────────────────────────────── */

private enum class BookingStep { Date, Party, Preferences, Confirm }

/**
 * Lightweight multi-step BookTable screen — date / party size / preferences /
 * confirmation. Uses the same restaurant id as the detail page.
 */
@Composable
fun BookTableScreen(
    restaurantId: String,
    onComplete: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val restaurant = remember(restaurantId) {
        DiscoverData.findById(restaurantId) ?: DiscoverData.MONTHLY_BEST.first()
    }
    var step by remember { mutableStateOf(BookingStep.Date) }
    var selectedDate by remember { mutableStateOf("Tonight") }
    var partySize by remember { mutableStateOf(2) }
    var preference by remember { mutableStateOf("Any seating") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface)
            .windowInsetsPadding(WindowInsets.systemBars),
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
                    text = "Book a table",
                    color = palette.foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = restaurant.name,
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                )
            }
        }

        StepIndicator(step = step)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (step) {
                BookingStep.Date -> {
                    StepLabel("Pick a date")
                    listOf("Tonight", "Tomorrow", "This Friday", "Next Saturday").forEach { option ->
                        OptionRow(label = option, selected = selectedDate == option) {
                            selectedDate = option
                        }
                    }
                }
                BookingStep.Party -> {
                    StepLabel("How many guests?")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..6).forEach { count ->
                            val active = count == partySize
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (active) palette.foreground else palette.mutedSurface)
                                    .clickable { partySize = count },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = count.toString(),
                                    color = if (active) palette.cardSurface else palette.foreground,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
                BookingStep.Preferences -> {
                    StepLabel("Any preferences?")
                    listOf("Any seating", "Window seat", "Chef's counter", "Quiet area").forEach { option ->
                        OptionRow(label = option, selected = preference == option) {
                            preference = option
                        }
                    }
                }
                BookingStep.Confirm -> {
                    StepLabel("Confirm your reservation")
                    SummaryRow(label = "When", value = "$selectedDate · 7:30 PM")
                    SummaryRow(label = "Party", value = "$partySize guest" + if (partySize == 1) "" else "s")
                    SummaryRow(label = "Seating", value = preference)
                    SummaryRow(label = "Where", value = restaurant.name)
                }
            }
        }

        // Footer actions.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (step != BookingStep.Date) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(999.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(999.dp))
                        .clickable { step = previousStep(step) }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Back", color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.brand)
                    .clickable {
                        if (step == BookingStep.Confirm) onComplete() else step = nextStep(step)
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (step == BookingStep.Confirm) "Confirm" else "Continue",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private fun nextStep(current: BookingStep): BookingStep = when (current) {
    BookingStep.Date -> BookingStep.Party
    BookingStep.Party -> BookingStep.Preferences
    BookingStep.Preferences -> BookingStep.Confirm
    BookingStep.Confirm -> BookingStep.Confirm
}

private fun previousStep(current: BookingStep): BookingStep = when (current) {
    BookingStep.Date -> BookingStep.Date
    BookingStep.Party -> BookingStep.Date
    BookingStep.Preferences -> BookingStep.Party
    BookingStep.Confirm -> BookingStep.Preferences
}

@Composable
private fun StepIndicator(step: BookingStep) {
    val palette = LocalRestaurantPalette.current
    val ordered = BookingStep.values().toList()
    val currentIdx = ordered.indexOf(step)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        ordered.forEachIndexed { i, _ ->
            val active = i <= currentIdx
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (active) palette.brand else palette.borderSoft)
                    .padding(2.dp),
            )
        }
    }
}

@Composable
private fun StepLabel(text: String) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = text,
        color = palette.foreground,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun OptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) palette.brandSoftSurface else palette.mutedSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.mutedSurface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = palette.mutedForeground, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(value, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}
