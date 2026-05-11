package com.mh.restaurantchainreservation.feature.dining.ui.scanqr

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.ScanMenuItem

@Composable
fun BillCard(
    items: List<ScanMenuItem>,
    subtotal: Double,
    tax: Double? = null,
    tip: Double? = null,
    total: Double? = null,
    tipPercent: Int? = null,
    onTipPercentChange: ((Int) -> Unit)? = null,
    compact: Boolean = false,
) {
    val palette = LocalRestaurantPalette.current
    val priceFmt = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US)

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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Receipt,
                    contentDescription = null,
                    tint = palette.brand,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = stringResource(if (compact) I18nR.string.scan_bill_live else I18nR.string.scan_bill_yours),
                    color = palette.foreground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            if (compact) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(palette.warning.copy(alpha = 0.10f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(palette.warning),
                    )
                    Text(
                        text = stringResource(I18nR.string.scan_bill_updating),
                        color = palette.warning,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(palette.mutedSurface),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = item.mark,
                            color = palette.foreground,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            color = palette.foreground,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                        Text(
                            text = "Qty ${item.qty}",
                            color = palette.mutedForeground,
                            fontSize = 12.sp,
                        )
                    }
                    Text(
                        text = priceFmt.format(item.price),
                        color = palette.foreground,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                if (index < items.lastIndex) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(palette.border))
                }
            }
        }

        if (tax != null && tip != null && total != null) {
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.border))
            Spacer(Modifier.height(10.dp))
            BillRow(stringResource(I18nR.string.receipt_subtotal), priceFmt.format(subtotal))
            BillRow(stringResource(I18nR.string.receipt_tax), priceFmt.format(tax))

            if (onTipPercentChange != null && tipPercent != null) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(I18nR.string.receipt_tip),
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                    )
                    Text(
                        text = priceFmt.format(tip),
                        color = palette.foreground,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf(15, 18, 20, 25).forEach { p ->
                        TipPill(
                            percent = p,
                            selected = tipPercent == p,
                            onClick = { onTipPercentChange(p) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.brand.copy(alpha = 0.08f))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(I18nR.string.receipt_total),
                    color = palette.foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = priceFmt.format(total),
                    color = palette.brand,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        } else {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.brand.copy(alpha = 0.08f))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(I18nR.string.scan_bill_running_total),
                        color = palette.brand,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = stringResource(I18nR.string.scan_bill_tax_at_checkout),
                        color = palette.mutedForeground,
                        fontSize = 11.sp,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = priceFmt.format(subtotal),
                    color = palette.brand,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

@Composable
private fun TipPill(percent: Int, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val container = if (selected) palette.brand else palette.mutedSurface
    val content = if (selected) Color.White else palette.foreground
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(container)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "$percent%", color = content, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun BillRow(label: String, value: String) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = palette.mutedForeground, fontSize = 13.sp)
        Text(text = value, color = palette.foreground, fontSize = 13.sp)
    }
}
