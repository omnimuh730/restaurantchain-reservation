package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingSubpageScreenHeader
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingTitleHeaderMetrics
import com.mh.restaurantchainreservation.core.designsystem.components.PageHeader
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

@Composable
fun SubpageScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Int = 20,
    /** When set, applied to scrollable/main content instead of [horizontalPadding] (header still uses [horizontalPadding]). */
    contentHorizontalPadding: Int? = null,
    scrollable: Boolean = true,
    subtitle: String? = null,
    headerActions: (@Composable (collapseProgress: Float) -> Unit)? = null,
    /** Defaults match profile/dining hub (34→20sp). Use slightly lower values for a subtler title. */
    titleFontExpandedSp: Float = 34f,
    titleFontCollapsedSp: Float = 20f,
    titleLineHeightExpandedSp: Float = 40f,
    titleLineHeightCollapsedSp: Float = 24f,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val backLabel = stringResource(I18nR.string.common_action_back)
    val contentPad = contentHorizontalPadding ?: horizontalPadding

    if (scrollable) {
        val scroll = rememberScrollState()
        val density = LocalDensity.current
        val expandedBodyDp =
            CollapsingTitleHeaderMetrics.subpageExpandedBodyHeight(subtitle != null)
        val collapseRangePx = remember(density, subtitle != null) {
            with(density) {
                (expandedBodyDp - CollapsingTitleHeaderMetrics.collapsedBodyHeight).toPx()
            }
                .coerceAtLeast(1f)
        }
        val collapseProgress = (scroll.value / collapseRangePx).coerceIn(0f, 1f)
        val statusBarTopDp = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
        val topInsetSpacerHeight = expandedBodyDp + statusBarTopDp

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(palette.cardSurface),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll),
            ) {
                Spacer(Modifier.height(topInsetSpacerHeight))
                Column(
                    modifier = Modifier.padding(horizontal = contentPad.dp),
                    content = content,
                )
            }
            CollapsingSubpageScreenHeader(
                title = title,
                collapseProgress = collapseProgress,
                onBack = onBack,
                backContentDescription = backLabel,
                subtitle = subtitle,
                actions = headerActions,
                horizontalPaddingDp = horizontalPadding,
                titleFontExpandedSp = titleFontExpandedSp,
                titleFontCollapsedSp = titleFontCollapsedSp,
                titleLineHeightExpandedSp = titleLineHeightExpandedSp,
                titleLineHeightCollapsedSp = titleLineHeightCollapsedSp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(2f),
            )
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
                actions = headerActions?.let { hp ->
                    { hp(0f) }
                },
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
