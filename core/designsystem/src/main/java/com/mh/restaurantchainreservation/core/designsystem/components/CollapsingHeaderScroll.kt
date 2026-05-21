package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.Modifier
import kotlin.math.max
import kotlin.math.min

/**
 * Scroll state for collapsing headers: list scroll drives collapse when content is tall
 * enough; extra overscroll collapse when content is short, reset when back at top.
 */
@Stable
class CollapsingHeaderScrollState(
    val collapseRangePx: Float,
) {
    var overscrollCollapsePx by mutableFloatStateOf(0f)

    fun collapseProgress(listState: LazyListState): Float =
        collapseProgress(
            firstIndex = listState.firstVisibleItemIndex,
            scrollOffset = listState.firstVisibleItemScrollOffset,
        )

    fun collapseProgress(gridState: LazyGridState): Float =
        collapseProgress(
            firstIndex = gridState.firstVisibleItemIndex,
            scrollOffset = gridState.firstVisibleItemScrollOffset,
        )

    private fun collapseProgress(firstIndex: Int, scrollOffset: Int): Float {
        if (collapseRangePx <= 0f) return 0f
        if (firstIndex > 0) return 1f
        if (scrollOffset > 0) {
            return (scrollOffset / collapseRangePx).coerceIn(0f, 1f)
        }
        return (overscrollCollapsePx / collapseRangePx).coerceIn(0f, 1f)
    }

    fun listNestedScrollConnection(listState: LazyListState): NestedScrollConnection =
        collapsingNestedScrollConnection(
            isAtTop = { listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0 },
            clearOverscrollWhenScrolled = { listState.firstVisibleItemScrollOffset > 0 },
        )

    fun gridNestedScrollConnection(gridState: LazyGridState): NestedScrollConnection =
        collapsingNestedScrollConnection(
            isAtTop = { gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset == 0 },
            clearOverscrollWhenScrolled = { gridState.firstVisibleItemScrollOffset > 0 },
        )

    private fun collapsingNestedScrollConnection(
        isAtTop: () -> Boolean,
        clearOverscrollWhenScrolled: () -> Boolean,
    ): NestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.y
            if (delta > 0f && overscrollCollapsePx > 0f) {
                val consumed = min(delta, overscrollCollapsePx)
                overscrollCollapsePx -= consumed
                return Offset(0f, consumed)
            }
            return Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            val delta = available.y
            if (delta < 0f && isAtTop()) {
                val room = collapseRangePx - overscrollCollapsePx
                if (room > 0f) {
                    val collapseConsumed = max(delta, -room)
                    overscrollCollapsePx -= collapseConsumed
                    return Offset(0f, collapseConsumed)
                }
            }
            return Offset.Zero
        }
    }

    @Composable
    fun BindListResetOnShortContent(listState: LazyListState) {
        LaunchedEffect(listState, collapseRangePx) {
            snapshotFlow {
                listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
            }.collect { (index, offset) ->
                if (offset > 0) {
                    overscrollCollapsePx = 0f
                } else if (index == 0 && offset == 0 && !listState.canScrollVertically()) {
                    overscrollCollapsePx = 0f
                }
            }
        }
    }

    @Composable
    fun BindGridResetOnShortContent(gridState: LazyGridState) {
        LaunchedEffect(gridState, collapseRangePx) {
            snapshotFlow {
                gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset
            }.collect { (index, offset) ->
                if (gridState.firstVisibleItemScrollOffset > 0) {
                    overscrollCollapsePx = 0f
                } else if (index == 0 && offset == 0 && !gridState.canScrollVertically()) {
                    overscrollCollapsePx = 0f
                }
            }
        }
    }
}

private fun LazyListState.canScrollVertically(): Boolean {
    val info = layoutInfo
    if (info.totalItemsCount == 0) return false
    val last = info.visibleItemsInfo.lastOrNull() ?: return false
    return last.index < info.totalItemsCount - 1 ||
        last.offset + last.size > info.viewportEndOffset + 1
}

private fun LazyGridState.canScrollVertically(): Boolean {
    val info = layoutInfo
    if (info.totalItemsCount == 0) return false
    val last = info.visibleItemsInfo.lastOrNull() ?: return false
    return last.index < info.totalItemsCount - 1 ||
        last.offset.y + last.size.height > info.viewportEndOffset + 1
}

@Composable
fun rememberCollapsingHeaderScrollState(collapseRangePx: Float): CollapsingHeaderScrollState =
    remember(collapseRangePx) { CollapsingHeaderScrollState(collapseRangePx) }

fun Modifier.collapsingHeaderListScroll(
    state: CollapsingHeaderScrollState,
    listState: LazyListState,
): Modifier = nestedScroll(state.listNestedScrollConnection(listState))

fun Modifier.collapsingHeaderGridScroll(
    state: CollapsingHeaderScrollState,
    gridState: LazyGridState,
): Modifier = nestedScroll(state.gridNestedScrollConnection(gridState))
