package com.mh.restaurantchainreservation.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.mh.restaurantchainreservation.core.designsystem.components.DsButton
import com.mh.restaurantchainreservation.core.designsystem.components.DsCard
import com.mh.restaurantchainreservation.core.designsystem.components.DsTopBar
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

object AuthRoutes {
    const val Root = "auth"
    const val Login = "auth/login"
    const val Register = "auth/register"
    const val Forgot = "auth/forgot"
}

@Composable
fun LoginScreen(
    onNavigateRegister: () -> Unit,
    onNavigateForgot: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DsTopBar(title = stringResource(I18nR.string.title_login))
        DsCard {
            Text(text = stringResource(I18nR.string.desc_sign_in), style = MaterialTheme.typography.bodyLarge)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) {
                DsButton(text = stringResource(I18nR.string.action_create_account), onClick = onNavigateRegister)
                DsButton(text = stringResource(I18nR.string.action_forgot_password), onClick = onNavigateForgot)
            }
        }
    }
}

@Composable
fun RegisterScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        DsTopBar(title = stringResource(I18nR.string.title_register))
    }
}

@Composable
fun ForgotPasswordScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        DsTopBar(title = stringResource(I18nR.string.title_forgot_password))
    }
}
