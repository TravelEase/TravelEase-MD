package com.example.travelease.data.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.travelease.data.entity.Itinerary

@Dao
interface ItineraryDao {
    @Insert
    suspend fun insert(itinerary: Itinerary) : Long

    @Update
    suspend fun update(itinerary: Itinerary)

    @Query("DELETE FROM itinerary WHERE id = :itineraryId")
    suspend fun delete(itineraryId: Int)

    @Query("DELETE FROM itinerary")
    suspend fun deleteAll()

    @Query("SELECT * FROM itinerary")
    suspend fun getAllItineraries(): List<Itinerary>

    @Query("SELECT * FROM itinerary WHERE id = :itineraryId")
    suspend fun getItineraryById(itineraryId: Int): Itinerary
}
