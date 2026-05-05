package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class DsButtonStyle {
    Primary,
    Secondary,
    Outline,
}

@Composable
fun DsButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: DsButtonStyle = DsButtonStyle.Primary,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(999.dp)
    val content: @Composable RowScope.() -> Unit = {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }

    when (style) {
        DsButtonStyle.Outline -> OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            content = content,
        )

        DsButtonStyle.Secondary -> Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            content = content,
        )

        DsButtonStyle.Primary -> Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            content = content,
        )
    }
}
