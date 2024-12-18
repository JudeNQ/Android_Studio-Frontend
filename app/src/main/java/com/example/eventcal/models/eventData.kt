package com.example.eventcal.models

import java.time.LocalDateTime

data class Event(
    val title: String,
    val dateTime: LocalDateTime,
    val description: String,
    val id: String

)
