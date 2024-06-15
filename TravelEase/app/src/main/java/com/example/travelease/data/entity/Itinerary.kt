package com.example.travelease.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itinerary")
data class Itinerary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startDate: String,
    val endDate: String,
    val city: String,
    val totalPrice: Int
)
