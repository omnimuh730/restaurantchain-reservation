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
 * Profile wallet + credit cards: main wallet balances, virtual cards, top-up, withdraw, send, gift, and ledger.
 */
object ProfileWalletStore {
    private const val PrefsName = "profile_wallet_prefs"
    private const val CardsKey = "wallet_cards_v1"

    private val _walletKrw = MutableStateFlow(0.0)
    private val _walletUsd = MutableStateFlow(0.0)
    val walletKrw: StateFlow<Double> = _walletKrw.asStateFlow()
    val walletUsd: StateFlow<Double> = _walletUsd.asStateFlow()

    /** Virtual credit cards only — excludes the main wallet account. */
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
        _walletKrw.value = MockProfileCreditCards.DEFAULT_WALLET_KRW
        _walletUsd.value = MockProfileCreditCards.DEFAULT_WALLET_USD
        _cards.value = if (stored != null) {
            decodeCards(stored) ?: defaultCards()
        } else {
            defaultCards()
        }
        if (_ledger.value.isEmpty()) {
            _ledger.value = seedLedger(_cards.value)
        }
    }

    fun walletAccountLastFour(): String = MockProfileCreditCards.WALLET_ACCOUNT_LAST_FOUR

    fun walletHolder(): String = MockProfileCreditCards.HOLDER

    /** Main wallet domestic balance (KRW). */
    fun totalKrwLong(): Long = _walletKrw.value.roundToLong()

    /** Main wallet foreign balance (USD). */
    fun totalUsd(): Double = _walletUsd.value

    fun creditCardsTotalKrwLong(): Long = _cards.value.sumOf { it.krwBalance.roundToLong() }

    fun creditCardsTotalUsd(): Double = _cards.value.sumOf { it.usdBalance }

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

    /** Account top-up (Profile → Top up): credits the main wallet. */
    fun topUpWallet(currency: Currency, amount: Double): WalletMutationResult {
        if (amount <= 0.0) return WalletMutationResult.Error("Enter an amount greater than zero.")
        return adjustWalletBalance(
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

    /** Gift from the main wallet. */
    fun sendGift(currency: Currency, amount: Double, recipient: String): WalletMutationResult {
        val to = recipient.trim()
        if (to.isEmpty()) return WalletMutationResult.Error("Enter a recipient username.")
        return adjustWalletBalance(
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

    /**
     * Opens a new card funded from the main wallet.
     * Deducts [initialKrw] / [initialUsd] from wallet balances before adding [record].
     */
    fun createCardWithFunding(
        record: WalletCardRecord,
        initialKrw: Double,
        initialUsd: Double,
    ): WalletMutationResult {
        val krw = initialKrw.coerceAtLeast(0.0)
        val usd = initialUsd.coerceAtLeast(0.0)
        if (krw <= 0.0 && usd <= 0.0) {
            addCard(record.copy(krwBalance = 0.0, usdBalance = 0.0))
            return WalletMutationResult.Success("Card opened.")
        }
        if (krw > _walletKrw.value) {
            return WalletMutationResult.Error("Insufficient wallet balance (KRW).")
        }
        if (usd > _walletUsd.value) {
            return WalletMutationResult.Error("Insufficient wallet balance (USD).")
        }
        val funded = record.copy(krwBalance = krw, usdBalance = usd)
        if (krw > 0.0) {
            _walletKrw.value = (_walletKrw.value - krw).coerceAtLeast(0.0)
            appendLedger(
                cardId = null,
                label = "Fund new card · ${funded.nickname}",
                amountDisplay = "-${formatLedgerKrw(krw)}",
                positive = false,
            )
        }
        if (usd > 0.0) {
            _walletUsd.value = (_walletUsd.value - usd).coerceAtLeast(0.0)
            appendLedger(
                cardId = null,
                label = "Fund new card · ${funded.nickname}",
                amountDisplay = "-${formatLedgerUsd(usd)}",
                positive = false,
            )
        }
        _cards.value = _cards.value + funded
        appendLedger(
            cardId = funded.id,
            label = "Card opened",
            amountDisplay = "—",
            positive = true,
        )
        persistCards()
        return WalletMutationResult.Success("Card opened.")
    }

    private fun formatLedgerKrw(value: Double): String = "W" + "%,.0f".format(value)

    private fun formatLedgerUsd(value: Double): String = "$" + "%,.2f".format(value)

    fun updateCard(record: WalletCardRecord) {
        _cards.value = _cards.value.map { if (it.id == record.id) record else it }
        persistCards()
    }

    fun removeCard(cardId: String): WalletMutationResult {
        val closing = cardById(cardId) ?: return WalletMutationResult.Error("Card not found.")
        val returnKrw = closing.krwBalance
        val returnUsd = closing.usdBalance
        _cards.value = _cards.value.filter { it.id != cardId }
        if (returnKrw > 0.0) {
            _walletKrw.value += returnKrw
            appendLedger(
                cardId = closing.id,
                label = "Closed card · moved to wallet",
                amountDisplay = "-${formatLedgerKrw(returnKrw)}",
                positive = false,
            )
            appendLedger(
                cardId = null,
                label = "From closed ${closing.nickname}",
                amountDisplay = "+${formatLedgerKrw(returnKrw)}",
                positive = true,
            )
        }
        if (returnUsd > 0.0) {
            _walletUsd.value += returnUsd
            appendLedger(
                cardId = closing.id,
                label = "Closed card · moved to wallet",
                amountDisplay = "-${formatLedgerUsd(returnUsd)}",
                positive = false,
            )
            appendLedger(
                cardId = null,
                label = "From closed ${closing.nickname}",
                amountDisplay = "+${formatLedgerUsd(returnUsd)}",
                positive = true,
            )
        }
        if (returnKrw <= 0.0 && returnUsd <= 0.0) {
            appendLedger(
                cardId = closing.id,
                label = "Card closed",
                amountDisplay = "—",
                positive = true,
            )
        }
        persistCards()
        return WalletMutationResult.Success("Card closed. Remaining balance moved to your wallet.")
    }

    private fun adjustWalletBalance(
        currency: Currency,
        delta: Double,
        ledgerLabel: String,
    ): WalletMutationResult {
        if (delta == 0.0) return WalletMutationResult.Error("Enter an amount greater than zero.")
        when (currency) {
            Currency.KRW -> {
                if (delta < 0 && _walletKrw.value + delta < 0) {
                    return WalletMutationResult.Error("Insufficient domestic (KRW) balance.")
                }
                _walletKrw.value = (_walletKrw.value + delta).coerceAtLeast(0.0)
            }
            Currency.USD -> {
                if (delta < 0 && _walletUsd.value + delta < 0) {
                    return WalletMutationResult.Error("Insufficient foreign (USD) balance.")
                }
                _walletUsd.value = (_walletUsd.value + delta).coerceAtLeast(0.0)
            }
        }
        val signed = formatSignedAmount(delta, currency)
        appendLedger(
            cardId = null,
            label = ledgerLabel,
            amountDisplay = signed,
            positive = delta > 0,
        )
        val verb = if (delta > 0) "added to" else "deducted from"
        val pocket = if (currency == Currency.KRW) "domestic" else "foreign"
        return WalletMutationResult.Success(
            "${formatAbsAmount(kotlin.math.abs(delta), currency)} $verb your $pocket balance.",
        )
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
        val verb = if (delta > 0) "added to" else "deducted from"
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
        val travel = cards.firstOrNull()
        return buildList {
            add(WalletLedgerEntry("seed-1", null, "Coffee & brunch", "-$24.80", false))
            add(WalletLedgerEntry("seed-2", null, "Wallet top up", "+₩50,000", true))
            if (travel != null) {
                add(WalletLedgerEntry("seed-3", travel.id, "Sent to Travel Card", "-$12.00", false))
            }
            add(WalletLedgerEntry("seed-4", null, "Dining reward", "+$4.25", true))
        }
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
