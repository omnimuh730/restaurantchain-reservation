package com.mh.restaurantchainreservation.core.designsystem.components.icons

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.mh.restaurantchainreservation.core.designsystem.R

/** QR Pay glyph for the bottom-nav FAB (512×512 source art, tinted with [color]). */
@Composable
fun QrPayNavIcon(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    contentDescription: String? = null,
) {
    Icon(
        painter = painterResource(R.drawable.ic_qr_pay_nav),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = color,
    )
}
