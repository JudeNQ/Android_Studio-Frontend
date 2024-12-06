package com.example.eventcal.models

import java.io.Serializable

data class TimeData(
    var startTime: String,
    var endTime: String,
) : Serializable
