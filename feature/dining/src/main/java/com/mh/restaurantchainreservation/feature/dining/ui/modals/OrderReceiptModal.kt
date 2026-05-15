package com.mh.restaurantchainreservation.feature.dining.ui.modals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.BottomModalSheet
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking

@Composable
fun OrderReceiptModal(
    booking: Booking,
    onDismiss: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val receipt = booking.receipt
    val priceFmt = remember { java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US) }

    var pop by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { pop = true }
    val iconScale by animateFloatAsState(
        targetValue = if (pop) 1f else 0.75f,
        animationSpec = spring(stiffness = 280f, dampingRatio = 0.45f),
        label = "receipt_icon_pop",
    )
    val iconAlpha by animateFloatAsState(
        targetValue = if (pop) 1f else 0f,
        animationSpec = tween(220),
        label = "receipt_icon_alpha",
    )

    BottomModalSheet(onDismiss = onDismiss) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            scaleX = iconScale; scaleY = iconScale
                            alpha = iconAlpha
                        }
                        .clip(CircleShape)
                        .background(palette.success.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Receipt,
                        contentDescription = null,
                        tint = palette.success,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Column {
                    Text(
                        text = stringResource(I18nR.string.receipt_title),
                        color = palette.foreground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = booking.restaurant,
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                        maxLines = 1,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            if (receipt == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(24.dp))
                        .background(palette.cardSurface),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(palette.mutedSurface.copy(alpha = 0.55f))
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = booking.restaurant,
                            color = palette.foreground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = booking.address,
                            color = palette.mutedForeground,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                        )
                        Text(
                            text = receipt.paidAt,
                            color = palette.mutedForeground,
                            fontSize = 12.sp,
                        )
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                            .padding(horizontal = 16.dp),
                    ) {
                        itemsIndexed(receipt.items, key = { i, item -> "${item.name}-$i" }) { i, item ->
                            ReceiptItemRow(
                                emoji = item.emoji ?: "🍽",
                                name = item.name,
                                qty = item.qty,
                                priceText = priceFmt.format(item.price * item.qty),
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
                    Column(modifier = Modifier.padding(16.dp)) {
                        ReceiptLine(
                            stringResource(I18nR.string.receipt_subtotal),
                            priceFmt.format(receipt.subtotal),
                        )
                        ReceiptLine(
                            stringResource(I18nR.string.receipt_tax),
                            priceFmt.format(receipt.tax),
                        )
                        ReceiptLine(
                            stringResource(I18nR.string.receipt_tip),
                            priceFmt.format(receipt.tip),
                        )
                        Spacer(Modifier.height(10.dp))
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CreditCard,
                                contentDescription = null,
                                tint = palette.mutedForeground,
                                modifier = Modifier.size(16.dp),
                            )
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = palette.warning,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.size(4.dp))
                                Text(
                                    text = "${booking.rating} – ${stringResource(I18nR.string.receipt_rating_label)}",
                                    color = palette.foreground,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ReceiptAction(
                    text = stringResource(I18nR.string.receipt_save),
                    icon = Icons.Outlined.Download,
                    primary = false,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
                ReceiptAction(
                    text = stringResource(I18nR.string.receipt_share),
                    icon = Icons.Outlined.Share,
                    primary = true,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ReceiptItemRow(emoji: String, name: String, qty: Int, priceText: String, index: Int) {
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
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(palette.mutedSurface),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = emoji, fontSize = 16.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Text(
                text = stringResource(I18nR.string.receipt_qty, qty),
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
        }
        Text(
            text = priceText,
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun ReceiptLine(label: String, value: String) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = palette.mutedForeground, fontSize = 14.sp)
        Text(text = value, color = palette.foreground, fontSize = 14.sp)
    }
}

@Composable
private fun ReceiptAction(
    text: String,
    icon: ImageVector,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    val container = if (primary) palette.brand else palette.cardSurface
    val content = if (primary) Color.White else palette.foreground
    Row(
        modifier = modifier
            .height(48.dp)
            .clip(shape)
            .let { if (!primary) it.border(1.dp, palette.border, shape) else it }
            .background(container)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = content, modifier = Modifier.size(16.dp))
        Spacer(Modifier.size(8.dp))
        Text(
            text = text,
            color = content,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}
