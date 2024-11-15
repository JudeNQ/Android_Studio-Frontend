package com.example.eventcal.pages.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _username = MutableLiveData<String>().apply {
        value = "Your Username"
    }
    val username: LiveData<String> = _username

    private val _biography = MutableLiveData<String>().apply {
        value = ""
    }
    val biography: LiveData<String> = _biography

    private val _eventsCount = MutableLiveData<Int>().apply {
        value = 0
    }
    val eventsCount: LiveData<Int> = _eventsCount

    private val _organizationsCount = MutableLiveData<Int>().apply {
        value = 0
    }
    val organizationsCount: LiveData<Int> = _organizationsCount

    fun updateBiography(bio: String) {
        _biography.value = bio
    }

    fun updateUsername(name: String) {
        _username.value = name
    }

    fun updateEventCount(count: Int) {
        _eventsCount.value = count
    }

    fun updateOrganizationCount(count: Int) {
        _organizationsCount.value = count
    }
}
