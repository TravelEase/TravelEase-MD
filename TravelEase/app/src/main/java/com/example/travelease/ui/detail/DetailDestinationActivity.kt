package com.example.travelease.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.travelease.R
import com.example.travelease.data.retrofit.ApiConfig
import com.example.travelease.data.response.AutoGenerateItineraryResponse
import com.example.travelease.databinding.ActivityDetailDestinationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailDestinationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailDestinationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailDestinationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val placeName = intent.getStringExtra("PLACE_NAME")
        val city = intent.getStringExtra("CITY")

        if (placeName != null && city != null) {
            fetchPlaceDetails(city, placeName)
        } else {
            Log.e("DetailDestinationActivity", "Place name or city is null")
        }

        binding.btnCreateItinerary.setOnClickListener {
            onBackPressed()
        }
    }

    private fun fetchPlaceDetails(city: String, placeName: String) {
        val client = ApiConfig.getApiService().searchPlaces(city, placeName)
        client.enqueue(object : Callback<List<AutoGenerateItineraryResponse>> {
            override fun onResponse(
                call: Call<List<AutoGenerateItineraryResponse>>,
                response: Response<List<AutoGenerateItineraryResponse>>
            ) {
                if (response.isSuccessful) {
                    val results = response.body()
                    if (!results.isNullOrEmpty()) {
                        val result = results[0]
                        displayPlaceDetails(result)
                    }
                } else {
                    Log.e("DetailDestinationActivity", "Failed to fetch place details")
                }
            }

            override fun onFailure(call: Call<List<AutoGenerateItineraryResponse>>, t: Throwable) {
                Log.e("DetailDestinationActivity", "API call failed", t)
            }
        })
    }

    private fun displayPlaceDetails(result: AutoGenerateItineraryResponse) {
        binding.tvTitle.text = result.placeName
        binding.tvCity.text = result.city
        binding.tvPrice.text = "Rp ${result.price}"
        binding.tvDescription.text = result.description
        binding.tvCategory.text = result.category
        binding.ratingBar.rating = result.rating!!.toFloat()
        binding.tvRating.text = result.rating.toString()

        Glide.with(this)
            .load(R.drawable.image_sample)
            .into(binding.ivMainImage)

        binding.btnFindLocation.setOnClickListener {
            val coordinates = result.coordinate!!.replace("{", "").replace("}", "").replace("'", "").split(", ")
            val lat = coordinates[0].split(": ")[1].toDouble()
            val lng = coordinates[1].split(": ")[1].toDouble()
            val uri = Uri.parse("geo:$lat,$lng?q=${result.placeName}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }
    }
}
