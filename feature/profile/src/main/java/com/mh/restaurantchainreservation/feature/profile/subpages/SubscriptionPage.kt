package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.core.model.BillingCycle
import com.mh.restaurantchainreservation.core.model.PlanType
import com.mh.restaurantchainreservation.core.model.SubscriptionStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class SubStep { Plan, Payment, Processing, Confirmed }

@Composable
fun SubscriptionPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val plan by SubscriptionStore.plan.collectAsState()
    var step by rememberSaveable { mutableStateOf(SubStep.Plan) }
    var cycle by rememberSaveable { mutableStateOf(BillingCycle.Yearly) }
    val scope = rememberCoroutineScope()

    if (plan.type == PlanType.Pro && step == SubStep.Plan) {
        AlreadyProView(onBack = onBack)
        return
    }

    when (step) {
        SubStep.Plan -> PlanPickerView(
            cycle = cycle,
            onCycleChange = { cycle = it },
            onContinue = { step = SubStep.Payment },
            onBack = onBack,
            modifier = modifier,
        )
        SubStep.Payment -> PaymentView(
            cycle = cycle,
            onConfirm = {
                step = SubStep.Processing
                scope.launch {
                    delay(2000)
                    SubscriptionStore.activatePro(cycle)
                    step = SubStep.Confirmed
                }
            },
            onBack = { step = SubStep.Plan },
            modifier = modifier,
        )
        SubStep.Processing -> ProcessingView(modifier = modifier)
        SubStep.Confirmed -> ConfirmedView(cycle = cycle, onDone = onBack, modifier = modifier)
    }
}

@Composable
private fun PlanPickerView(
    cycle: BillingCycle,
    onCycleChange: (BillingCycle) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    SubpageScaffold(
        title = stringResource(I18nR.string.subscription_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.WorkspacePremium,
                    contentDescription = null,
                    tint = palette.foreground,
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = stringResource(I18nR.string.subscription_brand),
                    color = palette.foreground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(I18nR.string.subscription_subtitle),
                color = palette.mutedForeground,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(I18nR.string.subscription_pro_benefits),
            color = palette.foreground,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        BenefitRow(I18nR.string.subscription_benefit_unlimited_label, I18nR.string.subscription_benefit_unlimited_desc)
        BenefitRow(I18nR.string.subscription_benefit_priority_label, I18nR.string.subscription_benefit_priority_desc)
        BenefitRow(I18nR.string.subscription_benefit_no_fees_label, I18nR.string.subscription_benefit_no_fees_desc)
        BenefitRow(I18nR.string.subscription_benefit_deals_label, I18nR.string.subscription_benefit_deals_desc)

        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(I18nR.string.subscription_choose_plan),
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            BillingCycle.entries.forEach { c ->
                PlanRow(c = c, selected = c == cycle, onSelect = { onCycleChange(c) })
            }
        }

        Spacer(Modifier.height(24.dp))
        BrandButton(text = stringResource(I18nR.string.subscription_continue_to_payment), onClick = onContinue)
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(I18nR.string.subscription_cancel_anytime),
            color = palette.mutedForeground,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun PaymentView(
    cycle: BillingCycle,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val canPay = cycle.price <= 24.50
    SubpageScaffold(
        title = stringResource(I18nR.string.subscription_pay_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(stringResource(I18nR.string.subscription_brand), color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(planLabel(cycle), color = palette.mutedForeground, fontSize = 14.sp)
            }
            Text(text = "$%.2f".format(cycle.price), color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        if (cycle.discount > 0) {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(palette.success.copy(alpha = 0.10f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = stringResource(I18nR.string.subscription_includes_discount, cycle.discount),
                    color = palette.success,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        Text(stringResource(I18nR.string.subscription_pay_with), color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        WalletPaymentRow()

        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = palette.mutedForeground,
                modifier = Modifier.size(14.dp).padding(top = 2.dp),
            )
            Text(
                text = stringResource(I18nR.string.subscription_secure),
                color = palette.mutedForeground,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }
        Spacer(Modifier.height(20.dp))
        BrandButton(
            text = stringResource(I18nR.string.subscription_pay_amount, cycle.price),
            onClick = onConfirm,
            enabled = canPay,
        )
        if (!canPay) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(I18nR.string.subscription_insufficient),
                color = palette.brand,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ProcessingView(modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SpinnerCircle()
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(I18nR.string.subscription_processing_payment),
                color = palette.foreground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(I18nR.string.subscription_please_wait),
                color = palette.mutedForeground,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun SpinnerCircle() {
    val palette = LocalRestaurantPalette.current
    val transition = rememberInfiniteTransition(label = "spin")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rot",
    )
    Box(
        modifier = Modifier
            .size(44.dp)
            .rotate(rotation)
            .clip(CircleShape)
            .background(palette.mutedSurface),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(palette.foreground),
        )
    }
}

@Composable
private fun ConfirmedView(cycle: BillingCycle, onDone: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    SubpageScaffold(
        title = stringResource(I18nR.string.subscription_active_title),
        onBack = onDone,
        modifier = modifier,
    ) {
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(palette.foreground)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = palette.cardSurface,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(I18nR.string.subscription_welcome_pro),
            color = palette.foreground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(I18nR.string.subscription_now_active),
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
                .background(palette.mutedSurface.copy(alpha = 0.5f))
                .padding(14.dp),
        ) {
            Column {
                Text(
                    text = "${planLabel(cycle)} plan",
                    color = palette.foreground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "$%.2f / %s".format(cycle.price, planUnit(cycle)),
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        BrandButton(text = stringResource(I18nR.string.subscription_start_exploring), onClick = onDone)
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun AlreadyProView(onBack: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val plan by SubscriptionStore.plan.collectAsState()
    var showCancel by rememberSaveable { mutableStateOf(false) }

    SubpageScaffold(
        title = stringResource(I18nR.string.subscription_active_title),
        onBack = onBack,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Outlined.WorkspacePremium, null, tint = palette.foreground, modifier = Modifier.size(24.dp))
                Text(stringResource(I18nR.string.subscription_brand), color = palette.foreground, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.success.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(stringResource(I18nR.string.subscription_active_badge), color = palette.success, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(20.dp))
        val cycle = plan.cycle ?: BillingCycle.Monthly
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DetailRow(stringResource(I18nR.string.subscription_plan_label), planLabel(cycle))
            DetailRow(stringResource(I18nR.string.subscription_price_label), "$%.2f".format(cycle.price))
            DetailRow(
                stringResource(I18nR.string.subscription_next_billing),
                plan.expiresAtEpochMs?.let { formatDate(it) } ?: "—",
            )
            DetailRow(
                stringResource(I18nR.string.subscription_member_since),
                plan.subscribedAtEpochMs?.let { formatDate(it) } ?: "—",
            )
        }

        Spacer(Modifier.height(20.dp))
        Text(stringResource(I18nR.string.subscription_pro_benefits), color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        BenefitRow(I18nR.string.subscription_benefit_unlimited_label, I18nR.string.subscription_benefit_unlimited_desc)
        BenefitRow(I18nR.string.subscription_benefit_priority_label, I18nR.string.subscription_benefit_priority_desc)
        BenefitRow(I18nR.string.subscription_benefit_no_fees_label, I18nR.string.subscription_benefit_no_fees_desc)
        BenefitRow(I18nR.string.subscription_benefit_deals_label, I18nR.string.subscription_benefit_deals_desc)

        Spacer(Modifier.height(20.dp))
        TextButton(onClick = { showCancel = true }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(I18nR.string.subscription_cancel_subscription), color = palette.destructive, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(40.dp))
    }

    if (showCancel) {
        AlertDialog(
            onDismissRequest = { showCancel = false },
            title = { Text(stringResource(I18nR.string.subscription_cancel_modal_title)) },
            text = { Text(stringResource(I18nR.string.subscription_cancel_modal_body), color = palette.mutedForeground) },
            confirmButton = {
                TextButton(onClick = {
                    SubscriptionStore.cancelPro()
                    showCancel = false
                }) {
                    Text(stringResource(I18nR.string.subscription_yes_cancel), color = palette.destructive, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancel = false }) {
                    Text(stringResource(I18nR.string.subscription_keep_plan))
                }
            },
        )
    }
}

@Composable
private fun BenefitRow(labelRes: Int, descRes: Int) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = null,
            tint = palette.foreground,
            modifier = Modifier.size(16.dp).padding(top = 2.dp),
        )
        Column {
            Text(stringResource(labelRes), color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(stringResource(descRes), color = palette.mutedForeground, fontSize = 11.sp)
        }
    }
}

@Composable
private fun PlanRow(c: BillingCycle, selected: Boolean, onSelect: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(if (selected) 2.dp else 1.dp, if (selected) palette.foreground else palette.border, shape)
            .background(palette.cardSurface)
            .clickable(onClick = onSelect)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .border(if (selected) 5.dp else 1.dp, if (selected) palette.foreground else palette.border, CircleShape),
            )
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(planLabel(c), color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    if (c.discount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(palette.success.copy(alpha = 0.10f))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = stringResource(I18nR.string.subscription_save_pct, c.discount).uppercase(),
                                color = palette.success,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(I18nR.string.subscription_per_month, c.perMonth),
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                )
            }
        }
        Text("$%.2f".format(c.price), color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun WalletPaymentRow() {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, palette.border, shape)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(palette.mutedSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.AccountBalanceWallet, null, tint = palette.foreground, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(I18nR.string.subscription_available_balance), color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(stringResource(I18nR.string.subscription_available_amount), color = palette.mutedForeground, fontSize = 13.sp)
        }
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .border(5.dp, palette.foreground, CircleShape),
        )
    }
}

@Composable
private fun BrandButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (enabled) palette.brand else palette.brand.copy(alpha = 0.45f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = palette.mutedForeground, fontSize = 13.sp)
        Text(value, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun planLabel(cycle: BillingCycle): String = when (cycle) {
    BillingCycle.Monthly -> stringResource(I18nR.string.subscription_monthly_label)
    BillingCycle.Quarterly -> stringResource(I18nR.string.subscription_quarterly_label)
    BillingCycle.Yearly -> stringResource(I18nR.string.subscription_yearly_label)
}

private fun planUnit(cycle: BillingCycle): String = when (cycle) {
    BillingCycle.Monthly -> "mo"
    BillingCycle.Quarterly -> "qtr"
    BillingCycle.Yearly -> "yr"
}

private fun formatDate(epochMs: Long): String =
    SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(epochMs))

@Suppress("unused")
private val unused: ImageVector? = null
