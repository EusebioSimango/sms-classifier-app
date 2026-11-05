package mz.smsclassifier.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

enum class SmsHome {
    MESSAGES,
    CATEGORIES,
}

private val Context.themeDataStore by preferencesDataStore(name = "settings")

class ThemePreferences(context: Context) {
    private val dataStore = context.applicationContext.themeDataStore

    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        prefs[KEY_THEME]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
            ?: ThemeMode.SYSTEM
    }

    val smsHome: Flow<SmsHome> = dataStore.data.map { prefs ->
        prefs[KEY_SMS_HOME]?.let { runCatching { SmsHome.valueOf(it) }.getOrNull() }
            ?: SmsHome.MESSAGES
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[KEY_THEME] = mode.name }
    }

    suspend fun setSmsHome(home: SmsHome) {
        dataStore.edit { it[KEY_SMS_HOME] = home.name }
    }

    companion object {
        private val KEY_THEME = stringPreferencesKey("theme_mode")
        private val KEY_SMS_HOME = stringPreferencesKey("sms_home")
    }
}
