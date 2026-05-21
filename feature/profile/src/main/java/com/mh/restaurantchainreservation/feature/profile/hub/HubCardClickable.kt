package com.mh.restaurantchainreservation.feature.profile.hub

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

/** Credit-card carousel taps without ripple or pressed overlay. */
@Composable
internal fun Modifier.hubCardClickable(
    onClick: () -> Unit,
    onClickLabel: String? = null,
    role: Role = Role.Button,
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return clickable(
        interactionSource = interactionSource,
        indication = null,
        role = role,
        onClickLabel = onClickLabel,
        onClick = onClick,
    )
}
