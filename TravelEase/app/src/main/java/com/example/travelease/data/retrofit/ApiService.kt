package com.example.travelease.data.retrofit

import com.example.travelease.data.response.AutoGenerateItineraryRequest
import com.example.travelease.data.response.AutoGenerateItineraryResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("recommend/")
    fun getRecommendations(@Body request: AutoGenerateItineraryRequest): Call<List<AutoGenerateItineraryResponse>>

}