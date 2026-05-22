package com.mh.restaurantchainreservation.feature.profile.hub

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantFontFamily

private val TileCorner = 32.dp

/**
 * Placeholder tile matching credit card aspect — light pink field, dashed brand border,
 * gradient + button, “Add new card” / “Open a new credit card” (hub + full cards list).
 */
@Composable
fun AddNewCreditCardTile(modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val brand = palette.brand
    val topWash = RestaurantColors.Overlay.imageCaption
    val bottomWash = brand.copy(alpha = 0.12f)
    val borderColor = brand.copy(alpha = 0.58f)

    Box(
        modifier = modifier
            .aspectRatio(1.58f)
            .clip(RoundedCornerShape(TileCorner))
            .background(
                Brush.verticalGradient(
                    colors = listOf(topWash, bottomWash),
                ),
            )
            .drawBehind {
                val w = 2.5f * density
                drawRoundRect(
                    color = borderColor,
                    topLeft = Offset(w * 0.5f, w * 0.5f),
                    size = Size(size.width - w, size.height - w),
                    cornerRadius = CornerRadius(
                        x = (32.dp.toPx() - w * 0.5f).coerceAtLeast(4f),
                        y = (32.dp.toPx() - w * 0.5f).coerceAtLeast(4f),
                    ),
                    style = Stroke(
                        width = w,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(11f * density, 7f * density), 0f),
                    ),
                )
            },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                brand.copy(alpha = 0.95f),
                                RestaurantColors.Avatar.rose.copy(alpha = 0.92f),
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = RestaurantColors.Base.white,
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Add new card",
                color = brand.copy(alpha = 0.92f),
                fontFamily = RestaurantFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Open a new credit card",
                color = brand.copy(alpha = 0.52f),
                fontFamily = RestaurantFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
