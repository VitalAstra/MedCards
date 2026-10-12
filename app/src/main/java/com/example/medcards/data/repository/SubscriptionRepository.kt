package com.example.medcards.data.repository

import android.content.Context
import com.example.medcards.data.model.MediPassPlan

class SubscriptionRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    val isPremiumActive: Boolean
        get() = false

    val activePlan: MediPassPlan
        get() {
            val storedPlan = preferences.getString(KEY_ACTIVE_PLAN, null)
            return MediPassPlan.entries.firstOrNull { it.name == storedPlan } ?: DEFAULT_PLAN
        }

    fun selectPlan(plan: MediPassPlan) {
        check(
            preferences.edit()
                .putString(KEY_ACTIVE_PLAN, plan.name)
                .commit()
        ) { "No se pudo guardar el plan de MediPass Pro." }
    }

    private companion object {
        const val PREFERENCES_NAME = "medipass_subscription"
        const val KEY_ACTIVE_PLAN = "active_plan"
        val DEFAULT_PLAN = MediPassPlan.SEMIANNUAL
    }
}
