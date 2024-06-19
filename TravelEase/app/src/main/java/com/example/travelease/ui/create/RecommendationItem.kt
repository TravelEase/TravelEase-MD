package com.example.travelease.ui.create

sealed class ListItem {
    data class DateHeader(val date: String) : ListItem()
    data class RecommendationItem(
        val timeMinutes: String,
        val placeName: String,
        val price: String,
        val date: String,
        val imageUrl: String,
        val coordinate: String
    ) : ListItem()
}

