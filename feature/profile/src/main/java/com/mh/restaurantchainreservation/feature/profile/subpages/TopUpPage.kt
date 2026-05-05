package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class TopUpStep { Select, Confirm, Processing, Done, Error }

private val PresetTopUpAmounts = listOf(10000, 30000, 50000, 100000, 200000, 500000)

@Composable
fun TopUpPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var step by rememberSaveable { mutableStateOf(TopUpStep.Select) }
    var amount by rememberSaveable { mutableStateOf(50000) }
    val scope = rememberCoroutineScope()

    when (step) {
        TopUpStep.Select -> SelectView(
            amount = amount,
            onAmountChange = { amount = it },
            onContinue = { step = TopUpStep.Confirm },
            onBack = onBack,
            modifier = modifier,
        )
        TopUpStep.Confirm -> ConfirmView(
            amount = amount,
            onConfirm = {
                step = TopUpStep.Processing
                scope.launch {
                    delay(1800)
                    step = TopUpStep.Done
                }
            },
            onBack = { step = TopUpStep.Select },
            modifier = modifier,
        )
        TopUpStep.Processing -> ProcessingView(modifier = modifier)
        TopUpStep.Done -> DoneView(amount = amount, onDone = onBack, modifier = modifier)
        TopUpStep.Error -> ErrorView(
            onRetry = { step = TopUpStep.Confirm },
            onClose = onBack,
            modifier = modifier,
        )
    }
}

@Composable
private fun SelectView(
    amount: Int,
    onAmountChange: (Int) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    SubpageScaffold(
        title = stringResource(I18nR.string.topup_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        SectionTitle(stringResource(I18nR.string.topup_select_amount))
        Spacer(Modifier.height(8.dp))
        AmountGrid(selected = amount, onSelect = onAmountChange)

        Spacer(Modifier.height(20.dp))
        SectionTitle(stringResource(I18nR.string.topup_payment_method))
        Spacer(Modifier.height(8.dp))
        PaymentRow(selected = true)

        Spacer(Modifier.height(28.dp))
        BrandPillButton(text = stringResource(I18nR.string.topup_continue), onClick = onContinue)
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ConfirmView(
    amount: Int,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    SubpageScaffold(
        title = stringResource(I18nR.string.topup_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(palette.mutedSurface)
                .padding(20.dp),
        ) {
            Column {
                Text(
                    text = stringResource(I18nR.string.topup_amount_format, "%,d".format(amount)),
                    color = palette.foreground,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(I18nR.string.topup_select_amount),
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        PaymentRow(selected = true)

        Spacer(Modifier.height(28.dp))
        BrandPillButton(text = stringResource(I18nR.string.topup_confirm_pay), onClick = onConfirm)
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ProcessingView(modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier.fillMaxSize().background(palette.cardSurface),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = palette.brand, strokeWidth = 4.dp)
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(I18nR.string.topup_processing_title),
                color = palette.foreground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(I18nR.string.topup_processing_subtitle),
                color = palette.mutedForeground,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun DoneView(amount: Int, onDone: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    LaunchedEffect(Unit) { /* could trigger wallet refresh */ }
    SubpageScaffold(
        title = stringResource(I18nR.string.topup_title),
        onBack = onDone,
        modifier = modifier,
    ) {
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(palette.success)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(I18nR.string.topup_done_title),
            color = palette.foreground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(I18nR.string.topup_done_subtitle),
            color = palette.mutedForeground,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(palette.mutedSurface)
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(I18nR.string.topup_amount_format, "%,d".format(amount)),
                color = palette.foreground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(24.dp))
        BrandPillButton(text = stringResource(I18nR.string.topup_done_action), onClick = onDone)
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ErrorView(onRetry: () -> Unit, onClose: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    SubpageScaffold(
        title = stringResource(I18nR.string.topup_title),
        onBack = onClose,
        modifier = modifier,
    ) {
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(palette.destructive.copy(alpha = 0.10f))
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.ErrorOutline, null, tint = palette.destructive, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(I18nR.string.topup_error_title),
            color = palette.foreground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(I18nR.string.topup_error_subtitle),
            color = palette.mutedForeground,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(24.dp))
        BrandPillButton(text = stringResource(I18nR.string.topup_retry), onClick = onRetry)
    }
}

@Composable
private fun AmountGrid(selected: Int, onSelect: (Int) -> Unit) {
    val rows = PresetTopUpAmounts.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { amt ->
                    Chip(amount = amt, selected = amt == selected, onSelect = { onSelect(amt) }, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) Box(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Chip(amount: Int, selected: Boolean, onSelect: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(shape)
            .border(if (selected) 2.dp else 1.dp, if (selected) palette.brand else palette.border.copy(alpha = 0.6f), shape)
            .background(if (selected) palette.brand.copy(alpha = 0.06f) else palette.cardSurface)
            .clickable(onClick = onSelect),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(I18nR.string.topup_amount_format, "%,d".format(amount)),
            color = if (selected) palette.brand else palette.foreground,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun PaymentRow(selected: Boolean) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(if (selected) 2.dp else 1.dp, if (selected) palette.brand else palette.border, shape)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(palette.mutedSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.CreditCard, null, tint = palette.foreground, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Tonight Wallet", color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text("Default payment", color = palette.mutedForeground, fontSize = 12.sp)
        }
        Icon(Icons.Outlined.AccountBalanceWallet, null, tint = palette.brand, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun BrandPillButton(text: String, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(palette.brand)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SectionTitle(text: String) {
    val palette = LocalRestaurantPalette.current
    Text(text = text, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
}
