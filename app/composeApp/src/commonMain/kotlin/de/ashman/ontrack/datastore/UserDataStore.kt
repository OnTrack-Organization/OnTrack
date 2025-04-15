package de.ashman.ontrack.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import co.touchlab.kermit.Logger
import de.ashman.ontrack.domain.newdomains.NewUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class UserDataStore(
    private val dataStore: DataStore<Preferences>,
) {
    private val currentUserKey = stringPreferencesKey("current_user")

    val currentUser: Flow<NewUser?> = dataStore.data.map { preferences ->
        preferences[currentUserKey]?.let { Json.decodeFromString(it) }
    }

    suspend fun getCurrentUser(): NewUser {
        val prefs = dataStore.data.first()
        val json = prefs[currentUserKey] ?: error("No user found in DataStore. This shouldn't happen.")
        val user = Json.decodeFromString<NewUser>(json)
        return user
    }

    suspend fun getCurrentUserId(): String = getCurrentUser().id

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