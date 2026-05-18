package com.mh.restaurantchainreservation.feature.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.DiscoverData
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookTableScreen(
    restaurantId: String,
    onBack: () -> Unit,
    onNavigateToDining: () -> Unit,
    onNavigateToDiscover: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val restaurant = remember(restaurantId) {
        DiscoverData.findById(restaurantId) ?: DiscoverData.MONTHLY_BEST.first()
    }
    val days = remember { createBookingDays() }
    val bookingId = remember(restaurantId) { genBookingId(restaurantId) }

    var step by remember { mutableStateOf(BookingFlowStep.Date) }
    var guests by remember { mutableIntStateOf(2) }
    var selectedDateIndex by remember { mutableIntStateOf(0) }
    var customDate by remember { mutableStateOf<LocalDate?>(null) }
    var showCustomDatePicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    val name = remember { "Alex Chen" }
    val phone = remember { "+1 (415) 555-0142" }
    var notes by remember { mutableStateOf("") }
    var occasion by remember { mutableStateOf<String?>(null) }
    val seating = remember { mutableStateListOf<String>() }
    val cuisinePrefs = remember { mutableStateListOf<String>() }
    val vibes = remember { mutableStateListOf<String>() }
    val amenities = remember { mutableStateListOf<String>() }
    var showPaymentSheet by remember { mutableStateOf(false) }
    var paymentConfirmed by remember { mutableStateOf(false) }

    val dateStr = remember(selectedDateIndex, customDate, days) {
        formatBookingDate(selectedDateIndex, customDate, days)
    }
    val depositAmount = guests * DEPOSIT_PER_GUEST
    val totalAmount = depositAmount + SERVICE_FEE
    val prefTags = remember(seating, cuisinePrefs, vibes, amenities) {
        collectPrefLabels(seating.toList(), cuisinePrefs.toList(), vibes.toList(), amenities.toList())
    }
    val occasionLabel = remember(occasion) { occasionLabel(occasion) }
    val totalPrefs = seating.size + cuisinePrefs.size + vibes.size + amenities.size

    val stepIndex = PROGRESS_STEPS.indexOf(step).coerceAtLeast(0)
    val showHeader = step in PROGRESS_STEPS
    val showFooter = step != BookingFlowStep.Awaiting

    LaunchedEffect(step) {
        if (step == BookingFlowStep.Awaiting) {
            kotlinx.coroutines.delay(3200)
            step = BookingFlowStep.Success
        }
    }

    fun goBack() {
        when (step) {
            BookingFlowStep.Date -> onBack()
            BookingFlowStep.Details -> step = BookingFlowStep.Date
            BookingFlowStep.Preferences -> step = BookingFlowStep.Details
            BookingFlowStep.Confirm -> step = BookingFlowStep.Preferences
            else -> Unit
        }
    }

    fun toggle(list: MutableList<String>, id: String) {
        if (list.contains(id)) list.remove(id) else list.add(id)
    }

    val headerTitle = when (step) {
        BookingFlowStep.Preferences -> "Preferences"
        BookingFlowStep.Confirm -> "Review and pay"
        else -> "Book a table"
    }

    if (showCustomDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = (customDate ?: LocalDate.now())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showCustomDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = pickerState.selectedDateMillis
                        if (millis != null) {
                            customDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            selectedDateIndex = -1
                        }
                        showCustomDatePicker = false
                    },
                ) { Text("Select") }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }

    val screenBackground = when (step) {
        BookingFlowStep.Success, BookingFlowStep.Awaiting -> Color.White
        else -> palette.cardSurface
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(screenBackground)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        if (showHeader) {
            BookingStepHeader(
                restaurant = restaurant,
                stepIndex = stepIndex,
                title = headerTitle,
                onBack = ::goBack,
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (step) {
                BookingFlowStep.Date -> BookingDateStep(
                    days = days,
                    guests = guests,
                    onGuestsChange = { guests = it },
                    selectedDateIndex = selectedDateIndex,
                    onSelectDate = { selectedDateIndex = it },
                    customDate = customDate,
                    onCustomDateClick = {
                        if (customDate != null) {
                            selectedDateIndex = -1
                        } else {
                            showCustomDatePicker = true
                        }
                    },
                    selectedTime = selectedTime,
                    onSelectTime = { selectedTime = it },
                )
                BookingFlowStep.Details -> BookingDetailsStep(
                    name = name,
                    phone = phone,
                    notes = notes,
                    onNotesChange = { notes = it },
                    occasion = occasion,
                    onOccasionSelect = { occasion = it },
                )
                BookingFlowStep.Preferences -> BookingPreferencesStep(
                    seating = seating,
                    cuisine = cuisinePrefs,
                    vibes = vibes,
                    amenities = amenities,
                    onToggleSeating = { toggle(seating, it) },
                    onToggleCuisine = { toggle(cuisinePrefs, it) },
                    onToggleVibe = { toggle(vibes, it) },
                    onToggleAmenity = { toggle(amenities, it) },
                )
                BookingFlowStep.Confirm -> BookingConfirmStep(
                    restaurant = restaurant,
                    dateStr = dateStr,
                    selectedTime = selectedTime,
                    guests = guests,
                    occasionLabel = occasionLabel,
                    name = name,
                    phone = phone,
                    notes = notes,
                    prefTags = prefTags,
                    depositAmount = depositAmount,
                    totalAmount = totalAmount,
                )
                BookingFlowStep.Awaiting -> BookingAwaitingStep(
                    restaurant = restaurant,
                    dateStr = dateStr,
                    selectedTime = selectedTime,
                    guests = guests,
                    totalAmount = totalAmount,
                )
                BookingFlowStep.Success -> BookingSuccessStep(
                    restaurant = restaurant,
                    bookingId = bookingId,
                    dateStr = dateStr,
                    selectedTime = selectedTime,
                    guests = guests,
                    occasionLabel = occasionLabel,
                    totalAmount = totalAmount,
                    prefTags = prefTags,
                )
            }
        }

        if (showFooter) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = palette.borderSoft)
                    .background(palette.cardSurface.copy(alpha = 0.96f))
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                when (step) {
                    BookingFlowStep.Date -> BookingPrimaryButton(
                        text = "Continue",
                        enabled = selectedTime != null,
                        onClick = { step = BookingFlowStep.Details },
                    )
                    BookingFlowStep.Details -> BookingPrimaryButton(
                        text = "Set preferences",
                        enabled = occasion != null,
                        onClick = { step = BookingFlowStep.Preferences },
                    )
                    BookingFlowStep.Preferences -> Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        BookingOutlineButton(
                            text = "Skip",
                            onClick = { step = BookingFlowStep.Confirm },
                            modifier = Modifier.weight(1f),
                        )
                        BookingContinueWithBadge(
                            totalPrefs = totalPrefs,
                            onClick = { step = BookingFlowStep.Confirm },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    BookingFlowStep.Confirm -> ConfirmPayButton(
                        totalAmount = totalAmount,
                        onClick = { showPaymentSheet = true },
                    )
                    BookingFlowStep.Success -> {
                        BookingPrimaryButton(text = "View reservations", onClick = onNavigateToDining)
                        Spacer(Modifier.height(8.dp))
                        BookingOutlineButton(text = "Back to discover", onClick = onNavigateToDiscover)
                    }
                    else -> Unit
                }
            }
        }
    }

    BookingPaymentSheet(
        visible = showPaymentSheet,
        payTo = restaurant.name,
        payToSub = dateStr,
        guests = guests,
        totalAmount = totalAmount,
        paymentConfirmed = paymentConfirmed,
        onDismiss = {
            showPaymentSheet = false
            paymentConfirmed = false
        },
        onPaymentComplete = { paymentConfirmed = true },
    )

    LaunchedEffect(paymentConfirmed) {
        if (paymentConfirmed) {
            kotlinx.coroutines.delay(1100)
            showPaymentSheet = false
            paymentConfirmed = false
            step = BookingFlowStep.Awaiting
        }
    }
}

@Composable
private fun ConfirmPayButton(
    totalAmount: Double,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(palette.brand)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Text(
                "Confirm and pay $${fmtMoney(totalAmount)}",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun BookingContinueWithBadge(
    totalPrefs: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(palette.brand)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Continue", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            if (totalPrefs > 0) {
                Text(
                    totalPrefs.toString(),
                    color = palette.brand,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
    }
}
