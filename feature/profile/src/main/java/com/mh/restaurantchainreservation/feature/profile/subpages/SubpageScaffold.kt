package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingSubpageScreenHeader
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingTitleHeaderMetrics
import com.mh.restaurantchainreservation.core.designsystem.components.LocalNavContentBottomPadding
import com.mh.restaurantchainreservation.core.designsystem.components.collapsingHeaderListScroll
import com.mh.restaurantchainreservation.core.designsystem.components.rememberCollapsingHeaderScrollState
import com.mh.restaurantchainreservation.core.designsystem.components.trackBottomNavScroll
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

/**
 * Shared layout for profile sub-pages: scroll-linked [CollapsingSubpageScreenHeader]
 * (same back control, title transition, and trailing row as Recently Viewed).
 */
@Composable
private fun SubpageCollapsingLayout(
    title: String,
    onBack: () -> Unit,
    backLabel: String,
    modifier: Modifier = Modifier,
    horizontalPadding: Int = 20,
    subtitle: String? = null,
    headerActions: (@Composable (collapseProgress: Float) -> Unit)? = null,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
    content: LazyListScope.() -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val hasSubtitle = subtitle != null
    val headerExpandedHeight = CollapsingTitleHeaderMetrics.subpageExpandedBodyHeight(hasSubtitle)
    val collapseRangePx = remember(density, headerExpandedHeight) {
        with(density) {
            (headerExpandedHeight - CollapsingTitleHeaderMetrics.collapsedBodyHeight).toPx()
        }.coerceAtLeast(1f)
    }
    val headerScroll = rememberCollapsingHeaderScrollState(collapseRangePx)
    headerScroll.BindListResetOnShortContent(listState)
    val statusBarTopDp = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
    val collapseProgress by remember {
        derivedStateOf { headerScroll.collapseProgress(listState) }
    }
    val topContentInset by remember {
        derivedStateOf {
            CollapsingTitleHeaderMetrics.collapsingTopContentInset(
                collapseProgress = collapseProgress,
                expandedBodyHeight = headerExpandedHeight,
                statusBarTopDp = statusBarTopDp,
                firstVisibleItemIndex = listState.firstVisibleItemIndex,
                firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .trackBottomNavScroll()
                .collapsingHeaderListScroll(headerScroll, listState),
            contentPadding = contentPadding,
        ) {
            item(key = "subpage_top_inset") {
                Spacer(Modifier.height(topContentInset))
            }
            content()
        }

        CollapsingSubpageScreenHeader(
            title = title,
            collapseProgress = collapseProgress,
            onBack = onBack,
            backContentDescription = backLabel,
            subtitle = subtitle,
            horizontalPaddingDp = horizontalPadding,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(2f),
            actions = headerActions,
        )
    }
}

@Composable
fun SubpageLazyScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Int = 20,
    contentHorizontalPadding: Int? = null,
    subtitle: String? = null,
    headerActions: (@Composable (collapseProgress: Float) -> Unit)? = null,
    listState: LazyListState = rememberLazyListState(),
    bottomContentPadding: Dp = 24.dp,
    content: LazyListScope.() -> Unit,
) {
    val backLabel = stringResource(I18nR.string.common_action_back)
    val contentPad = contentHorizontalPadding ?: horizontalPadding
    val navBottomPadding = LocalNavContentBottomPadding.current
    SubpageCollapsingLayout(
        title = title,
        onBack = onBack,
        backLabel = backLabel,
        modifier = modifier,
        horizontalPadding = horizontalPadding,
        subtitle = subtitle,
        headerActions = headerActions,
        listState = listState,
        contentPadding = PaddingValues(
            start = contentPad.dp,
            end = contentPad.dp,
            top = 4.dp,
            bottom = bottomContentPadding + navBottomPadding,
        ),
        content = content,
    )
}

@Composable
fun SubpageScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Int = 20,
    /** When set, applied to scrollable/main content instead of [horizontalPadding]. */
    contentHorizontalPadding: Int? = null,
    scrollable: Boolean = true,
    subtitle: String? = null,
    headerActions: (@Composable (collapseProgress: Float) -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val backLabel = stringResource(I18nR.string.common_action_back)
    val contentPad = contentHorizontalPadding ?: horizontalPadding
    val navBottomPadding = LocalNavContentBottomPadding.current

    if (scrollable) {
        SubpageCollapsingLayout(
            title = title,
            onBack = onBack,
            backLabel = backLabel,
            modifier = modifier,
            horizontalPadding = horizontalPadding,
            subtitle = subtitle,
            headerActions = headerActions,
            contentPadding = PaddingValues(
                start = contentPad.dp,
                end = contentPad.dp,
                top = 4.dp,
                bottom = 24.dp + navBottomPadding,
            ),
        ) {
            item(key = "subpage_body") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    content()
                }
            }
        }
    } else {
        val density = LocalDensity.current
        val hasSubtitle = subtitle != null
        val headerExpandedHeight = CollapsingTitleHeaderMetrics.subpageExpandedBodyHeight(hasSubtitle)
        val statusBarTopDp = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
        val topInset = statusBarTopDp + headerExpandedHeight + 8.dp

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(palette.pageBackground),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = contentPad.dp),
            ) {
                Spacer(Modifier.height(topInset))
                content()
            }
            CollapsingSubpageScreenHeader(
                title = title,
                collapseProgress = 0f,
                onBack = onBack,
                backContentDescription = backLabel,
                subtitle = subtitle,
                horizontalPaddingDp = horizontalPadding,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(2f),
                actions = headerActions,
            )
        }
    }
}
