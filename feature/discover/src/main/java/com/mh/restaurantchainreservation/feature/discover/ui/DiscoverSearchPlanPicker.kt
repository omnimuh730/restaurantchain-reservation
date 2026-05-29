package com.mh.restaurantchainreservation.feature.discover.ui

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.RestaurantModalBottomSheet
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlinx.coroutines.flow.distinctUntilChanged

internal data class SearchPlanState(
    val hasSelection: Boolean = false,
    val dateOffset: Int = 0,
    val dateLabel: String = "Tonight",
    val time24: String = "19:00",
    val partySize: Int = 2,
) {
    fun summary(): String = formatPlanSummary(dateLabel, time24, partySize)

    fun displaySummary(): String =
        if (hasSelection) summary() else "Select date, time & guests"

    fun dateSegmentLabel(): String = if (hasSelection) dateLabel else "Date"

    fun hourSegmentLabel(): String = if (hasSelection) formatReservationTime(time24) else "Hour"

    fun guestSegmentLabel(): String =
        if (hasSelection) {
            if (partySize == 1) "1 guest" else "$partySize guests"
        } else {
            "Pers"
        }
}

internal enum class PlanPickerColumn { Date, Time, Guests }

private val PlanPickerRowHeight = 48.dp
private val PlanPickerVisibleRows = 3
private val PlanPickerSliderHeight = PlanPickerRowHeight * PlanPickerVisibleRows
private val PlanPickerDividerColor = RestaurantColors.Neutral.dividerAlt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlanPickerSheet(
    visible: Boolean,
    initial: SearchPlanState,
    onDismiss: () -> Unit,
    onApply: (SearchPlanState) -> Unit,
) {
    if (!visible) return
    val palette = LocalRestaurantPalette.current

    var dateOffset by remember(visible, initial) { mutableIntStateOf(initial.dateOffset) }
    var time24 by remember(visible, initial) { mutableStateOf(initial.time24) }
    var partySize by remember(visible, initial) { mutableIntStateOf(initial.partySize) }

    val dateRows = remember {
        (0 until 14).map { offset ->
            val d = LocalDate.now().plusDays(offset.toLong())
            val label = when (offset) {
                0 -> "Tonight"
                1 -> "Tomorrow"
                else -> "${d.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${d.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${d.dayOfMonth}"
            }
            label to offset
        }
    }

    val showDeleteAction = initial.hasSelection

    RestaurantModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "What time do you want to reserve a table for?",
                color = palette.foreground,
                fontSize = 20.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 24.dp),
            )

            val dateLabels = dateRows.map { it.first }
            val timeLabels = DiscoverSearchData.planTimeSlots.map { formatReservationTime(it) }
            val guestLabels = DiscoverSearchData.guestOptions.map { n -> if (n == 1) "1 guest" else "$n guests" }
            val dateIdx = dateRows.indexOfFirst { it.second == dateOffset }.coerceAtLeast(0)
            val timeIdx = DiscoverSearchData.planTimeSlots.indexOf(time24).let { if (it < 0) 6 else it }
            val guestIdx = (partySize - 1).coerceIn(0, DiscoverSearchData.guestOptions.lastIndex)

            PlanTimePickerSlider(
                dateLabels = dateLabels,
                dateSelectedIndex = dateIdx,
                onDateSelect = { idx -> dateOffset = dateRows[idx].second },
                timeLabels = timeLabels,
                timeSelectedIndex = timeIdx,
                onTimeSelect = { idx -> time24 = DiscoverSearchData.planTimeSlots[idx] },
                guestLabels = guestLabels,
                guestSelectedIndex = guestIdx,
                onGuestSelect = { idx -> partySize = DiscoverSearchData.guestOptions[idx] },
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "APPLY",
                color = RestaurantColors.Base.white,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(percent = 50))
                    .background(palette.brandStrong)
                    .clickable {
                        val label = dateRows.find { it.second == dateOffset }?.first ?: "Tonight"
                        onApply(
                            SearchPlanState(
                                hasSelection = true,
                                dateOffset = dateOffset,
                                dateLabel = label,
                                time24 = time24,
                                partySize = partySize,
                            ),
                        )
                        onDismiss()
                    }
                    .padding(vertical = 12.dp),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(8.dp))

            if (showDeleteAction) {
                Text(
                    text = "Delete",
                    color = palette.mutedForeground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable {
                            dateOffset = 0
                            time24 = "19:00"
                            partySize = 2
                            onApply(SearchPlanState())
                            onDismiss()
                        }
                        .padding(vertical = 10.dp),
                    textAlign = TextAlign.Center,
                )
            } else {
                Text(
                    text = "Skip",
                    color = palette.mutedForeground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable(onClick = onDismiss)
                        .padding(vertical = 10.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun PlanTimePickerSlider(
    dateLabels: List<String>,
    dateSelectedIndex: Int,
    onDateSelect: (Int) -> Unit,
    timeLabels: List<String>,
    timeSelectedIndex: Int,
    onTimeSelect: (Int) -> Unit,
    guestLabels: List<String>,
    guestSelectedIndex: Int,
    onGuestSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(PlanPickerSliderHeight),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlanPickerWheelColumn(
                labels = dateLabels,
                selectedIndex = dateSelectedIndex,
                onSelect = onDateSelect,
                modifier = Modifier.weight(1f),
            )
            PlanPickerWheelColumn(
                labels = timeLabels,
                selectedIndex = timeSelectedIndex,
                onSelect = onTimeSelect,
                modifier = Modifier.weight(1f),
            )
            PlanPickerWheelColumn(
                labels = guestLabels,
                selectedIndex = guestSelectedIndex,
                onSelect = onGuestSelect,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PlanPickerWheelColumn(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val centerPaddingRows = 1
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val density = LocalDensity.current
    val itemHeightPx = with(density) { PlanPickerRowHeight.toPx() }

    LaunchedEffect(selectedIndex, labels.size) {
        if (labels.isEmpty()) return@LaunchedEffect
        val target = selectedIndex.coerceIn(0, labels.lastIndex)
        if (listState.firstVisibleItemIndex != target || listState.firstVisibleItemScrollOffset != 0) {
            listState.scrollToItem(target)
        }
    }

    LaunchedEffect(listState, labels.size) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { scrolling ->
                if (scrolling) return@collect
                val centered = listState.firstVisibleItemIndex.coerceIn(0, labels.lastIndex)
                if (centered != selectedIndex) {
                    onSelect(centered)
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        flingBehavior = flingBehavior,
        contentPadding = PaddingValues(vertical = PlanPickerRowHeight * centerPaddingRows),
    ) {
        itemsIndexed(labels) { index, label ->
            val interpolationFactor by remember {
                derivedStateOf {
                    val currentScroll = listState.firstVisibleItemIndex * itemHeightPx + listState.firstVisibleItemScrollOffset
                    val itemOffset = index * itemHeightPx
                    val distance = abs(currentScroll - itemOffset)
                    (1f - (distance / itemHeightPx)).coerceIn(0f, 1f)
                }
            }

            val fontSize = 14f + (15f - 14f) * interpolationFactor
            val fontWeight = FontWeight(400 + (300 * interpolationFactor).toInt())
            val color = lerp(Color(0xFFB8B8B8), Color(0xFF222222), interpolationFactor)
            val opacity = 0.6f + 0.4f * interpolationFactor
            val scale = 1.0f + 0.1f * interpolationFactor

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PlanPickerRowHeight)
                    .graphicsLayer {
                        this.scaleX = scale
                        this.scaleY = scale
                        this.alpha = opacity
                    }
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = color,
                    fontSize = fontSize.sp,
                    fontWeight = fontWeight,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}
