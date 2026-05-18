package com.mh.restaurantchainreservation.feature.dining.ui.modals

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.BottomModalSheet
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking
import com.mh.restaurantchainreservation.feature.dining.data.Receipt
import com.mh.restaurantchainreservation.feature.dining.data.ReceiptItemCategory
import com.mh.restaurantchainreservation.feature.dining.data.lineTotal

private const val ReceiptSheetHeightFraction = 0.78f
private const val ReceiptHeaderCollapseThresholdPx = 72f

private data class ReceiptCategoryTotals(
    val foodQty: Int,
    val foodAmount: Double,
    val drinkQty: Int,
    val drinkAmount: Double,
)

private fun Receipt.categoryTotals(): ReceiptCategoryTotals {
    val food = items.filter { it.category == ReceiptItemCategory.Food }
    val drinks = items.filter { it.category == ReceiptItemCategory.Drink }
    return ReceiptCategoryTotals(
        foodQty = food.sumOf { it.qty },
        foodAmount = food.sumOf { it.lineTotal() },
        drinkQty = drinks.sumOf { it.qty },
        drinkAmount = drinks.sumOf { it.lineTotal() },
    )
}

@Composable
fun OrderReceiptModal(
    booking: Booking,
    onDismiss: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val receipt = booking.receipt
    val priceFmt = remember { java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US) }
    val configuration = LocalConfiguration.current
    val sheetMaxHeight = (configuration.screenHeightDp * ReceiptSheetHeightFraction).dp

    BottomModalSheet(
        onDismiss = onDismiss,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(sheetMaxHeight)
                .padding(20.dp),
        ) {
            Text(
                text = stringResource(I18nR.string.receipt_title),
                color = palette.foreground,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(16.dp))

            if (receipt == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(palette.mutedSurface.copy(alpha = 0.5f))
                        .padding(vertical = 36.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(I18nR.string.receipt_none),
                        color = palette.mutedForeground,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                ReceiptCard(
                    booking = booking,
                    receipt = receipt,
                    priceFmt = priceFmt,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ReceiptCard(
    booking: Booking,
    receipt: Receipt,
    priceFmt: java.text.NumberFormat,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val categoryTotals = remember(receipt) { receipt.categoryTotals() }
    val listState = rememberLazyListState()

    val collapseProgress by remember {
        derivedStateOf {
            val offset = if (listState.firstVisibleItemIndex == 0) {
                listState.firstVisibleItemScrollOffset.toFloat()
            } else {
                ReceiptHeaderCollapseThresholdPx
            }
            (offset / ReceiptHeaderCollapseThresholdPx).coerceIn(0f, 1f)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, palette.border, RoundedCornerShape(24.dp))
            .background(palette.cardSurface),
    ) {
        CollapsibleReceiptStoreHeader(
            restaurant = booking.restaurant,
            address = booking.address,
            paidAt = receipt.paidAt,
            collapseProgress = collapseProgress,
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            itemsIndexed(receipt.items, key = { i, item -> "${item.name}-$i" }) { i, item ->
                ReceiptItemRow(
                    orderNumber = i + 1,
                    name = item.name,
                    unitPriceText = priceFmt.format(item.price),
                    qty = item.qty,
                    lineTotalText = priceFmt.format(item.lineTotal()),
                    index = i,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.border),
        )

        ReceiptSummaryFooter(
            receipt = receipt,
            booking = booking,
            categoryTotals = categoryTotals,
            priceFmt = priceFmt,
        )
    }
}

@Composable
private fun CollapsibleReceiptStoreHeader(
    restaurant: String,
    address: String,
    paidAt: String,
    collapseProgress: Float,
) {
    val palette = LocalRestaurantPalette.current
    val detailAlpha = 1f - collapseProgress
    val collapsedDateAlpha = collapseProgress
    val dividerAlpha = collapseProgress
    val titleAlign = if (collapseProgress < 0.08f) TextAlign.Center else TextAlign.Start
    val detailHeight by animateDpAsState(
        targetValue = if (detailAlpha > 0.01f) 48.dp else 0.dp,
        animationSpec = tween(180),
        label = "receipt_header_detail_height",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(palette.mutedSurface.copy(alpha = 0.55f))
            .padding(start = 16.dp, end = 16.dp, top = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = restaurant,
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = titleAlign,
                modifier = Modifier.weight(1f),
            )
            if (collapsedDateAlpha > 0.01f) {
                Text(
                    text = paidAt,
                    color = palette.mutedForeground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer { alpha = collapsedDateAlpha },
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(detailHeight)
                .graphicsLayer { alpha = detailAlpha },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (detailAlpha > 0.01f) {
                Text(
                    text = address,
                    color = palette.mutedForeground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp),
                )
                Text(
                    text = paidAt,
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = dividerAlpha }
                .height(1.dp)
                .background(palette.border),
        )
    }
}

@Composable
private fun ReceiptSummaryFooter(
    receipt: Receipt,
    booking: Booking,
    categoryTotals: ReceiptCategoryTotals,
    priceFmt: java.text.NumberFormat,
) {
    val palette = LocalRestaurantPalette.current

    Column(modifier = Modifier.padding(16.dp)) {
        if (categoryTotals.foodQty > 0) {
            ReceiptCategorySummaryRow(
                label = stringResource(I18nR.string.receipt_food_count, categoryTotals.foodQty),
                amountText = priceFmt.format(categoryTotals.foodAmount),
            )
            Spacer(Modifier.height(8.dp))
        }
        if (categoryTotals.drinkQty > 0) {
            ReceiptCategorySummaryRow(
                label = stringResource(I18nR.string.receipt_drinks_count, categoryTotals.drinkQty),
                amountText = priceFmt.format(categoryTotals.drinkAmount),
            )
            Spacer(Modifier.height(10.dp))
        }
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
                text = priceFmt.format(receipt.total),
                color = palette.brand,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(palette.mutedSurface.copy(alpha = 0.7f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(I18nR.string.receipt_paid_with),
                color = palette.mutedForeground,
                fontSize = 13.sp,
            )
            Text(
                text = receipt.paymentMethod,
                color = palette.foreground,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
            )
        }
        if (booking.rating != null) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "${booking.rating} – ${stringResource(I18nR.string.receipt_rating_label)}",
                color = palette.foreground,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ReceiptCategorySummaryRow(
    label: String,
    amountText: String,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = amountText,
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun ReceiptItemRow(
    orderNumber: Int,
    name: String,
    unitPriceText: String,
    qty: Int,
    lineTotalText: String,
    index: Int,
) {
    val palette = LocalRestaurantPalette.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val translateX by animateFloatAsState(
        targetValue = if (visible) 0f else -8f,
        animationSpec = tween(durationMillis = 200, delayMillis = index * 25),
        label = "receipt_x",
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 200, delayMillis = index * 25),
        label = "receipt_alpha",
    )
    Row(
        modifier = Modifier
            .graphicsLayer {
                translationX = translateX
                this.alpha = alpha
            }
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(I18nR.string.receipt_item_order, orderNumber),
            color = palette.mutedForeground,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(22.dp),
            textAlign = TextAlign.Center,
        )
        Text(
            text = name,
            color = palette.foreground,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = unitPriceText,
            color = palette.foreground,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.width(52.dp),
        )
        Text(
            text = qty.toString(),
            color = palette.mutedForeground,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.Center,
        )
        Text(
            text = lineTotalText,
            color = palette.foreground,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.End,
            modifier = Modifier.width(56.dp),
        )
    }
}
