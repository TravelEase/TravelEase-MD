package com.example.travelease.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.travelease.data.converter.ItineraryItemConverter
import com.example.travelease.data.entity.Itinerary
import androidx.room.migration.Migration
import com.example.travelease.data.Dao.ItineraryDao
import com.example.travelease.data.converter.ListStringConverter

@Database(entities = [Itinerary::class], version = 2, exportSchema = false)
@TypeConverters(ItineraryItemConverter::class, ListStringConverter::class)
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
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
