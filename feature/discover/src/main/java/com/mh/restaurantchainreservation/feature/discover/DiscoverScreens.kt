package com.mh.restaurantchainreservation.feature.discover

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
import com.mh.restaurantchainreservation.core.designsystem.components.DsButton
import com.mh.restaurantchainreservation.core.designsystem.components.DsCard
import com.mh.restaurantchainreservation.core.designsystem.components.DsTopBar
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

object DiscoverRoutes {
    const val Home = "discover"
}

@Composable
fun DiscoverHomeScreen(
    onOpenSearch: () -> Unit,
    onOpenRestaurant: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DsTopBar(
            title = stringResource(I18nR.string.title_discover),
            subtitle = stringResource(I18nR.string.desc_discover_subtitle),
        )
        DsCard {
            Text(stringResource(I18nR.string.desc_discover_search_hint), style = MaterialTheme.typography.bodyLarge)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) {
                DsButton(text = stringResource(I18nR.string.action_open_search), onClick = onOpenSearch)
                DsButton(text = stringResource(I18nR.string.action_open_restaurant_detail), onClick = onOpenRestaurant)
            }
        }
    }
}
