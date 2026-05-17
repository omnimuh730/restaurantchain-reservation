package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

/** Centered pill drag handle for custom bottom sheets ([BottomModalSheet], etc.). */
@Composable
fun ModalSheetDragHandle(
    modifier: Modifier = Modifier,
    width: Dp = 40.dp,
    height: Dp = 4.dp,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .size(width = width, height = height)
                .clip(RoundedCornerShape(2.dp))
                .background(palette.border),
        )
    }
}

/** Material drag handle wrapped so it stays centered on the sheet's top edge. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenteredMaterialDragHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        BottomSheetDefaults.DragHandle()
    }
}
