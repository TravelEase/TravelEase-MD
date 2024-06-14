package com.example.travelease.ui.create

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelease.R
import com.example.travelease.data.retrofit.ApiConfig
import com.example.travelease.data.response.AutoGenerateItineraryRequest
import com.example.travelease.data.response.AutoGenerateItineraryResponse
import com.example.travelease.databinding.ActivityContinueCreateItineraryBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ContinueCreateItineraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContinueCreateItineraryBinding
    private lateinit var expandableAdapter: ExpandableAdapter
    private val items = mutableListOf<ListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContinueCreateItineraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from intent
        val categories = intent.getStringArrayListExtra(CreateFragment.EXTRA_CATEGORY) ?: arrayListOf()
        val city = intent.getStringExtra(CreateFragment.EXTRA_CITY)
        val numberOfPeople = intent.getIntExtra(CreateFragment.EXTRA_NUMBER_OF_PEOPLE, 1)
        val dates = intent.getStringExtra(CreateFragment.EXTRA_DATES)

        // Set data to TextViews
        binding.tvCategory.text = categories.joinToString(", ")
        binding.tvCity.text = city
        binding.tvNumberOfPeople.text = "Number of people: $numberOfPeople"

        setupRecommendationRecyclerView(city)
        setupExpandableListView(dates, categories, city)
    }

    private fun setupRecommendationRecyclerView(city: String?) {
        if (city != null) {
            val categories = listOf("Taman Hiburan", "Budaya", "Cagar Alam", "Bahari", "Tempat Ibadah", "Pusat Perbelanjaan")
            val request = AutoGenerateItineraryRequest(categories, city)
            val client = ApiConfig.getApiService().getRecommendations(request)

            client.enqueue(object : Callback<List<AutoGenerateItineraryResponse>> {
                override fun onResponse(
                    call: Call<List<AutoGenerateItineraryResponse>>,
                    response: Response<List<AutoGenerateItineraryResponse>>
                ) {
                    if (response.isSuccessful) {
                        val recommendations = response.body()
                        if (recommendations != null) {
                            val items = recommendations.mapNotNull { recommendation ->
                                val placeName = recommendation.placeName ?: return@mapNotNull null
                                val price = recommendation.price?.let { "$ $it" } ?: return@mapNotNull null
                                SimpleRecommendationItem(
                                    R.drawable.image_sample,
                                    placeName,
                                    price
                                )
                            }
                            setupRecommendationAdapter(items)
                        }
                    } else {
                        Log.e("ContinueCreateItinerary", "Response not successful: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<AutoGenerateItineraryResponse>>, t: Throwable) {
                    Log.e("ContinueCreateItinerary", "Error fetching recommendations", t)
                }
            })
        }
    }

    private fun setupRecommendationAdapter(items: List<SimpleRecommendationItem>) {
        val adapter = SimpleRecommendationAdapter(items)
        binding.rvRecommendationItinerary.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvRecommendationItinerary.adapter = adapter
    }

    private fun setupExpandableListView(dates: String?, categories: List<String>, city: String?) {
        if (dates != null && city != null) {
            val request = AutoGenerateItineraryRequest(categories, city)
            val client = ApiConfig.getApiService().getRecommendations(request)

            client.enqueue(object : Callback<List<AutoGenerateItineraryResponse>> {
                override fun onResponse(
                    call: Call<List<AutoGenerateItineraryResponse>>,
                    response: Response<List<AutoGenerateItineraryResponse>>
                ) {
                    if (response.isSuccessful) {
                        val recommendations = response.body()
                        if (recommendations != null) {
                            val gson = Gson()
                            val type = object : TypeToken<List<AutoGenerateItineraryResponse>>() {}.type
                            val jsonResponse = gson.toJson(recommendations, type)
                            Log.d("ContinueCreateItinerary", "Raw JSON Response: $jsonResponse")

                            val dateList = dates.split(" to ")
                            if (dateList.size == 2) {
                                val startDateString = dateList[0]
                                val endDateString = dateList[1]

                                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                val startDate = dateFormat.parse(startDateString)
                                val endDate = dateFormat.parse(endDateString)

                                val allDates = getDatesBetween(startDate, endDate, dateFormat).sorted()

                                allDates.forEach { date ->
                                    items.add(ListItem.DateHeader(date))
                                    items.addAll(recommendations.mapNotNull { recommendation ->
                                        val placeName = recommendation.placeName ?: return@mapNotNull null
                                        val timeMinutes = recommendation.timeMinutes?.let { "$it minutes" } ?: return@mapNotNull null
                                        val price = recommendation.price?.let { "$ $it" } ?: return@mapNotNull null
                                        ListItem.RecommendationItem(
                                            R.drawable.image_sample,
                                            timeMinutes,
                                            placeName,
                                            price,
                                            date
                                        )
                                    })
                                }

                                setupRecyclerView()
                            }
                        } else {
                            Log.e("ContinueCreateItinerary", "No recommendations found")
                        }
                    } else {
                        Log.e("ContinueCreateItinerary", "Response not successful: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<AutoGenerateItineraryResponse>>, t: Throwable) {
                    Log.e("ContinueCreateItinerary", "Error fetching recommendations", t)
                }
            })
        } else {
            Log.e("ContinueCreateItinerary", "City or dates are null")
        }
    }

    private fun setupRecyclerView() {
        expandableAdapter = ExpandableAdapter(items) { item, date ->
            showDeleteConfirmationDialog(item, date)
        }
        binding.rvAutoItinerary.layoutManager = LinearLayoutManager(this)
        binding.rvAutoItinerary.adapter = expandableAdapter
    }

    private fun showDeleteConfirmationDialog(item: ListItem.RecommendationItem, date: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete destination '${item.placeName}' on this date: $date?")
            .setPositiveButton("OK") { _, _ ->
                expandableAdapter.removeItem(item)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun getDatesBetween(startDate: Date, endDate: Date, dateFormat: SimpleDateFormat): List<String> {
        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        while (!calendar.time.after(endDate)) {
            dates.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return dates
    }
}
