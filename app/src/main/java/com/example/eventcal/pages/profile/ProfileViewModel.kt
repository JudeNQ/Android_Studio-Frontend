package com.example.eventcal.pages.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _biography = MutableLiveData<String>()
    val biography: LiveData<String> get() = _biography

    private val _eventsCount = MutableLiveData<Int>()
    val eventsCount: LiveData<Int> get() = _eventsCount

    private val _organizationsCount = MutableLiveData<Int>()
    val organizationsCount: LiveData<Int> get() = _organizationsCount


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
