package com.example.eventcal.models

data class Group (
    val name : String,
    val bio : String,
    val leader : String,
    val members : List<String>
)