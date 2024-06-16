package com.example.travelease.data.retrofit

import com.example.travelease.data.response.AutoGenerateItineraryRequest
import com.example.travelease.data.response.AutoGenerateItineraryResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("recommend/")
    fun getRecommendations(@Body request: AutoGenerateItineraryRequest): Call<List<AutoGenerateItineraryResponse>>

    @GET("tourism")
    fun searchPlaces(
        @Query("city") city: String,
        @Query("name") name: String
    ): Call<List<AutoGenerateItineraryResponse>>

}