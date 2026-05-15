package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlin.math.min

private enum class TxnCategory(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val tone: Tone,
) {
    All("all", "All", Icons.Outlined.AccountBalanceWallet, Tone.Primary),
    Charge("charge", "Top ups", Icons.Outlined.Add, Tone.Success),
    Pay("pay", "Dining", Icons.Outlined.ShoppingBag, Tone.Neutral),
    Reward("reward", "Rewards", Icons.Outlined.Star, Tone.Primary),
    Referral("referral", "Referrals", Icons.AutoMirrored.Filled.Send, Tone.Blue),
    Gift("gift", "Gifts", Icons.Outlined.CardGiftcard, Tone.Success),
}

private enum class Tone { Primary, Success, Neutral, Blue }

private enum class PresetId(val id: String, val label: String) {
    Week("1w", "1W"),
    Month("1m", "1M"),
    Quarter("3m", "3M"),
    Year("1y", "1Y"),
    All("all", "All"),
    Custom("custom", "Custom"),
}

private data class DateRangeMs(val fromMs: Long, val toMs: Long, val preset: PresetId)

private data class InvoiceItem(val qty: Int, val name: String, val price: Double)

private data class Txn(
    val id: String,
    val label: String,
    val timestampMs: Long,
    val dateLabel: String,
    val timeLabel: String,
    val amountValue: Double, // positive value, sign comes from isDebit
    val isDebit: Boolean,
    val category: TxnCategory,
    val transactionId: String,
    val method: String? = null,
    val source: String? = null,
    val items: List<InvoiceItem> = emptyList(),
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val serviceFee: Double = 0.0,
    val discount: Double = 0.0,
    val restaurant: String? = null,
    val address: String? = null,
)

private const val HistoryPageSize = 20

private val MockTxns: List<Txn> by lazy { generateMockTransactions(2_000) }

@Composable
fun HistoryPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    var category by rememberSaveable { mutableStateOf(TxnCategory.All) }
    var range by rememberSaveable(stateSaver = dateRangeSaver()) {
        mutableStateOf(rangeFromPreset(PresetId.Month))
    }
    var visible by rememberSaveable { mutableIntStateOf(HistoryPageSize) }
    var loadingMore by rememberSaveable { mutableStateOf(false) }
    var openInvoice by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(category, range) {
        visible = HistoryPageSize
        loadingMore = false
    }

    val filtered = remember(category, range) {
        MockTxns.filter { txn ->
            (category == TxnCategory.All || txn.category == category) &&
                txn.timestampMs in range.fromMs..range.toMs
        }
    }
    val shown = filtered.take(visible)
    val hasMore = visible < filtered.size

    val groups = remember(shown) {
        val list = mutableListOf<Pair<String, MutableList<Txn>>>()
        for (t in shown) {
            val last = list.lastOrNull()
            if (last != null && last.first == t.dateLabel) last.second.add(t)
            else list.add(t.dateLabel to mutableListOf(t))
        }
        list.map { it.first to it.second.toList() }
    }

    val spent = filtered.filter { it.isDebit }.sumOf { it.amountValue }
    val added = filtered.filter { !it.isDebit }.sumOf { it.amountValue }
    val rewards = filtered.filter { it.category == TxnCategory.Reward || it.category == TxnCategory.Referral }
        .sumOf { it.amountValue }
    val listBottomPadding = 24.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface)
            .statusBarsPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderBar(
                title = "Activity",
                subtitle = "${filtered.size} transaction${if (filtered.size == 1) "" else "s"} in ${rangeLabel(range)}",
                onBack = onBack,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
                    .padding(top = 8.dp),
            ) {
                ActivitySummaryStatsCard(
                    spent = spent,
                    added = added,
                    rewards = rewards,
                )
                Spacer(Modifier.height(12.dp))
                ActivityStickyDateAndCategoryRow(
                    range = range,
                    onRangeChange = { range = it },
                    category = category,
                    onCategoryChange = { category = it },
                )
                Spacer(Modifier.height(8.dp))

                LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = listBottomPadding),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                item(key = "activity-count") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("${shown.size} shown", color = palette.mutedForeground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("${filtered.size} total", color = palette.mutedForeground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(10.dp))
                }

                if (groups.isEmpty()) {
                    item(key = "empty") { EmptyState() }
                } else {
                    groupedTransactionItems(groups = groups, onTap = { openInvoice = it })
                }

                item(key = "more") {
                    if (hasMore) {
                        LaunchedEffect(filtered.size, visible, loadingMore) {
                            if (!loadingMore) {
                                loadingMore = true
                                delay(420)
                                visible = min(visible + HistoryPageSize, filtered.size)
                                loadingMore = false
                            }
                        }
                        LoadingTransactionSkeleton()
                    } else if (filtered.size > HistoryPageSize) {
                        Text(
                            text = "You are all caught up.",
                            color = palette.mutedForeground,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
    }

    val openTxn = filtered.firstOrNull { it.id == openInvoice }
    if (openTxn != null) {
        InvoiceDialog(txn = openTxn, onDismiss = { openInvoice = null })
    }
}

private fun LazyListScope.groupedTransactionItems(
    groups: List<Pair<String, List<Txn>>>,
    onTap: (String) -> Unit,
) {
    groups.forEachIndexed { groupIndex, (date, items) ->
        item(key = "group-$date") {
            Column(modifier = Modifier.padding(top = if (groupIndex == 0) 0.dp else 16.dp)) {
                Text(
                    text = date.uppercase(Locale.US),
                    color = LocalRestaurantPalette.current.mutedForeground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                )
                GroupCard(items = items, onTap = onTap)
            }
        }
    }
}

@Composable
private fun LoadingTransactionSkeleton() {
    val palette = LocalRestaurantPalette.current
    val pulse by rememberInfiniteTransition(label = "history_skeleton").animateFloat(
        initialValue = 0.32f,
        targetValue = 0.76f,
        animationSpec = infiniteRepeatable(
            animation = tween(760),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "history_skeleton_alpha",
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, palette.border.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
                    .background(palette.cardSurface)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(palette.mutedSurface.copy(alpha = pulse)),
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.64f)
                            .height(13.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(palette.mutedSurface.copy(alpha = pulse)),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.38f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(palette.mutedSurface.copy(alpha = pulse * 0.75f)),
                    )
                }
                Box(
                    modifier = Modifier
                        .width(54.dp)
                        .height(13.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(palette.mutedSurface.copy(alpha = pulse)),
                )
            }
        }
    }
}

@Composable
private fun GroupCard(items: List<Txn>, onTap: (String) -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, shape, clip = false)
            .clip(shape)
            .border(1.dp, palette.border, shape)
            .background(palette.cardSurface),
    ) {
        items.forEachIndexed { i, t ->
            if (i > 0) {
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border.copy(alpha = 0.7f)))
            }
            TxnRow(txn = t, onClick = { onTap(t.id) })
        }
    }
}

@Composable
private fun HeaderBar(title: String, subtitle: String, onBack: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.fillMaxWidth().background(palette.cardSurface.copy(alpha = 0.95f))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.mutedSurface)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = palette.foreground, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = palette.foreground, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    subtitle,
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.brand.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Receipt, null, tint = palette.brand, modifier = Modifier.size(20.dp))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.border.copy(alpha = 0.4f)),
        )
    }
}

@Composable
private fun ActivitySummaryStatsCard(
    spent: Double,
    added: Double,
    rewards: Double,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(28.dp), clip = false)
            .clip(RoundedCornerShape(28.dp))
            .border(1.dp, palette.border, RoundedCornerShape(28.dp))
            .background(palette.cardSurface)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SummaryCell(label = "Spent", value = formatUsd(spent), modifier = Modifier.weight(1f))
            SummaryCell(label = "Added", value = formatUsd(added), modifier = Modifier.weight(1f))
            SummaryCell(label = "Rewards", value = formatUsd(rewards), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ActivityStickyDateAndCategoryRow(
    range: DateRangeMs,
    onRangeChange: (DateRangeMs) -> Unit,
    category: TxnCategory,
    onCategoryChange: (TxnCategory) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.cardSurface)
            .padding(top = 4.dp, bottom = 8.dp),
    ) {
        PeriodPicker(value = range, onChange = onRangeChange)
        Spacer(Modifier.height(10.dp))
        ActivityCategoryTabRow(
            selected = category,
            onSelect = onCategoryChange,
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 10.dp),
            color = palette.border.copy(alpha = 0.45f),
        )
    }
}

@Composable
private fun ActivityCategoryTabRow(
    selected: TxnCategory,
    onSelect: (TxnCategory) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = TxnCategory.entries.toList(),
            key = { it.id },
        ) { option ->
            ActivityCategoryChip(
                option = option,
                isSelected = selected == option,
                onClick = { onSelect(option) },
            )
        }
    }
}

@Composable
private fun ActivityCategoryChip(
    option: TxnCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(999.dp)
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .height(40.dp)
            .defaultMinSize(minWidth = 40.dp)
            .animateContentSize(animationSpec = spring(dampingRatio = 0.78f, stiffness = 380f))
            .clip(shape)
            .border(
                1.dp,
                if (isSelected) palette.brand else palette.border.copy(alpha = 0.65f),
                shape,
            )
            .background(if (isSelected) palette.brand.copy(alpha = 0.10f) else palette.mutedSurface.copy(alpha = 0.45f))
            .clickable(onClick = onClick)
            .padding(horizontal = if (isSelected) 12.dp else 0.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isSelected) Arrangement.Start else Arrangement.Center,
    ) {
        Icon(
            imageVector = option.icon,
            contentDescription = option.label,
            tint = if (isSelected) palette.brand else palette.mutedForeground,
            modifier = Modifier.size(18.dp),
        )
        AnimatedVisibility(
            visible = isSelected,
            enter = expandHorizontally(
                expandFrom = Alignment.Start,
                animationSpec = spring(dampingRatio = 0.82f, stiffness = 400f),
            ) + fadeIn(animationSpec = tween(durationMillis = 140, delayMillis = 20)),
            exit = shrinkHorizontally(
                shrinkTowards = Alignment.Start,
                animationSpec = tween(durationMillis = 140),
            ) + fadeOut(animationSpec = tween(durationMillis = 100)),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Spacer(Modifier.width(6.dp))
                Text(
                    text = option.label,
                    color = palette.brand,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun SummaryCell(label: String, value: String, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(palette.mutedSurface.copy(alpha = 0.6f))
            .padding(horizontal = 12.dp, vertical = 12.dp),
    ) {
        Text(label, color = palette.mutedForeground, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.4.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

// === Calendar PeriodPicker ===

@Composable
private fun PeriodPicker(value: DateRangeMs, onChange: (DateRangeMs) -> Unit) {
    val palette = LocalRestaurantPalette.current
    var open by remember { mutableStateOf(false) }
    var draftFrom by remember { mutableStateOf<Long?>(value.fromMs) }
    var draftTo by remember { mutableStateOf<Long?>(value.toMs) }
    var selecting by remember { mutableStateOf(SelectingEdge.From) }
    var draftIsCustom by remember { mutableStateOf(value.preset == PresetId.Custom) }
    var month by remember {
        mutableStateOf(monthAnchor(value.fromMs))
    }

    LaunchedEffect(open) {
        if (open) {
            draftFrom = value.fromMs
            draftTo = value.toMs
            month = monthAnchor(value.fromMs)
            selecting = SelectingEdge.From
            draftIsCustom = value.preset == PresetId.Custom
        }
    }

    val rotation by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (open) 90f else 0f,
        animationSpec = tween(180),
        label = "chev",
    )

    Column {
        // Trigger
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(percent = 50), clip = false)
                .clip(RoundedCornerShape(percent = 50))
                .background(palette.cardSurface)
                .border(
                    1.dp,
                    if (open) palette.brand else palette.border,
                    RoundedCornerShape(percent = 50),
                )
                .clickable { open = !open }
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Outlined.CalendarMonth, null, tint = palette.brand, modifier = Modifier.size(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "DATES",
                    color = palette.mutedForeground,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = rangeLabel(value),
                    color = palette.foreground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(palette.brand.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.ChevronRight,
                    null,
                    tint = palette.brand,
                    modifier = Modifier.size(16.dp).rotate(rotation),
                )
            }
        }

        AnimatedVisibility(
            visible = open,
            enter = fadeIn(tween(180)) + slideInVertically(tween(220)) { -it / 6 },
            exit = fadeOut(tween(140)) + slideOutVertically(tween(140)) { -it / 6 },
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(28.dp), clip = false)
                    .clip(RoundedCornerShape(28.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(28.dp))
                    .background(palette.cardSurface),
            ) {
                // Preset row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.mutedSurface.copy(alpha = 0.5f))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PresetId.entries.filter { it != PresetId.Custom }.forEach { p ->
                        val isActive = !draftIsCustom && value.preset == p
                        PresetChip(
                            label = p.label,
                            active = isActive,
                            onClick = {
                                draftIsCustom = false
                                onChange(rangeFromPreset(p))
                                open = false
                            },
                        )
                    }
                }
                // From / To
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    EdgeIndicator(
                        label = "FROM",
                        value = draftFrom?.let { formatShortDate(it) } ?: "Add date",
                        active = selecting == SelectingEdge.From,
                        emphasized = draftFrom != null,
                        onClick = {
                            draftIsCustom = true
                            selecting = SelectingEdge.From
                        },
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        null,
                        tint = palette.border,
                        modifier = Modifier.size(20.dp),
                    )
                    EdgeIndicator(
                        label = "TO",
                        value = draftTo?.let { formatShortDate(it) } ?: "Add date",
                        active = selecting == SelectingEdge.To,
                        emphasized = draftTo != null,
                        onClick = {
                            if (draftFrom != null) {
                                draftIsCustom = true
                                selecting = SelectingEdge.To
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                // Calendar
                CalendarGrid(
                    month = month,
                    onPrev = { month = monthShift(month, -1) },
                    onNext = { month = monthShift(month, 1) },
                    fromMs = draftFrom,
                    toMs = draftTo,
                    onPick = { picked ->
                        val pf = draftFrom
                        val pt = draftTo
                        draftIsCustom = true
                        if (selecting == SelectingEdge.From || pf == null || pt != null) {
                            draftFrom = picked
                            draftTo = null
                            selecting = SelectingEdge.To
                        } else if (picked < pf) {
                            draftFrom = picked
                            draftTo = null
                            selecting = SelectingEdge.To
                        } else {
                            draftTo = picked
                            selecting = SelectingEdge.From
                        }
                    },
                )
                // Actions
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(palette.border.copy(alpha = 0.7f)),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Clear dates",
                        color = palette.brand,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clickable {
                                draftFrom = null
                                draftTo = null
                                draftIsCustom = true
                                selecting = SelectingEdge.From
                            }
                            .padding(vertical = 4.dp),
                    )
                    val canSave = draftFrom != null && draftTo != null
                    Box(
                        modifier = Modifier
                            .heightIn(min = 44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (canSave) palette.brand else palette.mutedSurface)
                            .clickable(enabled = canSave) {
                                val f = draftFrom!!
                                val t = endOfDay(draftTo!!)
                                onChange(DateRangeMs(f, t, PresetId.Custom))
                                open = false
                            }
                            .padding(horizontal = 22.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Save",
                            color = if (canSave) Color.White else palette.mutedForeground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
        }
    }
}

private enum class SelectingEdge { From, To }

@Composable
private fun PresetChip(label: String, active: Boolean, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(if (active) palette.brand else palette.cardSurface)
            .border(1.dp, if (active) palette.brand else palette.border, RoundedCornerShape(percent = 50))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            label,
            color = if (active) Color.White else palette.foreground,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun EdgeIndicator(
    label: String,
    value: String,
    active: Boolean,
    emphasized: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val borderColor = if (active) palette.brand else palette.border
    val bg = if (active) palette.brand.copy(alpha = 0.05f) else palette.cardSurface
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(label, color = palette.mutedForeground, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            color = if (emphasized) palette.foreground else palette.mutedForeground,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun CalendarGrid(
    month: Long,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    fromMs: Long?,
    toMs: Long?,
    onPick: (Long) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val cal = Calendar.getInstance().apply { timeInMillis = month }
    val monthLabel = SimpleDateFormat("MMMM yyyy", Locale.US).format(cal.time)

    val days = remember(month) { generateMonthDays(month) }
    val today = startOfDay(System.currentTimeMillis())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(palette.brand.copy(alpha = 0.08f))
                    .clickable(onClick = onPrev),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.ChevronLeft, null, tint = palette.foreground, modifier = Modifier.size(18.dp))
            }
            Text(monthLabel, color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(palette.brand.copy(alpha = 0.08f))
                    .clickable(onClick = onNext),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.ChevronRight, null, tint = palette.foreground, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach {
                Text(
                    text = it,
                    color = palette.mutedForeground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).padding(vertical = 6.dp),
                )
            }
        }
        // 6 rows of 7 days
        val rows = days.chunked(7)
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { day ->
                    DayCell(
                        day = day,
                        anchorMonth = cal.get(Calendar.MONTH),
                        anchorYear = cal.get(Calendar.YEAR),
                        fromMs = fromMs,
                        toMs = toMs,
                        today = today,
                        onPick = { onPick(day.startMs) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

private data class DayInfo(val startMs: Long, val month: Int, val year: Int, val day: Int)

@Composable
private fun DayCell(
    day: DayInfo,
    anchorMonth: Int,
    anchorYear: Int,
    fromMs: Long?,
    toMs: Long?,
    today: Long,
    onPick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val outside = (day.month != anchorMonth) || (day.year != anchorYear)
    val isFrom = fromMs != null && fromMs == day.startMs
    val isTo = toMs != null && toMs == startOfDay(toMs)
    val isToInDay = toMs != null && day.startMs == startOfDay(toMs)
    val isEndpoint = isFrom || isToInDay
    val isInRange = fromMs != null && toMs != null && day.startMs > fromMs && day.startMs < startOfDay(toMs)
    val isToday = day.startMs == today

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(enabled = !outside, onClick = onPick),
        contentAlignment = Alignment.Center,
    ) {
        if (isInRange || isEndpoint) {
            val rangeBg = palette.mutedSurface
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(rangeBg),
            )
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isEndpoint) palette.brand else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = day.day.toString(),
                color = when {
                    isEndpoint -> Color.White
                    outside -> palette.mutedForeground.copy(alpha = 0.3f)
                    isToday -> palette.brand
                    else -> palette.foreground
                },
                fontSize = 13.sp,
                fontWeight = if (isEndpoint || isToday) FontWeight.Bold else FontWeight.Medium,
                textDecoration = if (isToday && !isEndpoint) TextDecoration.Underline else TextDecoration.None,
            )
        }
    }
}

// === Row + Empty ===

@Composable
private fun TxnRow(txn: Txn, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val (bg, fg) = toneColors(txn.category.tone, palette)
    val positive = !txn.isDebit
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(txn.category.icon, null, tint = fg, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                txn.label,
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(txn.timeLabel, color = palette.mutedForeground, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .clip(CircleShape)
                        .background(palette.border),
                )
                Text(txn.category.label, color = palette.mutedForeground, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Text(
            text = formatSignedUsd(txn.amountValue, positive = positive),
            color = if (positive) Color(0xFF059669) else palette.foreground,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Icon(Icons.Filled.ChevronRight, null, tint = palette.mutedForeground, modifier = Modifier.size(16.dp))
    }
}

private fun toneColors(tone: Tone, palette: com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantPalette): Pair<Color, Color> = when (tone) {
    Tone.Primary -> palette.brand.copy(alpha = 0.10f) to palette.brand
    Tone.Success -> palette.emeraldAccent.container to palette.emeraldAccent.onContainer
    Tone.Blue -> palette.blueAccent.container to palette.blueAccent.onContainer
    Tone.Neutral -> palette.mutedSurface to palette.foreground
}

@Composable
private fun EmptyState() {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(1.dp, palette.border, CircleShape)
                .background(palette.mutedSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Receipt, null, tint = palette.mutedForeground, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text("No transactions found", color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Try changing the date range or category.",
            color = palette.mutedForeground,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
    }
}

// === Invoice Modal ===

@Composable
private fun InvoiceDialog(txn: Txn, onDismiss: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp)
                .heightIn(max = 720.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(palette.cardSurface)
                .clickable(enabled = false) {}
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Transaction Receipt", color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(palette.mutedSurface)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Close, null, tint = palette.foreground, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(Modifier.height(20.dp))

            // Amount and check
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(6.dp, CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(if (!txn.isDebit && txn.category == TxnCategory.Reward) Color(0xFFFF5A5F) else Color.Black)
                    .padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = when {
                        txn.isDebit -> Icons.Outlined.Check
                        txn.category == TxnCategory.Reward -> Icons.Outlined.Star
                        else -> Icons.Outlined.Add
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = formatSignedUsd(txn.amountValue, positive = !txn.isDebit),
                color = when {
                    !txn.isDebit && txn.category == TxnCategory.Reward -> Color(0xFFFF5A5F)
                    else -> palette.foreground
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = when {
                    txn.isDebit -> "Payment Successful"
                    txn.category == TxnCategory.Reward -> "Reward Earned"
                    txn.category == TxnCategory.Referral -> "Referral Bonus"
                    txn.category == TxnCategory.Gift -> "Gift Received"
                    else -> "Top Up Successful"
                },
                color = palette.mutedForeground,
                fontSize = 13.sp,
            )

            Spacer(Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border.copy(alpha = 0.7f)))
            Spacer(Modifier.height(16.dp))

            if (txn.isDebit && txn.items.isNotEmpty()) {
                // Restaurant card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.mutedSurface)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(Icons.Outlined.LocationOn, null, tint = palette.foreground, modifier = Modifier.size(18.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            txn.restaurant ?: txn.label,
                            color = palette.foreground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        if (txn.address != null) {
                            Text(
                                txn.address,
                                color = palette.mutedForeground,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "ORDER SUMMARY",
                    color = palette.mutedForeground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                txn.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("${item.qty}x", color = palette.mutedForeground, fontSize = 13.sp)
                            Text(item.name, color = palette.foreground, fontSize = 13.sp)
                        }
                        Text("$" + "%.2f".format(item.price), color = palette.foreground, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border.copy(alpha = 0.5f)))
                Spacer(Modifier.height(10.dp))
                ReceiptLine("Subtotal", "$" + "%.2f".format(txn.subtotal))
                ReceiptLine("Tax", "$" + "%.2f".format(txn.tax))
                if (txn.serviceFee > 0) ReceiptLine("Service Fee", "$" + "%.2f".format(txn.serviceFee))
                if (txn.discount > 0) ReceiptLine("Discount", "-$" + "%.2f".format(txn.discount), valueColor = palette.success)
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border.copy(alpha = 0.7f)))
                Spacer(Modifier.height(12.dp))
            }

            ReceiptLine("Date", "${txn.dateLabel}, ${txn.timeLabel}")
            txn.method?.let { ReceiptLine(if (txn.isDebit) "Paid From" else "Via", it) }
            txn.source?.let { ReceiptLine("Source", it) }
            ReceiptLine("Transaction ID", txn.transactionId)

            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp), clip = false)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Text("Done", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun ReceiptLine(label: String, value: String, valueColor: Color? = null) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = palette.mutedForeground, fontSize = 13.sp)
        Text(value, color = valueColor ?: palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

// === Helpers ===

private fun formatUsd(value: Double): String = "$" + "%,.2f".format(value)

private fun formatSignedUsd(value: Double, positive: Boolean): String {
    val abs = "%,.2f".format(kotlin.math.abs(value))
    return (if (positive) "+" else "-") + "$" + abs
}

private fun monthAnchor(ms: Long): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = ms
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

private fun monthShift(month: Long, delta: Int): Long {
    val cal = Calendar.getInstance().apply { timeInMillis = month }
    cal.add(Calendar.MONTH, delta)
    return cal.timeInMillis
}

private fun startOfDay(ms: Long): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = ms
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

private fun endOfDay(ms: Long): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = ms
        set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
    }
    return cal.timeInMillis
}

private fun generateMonthDays(monthStart: Long): List<DayInfo> {
    val cal = Calendar.getInstance().apply { timeInMillis = monthStart }
    val firstWeekday = cal.get(Calendar.DAY_OF_WEEK) - 1 // Sunday=0
    cal.add(Calendar.DAY_OF_MONTH, -firstWeekday)
    val days = mutableListOf<DayInfo>()
    repeat(42) {
        days.add(
            DayInfo(
                startMs = cal.timeInMillis,
                month = cal.get(Calendar.MONTH),
                year = cal.get(Calendar.YEAR),
                day = cal.get(Calendar.DAY_OF_MONTH),
            ),
        )
        cal.add(Calendar.DAY_OF_MONTH, 1)
    }
    return days
}

private fun rangeFromPreset(p: PresetId): DateRangeMs {
    val now = System.currentTimeMillis()
    val today = startOfDay(now)
    val to = endOfDay(now)
    return when (p) {
        PresetId.Week -> DateRangeMs(today - 6L * 86_400_000L, to, p)
        PresetId.Month -> DateRangeMs(today - 29L * 86_400_000L, to, p)
        PresetId.Quarter -> DateRangeMs(today - 89L * 86_400_000L, to, p)
        PresetId.Year -> DateRangeMs(today - 364L * 86_400_000L, to, p)
        PresetId.All -> {
            val cal = Calendar.getInstance().apply { timeInMillis = now; add(Calendar.YEAR, -5) }
            DateRangeMs(cal.timeInMillis, to, p)
        }
        PresetId.Custom -> DateRangeMs(today - 29L * 86_400_000L, to, p)
    }
}

private fun rangeLabel(range: DateRangeMs): String {
    if (range.preset != PresetId.Custom) {
        return when (range.preset) {
            PresetId.Week -> "7 days"
            PresetId.Month -> "1 month"
            PresetId.Quarter -> "3 months"
            PresetId.Year -> "1 year"
            PresetId.All -> "All time"
            PresetId.Custom -> "Custom"
        }
    }
    val sameYear = sameYear(range.fromMs, range.toMs)
    val fromFmt = if (sameYear) "MMM d" else "MMM d, yyyy"
    val toFmt = "MMM d, yyyy"
    val from = SimpleDateFormat(fromFmt, Locale.US).format(Date(range.fromMs))
    val to = SimpleDateFormat(toFmt, Locale.US).format(Date(range.toMs))
    return "$from \u2013 $to"
}

private fun formatShortDate(ms: Long): String =
    SimpleDateFormat("MMM d", Locale.US).format(Date(ms))

private fun sameYear(a: Long, b: Long): Boolean {
    val ca = Calendar.getInstance().apply { timeInMillis = a }
    val cb = Calendar.getInstance().apply { timeInMillis = b }
    return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR)
}

private fun dateRangeSaver() = androidx.compose.runtime.saveable.Saver<DateRangeMs, List<Any>>(
    save = { listOf(it.fromMs, it.toMs, it.preset.name) },
    restore = {
        DateRangeMs(
            fromMs = it[0] as Long,
            toMs = it[1] as Long,
            preset = PresetId.valueOf(it[2] as String),
        )
    },
)

// === Mock generation ===

private fun generateMockTransactions(count: Int): List<Txn> {
    var seed = 12345L
    fun rand(): Double {
        seed = (seed * 1664525L + 1013904223L) and 0xFFFFFFFFL
        return seed.toDouble() / 0x100000000L.toDouble()
    }
    fun <T> pick(arr: List<T>): T = arr[(rand() * arr.size).toInt().coerceIn(0, arr.size - 1)]

    val restaurants = listOf(
        RestaurantInfo("Sakura Omakase", "456 Sushi Lane, San Francisco, CA 94110", listOf(InvoiceItem(1, "Omakase Set", 38.00), InvoiceItem(1, "Green Tea", 3.50))),
        RestaurantInfo("Bella Napoli", "789 Pizza St, San Francisco, CA 94102", listOf(InvoiceItem(1, "Margherita Pizza", 18.00), InvoiceItem(1, "Tiramisu", 8.00))),
        RestaurantInfo("Le Petit Bistro", "234 Bistro Ave, San Francisco, CA 94115", listOf(InvoiceItem(1, "Steak Frites", 42.00), InvoiceItem(1, "Red Wine", 15.00))),
        RestaurantInfo("Taco Fiesta", "567 Taco Blvd, San Francisco, CA 94103", listOf(InvoiceItem(1, "Taco Trio", 15.00), InvoiceItem(1, "Guacamole", 4.00))),
        RestaurantInfo("Gangnam BBQ", "120 Korea Way, San Francisco, CA 94108", listOf(InvoiceItem(1, "Wagyu Set", 48.00), InvoiceItem(1, "Banchan", 6.00))),
        RestaurantInfo("Saigon Pho", "88 Mission St, San Francisco, CA 94105", listOf(InvoiceItem(1, "Pho Bowl", 16.00), InvoiceItem(1, "Spring Rolls", 7.00))),
        RestaurantInfo("Verde Trattoria", "321 Vine St, San Francisco, CA 94117", listOf(InvoiceItem(1, "Truffle Pasta", 32.00), InvoiceItem(1, "Affogato", 9.00))),
        RestaurantInfo("The Burger Lab", "12 Market St, San Francisco, CA 94103", listOf(InvoiceItem(1, "Lab Burger", 17.50), InvoiceItem(1, "Truffle Fries", 8.00))),
    )
    val topupMethods = listOf("Apple Pay", "Google Pay", "PayPal", "Bank Transfer", "VISA \u2022\u20224242")
    val rewardSources = listOf("10% cashback", "Weekly streak bonus", "First-of-month bonus", "Tier upgrade reward")
    val referralNames = listOf("Maria Rodriguez", "Daniel Park", "Aiko Sato", "Chris Donovan", "Renee Cho")
    val giftSenders = listOf("Maria Rodriguez", "Jin Lee", "Hannah Wright", "Marco Bellini")

    val now = System.currentTimeMillis()
    val records = mutableListOf<Txn>()
    val dateFmt = SimpleDateFormat("MMM d", Locale.US)
    val timeFmt = SimpleDateFormat("h:mm a", Locale.US)

    for (i in 0 until count) {
        val daysAgo = (i.toDouble() * (rand() * 0.6 + 0.7)).toInt()
        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            add(Calendar.DAY_OF_MONTH, -daysAgo)
            set(Calendar.HOUR_OF_DAY, 8 + (rand() * 13).toInt())
            set(Calendar.MINUTE, (rand() * 60).toInt())
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val ts = cal.timeInMillis
        val date = Date(ts)
        val dateLabel = dateFmt.format(date)
        val timeLabel = timeFmt.format(date)
        val txnId = "TXN-${50_000_000 + i * 137 + (rand() * 99).toInt()}"

        val roll = rand()
        val cat = when {
            roll < 0.45 -> TxnCategory.Pay
            roll < 0.65 -> TxnCategory.Charge
            roll < 0.83 -> TxnCategory.Reward
            roll < 0.93 -> TxnCategory.Referral
            else -> TxnCategory.Gift
        }
        val id = "tx-$i"
        when (cat) {
            TxnCategory.Pay -> {
                val r = pick(restaurants)
                val subtotal = r.items.sumOf { it.price * it.qty }
                val tax = (subtotal * 0.09 * 100).toInt() / 100.0
                val serviceFee = (subtotal * 0.05 * 100).toInt() / 100.0
                val total = subtotal + tax + serviceFee
                records.add(
                    Txn(
                        id = id,
                        label = r.name,
                        timestampMs = ts,
                        dateLabel = dateLabel,
                        timeLabel = timeLabel,
                        amountValue = total,
                        isDebit = true,
                        category = TxnCategory.Pay,
                        method = "Balance",
                        transactionId = txnId,
                        items = r.items,
                        subtotal = subtotal,
                        tax = tax,
                        serviceFee = serviceFee,
                        discount = 0.0,
                        restaurant = r.name,
                        address = r.address,
                    ),
                )
            }
            TxnCategory.Charge -> {
                val v = pick(listOf(25.0, 50.0, 75.0, 100.0, 150.0, 200.0))
                records.add(
                    Txn(
                        id = id,
                        label = "Top Up",
                        timestampMs = ts,
                        dateLabel = dateLabel,
                        timeLabel = timeLabel,
                        amountValue = v,
                        isDebit = false,
                        category = TxnCategory.Charge,
                        method = pick(topupMethods),
                        transactionId = txnId,
                    ),
                )
            }
            TxnCategory.Reward -> {
                val v = ((rand() * 9.0 + 1.0) * 100).toInt() / 100.0
                records.add(
                    Txn(
                        id = id,
                        label = "Reward Earned",
                        timestampMs = ts,
                        dateLabel = dateLabel,
                        timeLabel = timeLabel,
                        amountValue = v,
                        isDebit = false,
                        category = TxnCategory.Reward,
                        source = pick(rewardSources),
                        transactionId = txnId,
                    ),
                )
            }
            TxnCategory.Referral -> {
                val v = pick(listOf(10.0, 15.0, 20.0, 25.0))
                records.add(
                    Txn(
                        id = id,
                        label = "Referral Bonus",
                        timestampMs = ts,
                        dateLabel = dateLabel,
                        timeLabel = timeLabel,
                        amountValue = v,
                        isDebit = false,
                        category = TxnCategory.Referral,
                        source = "${pick(referralNames)} signed up",
                        transactionId = txnId,
                    ),
                )
            }
            TxnCategory.Gift -> {
                val v = pick(listOf(10.0, 25.0, 30.0, 50.0))
                records.add(
                    Txn(
                        id = id,
                        label = "Gift Received",
                        timestampMs = ts,
                        dateLabel = dateLabel,
                        timeLabel = timeLabel,
                        amountValue = v,
                        isDebit = false,
                        category = TxnCategory.Gift,
                        method = "Gift Card",
                        source = pick(giftSenders),
                        transactionId = txnId,
                    ),
                )
            }
            TxnCategory.All -> Unit
        }
    }
    return records.sortedByDescending { it.timestampMs }
}

private data class RestaurantInfo(val name: String, val address: String, val items: List<InvoiceItem>)
