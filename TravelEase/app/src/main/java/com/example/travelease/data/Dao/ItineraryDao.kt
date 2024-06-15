package com.example.travelease.data.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.travelease.data.entity.Itinerary

@Dao
interface ItineraryDao {
    @Insert
    suspend fun insert(itinerary: Itinerary)

    @Query("SELECT * FROM itinerary")
    suspend fun getAllItineraries(): List<Itinerary>
}
