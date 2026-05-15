package com.mh.restaurantchainreservation.feature.profile.data

import com.mh.restaurantchainreservation.feature.profile.hub.HubCardPattern
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardThemeId
import com.mh.restaurantchainreservation.feature.profile.hub.hubCardThemeSpec

/**
 * Single mock schema for credit cards across Profile hub, wallet strip, and Credit cards page.
 */
internal object MockProfileCreditCards {
    const val HOLDER = "Alex Chen"

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

    val cards: List<CardDef> = listOf(
        CardDef(
            id = "card-main",
            nickname = "Tonight Black",
            number = "4890123456784242",
            expiry = "08/29",
            themeId = HubCardThemeId.Rose,
            pattern = hubCardThemeSpec(HubCardThemeId.Rose).pattern,
            krwBalance = 820_000.0,
            usdBalance = 216.45,
        ),
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

    fun totalKrwLong(): Long = cards.sumOf { it.krwBalanceLong }

    fun totalUsd(): Double = cards.sumOf { it.usdBalance }

    /** Primary (first) card last four for wallet / masked lines. */
    fun primaryLastFour(): String = cards.first().lastFour
}
