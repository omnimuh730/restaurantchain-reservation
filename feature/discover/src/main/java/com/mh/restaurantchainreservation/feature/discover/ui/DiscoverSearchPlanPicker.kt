package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

internal data class SearchPlanState(
    val dateOffset: Int = 0,
    val dateLabel: String = "Tonight",
    val time24: String = "19:00",
    val partySize: Int = 2,
) {
    fun summary(): String = formatPlanSummary(dateLabel, time24, partySize)
}

internal enum class PlanPickerColumn { Date, Time, Guests }

@Composable
internal fun PlanPickerSheet(
    visible: Boolean,
    initial: SearchPlanState,
    onDismiss: () -> Unit,
    onApply: (SearchPlanState) -> Unit,
) {
    if (!visible) return
    val palette = LocalRestaurantPalette.current
    var dateOffset by remember(visible) { mutableIntStateOf(initial.dateOffset) }
    var time24 by remember(visible) { mutableStateOf(initial.time24) }
    var partySize by remember(visible) { mutableIntStateOf(initial.partySize) }

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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(palette.cardSurface)
                    .clickable(enabled = false) {}
                    .padding(bottom = 24.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(Color(0xFFD1D1D1)),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
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
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(horizontal = 8.dp),
                ) {
                    val dateIdx = dateRows.indexOfFirst { it.second == dateOffset }.coerceAtLeast(0)
                    val timeIdx = DiscoverSearchData.planTimeSlots.indexOf(time24).let { if (it < 0) 6 else it }
                    val guestIdx = (partySize - 1).coerceIn(0, 9)
                    PlanWheel(
                        labels = dateRows.map { it.first },
                        selectedIndex = dateIdx,
                        onSelect = { idx -> dateOffset = dateRows[idx].second },
                        modifier = Modifier.weight(1f),
                    )
                    PlanWheel(
                        labels = DiscoverSearchData.planTimeSlots.map { formatReservationTime(it) },
                        selectedIndex = timeIdx,
                        onSelect = { idx -> time24 = DiscoverSearchData.planTimeSlots[idx] },
                        modifier = Modifier.weight(1f),
                    )
                    PlanWheel(
                        labels = DiscoverSearchData.guestOptions.map { n -> if (n == 1) "1 guest" else "$n guests" },
                        selectedIndex = guestIdx,
                        onSelect = { idx -> partySize = DiscoverSearchData.guestOptions[idx] },
                        modifier = Modifier.weight(1f),
                    )
                }
                Text(
                    text = "APPLY",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(palette.brandStrong)
                        .clickable {
                            val label = dateRows.find { it.second == dateOffset }?.first ?: "Tonight"
                            onApply(SearchPlanState(dateOffset = dateOffset, dateLabel = label, time24 = time24, partySize = partySize))
                            onDismiss()
                        }
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun PlanWheel(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val listState = rememberLazyListState()
    LaunchedEffect(selectedIndex, labels.size) {
        listState.scrollToItem(selectedIndex.coerceIn(0, (labels.size - 1).coerceAtLeast(0)))
    }
    LazyColumn(
        state = listState,
        modifier = modifier
            .padding(vertical = 8.dp)
            .border(1.dp, palette.borderSoft, RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        itemsIndexed(labels) { index, label ->
            val sel = index == selectedIndex
            Text(
                text = label,
                color = if (sel) palette.foreground else palette.mutedForeground,
                fontSize = if (sel) 17.sp else 15.sp,
                fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(index) }
                    .padding(vertical = 10.dp, horizontal = 4.dp),
            )
        }
    }
}
