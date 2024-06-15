package com.example.travelease.ui.saved

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelease.R
import com.example.travelease.SavedActivity
import com.example.travelease.data.entity.Itinerary
import com.example.travelease.data.retrofit.ApiConfig
import com.example.travelease.data.response.AutoGenerateItineraryRequest
import com.example.travelease.data.response.AutoGenerateItineraryResponse
import com.example.travelease.data.room.AppDatabase
import com.example.travelease.databinding.ActivityEditItineraryBinding
import com.example.travelease.ui.create.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EditItineraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditItineraryBinding
    private lateinit var expandableAdapter: ExpandableAdapter
    private lateinit var recommendationAdapter: SimpleRecommendationAdapter
    private val items = mutableListOf<ListItem>()
    private val recommendationItems = mutableListOf<SimpleRecommendationItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditItineraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itineraryId = intent.getIntExtra("itinerary_id", -1)
        if (itineraryId != -1) {
            val itineraryDao = AppDatabase.getDatabase(this).itineraryDao()
            lifecycleScope.launch {
                val itinerary = itineraryDao.getItineraryById(itineraryId)
                displayItineraryDetails(itinerary)
                fetchRecommendations(itinerary.city)
            }
        }
    }

    private fun displayItineraryDetails(itinerary: Itinerary) {
        binding.tvCity.text = itinerary.city
        binding.tvNumberOfPeople.text = "Number of people: ${intent.getIntExtra(CreateFragment.EXTRA_NUMBER_OF_PEOPLE, 1)}"
        binding.tvTotalPrice.text = "Rp ${itinerary.totalPrice}"

        val dateList = itinerary.startDate.split(" to ")
        if (dateList.size == 2) {
            val startDateString = dateList[0]
            val endDateString = dateList[1]

            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val startDate = dateFormat.parse(startDateString)
            val endDate = dateFormat.parse(endDateString)

            val allDates = getDatesBetween(startDate, endDate, dateFormat).sorted()

            allDates.forEach { date ->
                items.add(ListItem.DateHeader(date))
                // Assuming you have saved ListItem.RecommendationItem as part of Itinerary
                // Add logic to fetch these items and add to 'items'
            }

            setupRecyclerView()
        }

        binding.btnEdit.setOnClickListener {
            saveItinerary(itinerary)
        }
    }

    private fun fetchRecommendations(city: String) {
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
                        recommendationItems.clear()
                        recommendationItems.addAll(items)
                        setupRecommendationAdapter()
                    }
                }
            }

            override fun onFailure(call: Call<List<AutoGenerateItineraryResponse>>, t: Throwable) {
                // Handle the error
            }
        })
    }

    private fun setupRecommendationAdapter() {
        recommendationAdapter = SimpleRecommendationAdapter(recommendationItems) { item ->
            // Handle the click
        }
        binding.rvRecommendationItinerary.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvRecommendationItinerary.adapter = recommendationAdapter
    }

    private fun setupRecyclerView() {
        expandableAdapter = ExpandableAdapter(items) { item, date ->
            showDeleteConfirmationDialog(item, date)
        }
        binding.rvAutoItinerary.layoutManager = LinearLayoutManager(this)
        binding.rvAutoItinerary.adapter = expandableAdapter

        updateTotalPrice()
    }

    private fun showDeleteConfirmationDialog(item: ListItem.RecommendationItem, date: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete destination '${item.placeName}' on this date: $date?")
            .setPositiveButton("OK") { _, _ ->
                expandableAdapter.removeItem(item)
                updateTotalPrice()
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

    private fun calculateTotalPrice(itineraryItems: List<ListItem>): Int {
        val numberOfPeople = intent.getIntExtra(CreateFragment.EXTRA_NUMBER_OF_PEOPLE, 1)
        var totalPrice = 0

        itineraryItems.forEach { item ->
            if (item is ListItem.RecommendationItem) {
                val priceString = item.price.removePrefix("$ ").replace(",", "")
                val price = try {
                    priceString.toInt()
                } catch (e: NumberFormatException) {
                    0
                }
                totalPrice += price
            }
        }

        return totalPrice * numberOfPeople
    }

    private fun updateTotalPrice() {
        val totalPrice = calculateTotalPrice(items)
        binding.tvTotalPrice.text = "Rp $totalPrice"
    }

    private fun saveItinerary(itinerary: Itinerary) {
        val itineraryDao = AppDatabase.getDatabase(this).itineraryDao()
        lifecycleScope.launch {
            itineraryDao.insert(itinerary)

            val intent = Intent(this@EditItineraryActivity, SavedActivity::class.java)
            startActivity(intent)
        }
    }
}
