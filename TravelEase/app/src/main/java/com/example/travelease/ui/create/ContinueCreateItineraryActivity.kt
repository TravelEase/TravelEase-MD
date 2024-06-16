package com.example.travelease.ui.create

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
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
import com.example.travelease.databinding.ActivityContinueCreateItineraryBinding
import com.example.travelease.ui.search.SearchResult
import com.example.travelease.ui.search.SearchResultsAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ContinueCreateItineraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContinueCreateItineraryBinding
    private lateinit var expandableAdapter: ExpandableAdapter
    private val items = mutableListOf<ListItem>()
    private val recommendationItems = mutableListOf<SimpleRecommendationItem>()
    private lateinit var searchResultsAdapter: SearchResultsAdapter
    private val searchResults = mutableListOf<SearchResult>()
    private lateinit var itinerary: Itinerary
    private lateinit var city: String
    private lateinit var categories: List<String>
    private lateinit var dates: String
    private var numberOfPeople: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContinueCreateItineraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from intent
        categories = intent.getStringArrayListExtra(CreateFragment.EXTRA_CATEGORY) ?: arrayListOf()
        city = intent.getStringExtra(CreateFragment.EXTRA_CITY) ?: ""
        numberOfPeople = intent.getIntExtra(CreateFragment.EXTRA_NUMBER_OF_PEOPLE, 1)
        dates = intent.getStringExtra(CreateFragment.EXTRA_DATES) ?: ""

        // Set data to TextViews
        binding.tvCategory.text = categories.joinToString(", ")
        binding.tvCity.text = city
        binding.tvNumberOfPeople.text = "Number of people: $numberOfPeople"

        // Initialize itinerary
        initializeItinerary()

        setupRecommendationRecyclerView()
        setupExpandableListView()
        setupSearchResultsRecyclerView()
        setupSearchView()

        binding.btnSave.setOnClickListener {
            saveItinerary()
        }

        // Add listener for the "Add" button
        binding.btnAdd.setOnClickListener {
            val placeName = binding.searchBar.query.toString()
            if (placeName.isNotEmpty()) {
                showDateSelectionDialogForAdd(placeName)
            } else {
                Toast.makeText(this, "Please enter a place name to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeItinerary() {
        itinerary = Itinerary(
            startDate = "",  // Initialize with default values
            endDate = "",
            city = city,
            totalPrice = 0,
            items = mutableListOf(),
            kategori = categories,
            numberOfPeople = numberOfPeople
        )
    }

    //UNTUK SEARCH
    // Setup for search results RecyclerView
    private fun setupSearchResultsRecyclerView() {
        searchResultsAdapter = SearchResultsAdapter(searchResults) { result ->
            binding.searchBar.setQuery(result.placeName, false)
            binding.searchResultsRecyclerView.visibility = View.GONE
        }
        binding.searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchResultsRecyclerView.adapter = searchResultsAdapter
    }

    // Setup for SearchView
    private fun setupSearchView() {
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.isNotEmpty()) {
                    searchPlaces(newText)
                } else {
                    binding.searchResultsRecyclerView.visibility = View.GONE
                }
                return true
            }
        })
    }

    private fun searchPlaces(query: String) {
        val client = ApiConfig.getApiService().searchPlaces(city, query)
        client.enqueue(object : Callback<List<AutoGenerateItineraryResponse>> {
            override fun onResponse(
                call: Call<List<AutoGenerateItineraryResponse>>,
                response: Response<List<AutoGenerateItineraryResponse>>
            ) {
                if (response.isSuccessful) {
                    val results = response.body()
                    if (results != null) {
                        searchResults.clear()
                        results.forEach { result ->
                            searchResults.add(
                                SearchResult(
                                    placeName = result.placeName.toString(),
                                    price = "Rp ${result.price}"
                                )
                            )
                        }
                        searchResultsAdapter.notifyDataSetChanged()
                        binding.searchResultsRecyclerView.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<List<AutoGenerateItineraryResponse>>, t: Throwable) {
                Log.e("ContinueCreateItineraryActivity", "Search API call failed", t)
            }
        })
    }

    //ADD PADA SEARCH
    private fun showDateSelectionDialogForAdd(placeName: String) {
        val dates = items.filterIsInstance<ListItem.DateHeader>().map { it.date }
        val options = dates.toTypedArray()
        var selectedDate: String? = null

        val dialog = AlertDialog.Builder(this)
            .setTitle("Select Date")
            .setSingleChoiceItems(options, -1) { _, which ->
                selectedDate = options[which]
            }
            .setPositiveButton("OK") { _, _ ->
                selectedDate?.let { date ->
                    fetchAndAddItemToItinerary(placeName, date)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun fetchAndAddItemToItinerary(placeName: String, date: String) {
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
                        val newItem = ListItem.RecommendationItem(
                            imageResId = R.drawable.image_sample,  // Use the default sample image
                            timeMinutes = "N/A",  // Default value
                            placeName = result.placeName.toString(),
                            price = "Rp ${result.price}",
                            date = date
                        )
                        expandableAdapter.addItemToDate(newItem, date)
                        updateTotalPrice()
                    }
                } else {
                    Toast.makeText(this@ContinueCreateItineraryActivity, "Failed to fetch place details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AutoGenerateItineraryResponse>>, t: Throwable) {
                Log.e("ContinueCreateItineraryActivity", "API call failed", t)
                Toast.makeText(this@ContinueCreateItineraryActivity, "Failed to fetch place details", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //CODE UNTUK CARD RECOMMENDATION
    private fun setupRecommendationRecyclerView() {
        if (city.isNotEmpty()) {
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
                                val price = recommendation.price?.let { "Rp $it" } ?: return@mapNotNull null
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

    private fun setupRecommendationAdapter() {
        val adapter = SimpleRecommendationAdapter(recommendationItems) { item ->
            showDateSelectionDialog(item)
        }
        binding.rvRecommendationItinerary.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvRecommendationItinerary.adapter = adapter
    }

    //CODE UNTUK CARD RECOMMENDATION
    //CODE UNTUK ADD DESTINATION ITEM
    private fun showDateSelectionDialog(item: SimpleRecommendationItem) {
        val dates = items.filterIsInstance<ListItem.DateHeader>().map { it.date }
        val options = dates.toTypedArray()
        var selectedDate: String? = null

        val dialog = AlertDialog.Builder(this)
            .setTitle("Select Date")
            .setSingleChoiceItems(options, -1) { _, which ->
                selectedDate = options[which]
            }
            .setPositiveButton("OK") { _, _ ->
                selectedDate?.let { date ->
                    addItemToItinerary(item, date)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun addItemToItinerary(item: SimpleRecommendationItem, date: String) {
        val newItem = ListItem.RecommendationItem(
            item.imageResId,
            "Isi waktu disini",
            item.placeName,
            item.price,
            date
        )
        val dateIndex = items.indexOfFirst { it is ListItem.DateHeader && it.date == date }
        if (dateIndex != -1) {
            items.add(dateIndex + 1, newItem)
            expandableAdapter.notifyItemInserted(dateIndex + 1)
            updateTotalPrice()
        }
    }

    //CODE UNTUK AUTO GENERATE ITINERARY
    private fun setupExpandableListView() {
        if (dates.isNotEmpty() && city.isNotEmpty()) {
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
                                        val timeMinutes = recommendation.timeMinutes?.let { "${it.toInt()} minutes" } ?: return@mapNotNull null
                                        val price = recommendation.price?.let { "Rp $it" } ?: return@mapNotNull null
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

    //CODE UNTUK TOTAL PRICE
    private fun calculateTotalPrice(itineraryItems: List<ListItem>): Int {
        var totalPrice = 0

        itineraryItems.forEach { item ->
            if (item is ListItem.RecommendationItem) {
                val priceString = item.price.removePrefix("Rp ").replace(",", "")
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

    //CODE UNTUK SAVE ITINERARY
    private fun saveItinerary() {
        if (dates.isNotEmpty() && city.isNotEmpty()) {
            val dateList = dates.split(" to ")
            if (dateList.size == 2) {
                val startDate = dateList[0]
                val endDate = dateList[1]

                val totalPrice = calculateTotalPrice(items)
                val itinerary = Itinerary(
                    startDate = startDate,
                    endDate = endDate,
                    city = city,
                    totalPrice = totalPrice,
                    items = items.filterIsInstance<ListItem.RecommendationItem>(),
                    kategori = categories,
                    numberOfPeople = numberOfPeople
                )

                val gson = Gson()
                val itineraryJson = gson.toJson(itinerary)

                // Log the JSON string
                Log.d("ContinueCreateItinerary", "EXTRA_ITINERARY: $itineraryJson")

                val itineraryDao = AppDatabase.getDatabase(this).itineraryDao()
                lifecycleScope.launch {
                    val newId = itineraryDao.insert(itinerary)
                    val intent = Intent(this@ContinueCreateItineraryActivity, SavedActivity::class.java)
                    intent.putExtra("EXTRA_ITINERARY_ID", newId.toString())
                    intent.putExtra("EXTRA_ITINERARY", itineraryJson)
                    startActivity(intent)
                }
            }
        }
    }
}
