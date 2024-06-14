package com.example.travelease.ui.create

sealed class ListItem {
    data class DateHeader(val date: String) : ListItem()
    data class RecommendationItem(
        val imageResId: Int,
        val timeMinutes: String,
        val placeName: String,
        val price: String
    ) : ListItem()
}
