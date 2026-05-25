package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Centered drag handle for rising bottom sheets. Uses the same Material pill and
 * insets as [RestaurantModalBottomSheet] so spacing stays consistent app-wide.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalSheetDragHandle(
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") width: Dp = 40.dp,
    @Suppress("UNUSED_PARAMETER") height: Dp = 4.dp,
) {
    CenteredMaterialDragHandle(modifier)
}

/** Material drag handle centered on the sheet's top edge (card info modal reference). */
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

/**
 * Prevents sheet drag/dismiss gestures from starting on modal body content.
 * The drag handle remains the only area that can resize or dismiss the sheet.
 */
fun Modifier.blockModalSheetBodyDrag(): Modifier = nestedScroll(
    object : NestedScrollConnection {
        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset = available
    },
)
