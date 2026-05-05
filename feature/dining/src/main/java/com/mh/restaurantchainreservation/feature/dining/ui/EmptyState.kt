package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

@Composable
fun EmptyDiningState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onAction: (() -> Unit)? = null,
) {
    val palette = LocalRestaurantPalette.current
    val cardShape = RoundedCornerShape(28.dp)
    DiningHeaderFadeIn {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .shadow(elevation = 4.dp, shape = cardShape, ambientColor = Color.Black.copy(alpha = 0.05f))
                .clip(cardShape)
                .border(1.dp, palette.border, cardShape)
                .background(palette.cardSurface)
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(palette.brand.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = palette.brand,
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = title,
                color = palette.foreground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = description,
                color = palette.mutedForeground,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))
            ChipButton(
                text = stringResource(I18nR.string.dining_empty_find_restaurant),
                icon = Icons.Outlined.CheckCircle,
                onClick = { onAction?.invoke() },
                variant = ChipVariant.Primary,
            )
        }
    }
}
