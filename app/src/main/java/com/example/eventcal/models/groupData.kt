package com.example.eventcal.models

import com.example.eventcal.pojo.Schedule

data class Group(
    var name: String,
    var bio: String,
    var leader: String,
    var members: List<String>,
    var id: String,
    var schedule: Schedule?
)