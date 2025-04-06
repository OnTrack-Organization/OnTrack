package de.ashman.ontrack.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import co.touchlab.kermit.Logger
import de.ashman.ontrack.domain.user.NewUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class UserDataStore(
    private val dataStore: DataStore<Preferences>,
) {
    val currentUserKey = stringPreferencesKey("current_user")

    val currentUser: Flow<NewUser?> = dataStore.data.map { preferences ->
        preferences[currentUserKey]?.let { Json.decodeFromString(it) }
    }

    suspend fun saveUser(user: NewUser) {
        dataStore.edit { preferences ->
            preferences[currentUserKey] = Json.encodeToString(user)
        }
        Logger.d("Saving user: $user")
    }

    suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences.remove(currentUserKey)
        }
    }
}