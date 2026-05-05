package com.mh.restaurantchainreservation.core.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

enum class PlanType { Free, Pro }

enum class BillingCycle(val id: String, val price: Double, val perMonth: Double, val discount: Int) {
    Monthly("monthly", 9.99, 9.99, 0),
    Quarterly("quarterly", 24.99, 8.33, 17),
    Yearly("yearly", 79.99, 6.67, 33),
}

data class PlanInfo(
    val type: PlanType = PlanType.Free,
    val cycle: BillingCycle? = null,
    val subscribedAtEpochMs: Long? = null,
    val expiresAtEpochMs: Long? = null,
)

object SubscriptionStore {
    private val state = MutableStateFlow(PlanInfo())
    val plan: StateFlow<PlanInfo> = state.asStateFlow()

    fun activatePro(cycle: BillingCycle) {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        when (cycle) {
            BillingCycle.Monthly -> cal.add(Calendar.MONTH, 1)
            BillingCycle.Quarterly -> cal.add(Calendar.MONTH, 3)
            BillingCycle.Yearly -> cal.add(Calendar.YEAR, 1)
        }
        state.value = PlanInfo(
            type = PlanType.Pro,
            cycle = cycle,
            subscribedAtEpochMs = now,
            expiresAtEpochMs = cal.timeInMillis,
        )
    }

    fun cancelPro() {
        state.value = PlanInfo(type = PlanType.Free)
    }
}
