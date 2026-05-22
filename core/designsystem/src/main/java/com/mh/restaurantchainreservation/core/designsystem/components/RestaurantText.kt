package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantTextColor
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantTextRole
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantTextStyles

/**
 * Typed text using the Airbnb-inspired [RestaurantTextRole] + [RestaurantTextColor] system.
 *
 * ```kotlin
 * RestaurantText("Account settings", RestaurantTextRole.SectionTitle)
 * RestaurantText(locationAddress, RestaurantTextRole.Caption, color = RestaurantTextColor.Sub)
 * ```
 */
@Composable
fun RestaurantText(
    text: String,
    role: RestaurantTextRole,
    modifier: Modifier = Modifier,
    color: RestaurantTextColor = RestaurantTextColor.Main,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    style: TextStyle? = null,
) {
    val resolved = RestaurantTextStyles.resolve(role, color)
    Text(
        text = text,
        modifier = modifier,
        style = if (style != null) resolved.merge(style) else resolved,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
    )
}
