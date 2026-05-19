package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.PageHeader
import com.mh.restaurantchainreservation.core.designsystem.components.SubpageCollapsingTopBar
import com.mh.restaurantchainreservation.core.designsystem.components.rememberSubpageCollapsingTopBarScrollBehavior
import com.mh.restaurantchainreservation.core.designsystem.components.trackBottomNavScroll
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

@Composable
private fun SubpageSubtitle(text: String, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = text,
        color = palette.mutedForeground,
        fontSize = 14.sp,
        modifier = modifier.padding(bottom = 12.dp),
    )
}

/**
 * Shared layout for profile sub-pages and wishlist collection detail: [LargeTopAppBar] that
 * collapses when the list below scrolls (same structure as Recently searched).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubpageCollapsingLayout(
    title: String,
    onBack: () -> Unit,
    backLabel: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    headerActions: (@Composable () -> Unit)? = null,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
    content: LazyListScope.() -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val scrollBehavior = rememberSubpageCollapsingTopBarScrollBehavior()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface),
    ) {
        SubpageCollapsingTopBar(
            title = title,
            onBack = onBack,
            backContentDescription = backLabel,
            scrollBehavior = scrollBehavior,
            actions = { headerActions?.invoke() },
        )
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .trackBottomNavScroll(),
            contentPadding = contentPadding,
        ) {
            if (subtitle != null) {
                item(key = "subpage_subtitle") {
                    SubpageSubtitle(subtitle, Modifier.padding(top = 4.dp))
                }
            }
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubpageLazyScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Int = 20,
    contentHorizontalPadding: Int? = null,
    subtitle: String? = null,
    headerActions: (@Composable () -> Unit)? = null,
    listState: LazyListState = rememberLazyListState(),
    bottomContentPadding: Dp = 24.dp,
    content: LazyListScope.() -> Unit,
) {
    val backLabel = stringResource(I18nR.string.common_action_back)
    val contentPad = contentHorizontalPadding ?: horizontalPadding
    SubpageCollapsingLayout(
        title = title,
        onBack = onBack,
        backLabel = backLabel,
        modifier = modifier,
        subtitle = subtitle,
        headerActions = headerActions,
        listState = listState,
        contentPadding = PaddingValues(
            start = contentPad.dp,
            end = contentPad.dp,
            top = 4.dp,
            bottom = bottomContentPadding,
        ),
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
    headerActions: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val backLabel = stringResource(I18nR.string.common_action_back)
    val contentPad = contentHorizontalPadding ?: horizontalPadding

    if (scrollable) {
        SubpageCollapsingLayout(
            title = title,
            onBack = onBack,
            backLabel = backLabel,
            modifier = modifier,
            subtitle = subtitle,
            headerActions = headerActions,
            contentPadding = PaddingValues(
                start = contentPad.dp,
                end = contentPad.dp,
                top = 4.dp,
                bottom = 24.dp,
            ),
        ) {
            item(key = "subpage_body") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    content()
                }
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(palette.cardSurface)
                .statusBarsPadding()
                .padding(top = 16.dp),
        ) {
            PageHeader(
                title = title,
                onBack = onBack,
                backContentDescription = backLabel,
                subtitle = subtitle,
                actions = headerActions,
                modifier = Modifier.padding(horizontal = horizontalPadding.dp),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = contentPad.dp),
                content = content,
            )
        }
    }
}
