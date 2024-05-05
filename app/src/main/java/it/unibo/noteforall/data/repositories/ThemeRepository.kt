package it.unibo.noteforall.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import it.unibo.noteforall.ui.screen.settings.Theme
import kotlinx.coroutines.flow.map

class ThemeRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
    }

    val theme = dataStore.data.map { preferences ->
        try {
            Theme.valueOf(preferences[THEME_KEY] ?: "System")
        } catch (_: Exception) {
            Theme.System
        }
    }

    suspend fun setTheme(theme: Theme) = dataStore.edit { it[THEME_KEY] = theme.toString() }

    suspend fun deleteTheme() = dataStore.edit { it.remove(THEME_KEY) }
}
