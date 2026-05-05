package com.mh.restaurantchainreservation.feature.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.components.DsBottomSheet
import com.mh.restaurantchainreservation.core.designsystem.components.DsButton
import com.mh.restaurantchainreservation.core.designsystem.components.DsCard
import com.mh.restaurantchainreservation.core.designsystem.components.DsTopBar
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

object SearchRoutes {
    const val Results = "discover/search"
}

@Composable
fun SearchResultsScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DsTopBar(title = stringResource(I18nR.string.title_search_results))
        DsCard {
            Text(
                text = stringResource(I18nR.string.desc_search_sheet),
                style = MaterialTheme.typography.bodyLarge,
            )
            DsButton(
                text = stringResource(I18nR.string.action_filter),
                onClick = { },
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
fun SearchFilterSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
) {
    DsBottomSheet(visible = visible, onDismiss = onDismiss) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(I18nR.string.desc_search_sheet), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(I18nR.string.desc_filter_placeholder), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
