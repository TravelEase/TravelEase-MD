package com.example.travelease.data.response

import com.google.gson.annotations.SerializedName

data class AutoGenerateItineraryResponse(
	@SerializedName("Place_Id") val placeId: Int,
	@SerializedName("Place_Name") val placeName: String?,
	@SerializedName("Description") val description: String?,
	@SerializedName("Category") val category: String?,
	@SerializedName("City") val city: String?,
	@SerializedName("Price") val price: Int?,
	@SerializedName("Rating") val rating: Double?,
	@SerializedName("Time_Minutes") val timeMinutes: Double?,
	@SerializedName("Coordinate") val coordinate: String?
)

