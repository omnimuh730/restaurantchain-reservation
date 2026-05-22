package com.mh.restaurantchainreservation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.mh.restaurantchainreservation.core.designsystem.theme.RestaurantTheme
import com.mh.restaurantchainreservation.core.i18n.LocaleManager
import com.mh.restaurantchainreservation.core.model.AuthSessionStore
import com.mh.restaurantchainreservation.core.model.DailyBonusStore
import com.mh.restaurantchainreservation.core.model.LocalDataSyncStore
import com.mh.restaurantchainreservation.core.navigation.RestaurantNavHost
import com.mh.restaurantchainreservation.feature.profile.data.ProfileWalletStore

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        LocaleManager.initialize(applicationContext)
        AuthSessionStore.initialize(applicationContext)
        LocalDataSyncStore.init(applicationContext)
        super.onCreate(savedInstanceState)
        DailyBonusStore.init(applicationContext)
        ProfileWalletStore.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            RestaurantTheme {
                RestaurantNavHost(windowSizeClass = windowSizeClass)
            }
        }
    }
}
