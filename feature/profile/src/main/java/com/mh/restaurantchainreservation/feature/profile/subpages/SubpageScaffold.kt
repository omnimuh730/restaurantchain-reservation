package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
    headerActions: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val backLabel = stringResource(I18nR.string.common_action_back)
    val contentPad = contentHorizontalPadding ?: horizontalPadding
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
        if (scrollable) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = contentPad.dp),
                content = content,
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = contentPad.dp),
                content = content,
            )
        }
    }
}
