package com.mh.restaurantchainreservation.feature.dining.ui.addbooking

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainreservation.core.designsystem.components.RestaurantModalBottomSheet
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking
import com.mh.restaurantchainreservation.feature.dining.data.BookingStatus
import com.mh.restaurantchainreservation.feature.dining.data.DEFAULT_ADD_BOOKING_QR_TARGET
import com.mh.restaurantchainreservation.feature.dining.data.DiningStore
import com.mh.restaurantchainreservation.feature.dining.data.lookupJoinableBooking
import com.mh.restaurantchainreservation.feature.dining.ui.BookingCard
import com.mh.restaurantchainreservation.feature.dining.ui.scanqr.QRCodeVisual
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val AddBookingSheetHeightFraction = 0.88f

private data class AddBookingStepDescriptor(val labelRes: Int)

private val AddBookingSteps = listOf(
    AddBookingStepDescriptor(I18nR.string.add_booking_step_method),
    AddBookingStepDescriptor(I18nR.string.add_booking_step_verify),
    AddBookingStepDescriptor(I18nR.string.add_booking_step_review),
    AddBookingStepDescriptor(I18nR.string.add_booking_step_done),
)

private fun DiningStore.AddBookingFlowStep.toProgressIndex(): Int = when (this) {
    DiningStore.AddBookingFlowStep.ChooseMethod -> 0
    DiningStore.AddBookingFlowStep.EnterCode,
    DiningStore.AddBookingFlowStep.ScanQr,
    -> 1
    DiningStore.AddBookingFlowStep.Review -> 2
    DiningStore.AddBookingFlowStep.Success -> 3
}

/**
 * Dialog host with a rising bottom sheet for the full add-dinner flow and tab-style step progress.
 */
@Composable
fun AddBookingFlowHost(
    onDismiss: () -> Unit,
    onViewBookings: (Booking) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        AddBookingFlowSheet(
            onDismiss = onDismiss,
            onViewBookings = onViewBookings,
        )
    }
}

@Composable
private fun AddBookingFlowSheet(
    onDismiss: () -> Unit,
    onViewBookings: (Booking) -> Unit,
) {
    var step by remember { mutableStateOf(DiningStore.AddBookingFlowStep.ChooseMethod) }
    var method by remember { mutableStateOf<DiningStore.AddBookingMethod?>(null) }
    var code by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var resolvedBooking by remember { mutableStateOf<Booking?>(null) }
    var scanDetected by remember { mutableStateOf(false) }
    var addedBooking by remember { mutableStateOf<Booking?>(null) }
    var isLookingUp by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val notFoundMsg = stringResource(I18nR.string.add_code_error_not_found)
    val notActiveMsg = stringResource(I18nR.string.add_code_error_not_active)

    fun resolveFromCode(input: String): Booking? {
        val match = lookupJoinableBooking(input)
        if (match == null) {
            error = notFoundMsg
            return null
        }
        if (match.status != BookingStatus.Confirmed && match.status != BookingStatus.Pending) {
            error = notActiveMsg
            return null
        }
        error = null
        return match
    }

    fun maxReachableStepIndex(): Int = when (step) {
        DiningStore.AddBookingFlowStep.ChooseMethod -> 0
        DiningStore.AddBookingFlowStep.EnterCode,
        DiningStore.AddBookingFlowStep.ScanQr,
        -> 1
        DiningStore.AddBookingFlowStep.Review -> 2
        DiningStore.AddBookingFlowStep.Success -> 3
    }

    fun navigateToProgressIndex(index: Int) {
        if (index > maxReachableStepIndex()) return
        when (index) {
            0 -> {
                step = DiningStore.AddBookingFlowStep.ChooseMethod
                method = null
                isLookingUp = false
            }
            1 -> {
                isLookingUp = false
                when (method) {
                    DiningStore.AddBookingMethod.Scan -> {
                        scanDetected = false
                        step = DiningStore.AddBookingFlowStep.ScanQr
                    }
                    else -> step = DiningStore.AddBookingFlowStep.EnterCode
                }
            }
            2 -> if (resolvedBooking != null) {
                step = DiningStore.AddBookingFlowStep.Review
            }
            3 -> if (step == DiningStore.AddBookingFlowStep.Success) {
                step = DiningStore.AddBookingFlowStep.Success
            }
        }
    }

    fun goBack() {
        if (isLookingUp) {
            isLookingUp = false
            return
        }
        when (step) {
            DiningStore.AddBookingFlowStep.EnterCode,
            DiningStore.AddBookingFlowStep.ScanQr,
            -> navigateToProgressIndex(0)
            DiningStore.AddBookingFlowStep.Review -> navigateToProgressIndex(1)
            DiningStore.AddBookingFlowStep.Success -> onDismiss()
            else -> onDismiss()
        }
    }

    RestaurantModalBottomSheet(onDismissRequest = onDismiss) {
        val configuration = LocalConfiguration.current
        val sheetMaxHeight = (configuration.screenHeightDp * AddBookingSheetHeightFraction).dp

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(sheetMaxHeight)
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
        ) {
            AddBookingSheetTopBar(
                title = stringResource(I18nR.string.add_booking_method_title),
                showBack = step != DiningStore.AddBookingFlowStep.ChooseMethod &&
                    step != DiningStore.AddBookingFlowStep.Success,
                onBack = ::goBack,
            )
            Spacer(Modifier.height(8.dp))
            AddBookingStepProgressBar(
                currentStepIndex = step.toProgressIndex(),
                maxSelectableIndex = maxReachableStepIndex(),
                onStepSelect = ::navigateToProgressIndex,
            )
            Spacer(Modifier.height(12.dp))

            if (isLookingUp) {
                LookingUpStep(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            } else {
                AnimatedContent(
                    targetState = step,
                    label = "add_booking_sheet_step",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    transitionSpec = {
                        val forward = targetState.toProgressIndex() >= initialState.toProgressIndex()
                        val enterOffset = if (forward) { offset: Int -> offset / 5 } else { offset: Int -> -offset / 5 }
                        val exitOffset = if (forward) { offset: Int -> -offset / 5 } else { offset: Int -> offset / 5 }
                        (slideInHorizontally(tween(220), enterOffset) + fadeIn(tween(180))) togetherWith
                            (slideOutHorizontally(tween(200), exitOffset) + fadeOut(tween(160)))
                    },
                ) { currentStep ->
                    if (currentStep == DiningStore.AddBookingFlowStep.Success) {
                        SuccessStep(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                        ) {
                            when (currentStep) {
                                DiningStore.AddBookingFlowStep.ChooseMethod -> ChooseMethodStep(
                                    onSelectCode = {
                                        method = DiningStore.AddBookingMethod.Code
                                        step = DiningStore.AddBookingFlowStep.EnterCode
                                    },
                                    onSelectScan = {
                                        method = DiningStore.AddBookingMethod.Scan
                                        step = DiningStore.AddBookingFlowStep.ScanQr
                                    },
                                )
                                DiningStore.AddBookingFlowStep.EnterCode -> EnterCodeStep(
                                    code = code,
                                    error = error,
                                    onCodeChange = {
                                        code = it
                                        error = null
                                    },
                                )
                                DiningStore.AddBookingFlowStep.ScanQr -> ScanQrStep(
                                    detected = scanDetected,
                                )
                                DiningStore.AddBookingFlowStep.Review -> {
                                    resolvedBooking?.let { booking ->
                                        ReviewStep(booking = booking)
                                    }
                                }
                                else -> Unit
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            if (!isLookingUp) {
                AddBookingSheetFooter(
                    step = step,
                    code = code,
                    resolvedBooking = resolvedBooking,
                    onContinueCode = {
                        scope.launch {
                            isLookingUp = true
                            delay(1_400)
                            val match = resolveFromCode(code)
                            isLookingUp = false
                            if (match != null) {
                                resolvedBooking = match
                                step = DiningStore.AddBookingFlowStep.Review
                            }
                        }
                    },
                    onSimulateScan = {
                        scope.launch {
                            isLookingUp = true
                            delay(1_400)
                            scanDetected = true
                            resolvedBooking = DEFAULT_ADD_BOOKING_QR_TARGET
                            isLookingUp = false
                            step = DiningStore.AddBookingFlowStep.Review
                        }
                    },
                    onAddBooking = {
                        val added = DiningStore.confirmAddBooking(resolvedBooking!!)
                        addedBooking = added
                        step = DiningStore.AddBookingFlowStep.Success
                    },
                    onViewBookings = {
                        addedBooking?.let(onViewBookings)
                        onDismiss()
                    },
                )
            }
        }
    }
}

@Composable
private fun AddBookingSheetFooter(
    step: DiningStore.AddBookingFlowStep,
    code: String,
    resolvedBooking: Booking?,
    onContinueCode: () -> Unit,
    onSimulateScan: () -> Unit,
    onAddBooking: () -> Unit,
    onViewBookings: () -> Unit,
) {
    when (step) {
        DiningStore.AddBookingFlowStep.EnterCode -> {
            AddBookingPrimaryButton(
                text = stringResource(I18nR.string.add_booking_continue),
                enabled = code.trim().isNotEmpty(),
                onClick = onContinueCode,
            )
        }
        DiningStore.AddBookingFlowStep.ScanQr -> {
            AddBookingPrimaryButton(
                text = stringResource(I18nR.string.scan_button_simulate),
                onClick = onSimulateScan,
            )
        }
        DiningStore.AddBookingFlowStep.Review -> {
            if (resolvedBooking != null) {
                AddBookingPrimaryButton(
                    text = stringResource(I18nR.string.add_booking_add),
                    onClick = onAddBooking,
                )
            }
        }
        DiningStore.AddBookingFlowStep.Success -> {
            AddBookingPrimaryButton(
                text = stringResource(I18nR.string.add_booking_view),
                onClick = onViewBookings,
            )
        }
        else -> Unit
    }
}

@Composable
private fun AddBookingSheetTopBar(
    title: String,
    showBack: Boolean,
    onBack: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Box(modifier = Modifier.fillMaxWidth()) {
        if (showBack) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = palette.foreground,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        Text(
            text = title,
            color = palette.foreground,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
        )
    }
}

@Composable
private fun AddBookingStepProgressBar(
    currentStepIndex: Int,
    maxSelectableIndex: Int,
    onStepSelect: (Int) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(I18nR.string.add_booking_method_title),
                color = palette.mutedForeground,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = stringResource(AddBookingSteps[currentStepIndex.coerceIn(AddBookingSteps.indices)].labelRes),
                color = palette.brand,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            AddBookingSteps.forEachIndexed { index, step ->
                val completed = index < currentStepIndex
                val active = index == currentStepIndex
                val segmentColor = when {
                    completed -> palette.success
                    active -> palette.brand
                    else -> palette.border
                }
                val labelColor = when {
                    completed -> palette.success
                    active -> palette.brand
                    else -> palette.mutedForeground.copy(alpha = 0.45f)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(segmentColor)
                            .then(
                                if (index <= maxSelectableIndex) {
                                    Modifier.clickable(
                                        role = Role.Tab,
                                        onClickLabel = stringResource(step.labelRes),
                                        onClick = { onStepSelect(index) },
                                    )
                                } else {
                                    Modifier
                                },
                            ),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(step.labelRes),
                        color = labelColor,
                        fontSize = 10.sp,
                        fontWeight = if (active || completed) FontWeight.ExtraBold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChooseMethodStep(
    onSelectCode: () -> Unit,
    onSelectScan: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(I18nR.string.add_booking_method_subtitle),
            color = palette.mutedForeground,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )
        MethodOptionCard(
            icon = Icons.Outlined.ConfirmationNumber,
            title = stringResource(I18nR.string.add_booking_with_code),
            subtitle = stringResource(I18nR.string.add_booking_with_code_desc),
            onClick = onSelectCode,
        )
        MethodOptionCard(
            icon = Icons.Outlined.QrCodeScanner,
            title = stringResource(I18nR.string.add_booking_scan_qr),
            subtitle = stringResource(I18nR.string.add_booking_scan_qr_desc),
            onClick = onSelectScan,
        )
    }
}

@Composable
private fun MethodOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, palette.border, shape)
            .background(palette.cardSurface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(palette.brand.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = palette.brand, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = palette.mutedForeground, fontSize = 13.sp, lineHeight = 18.sp)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = palette.mutedForeground,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun LookingUpStep(modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CircularProgressIndicator(
            color = palette.brand,
            strokeWidth = 3.dp,
            modifier = Modifier.size(40.dp),
        )
        Text(
            text = stringResource(I18nR.string.add_booking_looking_up),
            color = palette.mutedForeground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun EnterCodeStep(
    code: String,
    error: String?,
    onCodeChange: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(I18nR.string.add_booking_code_prompt),
            color = palette.foreground,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
        val inputShape = RoundedCornerShape(16.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(inputShape)
                .background(palette.mutedSurface.copy(alpha = 0.55f))
                .border(1.dp, palette.border, inputShape)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = code,
                onValueChange = onCodeChange,
                singleLine = true,
                cursorBrush = SolidColor(palette.brand),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                textStyle = TextStyle(color = palette.foreground, fontSize = 16.sp),
                decorationBox = { inner ->
                    if (code.isEmpty()) {
                        Text(
                            text = stringResource(I18nR.string.add_code_placeholder),
                            color = palette.mutedForeground,
                            fontSize = 16.sp,
                        )
                    }
                    inner()
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (error != null) {
            Text(error, color = palette.destructive, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ScanQrStep(
    detected: Boolean,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(I18nR.string.add_booking_scan_prompt),
            color = palette.foreground,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(RestaurantColors.Base.black)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            QRCodeVisual(active = !detected)
        }
        if (!detected) {
            Text(
                text = stringResource(I18nR.string.scan_status_scanning),
                color = palette.mutedForeground,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun ReviewStep(booking: Booking) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(I18nR.string.add_booking_review_title),
            color = LocalRestaurantPalette.current.foreground,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
        BookingCard(
            booking = booking,
            onTap = {},
            showActions = false,
        )
    }
}

@Composable
private fun SuccessStep(modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(palette.success.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = palette.success,
                    modifier = Modifier.size(32.dp),
                )
            }
            Text(
                text = stringResource(I18nR.string.add_booking_success_message),
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 21.sp,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
    }
}

@Composable
private fun AddBookingPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    val active = enabled && !isLoading
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(if (active) palette.brand else palette.mutedSurface)
            .clickable(enabled = active, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = RestaurantColors.Base.white,
                strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp),
            )
        } else {
            Text(
                text = text,
                color = if (active) RestaurantColors.Base.white else palette.mutedForeground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
