package com.example.travelease.data.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.travelease.data.Dao.ItineraryDao
import com.example.travelease.data.entity.Itinerary

@Database(entities = [Itinerary::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itineraryDao(): ItineraryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "itinerary_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
