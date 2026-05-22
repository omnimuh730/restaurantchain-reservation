package com.mh.restaurantchainreservation.feature.dining.ui.modals

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.MenuBook
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.BottomModalSheet
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking
import com.mh.restaurantchainreservation.feature.dining.data.MenuItemDish
import com.mh.restaurantchainreservation.feature.dining.data.menuFor

enum class MenuVariant { Order, Preview }

@Composable
fun MenuModal(
    booking: Booking,
    variant: MenuVariant,
    onDismiss: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val menu = remember(booking.id) { menuFor(booking) }
    val isPreview = variant == MenuVariant.Preview
    val priceFmt = remember { java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US) }

    BottomModalSheet(onDismiss = onDismiss) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
            ) {
                AsyncImage(
                    model = booking.image,
                    contentDescription = booking.restaurant,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    RestaurantColors.Base.black.copy(alpha = 0.25f),
                                    RestaurantColors.Base.black.copy(alpha = 0.78f),
                                ),
                            ),
                        ),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(RestaurantColors.Base.whiteAlpha(0.18f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MenuBook,
                            contentDescription = null,
                            tint = RestaurantColors.Base.white,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = stringResource(if (isPreview) I18nR.string.menu_modal_chip_preview else I18nR.string.menu_modal_chip_order),
                            color = RestaurantColors.Base.white,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = booking.restaurant,
                        color = RestaurantColors.Base.white,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                    )
                    Text(
                        text = stringResource(I18nR.string.menu_modal_subtitle),
                        color = RestaurantColors.Base.white.copy(alpha = 0.78f),
                        fontSize = 13.sp,
                        maxLines = 1,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = stringResource(if (isPreview) I18nR.string.menu_modal_title_preview else I18nR.string.menu_modal_title_order),
                        color = palette.foreground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = stringResource(I18nR.string.menu_modal_dishes_count, menu.size),
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(palette.brand.copy(alpha = 0.10f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = booking.cuisine.split(" ").firstOrNull().orEmpty(),
                        color = palette.brand,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(menu, key = { _, dish -> dish.name }) { index, dish ->
                    MenuRow(
                        dish = dish,
                        priceText = priceFmt.format(dish.price),
                        index = index,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                MenuDoneButton(
                    text = stringResource(I18nR.string.menu_modal_done),
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MenuRow(
    dish: MenuItemDish,
    priceText: String,
    index: Int,
) {
    val palette = LocalRestaurantPalette.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val translateY by animateFloatAsState(
        targetValue = if (visible) 0f else 8f,
        animationSpec = tween(durationMillis = 220, delayMillis = index * 35),
        label = "menu_row_y",
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 220, delayMillis = index * 35),
        label = "menu_row_alpha",
    )
    Row(
        modifier = Modifier
            .graphicsLayer {
                translationY = translateY
                this.alpha = alpha
            }
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, palette.border, RoundedCornerShape(20.dp))
            .background(palette.cardSurface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(palette.mutedSurface),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = dish.emoji, fontSize = 20.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = dish.name,
                    color = palette.foreground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                )
                if (dish.popular) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = palette.warning,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            Text(
                text = dish.description,
                color = palette.mutedForeground,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2,
            )
        }
        Text(
            text = priceText,
            color = palette.brand,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun MenuDoneButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(shape)
            .background(palette.brand)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = RestaurantColors.Base.white,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}
