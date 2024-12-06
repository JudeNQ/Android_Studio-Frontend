package com.example.eventcal.pojo

// In a POJO or Model folder
data class ProfileResponse(
    val username: String,
    val biography: String,
    val eventsCount: Int,
    val organizationsCount: Int
)
