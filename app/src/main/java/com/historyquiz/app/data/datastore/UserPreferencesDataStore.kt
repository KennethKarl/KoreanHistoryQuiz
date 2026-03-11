package com.historyquiz.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

class UserPreferencesDataStore(private val context: Context) {

    // ── Keys (architecture.md §7) ──────────────────────────────────────────
    private object Keys {
        val IS_ONBOARDING_DONE = booleanPreferencesKey("is_onboarding_done")
        val LAST_SIGNED_IN_EMAIL = stringPreferencesKey("last_signed_in_email")
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        val WEEKLY_GOAL = intPreferencesKey("weekly_goal")
        val APP_THEME = stringPreferencesKey("app_theme")
    }

    // ── Reads ──────────────────────────────────────────────────────────────

    val isOnboardingDone: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.IS_ONBOARDING_DONE] ?: false }

    val lastSignedInEmail: Flow<String?> = context.dataStore.data
        .map { it[Keys.LAST_SIGNED_IN_EMAIL] }

    val notificationEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.NOTIFICATION_ENABLED] ?: true }

    val weeklyGoal: Flow<Int> = context.dataStore.data
        .map { it[Keys.WEEKLY_GOAL] ?: 20 }

    val appTheme: Flow<String> = context.dataStore.data
        .map { it[Keys.APP_THEME] ?: "dancheong" }

    // ── Writes ─────────────────────────────────────────────────────────────

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[Keys.IS_ONBOARDING_DONE] = done }
    }

    suspend fun setLastSignedInEmail(email: String?) {
        context.dataStore.edit {
            if (email != null) it[Keys.LAST_SIGNED_IN_EMAIL] = email
            else it.remove(Keys.LAST_SIGNED_IN_EMAIL)
        }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATION_ENABLED] = enabled }
    }

    suspend fun setWeeklyGoal(goal: Int) {
        context.dataStore.edit { it[Keys.WEEKLY_GOAL] = goal }
    }

    suspend fun setAppTheme(theme: String) {
        context.dataStore.edit { it[Keys.APP_THEME] = theme }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
