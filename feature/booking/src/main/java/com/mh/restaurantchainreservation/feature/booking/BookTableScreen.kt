package com.mh.restaurantchainreservation.feature.booking

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.snapshotFlow
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BookTableScreen(
    restaurantId: String,
    initialState: BookTableInitialState? = null,
    onBack: () -> Unit,
    onNavigateToDining: () -> Unit,
    onNavigateToDiscover: () -> Unit,
    onBookingUpdated: ((BookTableResult) -> Unit)? = null,
    onBookingCompleted: ((String, BookTableResult) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val restaurant = remember(restaurantId) {
        DiscoverData.findById(restaurantId) ?: DiscoverData.MONTHLY_BEST.first()
    }
    val days = remember { createBookingDays() }
    val bookingId = remember(restaurantId, initialState?.existingBookingId) {
        initialState?.existingBookingId ?: genBookingId(restaurantId)
    }
    val isModifyMode = initialState?.existingBookingId != null
    var maxVisitedStepIndex by remember(isModifyMode) {
        mutableIntStateOf(if (isModifyMode) PROGRESS_STEPS.lastIndex else 0)
    }
    val accessiblePageCount = if (isModifyMode) {
        PROGRESS_STEPS.size
    } else {
        maxVisitedStepIndex + 1
    }
    val canNavigateLaterally = accessiblePageCount > 1
    var pendingPagerPage by remember { mutableIntStateOf(-1) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { accessiblePageCount },
    )

    var step by remember { mutableStateOf(BookingFlowStep.Date) }
    var isProgrammaticScroll by remember { mutableStateOf(false) }
    var guests by remember(initialState) { mutableIntStateOf(initialState?.guests ?: 2) }
    var selectedDateIndex by remember(initialState) { mutableIntStateOf(initialState?.selectedDateIndex ?: 0) }
    var customDate by remember(initialState) { mutableStateOf(initialState?.customDate) }
    var showCustomDatePicker by remember { mutableStateOf(false) }
    var selectedTime by remember(initialState) { mutableStateOf(initialState?.selectedTime) }
    val name = remember(initialState) { initialState?.contactName?.takeIf { it.isNotBlank() } ?: "Alex Chen" }
    val phone = remember(initialState) { initialState?.phone?.takeIf { it.isNotBlank() } ?: "+1 (415) 555-0142" }
    var notes by remember(initialState) { mutableStateOf(initialState?.notes.orEmpty()) }
    var occasion by remember(initialState) { mutableStateOf(initialState?.occasion) }
    val seating = remember(initialState) {
        mutableStateListOf<String>().apply { initialState?.seating?.let { addAll(it) } }
    }
    val cuisinePrefs = remember(initialState) {
        mutableStateListOf<String>().apply { initialState?.cuisinePrefs?.let { addAll(it) } }
    }
    val vibes = remember(initialState) {
        mutableStateListOf<String>().apply { initialState?.vibes?.let { addAll(it) } }
    }
    val amenities = remember(initialState) {
        mutableStateListOf<String>().apply { initialState?.amenities?.let { addAll(it) } }
    }
    var showPaymentSheet by remember { mutableStateOf(false) }
    var paymentConfirmed by remember { mutableStateOf(false) }
    var bookingSaved by remember { mutableStateOf(false) }

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
        if (!isModifyMode && step == BookingFlowStep.Awaiting) {
            kotlinx.coroutines.delay(3200)
            step = BookingFlowStep.Success
        }
    }

    LaunchedEffect(accessiblePageCount, pendingPagerPage) {
        if (pendingPagerPage < 0) return@LaunchedEffect
        val targetPage = pendingPagerPage.coerceIn(0, accessiblePageCount - 1)
        isProgrammaticScroll = true
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
        isProgrammaticScroll = false
        pendingPagerPage = -1
    }

    LaunchedEffect(accessiblePageCount) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            if (isProgrammaticScroll || pendingPagerPage >= 0 || step !in PROGRESS_STEPS) return@collect
            val newStep = PROGRESS_STEPS[page]
            if (step != newStep) {
                step = newStep
            }
        }
    }

    fun advanceToStep(nextStep: BookingFlowStep) {
        val nextIndex = PROGRESS_STEPS.indexOf(nextStep).coerceAtLeast(0)
        isProgrammaticScroll = true
        if (nextIndex > maxVisitedStepIndex) {
            maxVisitedStepIndex = nextIndex
        }
        step = nextStep
        pendingPagerPage = nextIndex
    }

    fun navigateToVisitedStep(nextStep: BookingFlowStep) {
        val nextIndex = PROGRESS_STEPS.indexOf(nextStep).coerceAtLeast(0)
        if (nextIndex > maxVisitedStepIndex) return
        isProgrammaticScroll = true
        step = nextStep
        pendingPagerPage = nextIndex
    }

    fun currentBookingDate(): LocalDate = when {
        selectedDateIndex == -1 && customDate != null -> customDate!!
        else -> days.getOrNull(selectedDateIndex)?.full ?: LocalDate.now()
    }

    fun buildBookTableResult(): BookTableResult = BookTableResult(
        bookingDate = currentBookingDate(),
        selectedTime = selectedTime.orEmpty(),
        guests = guests,
        contactName = name,
        phone = phone,
        notes = notes,
        occasion = occasion,
        seating = seating.toList(),
        cuisinePrefs = cuisinePrefs.toList(),
        vibes = vibes.toList(),
        amenities = amenities.toList(),
    )

    LaunchedEffect(step, isModifyMode) {
        if (isModifyMode || step != BookingFlowStep.Success || bookingSaved) return@LaunchedEffect
        bookingSaved = true
        onBookingCompleted?.invoke(bookingId, buildBookTableResult())
    }

    fun goBack() {
        when {
            step == BookingFlowStep.Awaiting || step == BookingFlowStep.Success -> Unit
            canNavigateLaterally && step == BookingFlowStep.Date -> onBack()
            canNavigateLaterally -> {
                val previous = PROGRESS_STEPS.getOrNull(PROGRESS_STEPS.indexOf(step) - 1)
                if (previous != null) navigateToVisitedStep(previous) else onBack()
            }
            step == BookingFlowStep.Date -> onBack()
            step == BookingFlowStep.Details -> step = BookingFlowStep.Date
            step == BookingFlowStep.Preferences -> step = BookingFlowStep.Details
            step == BookingFlowStep.Confirm -> step = BookingFlowStep.Preferences
            else -> Unit
        }
    }

    fun toggle(list: MutableList<String>, id: String) {
        if (list.contains(id)) list.remove(id) else list.add(id)
    }

    val headerTitle = when (step) {
        BookingFlowStep.Preferences -> "Preferences"
        BookingFlowStep.Confirm -> if (isModifyMode) "Review changes" else "Review and pay"
        else -> if (isModifyMode) "Modify booking" else "Book a table"
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
        BookingFlowStep.Success, BookingFlowStep.Awaiting -> RestaurantColors.Base.white
        else -> palette.cardSurface
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(screenBackground),
    ) {
        if (showHeader) {
            BookingStepHeader(
                restaurant = restaurant,
                stepIndex = stepIndex,
                title = headerTitle,
                onBack = ::goBack,
                onClose = onBack,
                onStepSelect = if (canNavigateLaterally) {
                    { index ->
                        if (index <= maxVisitedStepIndex) {
                            navigateToVisitedStep(PROGRESS_STEPS[index])
                        }
                    }
                } else {
                    null
                },
                maxSelectableStepIndex = maxVisitedStepIndex,
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (step) {
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
                else -> HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = 1,
                    userScrollEnabled = canNavigateLaterally,
                ) { page ->
                    BookingFlowStepContent(
                        step = PROGRESS_STEPS[page],
                        restaurant = restaurant,
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
                        name = name,
                        phone = phone,
                        notes = notes,
                        onNotesChange = { notes = it },
                        occasion = occasion,
                        onOccasionSelect = { occasion = it },
                        seating = seating,
                        cuisinePrefs = cuisinePrefs,
                        vibes = vibes,
                        amenities = amenities,
                        onToggleSeating = { toggle(seating, it) },
                        onToggleCuisine = { toggle(cuisinePrefs, it) },
                        onToggleVibe = { toggle(vibes, it) },
                        onToggleAmenity = { toggle(amenities, it) },
                        dateStr = dateStr,
                        occasionLabel = occasionLabel,
                        prefTags = prefTags,
                        depositAmount = depositAmount,
                        totalAmount = totalAmount,
                    )
                }
            }

            if (!showHeader) {
                BookingFlowCloseButton(
                    onClose = onBack,
                    modifier = Modifier.align(Alignment.TopEnd),
                )
            }
        }

        if (showFooter) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = palette.border)
                    .background(palette.cardSurface.copy(alpha = 0.96f))
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                when (step) {
                    BookingFlowStep.Date -> BookingPrimaryButton(
                        text = "Continue",
                        enabled = selectedTime != null,
                        onClick = { advanceToStep(BookingFlowStep.Details) },
                    )
                    BookingFlowStep.Details -> BookingPrimaryButton(
                        text = "Set preferences",
                        enabled = occasion != null,
                        onClick = { advanceToStep(BookingFlowStep.Preferences) },
                    )
                    BookingFlowStep.Preferences -> Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        BookingOutlineButton(
                            text = "Skip",
                            onClick = { advanceToStep(BookingFlowStep.Confirm) },
                            modifier = Modifier.weight(1f),
                        )
                        BookingContinueWithBadge(
                            totalPrefs = totalPrefs,
                            onClick = { advanceToStep(BookingFlowStep.Confirm) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    BookingFlowStep.Confirm -> if (isModifyMode) {
                        BookingPrimaryButton(
                            text = "Save changes",
                            onClick = {
                                onBookingUpdated?.invoke(buildBookTableResult())
                                step = BookingFlowStep.Success
                            },
                        )
                    } else {
                        ConfirmPayButton(
                            totalAmount = totalAmount,
                            onClick = { showPaymentSheet = true },
                        )
                    }
                    BookingFlowStep.Success -> if (isModifyMode) {
                        BookingPrimaryButton(text = "Back to reservation", onClick = onBack)
                    } else {
                        BookingPrimaryButton(text = "View reservations", onClick = onNavigateToDining)
                        Spacer(Modifier.height(8.dp))
                        BookingOutlineButton(text = "Back to discover", onClick = onNavigateToDiscover)
                    }
                    else -> Unit
                }
            }
        }
    }

    if (!isModifyMode) {
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
}

@Composable
private fun BookingFlowStepContent(
    step: BookingFlowStep,
    restaurant: com.mh.restaurantchainreservation.core.model.Restaurant,
    days: List<BookingDayRow>,
    guests: Int,
    onGuestsChange: (Int) -> Unit,
    selectedDateIndex: Int,
    onSelectDate: (Int) -> Unit,
    customDate: LocalDate?,
    onCustomDateClick: () -> Unit,
    selectedTime: String?,
    onSelectTime: (String) -> Unit,
    name: String,
    phone: String,
    notes: String,
    onNotesChange: (String) -> Unit,
    occasion: String?,
    onOccasionSelect: (String) -> Unit,
    seating: List<String>,
    cuisinePrefs: List<String>,
    vibes: List<String>,
    amenities: List<String>,
    onToggleSeating: (String) -> Unit,
    onToggleCuisine: (String) -> Unit,
    onToggleVibe: (String) -> Unit,
    onToggleAmenity: (String) -> Unit,
    dateStr: String,
    occasionLabel: String,
    prefTags: List<String>,
    depositAmount: Int,
    totalAmount: Double,
) {
    when (step) {
        BookingFlowStep.Date -> BookingDateStep(
            days = days,
            guests = guests,
            onGuestsChange = onGuestsChange,
            selectedDateIndex = selectedDateIndex,
            onSelectDate = onSelectDate,
            customDate = customDate,
            onCustomDateClick = onCustomDateClick,
            selectedTime = selectedTime,
            onSelectTime = onSelectTime,
        )
        BookingFlowStep.Details -> BookingDetailsStep(
            name = name,
            phone = phone,
            notes = notes,
            onNotesChange = onNotesChange,
            occasion = occasion,
            onOccasionSelect = onOccasionSelect,
        )
        BookingFlowStep.Preferences -> BookingPreferencesStep(
            seating = seating,
            cuisine = cuisinePrefs,
            vibes = vibes,
            amenities = amenities,
            onToggleSeating = onToggleSeating,
            onToggleCuisine = onToggleCuisine,
            onToggleVibe = onToggleVibe,
            onToggleAmenity = onToggleAmenity,
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
        else -> Unit
    }
}

@Composable
private fun BookingFlowCloseButton(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = 8.dp, end = 20.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(palette.mutedSurface)
            .clickable(onClick = onClose),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Filled.Close,
            contentDescription = "Close",
            tint = palette.foreground,
            modifier = Modifier.size(20.dp),
        )
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
            Icon(Icons.Filled.Check, contentDescription = null, tint = RestaurantColors.Base.white, modifier = Modifier.size(18.dp))
            Text(
                "Confirm and pay $${fmtMoney(totalAmount)}",
                color = RestaurantColors.Base.white,
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
            Text("Continue", color = RestaurantColors.Base.white, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            if (totalPrefs > 0) {
                Text(
                    totalPrefs.toString(),
                    color = palette.brand,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(RestaurantColors.Base.white)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
    }
}
