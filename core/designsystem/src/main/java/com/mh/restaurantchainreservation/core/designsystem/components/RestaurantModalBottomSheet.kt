package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

/**
 * Material [ModalBottomSheet] matching the add-new-card flow: animates up from the bottom,
 * centered drag handle, 28dp top corners, app surface colors.
 *
 * Sheet height is controlled only via the drag handle; body content does not initiate drag.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = containerColor ?: palette.cardSurface,
        contentColor = palette.foreground,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { CenteredMaterialDragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .blockModalSheetBodyDrag(),
            content = content,
        )
    }
}
