package com.mh.restaurantchainreservation.feature.profile

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

object ProfileRoutes {
    const val Home = "profile"
    const val Settings = "profile/settings"
}

@Composable
fun ProfileHomeScreen(
    onOpenSettings: () -> Unit,
    onOpenNotifications: () -> Unit,
    onSwitchKorean: () -> Unit,
    onSwitchEnglish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DsTopBar(title = stringResource(I18nR.string.title_profile))
        DsCard {
            Text(stringResource(I18nR.string.desc_profile_wallet), style = MaterialTheme.typography.bodyLarge)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) {
                DsButton(text = stringResource(I18nR.string.action_settings), onClick = onOpenSettings)
                DsButton(text = stringResource(I18nR.string.action_notifications), onClick = onOpenNotifications)
                DsButton(text = stringResource(I18nR.string.action_switch_to_korean), onClick = onSwitchKorean)
                DsButton(text = stringResource(I18nR.string.action_switch_to_english), onClick = onSwitchEnglish)
            }
        }
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        DsTopBar(title = stringResource(I18nR.string.title_settings))
    }
}
