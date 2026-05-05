package com.mh.restaurantchainreservation.feature.dining

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

object DiningRoutes {
    const val Home = "dining"
    const val Detail = "dining/{bookingId}"
    const val Enjoy = "dining/{bookingId}/enjoy"
}

@Composable
fun DiningHomeScreen(
    onOpenDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DsTopBar(title = stringResource(I18nR.string.title_dining))
        DsCard {
            Text(stringResource(I18nR.string.desc_dining_tabs), style = MaterialTheme.typography.bodyLarge)
            DsButton(text = stringResource(I18nR.string.action_open_booking_detail), onClick = onOpenDetail, modifier = Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
fun DiningDetailScreen(
    onOpenEnjoy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DsTopBar(title = stringResource(I18nR.string.title_dining_detail))
        DsCard {
            Text(stringResource(I18nR.string.desc_dining_manage), style = MaterialTheme.typography.bodyLarge)
            DsButton(text = stringResource(I18nR.string.action_open_enjoy_mode), onClick = onOpenEnjoy, modifier = Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
fun DiningEnjoyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        DsTopBar(title = stringResource(I18nR.string.title_enjoy_meal))
    }
}
