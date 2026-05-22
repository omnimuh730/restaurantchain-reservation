package com.mh.restaurantchainreservation.feature.booking

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

@Composable
fun HowReviewsWorkDialog(onClose: () -> Unit) {
    val palette = LocalRestaurantPalette.current

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RestaurantColors.Overlay.scrimMedium)
                .clickable(onClick = onClose),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(palette.cardSurface)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {},
                    )
                    .padding(24.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = "How reviews work",
                        color = palette.foreground,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = palette.foreground)
                    }
                }

                val paragraphs = listOf(
                    "Reviews come from guests who have dined at this restaurant.",
                    "Reviews are sorted by recency by default. You can also sort by highest or lowest rating.",
                    "Only guests who completed a reservation can leave a review. Reviews are checked for policy issues before they appear.",
                )
                paragraphs.forEach { paragraph ->
                    Text(
                        text = paragraph,
                        color = palette.foreground.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        lineHeight = 28.sp,
                        modifier = Modifier.padding(top = 20.dp),
                    )
                }

                Text(
                    text = "Learn more in our Help Center",
                    color = palette.foreground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
        }
    }
}
