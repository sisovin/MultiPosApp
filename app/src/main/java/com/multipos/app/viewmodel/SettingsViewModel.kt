package com.multipos.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "settings")

data class SettingsState(
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val autoPrintEnabled: Boolean = false,
    val lowStockAlerts: Boolean = true,
    val pin: String = "",
    val appVersion: String = "1.0.0"
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val ds = application.dataStore

    companion object {
        val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode_enabled")
        val KEY_AUTO_PRINT = booleanPreferencesKey("auto_print_enabled")
        val KEY_LOW_STOCK = booleanPreferencesKey("low_stock_alerts")
        val KEY_PIN = stringPreferencesKey("login_pin")
        val KEY_VERSION = stringPreferencesKey("app_version")
    }

    val uiState = ds.data.map { prefs ->
        SettingsState(
            notificationsEnabled = prefs[KEY_NOTIFICATIONS] ?: true,
            darkModeEnabled = prefs[KEY_DARK_MODE] ?: false,
            autoPrintEnabled = prefs[KEY_AUTO_PRINT] ?: false,
            lowStockAlerts = prefs[KEY_LOW_STOCK] ?: true,
            pin = prefs[KEY_PIN] ?: "",
            appVersion = prefs[KEY_VERSION] ?: "1.0.0"
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SettingsState())

    fun setNotifications(enabled: Boolean) = viewModelScope.launch { ds.edit { it[KEY_NOTIFICATIONS] = enabled } }
    fun setDarkMode(enabled: Boolean) = viewModelScope.launch { ds.edit { it[KEY_DARK_MODE] = enabled } }
    fun setAutoPrint(enabled: Boolean) = viewModelScope.launch { ds.edit { it[KEY_AUTO_PRINT] = enabled } }
    fun setLowStockAlerts(enabled: Boolean) = viewModelScope.launch { ds.edit { it[KEY_LOW_STOCK] = enabled } }
    fun setPin(value: String) = viewModelScope.launch { ds.edit { it[KEY_PIN] = value } }
    fun setAppVersion(value: String) = viewModelScope.launch { ds.edit { it[KEY_VERSION] = value } }
}

