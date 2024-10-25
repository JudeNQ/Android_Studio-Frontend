package com.example.eventcal.dataCoordinator

import android.util.Log
import androidx.datastore.preferences.core.edit
import com.example.eventcal.keys.PreferenceKeys
import kotlinx.coroutines.flow.firstOrNull

//Function to get the user string
suspend fun DataCoordinator.getSampleUserString(): String {
    val context = this.context ?: return defaultUserIdentifier
    return context.dataStore.data.firstOrNull()?.get(PreferenceKeys.userId)
        ?: defaultUserIdentifier
}

//Function to set the userString
suspend fun DataCoordinator.setSampleUserString(value: String) {
    val context = this.context ?: return

    context.dataStore.edit { preferences ->
        preferences[PreferenceKeys.userId] = value
    }
}