package com.mh.restaurantchainreservation.feature.profile.subpages

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

private enum class HistoryFilter { All, Income, Expense }
private enum class Period { Today, Week, Month, Quarter }

private data class TxnRow(
    val id: String,
    val title: String,
    val date: String,
    val amount: Long,
    val income: Boolean,
)

private val DemoTxns = listOf(
    TxnRow("t1", "Top up", "May 04", 50000, income = true),
    TxnRow("t2", "Cha-Cha-Lounge reservation", "May 03", -32000, income = false),
    TxnRow("t3", "Daily reward bonus", "May 02", 1500, income = true),
    TxnRow("t4", "Refer & earn", "May 01", 10000, income = true),
    TxnRow("t5", "Olive Garden booking fee", "Apr 30", -8000, income = false),
    TxnRow("t6", "Hannam Tonkatsu", "Apr 28", -22000, income = false),
)

@Composable
fun HistoryPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    var filter by rememberSaveable { mutableStateOf(HistoryFilter.All) }
    var period by rememberSaveable { mutableStateOf(Period.Month) }
    var showPicker by rememberSaveable { mutableStateOf(false) }
    var openInvoice by rememberSaveable { mutableStateOf<String?>(null) }

    val filtered = when (filter) {
        HistoryFilter.All -> DemoTxns
        HistoryFilter.Income -> DemoTxns.filter { it.income }
        HistoryFilter.Expense -> DemoTxns.filter { !it.income }
    }

    SubpageScaffold(
        title = stringResource(I18nR.string.history_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        FilterTabsRow(filter = filter, onChange = { filter = it })
        Spacer(Modifier.height(12.dp))
        PeriodPickerRow(period = period, onClick = { showPicker = true })
        Spacer(Modifier.height(16.dp))

        if (filtered.isEmpty()) {
            EmptyHistoryState()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filtered.forEach { txn ->
                    TxnCard(txn = txn, onClick = { openInvoice = txn.id })
                }
            }
        }
        Spacer(Modifier.height(40.dp))
    }

    if (showPicker) {
        PeriodPickerDialog(
            selected = period,
            onSelect = {
                period = it
                showPicker = false
            },
            onDismiss = { showPicker = false },
        )
    }

    val openTxn = filtered.firstOrNull { it.id == openInvoice }
    if (openTxn != null) {
        InvoiceDialog(txn = openTxn, onDismiss = { openInvoice = null })
    }
}

@Composable
private fun FilterTabsRow(filter: HistoryFilter, onChange: (HistoryFilter) -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.mutedSurface)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        listOf(
            stringResource(I18nR.string.history_filter_all) to HistoryFilter.All,
            stringResource(I18nR.string.history_filter_income) to HistoryFilter.Income,
            stringResource(I18nR.string.history_filter_expense) to HistoryFilter.Expense,
        ).forEach { (label, value) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (filter == value) palette.cardSurface else Color.Transparent)
                    .clickable { onChange(value) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (filter == value) palette.foreground else palette.mutedForeground,
                    fontSize = 13.sp,
                    fontWeight = if (filter == value) FontWeight.SemiBold else FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun PeriodPickerRow(period: Period, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val label = when (period) {
        Period.Today -> stringResource(I18nR.string.history_period_today)
        Period.Week -> stringResource(I18nR.string.history_period_this_week)
        Period.Month -> stringResource(I18nR.string.history_period_this_month)
        Period.Quarter -> stringResource(I18nR.string.history_period_3_months)
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, palette.border, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(Icons.Outlined.History, null, tint = palette.foreground, modifier = Modifier.size(16.dp))
        Text(label, color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun TxnCard(txn: TxnRow, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(16.dp)
    val isIncome = txn.income
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, palette.border.copy(alpha = 0.6f), shape)
            .background(palette.cardSurface)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background((if (isIncome) palette.success else palette.brand).copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isIncome) Icons.Outlined.ArrowDownward else Icons.Outlined.ArrowUpward,
                contentDescription = null,
                tint = if (isIncome) palette.success else palette.brand,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(txn.title, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(txn.date, color = palette.mutedForeground, fontSize = 12.sp)
        }
        Text(
            text = "${if (txn.amount > 0) "+" else "-"}₩${"%,d".format(kotlin.math.abs(txn.amount))}",
            color = if (isIncome) palette.success else palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun EmptyHistoryState() {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(64.dp).clip(CircleShape).background(palette.mutedSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Receipt, null, tint = palette.mutedForeground, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(stringResource(I18nR.string.history_empty_title), color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(I18nR.string.history_empty_subtitle),
            color = palette.mutedForeground,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
    }
}

@Composable
private fun PeriodPickerDialog(selected: Period, onSelect: (Period) -> Unit, onDismiss: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Period") },
        text = {
            Column {
                listOf(
                    Period.Today to stringResource(I18nR.string.history_period_today),
                    Period.Week to stringResource(I18nR.string.history_period_this_week),
                    Period.Month to stringResource(I18nR.string.history_period_this_month),
                    Period.Quarter to stringResource(I18nR.string.history_period_3_months),
                ).forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(value) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .border(if (selected == value) 5.dp else 1.dp, if (selected == value) palette.brand else palette.border, CircleShape),
                        )
                        Spacer(Modifier.size(12.dp))
                        Text(label, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(I18nR.string.common_cancel)) }
        },
    )
}

@Composable
private fun InvoiceDialog(txn: TxnRow, onDismiss: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(I18nR.string.history_invoice_title)) },
        text = {
            Column {
                Text(txn.title, color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(txn.date, color = palette.mutedForeground, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.mutedSurface)
                        .padding(12.dp),
                ) {
                    Text(
                        text = "${if (txn.amount > 0) "+" else "-"}₩${"%,d".format(kotlin.math.abs(txn.amount))}",
                        color = palette.foreground,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(I18nR.string.common_continue), fontWeight = FontWeight.SemiBold) }
        },
    )
}
