package com.example.travelease.data.response

data class AutoGenerateItineraryRequest(
    val categories: List<String>,
    val city: String
)