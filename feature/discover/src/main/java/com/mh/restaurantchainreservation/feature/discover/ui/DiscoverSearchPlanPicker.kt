package com.mh.restaurantchainreservation.feature.discover.ui

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.cardSurface,
        contentColor = palette.foreground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.width(40.dp))
                Text(
                    text = "What time do you want to reserve a table for?",
                    color = palette.foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp, top = 12.dp, end = 4.dp, bottom = 4.dp),
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = palette.foreground)
                }
            }

            Spacer(Modifier.height(12.dp))

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

            Spacer(Modifier.height(16.dp))
            Text(
                text = "APPLY",
                color = RestaurantColors.Base.white,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
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
                    .padding(vertical = 16.dp),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(10.dp))
            if (showDeleteAction) {
                Text(
                    text = "Delete",
                    color = palette.mutedForeground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            dateOffset = 0
                            time24 = "19:00"
                            partySize = 2
                            onApply(SearchPlanState())
                            onDismiss()
                        }
                        .padding(vertical = 14.dp),
                    textAlign = TextAlign.Center,
                )
            } else {
                Text(
                    text = "Skip",
                    color = palette.mutedForeground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onDismiss)
                        .padding(vertical = 14.dp),
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
        ) {
            HorizontalDivider(color = PlanPickerDividerColor, thickness = 1.dp)
            Spacer(Modifier.height(PlanPickerRowHeight))
            HorizontalDivider(color = PlanPickerDividerColor, thickness = 1.dp)
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
    val palette = LocalRestaurantPalette.current
    val listState = rememberLazyListState()
    val centerPaddingRows = 1
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

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
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PlanPickerRowHeight)
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (selected) palette.foreground else palette.mutedForeground.copy(alpha = 0.5f),
                    fontSize = if (selected) 17.sp else 15.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}
