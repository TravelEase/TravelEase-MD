package com.example.travelease.data.converter

import androidx.room.TypeConverter
import com.example.travelease.ui.create.ListItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ItineraryItemConverter {

    @TypeConverter
    fun fromRecommendationItemList(items: List<ListItem.RecommendationItem>): String {
        val gson = Gson()
        val type = object : TypeToken<List<ListItem.RecommendationItem>>() {}.type
        return gson.toJson(items, type)
    }

    @TypeConverter
    fun toRecommendationItemList(itemsString: String): List<ListItem.RecommendationItem> {
        val gson = Gson()
        val type = object : TypeToken<List<ListItem.RecommendationItem>>() {}.type
        return gson.fromJson(itemsString, type)
    }
}
