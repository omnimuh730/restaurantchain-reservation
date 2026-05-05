package com.mh.restaurantchainreservation.feature.dining.ui.scanqr

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking
import com.mh.restaurantchainreservation.feature.dining.data.FullScanMenu
import com.mh.restaurantchainreservation.feature.dining.data.InitialScanMenu
import com.mh.restaurantchainreservation.feature.dining.data.ReviewTags
import com.mh.restaurantchainreservation.feature.dining.data.ScanStep
import kotlinx.coroutines.delay

/**
 * Full-screen 6-step QR flow:
 * scan → arrived → dining → bill → pay → review → all done.
 * Mirrors the React `ScanQRFlow.tsx` step machine and animations.
 */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun ScanQRFlowScreen(
    booking: Booking,
    initialStep: ScanStep,
    onCheckedIn: () -> Unit,
    onClose: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val priceFmt = remember { java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US) }

    var step by remember { mutableStateOf(initialStep) }
    var scanned by remember { mutableStateOf(false) }
    var tipPercent by remember { mutableIntStateOf(18) }
    var paymentDone by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }
    var reviewSubmitted by remember { mutableStateOf(false) }
    var allDone by remember { mutableStateOf(false) }

    val selectedTags = remember { mutableStateListOf<String>() }
    var reviewText by remember { mutableStateOf("") }
    var tasteRating by remember { mutableStateOf<Int?>(null) }
    var ambienceRating by remember { mutableStateOf<Int?>(null) }
    var serviceRating by remember { mutableStateOf<Int?>(null) }
    var valueRating by remember { mutableStateOf<Int?>(null) }

    val subtotal = 428.0
    val tax = subtotal * 0.0875
    val tip = subtotal * (tipPercent / 100.0)
    val total = subtotal + tax + tip

    val ratedSubs = listOfNotNull(tasteRating, ambienceRating, serviceRating, valueRating)
    val overallRating = if (ratedSubs.isEmpty()) 0 else (ratedSubs.average()).toInt().coerceIn(0, 5)
    val ratingLabels = listOf(
        "",
        stringResource(I18nR.string.scan_rating_label_1),
        stringResource(I18nR.string.scan_rating_label_2),
        stringResource(I18nR.string.scan_rating_label_3),
        stringResource(I18nR.string.scan_rating_label_4),
        stringResource(I18nR.string.scan_rating_label_5),
    )
    val ratingLabel = ratingLabels.getOrElse(overallRating) { "" }

    val stepIdx = ScanSteps.indexOfFirst { it.id == step }.coerceAtLeast(0)
    fun goNext() {
        if (stepIdx < ScanSteps.lastIndex) step = ScanSteps[stepIdx + 1].id
    }
    fun goPrev() {
        if (stepIdx > 0) step = ScanSteps[stepIdx - 1].id
    }

    LaunchedEffect(scanned) {
        if (scanned) {
            delay(1050L)
            step = ScanStep.Arrived
            onCheckedIn()
        }
    }

    LaunchedEffect(paymentDone) {
        if (paymentDone) {
            showConfetti = true
            delay(1700L)
            step = ScanStep.Review
            showConfetti = false
        }
    }

    LaunchedEffect(reviewSubmitted) {
        if (reviewSubmitted) {
            delay(1200L)
            allDone = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.cardSurface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .imePadding(),
    ) {
        if (allDone) {
            AllDoneScreen(
                booking = booking,
                subtotal = subtotal,
                tax = tax,
                tip = tip,
                total = total,
                onClose = onClose,
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    CircleIconButton(icon = Icons.Filled.ChevronLeft, onClick = { if (stepIdx > 0) goPrev() else onClose() })
                    Text(
                        text = stringResource(I18nR.string.scan_title),
                        color = palette.foreground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    CircleIconButton(icon = Icons.Filled.Close, onClick = onClose)
                }
                Spacer(Modifier.height(20.dp))
                StepProgressBar(currentStep = step)
                Spacer(Modifier.height(24.dp))

                AnimatedContent(
                    targetState = "$step-${if (paymentDone) "done" else "active"}-${if (reviewSubmitted) "rev" else "open"}",
                    transitionSpec = {
                        (slideInVertically { it / 6 } + fadeIn(tween(220))) togetherWith
                            (slideOutVertically { -it / 6 } + fadeOut(tween(180)))
                    },
                    label = "scan_step_transition",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) { _ ->
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .fillMaxWidth()
                            .heightIn(min = 0.dp)
                            .fillMaxHeight(),
                    ) {
                        when (step) {
                            ScanStep.Scan -> ScanStepView(
                                booking = booking,
                                scanned = scanned,
                                onSimulateScan = { scanned = true },
                            )

                            ScanStep.Arrived -> ArrivedStepView(
                                booking = booking,
                                onContinue = ::goNext,
                            )

                            ScanStep.Dining -> DiningStepView(
                                booking = booking,
                                priceFmt = priceFmt,
                                onRequestBill = { step = ScanStep.Bill },
                            )

                            ScanStep.Bill -> BillStepView(
                                subtotal = subtotal,
                                tax = tax,
                                tip = tip,
                                total = total,
                                tipPercent = tipPercent,
                                onTipPercentChange = { tipPercent = it },
                                onContinue = ::goNext,
                            )

                            ScanStep.Pay -> {
                                if (!paymentDone) {
                                    PayStepView(
                                        booking = booking,
                                        amount = total,
                                        onComplete = { paymentDone = true },
                                    )
                                } else {
                                    StepIntro(
                                        icon = Icons.Outlined.CreditCard,
                                        title = stringResource(I18nR.string.scan_intro_pay_done_title),
                                        desc = stringResource(I18nR.string.scan_intro_pay_done_desc),
                                        tone = IntroTone.Success,
                                    )
                                }
                            }

                            ScanStep.Review -> {
                                if (!reviewSubmitted) {
                                    ReviewStepView(
                                        booking = booking,
                                        priceText = priceFmt.format(total),
                                        tasteRating = tasteRating,
                                        ambienceRating = ambienceRating,
                                        serviceRating = serviceRating,
                                        valueRating = valueRating,
                                        onTasteChange = { tasteRating = it },
                                        onAmbienceChange = { ambienceRating = it },
                                        onServiceChange = { serviceRating = it },
                                        onValueChange = { valueRating = it },
                                        overallRating = overallRating,
                                        ratingLabel = ratingLabel,
                                        reviewText = reviewText,
                                        onReviewTextChange = { reviewText = it },
                                        selectedTags = selectedTags.toList(),
                                        onToggleTag = { tag ->
                                            if (selectedTags.contains(tag)) selectedTags.remove(tag)
                                            else selectedTags.add(tag)
                                        },
                                        onSubmit = { reviewSubmitted = true },
                                        onSkip = { allDone = true },
                                    )
                                } else {
                                    StepIntro(
                                        icon = Icons.Outlined.ThumbUp,
                                        title = stringResource(I18nR.string.scan_review_thanks_title),
                                        desc = stringResource(I18nR.string.scan_review_thanks_desc),
                                        tone = IntroTone.Warning,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showConfetti) {
            ConfettiEffect()
        }
    }
}

@Composable
private fun CircleIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(palette.mutedSurface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun ScanStepView(
    booking: Booking,
    scanned: Boolean,
    onSimulateScan: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        StepIntro(
            icon = Icons.Outlined.CameraAlt,
            title = stringResource(I18nR.string.scan_intro_scan_title),
            desc = stringResource(I18nR.string.scan_intro_scan_desc),
            tone = IntroTone.Primary,
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .border(1.dp, palette.border, RoundedCornerShape(28.dp))
                .background(palette.mutedSurface.copy(alpha = 0.55f))
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            QRCodeVisual(active = !scanned)
        }
        Spacer(Modifier.height(14.dp))
        // Status pill
        val pulsing = rememberInfiniteTransition(label = "pulse")
        val pulseAlpha by pulsing.animateFloat(
            initialValue = 1f,
            targetValue = 0.3f,
            animationSpec = infiniteRepeatable(animation = tween(900), repeatMode = RepeatMode.Reverse),
            label = "pulse_alpha",
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(palette.cardSurface)
                .border(1.dp, palette.border, RoundedCornerShape(percent = 50))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (scanned) palette.success else palette.brand.copy(alpha = pulseAlpha)),
            )
            Text(
                text = if (scanned) stringResource(I18nR.string.scan_status_detected) else stringResource(I18nR.string.scan_status_scanning),
                color = palette.mutedForeground,
                fontSize = 13.sp,
            )
        }
        Spacer(Modifier.height(20.dp))
        BookingMiniCard(
            image = booking.image,
            restaurant = booking.restaurant,
            date = booking.date,
            time = booking.time,
            guests = booking.guests,
            seating = booking.seating,
        )
        Spacer(Modifier.height(28.dp))
        PrimaryFlowButton(
            text = stringResource(if (scanned) I18nR.string.scan_button_detected else I18nR.string.scan_button_simulate),
            enabled = !scanned,
            icon = Icons.Outlined.CameraAlt,
            onClick = onSimulateScan,
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ArrivedStepView(booking: Booking, onContinue: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        StepIntro(
            icon = Icons.Filled.Check,
            title = stringResource(I18nR.string.scan_intro_arrived_title),
            desc = stringResource(I18nR.string.scan_intro_arrived_desc),
            tone = IntroTone.Success,
        )
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, palette.success.copy(alpha = 0.20f), RoundedCornerShape(20.dp))
                .background(palette.success.copy(alpha = 0.06f))
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ArrivedTile(
                icon = Icons.Outlined.RestaurantMenu,
                label = stringResource(I18nR.string.scan_arrived_table),
                value = "P1",
                modifier = Modifier.weight(1f),
            )
            ArrivedTile(
                icon = Icons.Outlined.Group,
                label = stringResource(I18nR.string.scan_arrived_party),
                value = "${booking.guests} ${if (booking.guests == 1) "guest" else "guests"}",
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(28.dp))
        PrimaryFlowButton(
            text = stringResource(I18nR.string.scan_arrived_continue),
            onClick = onContinue,
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ArrivedTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(palette.cardSurface)
            .padding(vertical = 12.dp, horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, null, tint = palette.brand, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(text = label, color = palette.mutedForeground, fontSize = 11.sp)
        Text(text = value, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun DiningStepView(booking: Booking, priceFmt: java.text.NumberFormat, onRequestBill: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.fillMaxWidth()) {
        StepIntro(
            icon = Icons.Outlined.RestaurantMenu,
            title = stringResource(I18nR.string.scan_intro_dining_title),
            desc = stringResource(I18nR.string.scan_intro_dining_desc),
            tone = IntroTone.Warning,
        )
        Spacer(Modifier.height(20.dp))
        BookingMiniCard(
            image = booking.image,
            restaurant = booking.restaurant,
            date = booking.date,
            time = booking.time,
            guests = booking.guests,
            seating = booking.seating,
            right = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(palette.success.copy(alpha = 0.10f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(text = stringResource(I18nR.string.scan_chip_dining), color = palette.success, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
            },
        )
        Spacer(Modifier.height(16.dp))
        BillCard(
            items = InitialScanMenu,
            subtotal = InitialScanMenu.sumOf { it.price },
            compact = true,
        )
        Spacer(Modifier.height(28.dp))
        PrimaryFlowButton(
            text = stringResource(I18nR.string.scan_request_bill),
            icon = Icons.Outlined.Receipt,
            onClick = onRequestBill,
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun BillStepView(
    subtotal: Double,
    tax: Double,
    tip: Double,
    total: Double,
    tipPercent: Int,
    onTipPercentChange: (Int) -> Unit,
    onContinue: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        StepIntro(
            icon = Icons.Outlined.Receipt,
            title = stringResource(I18nR.string.scan_intro_bill_title),
            desc = stringResource(I18nR.string.scan_intro_bill_desc),
            tone = IntroTone.Info,
        )
        Spacer(Modifier.height(20.dp))
        BillCard(
            items = FullScanMenu,
            subtotal = subtotal,
            tax = tax,
            tip = tip,
            total = total,
            tipPercent = tipPercent,
            onTipPercentChange = onTipPercentChange,
        )
        Spacer(Modifier.height(28.dp))
        PrimaryFlowButton(
            text = stringResource(I18nR.string.scan_continue_payment),
            onClick = onContinue,
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun PayStepView(booking: Booking, amount: Double, onComplete: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val priceFmt = remember { java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US) }
    Column(modifier = Modifier.fillMaxWidth()) {
        StepIntro(
            icon = Icons.Outlined.CreditCard,
            title = "Pay ${priceFmt.format(amount)}",
            desc = "${booking.restaurant} · Table P1",
            tone = IntroTone.Primary,
        )
        Spacer(Modifier.height(28.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, palette.border, RoundedCornerShape(20.dp))
                .background(palette.cardSurface)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(palette.brand.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.CreditCard, null, tint = palette.brand)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Tonight Wallet", color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Text("•••• 4521", color = palette.mutedForeground, fontSize = 12.sp)
            }
            Text(priceFmt.format(amount), color = palette.brand, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(20.dp))
        PrimaryFlowButton(
            text = stringResource(I18nR.string.scan_pay_slide, priceFmt.format(amount)),
            icon = Icons.Outlined.CreditCard,
            onClick = onComplete,
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ReviewStepView(
    booking: Booking,
    priceText: String,
    tasteRating: Int?,
    ambienceRating: Int?,
    serviceRating: Int?,
    valueRating: Int?,
    onTasteChange: (Int?) -> Unit,
    onAmbienceChange: (Int?) -> Unit,
    onServiceChange: (Int?) -> Unit,
    onValueChange: (Int?) -> Unit,
    overallRating: Int,
    ratingLabel: String,
    reviewText: String,
    onReviewTextChange: (String) -> Unit,
    selectedTags: List<String>,
    onToggleTag: (String) -> Unit,
    onSubmit: () -> Unit,
    onSkip: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.fillMaxWidth()) {
        StepIntro(
            icon = Icons.Outlined.AutoAwesome,
            title = stringResource(I18nR.string.scan_intro_review_title),
            desc = stringResource(I18nR.string.scan_intro_review_desc),
            tone = IntroTone.Warning,
        )
        Spacer(Modifier.height(16.dp))
        BookingMiniCard(
            image = booking.image,
            restaurant = booking.restaurant,
            date = booking.date,
            time = booking.time,
            guests = booking.guests,
            seating = booking.seating,
            right = {
                Text(text = priceText, color = palette.brand, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            },
        )
        Spacer(Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SubRatingRow(stringResource(I18nR.string.scan_review_taste), tasteRating, onTasteChange)
            SubRatingRow(stringResource(I18nR.string.scan_review_ambience), ambienceRating, onAmbienceChange)
            SubRatingRow(stringResource(I18nR.string.scan_review_service), serviceRating, onServiceChange)
            SubRatingRow(stringResource(I18nR.string.scan_review_value), valueRating, onValueChange)
        }
        if (overallRating > 0) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(I18nR.string.scan_review_overall, overallRating, ratingLabel),
                color = palette.warning,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, palette.border, RoundedCornerShape(20.dp))
                .background(palette.cardSurface)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            BasicTextField(
                value = reviewText,
                onValueChange = { if (it.length <= 500) onReviewTextChange(it) },
                cursorBrush = SolidColor(palette.brand),
                textStyle = TextStyle(color = palette.foreground, fontSize = 14.sp),
                decorationBox = { inner ->
                    if (reviewText.isEmpty()) {
                        Text(text = stringResource(I18nR.string.scan_review_placeholder), color = palette.mutedForeground, fontSize = 14.sp)
                    }
                    inner()
                },
                modifier = Modifier.fillMaxSize(),
            )
            Text(
                text = "${reviewText.length}/500",
                color = palette.mutedForeground.copy(alpha = 0.6f),
                fontSize = 11.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 4.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        androidx.compose.foundation.layout.FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ReviewTags.forEach { tag ->
                val selected = selectedTags.contains(tag)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(if (selected) palette.brand.copy(alpha = 0.08f) else Color.Transparent)
                        .border(1.dp, if (selected) palette.brand else palette.border, RoundedCornerShape(percent = 50))
                        .clickable { onToggleTag(tag) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = tag,
                        color = if (selected) palette.brand else palette.mutedForeground,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        PrimaryFlowButton(
            text = stringResource(I18nR.string.scan_review_submit),
            icon = Icons.Filled.Send,
            enabled = overallRating > 0,
            onClick = onSubmit,
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(percent = 50))
                .clickable(onClick = onSkip),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(I18nR.string.scan_review_skip),
                color = palette.mutedForeground,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun AllDoneScreen(
    booking: Booking,
    subtotal: Double,
    tax: Double,
    tip: Double,
    total: Double,
    onClose: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val priceFmt = remember { java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            CircleIconButton(icon = Icons.Filled.ChevronLeft, onClick = onClose)
            CircleIconButton(icon = Icons.Filled.Close, onClick = onClose)
        }
        Spacer(Modifier.height(20.dp))
        StepProgressBar(currentStep = ScanStep.Review, complete = true)
        Spacer(Modifier.height(28.dp))
        StepIntro(
            icon = Icons.Outlined.AutoAwesome,
            title = stringResource(I18nR.string.scan_done_title),
            desc = stringResource(I18nR.string.scan_done_subtitle, booking.restaurant),
            tone = IntroTone.Success,
        )
        Spacer(Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, palette.border, RoundedCornerShape(24.dp))
                .background(palette.cardSurface)
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = stringResource(I18nR.string.scan_done_receipt), color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(palette.success.copy(alpha = 0.10f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(text = stringResource(I18nR.string.scan_done_paid_chip), color = palette.success, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(Modifier.height(10.dp))
            DoneRow(stringResource(I18nR.string.scan_done_items, 8), priceFmt.format(subtotal), palette)
            DoneRow(stringResource(I18nR.string.receipt_tax), priceFmt.format(tax), palette)
            DoneRow(stringResource(I18nR.string.receipt_tip), priceFmt.format(tip), palette)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.brand.copy(alpha = 0.08f))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = stringResource(I18nR.string.scan_done_total_paid), color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = priceFmt.format(total), color = palette.brand, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, palette.border, RoundedCornerShape(24.dp))
                .background(palette.cardSurface)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(palette.warning.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.AutoAwesome, null, tint = palette.warning)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(I18nR.string.scan_done_reward_points, (total * 2).toInt()), color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = stringResource(I18nR.string.scan_done_reward_desc), color = palette.mutedForeground, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(28.dp))
        PrimaryFlowButton(
            text = stringResource(I18nR.string.scan_done_discover_more),
            onClick = onClose,
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(percent = 50))
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(I18nR.string.scan_done_back),
                color = palette.mutedForeground,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun DoneRow(label: String, value: String, palette: com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantPalette) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = palette.mutedForeground, fontSize = 13.sp)
        Text(value, color = palette.foreground, fontSize = 13.sp)
    }
}

@Composable
private fun PrimaryFlowButton(
    text: String,
    enabled: Boolean = true,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val container = if (enabled) palette.brand else palette.mutedSurface
    val content = if (enabled) Color.White else palette.mutedForeground
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(container)
            .clickable(enabled = enabled, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = content, modifier = Modifier.size(16.dp))
            Spacer(Modifier.size(8.dp))
        }
        Text(text = text, color = content, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
    }
}

