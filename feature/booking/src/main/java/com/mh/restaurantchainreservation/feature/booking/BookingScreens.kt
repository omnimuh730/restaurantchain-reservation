package com.mh.restaurantchainreservation.feature.booking

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

object BookingRoutes {
    const val RestaurantDetail = "discover/restaurant/{restaurantId}"
    const val BookTable = "discover/restaurant/{restaurantId}/book"
}

@Composable
fun RestaurantDetailScreen(
    onBookNow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DsTopBar(title = stringResource(I18nR.string.title_restaurant_detail))
        DsCard {
            Text(stringResource(I18nR.string.desc_booking_detail), style = MaterialTheme.typography.bodyLarge)
            DsButton(text = stringResource(I18nR.string.action_book_table), onClick = onBookNow, modifier = Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
fun BookTableScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DsTopBar(title = stringResource(I18nR.string.title_book_table))
        DsCard {
            Text(stringResource(I18nR.string.desc_booking_steps), style = MaterialTheme.typography.bodyLarge)
            DsButton(text = stringResource(I18nR.string.action_complete_booking), onClick = onComplete, modifier = Modifier.padding(top = 12.dp))
        }
    }
}
