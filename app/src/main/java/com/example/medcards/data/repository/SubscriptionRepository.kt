package com.example.medcards.data.repository

import android.content.Context
import com.example.medcards.data.model.MediPassPlan

class SubscriptionRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    val isPremiumActive: Boolean
        get() = preferences.getBoolean(KEY_PREMIUM_ACTIVE, true)

    val activePlan: MediPassPlan
        get() {
            val storedPlan = preferences.getString(KEY_ACTIVE_PLAN, null)
            return MediPassPlan.entries.firstOrNull { it.name == storedPlan } ?: DEFAULT_PLAN
        }

    fun selectPlan(plan: MediPassPlan) {
        check(
            preferences.edit()
                .putString(KEY_ACTIVE_PLAN, plan.name)
                .putBoolean(KEY_PREMIUM_ACTIVE, true)
                .commit()
        ) { "No se pudo guardar el plan de MediPass Pro." }
    }

    private companion object {
        const val PREFERENCES_NAME = "medipass_subscription"
        const val KEY_PREMIUM_ACTIVE = "premium_active"
        const val KEY_ACTIVE_PLAN = "active_plan"
        val DEFAULT_PLAN = MediPassPlan.SEMIANNUAL
    }
}
