package com.mh.restaurantchainreservation.feature.profile.data

import com.mh.restaurantchainreservation.feature.profile.hub.HubCardPattern
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardThemeId
import com.mh.restaurantchainreservation.feature.profile.hub.hubCardThemeSpec

/**
 * Mock schema for the main wallet account and virtual credit cards across Profile.
 */
internal object MockProfileCreditCards {
    const val HOLDER = "Alex Chen"
    const val WALLET_ACCOUNT_LAST_FOUR = "4242"

    const val DEFAULT_WALLET_KRW = 820_000.0
    const val DEFAULT_WALLET_USD = 216.45

    internal data class CardDef(
        val id: String,
        val nickname: String,
        val number: String,
        val expiry: String,
        val themeId: HubCardThemeId,
        val pattern: HubCardPattern,
        val krwBalance: Double,
        val usdBalance: Double,
    ) {
        val lastFour: String get() = number.takeLast(4).ifEmpty { "0000" }
        val showDualBalance: Boolean get() = krwBalance > 0.0 && usdBalance > 0.0
        val krwBalanceLong: Long get() = krwBalance.toLong()
    }

    /** Virtual credit cards only — the main wallet is not included here. */
    val cards: List<CardDef> = listOf(
        CardDef(
            id = "card-travel",
            nickname = "Travel Card",
            number = "5339123411119021",
            expiry = "11/30",
            themeId = HubCardThemeId.Ocean,
            pattern = HubCardPattern.Rays,
            krwBalance = 120_000.0,
            usdBalance = 84.0,
        ),
    )
}
