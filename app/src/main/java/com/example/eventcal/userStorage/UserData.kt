package com.example.eventcal.userStorage

import android.content.Context

class UserData {
    companion object {
        val shared = UserData()
    }

    var context: Context? = null

    //Create variables for user settings
    /// User ID
    var userId: String = ""
    val defaultUserId: String = ""

    fun initialize(context: Context) {
        // Set Context
        this.context = context
        userId = "";
    }

    /*
    fun getUserId(): String {
        return userId
    }

    fun setUserId(value: String) {
        userId = value
    }
    */
}