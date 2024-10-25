package com.example.eventcal.dataCoordinator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//DataStore Update Functionality

//Update the userId
fun DataCoordinator.updateUserId(value: String) {
    // Update Value
    this.sampleUserIdentifier = value
    // Save to System
    GlobalScope.launch(Dispatchers.Default) {
        // Update DataStore
        setSampleUserString(value)
    }
}