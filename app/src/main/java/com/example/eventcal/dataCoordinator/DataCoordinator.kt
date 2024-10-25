package com.example.eventcal.dataCoordinator

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DataCoordinator {
    companion object {
        val shared = DataCoordinator()
        const val identifier = "[DataCoordinator]"
    }

    var context: Context? = null
    // Create a variable for each preference, along with a default value.
    // This is to guarantee that if it can't find it it resets to a value that you can control.
    /// Sample String
    var sampleUserIdentifier: String = ""
    val defaultUserIdentifier: String = ""

    private val USER_PREFERENCES_NAME = "user_preferences"
    val Context.dataStore by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )

    fun initialize(context: Context, onLoad: () -> Unit) {
        // Set Context
        this.context = context
        // Load DataStore Settings
        GlobalScope.launch(Dispatchers.Default) {
            // Set storage variables to default
            sampleUserIdentifier = getSampleUserString()

            //Call the function to be called on load
            onLoad()
        }
    }
}