package com.mh.restaurantchainreservation.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.components.DsTopBar
import com.mh.restaurantchainreservation.feature.profile.hub.ProfileHubScreen
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

object ProfileRoutes {
    const val Home = "profile"
    const val Settings = "profile/settings"
}

@Composable
fun ProfileHomeScreen(
    onOpenSettings: () -> Unit,
    onOpenNotifications: () -> Unit,
    @Suppress("UNUSED_PARAMETER") onSwitchKorean: () -> Unit = {},
    @Suppress("UNUSED_PARAMETER") onSwitchEnglish: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    ProfileHubScreen(
        onOpenSettings = onOpenSettings,
        onOpenNotifications = onOpenNotifications,
        modifier = modifier,
    )
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
