package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DsTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        if (subtitle == null) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
            )
        } else {
            androidx.compose.foundation.layout.Column {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
