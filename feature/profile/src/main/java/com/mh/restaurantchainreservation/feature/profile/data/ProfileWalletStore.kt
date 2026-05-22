package com.mh.restaurantchainreservation.feature.profile.data

import android.content.Context
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardPattern
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardThemeId
import com.mh.restaurantchainreservation.feature.profile.subpages.components.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import kotlin.math.roundToLong

data class WalletCardRecord(
    val id: String,
    val nickname: String,
    val holder: String,
    val number: String,
    val expiry: String,
    val themeId: HubCardThemeId,
    val pattern: HubCardPattern,
    val frozen: Boolean = false,
    val externalUse: Boolean = true,
    val krwBalance: Double,
    val usdBalance: Double,
) {
    val lastFour: String get() = number.takeLast(4).ifEmpty { "0000" }
    val showDualBalance: Boolean get() = krwBalance > 0.0 && usdBalance > 0.0
}

data class WalletLedgerEntry(
    val id: String,
    val cardId: String?,
    val label: String,
    val amountDisplay: String,
    val positive: Boolean,
    val timestampMs: Long = System.currentTimeMillis(),
)

sealed class WalletMutationResult {
    data class Success(val message: String) : WalletMutationResult()
    data class Error(val message: String) : WalletMutationResult()
}

/**
 * Profile wallet + credit cards: balances, top-up, withdraw, send, gift, and ledger.
 */
object ProfileWalletStore {
    private const val PrefsName = "profile_wallet_prefs"
    private const val CardsKey = "wallet_cards_v1"

    private val _cards = MutableStateFlow<List<WalletCardRecord>>(emptyList())
    val cards: StateFlow<List<WalletCardRecord>> = _cards.asStateFlow()

    private val _ledger = MutableStateFlow<List<WalletLedgerEntry>>(emptyList())
    val ledger: StateFlow<List<WalletLedgerEntry>> = _ledger.asStateFlow()

    private var prefsLoaded = false

    fun init(context: Context) {
        if (prefsLoaded) return
        prefsLoaded = true
        val prefs = context.applicationContext.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
        val stored = prefs.getString(CardsKey, null)
        _cards.value = if (stored != null) {
            decodeCards(stored) ?: defaultCards()
        } else {
            defaultCards()
        }
        if (_ledger.value.isEmpty()) {
            _ledger.value = seedLedger(_cards.value)
        }
    }

    fun totalKrwLong(): Long = _cards.value.sumOf { it.krwBalance.roundToLong() }

    fun totalUsd(): Double = _cards.value.sumOf { it.usdBalance }

    fun primaryCard(): WalletCardRecord? = _cards.value.firstOrNull()

    fun cardById(id: String): WalletCardRecord? = _cards.value.firstOrNull { it.id == id }

    fun ledgerForCard(cardId: String, limit: Int = 12): List<WalletLedgerEntry> =
        _ledger.value.filter { it.cardId == cardId }.take(limit)

    fun balanceForCurrency(currency: Currency): Double = when (currency) {
        Currency.KRW -> totalKrwLong().toDouble()
        Currency.USD -> totalUsd()
    }

    fun cardBalanceForCurrency(card: WalletCardRecord, currency: Currency): Double = when (currency) {
        Currency.KRW -> card.krwBalance
        Currency.USD -> card.usdBalance
    }

    /** Account top-up (Profile → Top up): credits primary card. */
    fun topUpWallet(currency: Currency, amount: Double): WalletMutationResult {
        if (amount <= 0.0) return WalletMutationResult.Error("Enter an amount greater than zero.")
        val primary = primaryCard() ?: return WalletMutationResult.Error("No card on file.")
        return adjustCardBalance(
            cardId = primary.id,
            currency = currency,
            delta = amount,
            ledgerLabel = if (currency == Currency.KRW) "Wallet top up · Domestic" else "Wallet top up · Foreign",
        )
    }

    fun topUpCard(cardId: String, currency: Currency, amount: Double): WalletMutationResult =
        adjustCardBalance(
            cardId = cardId,
            currency = currency,
            delta = amount,
            ledgerLabel = "Card top up",
        )

    fun withdrawCard(cardId: String, currency: Currency, amount: Double): WalletMutationResult =
        adjustCardBalance(
            cardId = cardId,
            currency = currency,
            delta = -amount,
            ledgerLabel = "Withdrawal",
        )

    fun sendFromCard(
        cardId: String,
        currency: Currency,
        amount: Double,
        recipient: String,
    ): WalletMutationResult {
        val to = recipient.trim()
        if (to.isEmpty()) return WalletMutationResult.Error("Enter who you are sending to.")
        return adjustCardBalance(
            cardId = cardId,
            currency = currency,
            delta = -amount,
            ledgerLabel = "Sent to $to",
        )
    }

    /** Gift from wallet (primary card). */
    fun sendGift(currency: Currency, amount: Double, recipient: String): WalletMutationResult {
        val to = recipient.trim()
        if (to.isEmpty()) return WalletMutationResult.Error("Enter a recipient username.")
        val primary = primaryCard() ?: return WalletMutationResult.Error("No card on file.")
        return adjustCardBalance(
            cardId = primary.id,
            currency = currency,
            delta = -amount,
            ledgerLabel = "Gift to $to",
        )
    }

    fun addCard(record: WalletCardRecord) {
        _cards.value = _cards.value + record
        appendLedger(
            cardId = record.id,
            label = "Card opened",
            amountDisplay = "—",
            positive = true,
        )
        persistCards()
    }

    fun updateCard(record: WalletCardRecord) {
        _cards.value = _cards.value.map { if (it.id == record.id) record else it }
        persistCards()
    }

    fun removeCard(cardId: String): WalletMutationResult {
        if (_cards.value.size <= 1) {
            return WalletMutationResult.Error("Keep at least one card on file.")
        }
        _cards.value = _cards.value.filter { it.id != cardId }
        persistCards()
        return WalletMutationResult.Success("Card removed.")
    }

    private fun adjustCardBalance(
        cardId: String,
        currency: Currency,
        delta: Double,
        ledgerLabel: String,
    ): WalletMutationResult {
        if (delta == 0.0) return WalletMutationResult.Error("Enter an amount greater than zero.")
        val card = cardById(cardId) ?: return WalletMutationResult.Error("Card not found.")
        if (card.frozen && delta < 0) {
            return WalletMutationResult.Error("This card is frozen. Unfreeze it in Settings first.")
        }
        val updated = when (currency) {
            Currency.KRW -> {
                if (delta < 0 && card.krwBalance + delta < 0) {
                    return WalletMutationResult.Error("Insufficient domestic (KRW) balance.")
                }
                card.copy(krwBalance = (card.krwBalance + delta).coerceAtLeast(0.0))
            }
            Currency.USD -> {
                if (delta < 0 && card.usdBalance + delta < 0) {
                    return WalletMutationResult.Error("Insufficient foreign (USD) balance.")
                }
                card.copy(usdBalance = (card.usdBalance + delta).coerceAtLeast(0.0))
            }
        }
        updateCard(updated)
        val signed = formatSignedAmount(delta, currency)
        appendLedger(
            cardId = cardId,
            label = ledgerLabel,
            amountDisplay = signed,
            positive = delta > 0,
        )
        val verb = when {
            delta > 0 -> "added to"
            else -> "deducted from"
        }
        val pocket = if (currency == Currency.KRW) "domestic" else "foreign"
        return WalletMutationResult.Success(
            "${formatAbsAmount(kotlin.math.abs(delta), currency)} $verb your $pocket balance.",
        )
    }

    private fun appendLedger(
        cardId: String?,
        label: String,
        amountDisplay: String,
        positive: Boolean,
    ) {
        val entry = WalletLedgerEntry(
            id = UUID.randomUUID().toString(),
            cardId = cardId,
            label = label,
            amountDisplay = amountDisplay,
            positive = positive,
        )
        _ledger.value = listOf(entry) + _ledger.value.take(199)
    }

    private fun persistCards() {
        // In-memory for this build; hook SharedPreferences when prefs are wired from init().
    }

    private fun defaultCards(): List<WalletCardRecord> =
        MockProfileCreditCards.cards.map { def ->
            WalletCardRecord(
                id = def.id,
                nickname = def.nickname,
                holder = MockProfileCreditCards.HOLDER,
                number = def.number,
                expiry = def.expiry,
                themeId = def.themeId,
                pattern = def.pattern,
                krwBalance = def.krwBalance,
                usdBalance = def.usdBalance,
            )
        }

    private fun seedLedger(cards: List<WalletCardRecord>): List<WalletLedgerEntry> {
        val main = cards.firstOrNull() ?: return emptyList()
        return listOf(
            WalletLedgerEntry("seed-1", main.id, "Coffee & brunch", "-$24.80", false),
            WalletLedgerEntry("seed-2", main.id, "Card top up", "+₩50,000", true),
            WalletLedgerEntry("seed-3", cards.getOrNull(1)?.id, "Sent to Travel Card", "-$12.00", false),
            WalletLedgerEntry("seed-4", main.id, "Dining reward", "+$4.25", true),
        )
    }

    private fun decodeCards(raw: String): List<WalletCardRecord>? {
        // Reserved for persisted encoding; fall back to defaults if parse fails.
        return if (raw.isBlank()) null else null
    }

    fun formatAbsAmount(amount: Double, currency: Currency): String = when (currency) {
        Currency.KRW -> "₩%,.0f".format(amount)
        Currency.USD -> "$%,.2f".format(amount)
    }

    private fun formatSignedAmount(delta: Double, currency: Currency): String {
        val sign = if (delta >= 0) "+" else "-"
        return sign + when (currency) {
            Currency.KRW -> "₩%,.0f".format(kotlin.math.abs(delta))
            Currency.USD -> "$%,.2f".format(kotlin.math.abs(delta))
        }
    }
}
