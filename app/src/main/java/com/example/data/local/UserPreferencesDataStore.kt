package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.AppPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "santali_juri_prefs")

class UserPreferencesDataStore(private val context: Context) {

    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val EXCLUDE_MY_CLAN = booleanPreferencesKey("exclude_my_clan")
        val PREFERRED_CLANS = stringSetPreferencesKey("preferred_clans")
        val MIN_AGE = intPreferencesKey("min_age")
        val MAX_AGE = intPreferencesKey("max_age")
        val SELECTED_STATE = stringPreferencesKey("selected_state")
        val SELECTED_EDUCATION = stringPreferencesKey("selected_education")
        val VERIFIED_ONLY = booleanPreferencesKey("verified_only")
        val HIDE_FROM_SAME_CLAN = booleanPreferencesKey("hide_from_same_clan")
        val PHOTO_PRIVACY_VERIFIED_ONLY = booleanPreferencesKey("photo_privacy_verified_only")
        val ALLOW_CALLS = booleanPreferencesKey("allow_calls")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val PREMIUM_PLAN_ID = stringPreferencesKey("premium_plan_id")
        val BOOST_PROFILE_VISIBILITY = booleanPreferencesKey("boost_profile_visibility")
    }

    val preferencesFlow: Flow<AppPreferences> = context.dataStore.data.map { prefs ->
        AppPreferences(
            isLoggedIn = prefs[Keys.IS_LOGGED_IN] ?: false,
            excludeMyClan = prefs[Keys.EXCLUDE_MY_CLAN] ?: true,
            preferredClans = prefs[Keys.PREFERRED_CLANS] ?: emptySet(),
            minAge = prefs[Keys.MIN_AGE] ?: 20,
            maxAge = prefs[Keys.MAX_AGE] ?: 34,
            selectedState = prefs[Keys.SELECTED_STATE] ?: "All Regions",
            selectedEducation = prefs[Keys.SELECTED_EDUCATION] ?: "Any",
            verifiedOnly = prefs[Keys.VERIFIED_ONLY] ?: false,
            hideFromSameClan = prefs[Keys.HIDE_FROM_SAME_CLAN] ?: true,
            photoPrivacyVerifiedOnly = prefs[Keys.PHOTO_PRIVACY_VERIFIED_ONLY] ?: false,
            allowCalls = prefs[Keys.ALLOW_CALLS] ?: true,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
            isPremium = prefs[Keys.IS_PREMIUM] ?: false,
            premiumPlanId = prefs[Keys.PREMIUM_PLAN_ID],
            boostProfileVisibility = prefs[Keys.BOOST_PROFILE_VISIBILITY] ?: false
        )
    }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { it[Keys.IS_LOGGED_IN] = loggedIn }
    }

    suspend fun updateFilters(
        excludeMyClan: Boolean,
        preferredClans: Set<String>,
        minAge: Int,
        maxAge: Int,
        selectedState: String,
        selectedEducation: String,
        verifiedOnly: Boolean
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.EXCLUDE_MY_CLAN] = excludeMyClan
            prefs[Keys.PREFERRED_CLANS] = preferredClans
            prefs[Keys.MIN_AGE] = minAge
            prefs[Keys.MAX_AGE] = maxAge
            prefs[Keys.SELECTED_STATE] = selectedState
            prefs[Keys.SELECTED_EDUCATION] = selectedEducation
            prefs[Keys.VERIFIED_ONLY] = verifiedOnly
        }
    }

    suspend fun resetFilters() {
        updateFilters(
            excludeMyClan = true,
            preferredClans = emptySet(),
            minAge = 20,
            maxAge = 34,
            selectedState = "All Regions",
            selectedEducation = "Any",
            verifiedOnly = false
        )
    }

    suspend fun setHideFromSameClan(value: Boolean) {
        context.dataStore.edit {
            it[Keys.HIDE_FROM_SAME_CLAN] = value
            it[Keys.EXCLUDE_MY_CLAN] = value
        }
    }

    suspend fun setPhotoPrivacyVerifiedOnly(value: Boolean) {
        context.dataStore.edit { it[Keys.PHOTO_PRIVACY_VERIFIED_ONLY] = value }
    }

    suspend fun setAllowCalls(value: Boolean) {
        context.dataStore.edit { it[Keys.ALLOW_CALLS] = value }
    }

    suspend fun setNotificationsEnabled(value: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = value }
    }

    suspend fun activatePremium(planId: String) {
        context.dataStore.edit {
            it[Keys.IS_PREMIUM] = true
            it[Keys.PREMIUM_PLAN_ID] = planId
            it[Keys.BOOST_PROFILE_VISIBILITY] = planId == "12_months"
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
