package com.example.travelease.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.travelease.data.converter.ItineraryItemConverter
import com.example.travelease.data.converter.ListStringConverter
import com.example.travelease.ui.create.ListItem

@Entity(tableName = "itinerary")
@TypeConverters(ItineraryItemConverter::class, ListStringConverter::class)
data class Itinerary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startDate: String,
    val endDate: String,
    val city: String,
    val totalPrice: Int,
    var items: List<ListItem.RecommendationItem>,
    val kategori: List<String>,
    val numberOfPeople: Int,
    val imageUrl: String,
    val userId: String
)
