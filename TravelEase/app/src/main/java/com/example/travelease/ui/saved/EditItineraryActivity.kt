package com.example.travelease.ui.saved

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
import com.example.travelease.data.response.AutoGenerateItineraryRequest
import com.example.travelease.data.response.AutoGenerateItineraryResponse
import com.example.travelease.data.retrofit.ApiConfig
import com.example.travelease.data.room.AppDatabase
import com.example.travelease.databinding.ActivityEditItineraryBinding
import com.example.travelease.ui.create.ExpandableAdapter
import com.example.travelease.ui.create.ListItem
import com.example.travelease.ui.create.SimpleRecommendationAdapter
import com.example.travelease.ui.create.SimpleRecommendationItem
import com.example.travelease.ui.detail.DetailDestinationActivity
import com.example.travelease.ui.search.SearchResult
import com.example.travelease.ui.search.SearchResultsAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class EditItineraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditItineraryBinding
    private lateinit var expandableAdapter: ExpandableAdapter
    private lateinit var recommendationAdapter: SimpleRecommendationAdapter
    private val items = mutableListOf<ListItem>()
    private val recommendationItems = mutableListOf<SimpleRecommendationItem>()
    private lateinit var itinerary: Itinerary
    private lateinit var searchResultsAdapter: SearchResultsAdapter
    private val searchResults = mutableListOf<SearchResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditItineraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itineraryJson = intent.getStringExtra("EXTRA_ITINERARY")
        Log.d("EditItineraryActivity", "EXTRA_ITINERARY: $itineraryJson")

        if (itineraryJson != null) {
            val gson = Gson()
            val type = object : TypeToken<Itinerary>() {}.type
            itinerary = gson.fromJson(itineraryJson, type)
            displayItineraryDetails(itinerary)
            fetchRecommendations(itinerary.city)
        }

        setupSearchResultsRecyclerView()
        setupSearchView()

        binding.btnAdd.setOnClickListener {
            val placeName = binding.searchBar.query.toString()
            if (placeName.isNotEmpty()) {
                showDateSelectionDialogForAdd(placeName)
            } else {
                Toast.makeText(this, "Please enter a place name to add", Toast.LENGTH_SHORT).show()
            }
        }

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
        val city = itinerary.city
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
                Log.e("EditItineraryActivity", "Search API call failed", t)
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
        val city = itinerary.city
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
                    Toast.makeText(this@EditItineraryActivity, "Failed to fetch place details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AutoGenerateItineraryResponse>>, t: Throwable) {
                Log.e("EditItineraryActivity", "API call failed", t)
                Toast.makeText(this@EditItineraryActivity, "Failed to fetch place details", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //UNTUK DISPLAY ITINERARY
    private fun displayItineraryDetails(itinerary: Itinerary) {
        binding.tvCity.text = itinerary.city
        binding.tvNumberOfPeople.text = "Number of people: ${itinerary.numberOfPeople}"
        binding.tvTotalPrice.text = "Rp ${itinerary.totalPrice}"
        binding.tvCategory.text = "Categories: ${itinerary.kategori.joinToString(", ")}"

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val startDate = dateFormat.parse(itinerary.startDate)
        val endDate = dateFormat.parse(itinerary.endDate)

        val allDates = getDatesBetween(startDate, endDate, dateFormat).sorted()

        allDates.forEach { date ->
            items.add(ListItem.DateHeader(date))
            val itineraryItems = itinerary.items.filter { it.date == date }
            items.addAll(itineraryItems)
        }

        setupRecyclerView()

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

//    private fun setupRecommendationAdapter() {
//        recommendationAdapter = SimpleRecommendationAdapter(recommendationItems) { item ->
//            showDateSelectionDialog(item)
//        }
//        binding.rvRecommendationItinerary.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        binding.rvRecommendationItinerary.adapter = recommendationAdapter
//    }

    private fun setupRecommendationAdapter() {
        val adapter = SimpleRecommendationAdapter(recommendationItems, { item ->
            showDateSelectionDialog(item)
        }, { item ->
            val intent = Intent(this, DetailDestinationActivity::class.java)
            intent.putExtra("PLACE_NAME", item.placeName)
            intent.putExtra("CITY", binding.tvCity.text.toString())
            startActivity(intent)
        })
        binding.rvRecommendationItinerary.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvRecommendationItinerary.adapter = adapter
    }

    private fun setupRecyclerView() {
        expandableAdapter = ExpandableAdapter(items, { item, date ->
            showDeleteConfirmationDialog(item, date)
        }, { item ->
            val intent = Intent(this, DetailDestinationActivity::class.java)
            intent.putExtra("PLACE_NAME", item.placeName)
            intent.putExtra("CITY", itinerary.city)
            startActivity(intent)
        })
        binding.rvAutoItinerary.layoutManager = LinearLayoutManager(this)
        binding.rvAutoItinerary.adapter = expandableAdapter

        updateTotalPrice()
    }


    private fun showDeleteConfirmationDialog(item: ListItem.RecommendationItem, date: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete destination '${item.placeName}' on this date: $date?")
            .setPositiveButton("OK") { _, _ ->
                removeItemFromDatabase(item)
                expandableAdapter.removeItem(item)
                updateTotalPrice()
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun removeItemFromDatabase(item: ListItem.RecommendationItem) {
        val updatedItems = itinerary.items.toMutableList()
        updatedItems.removeIf { it.placeName == item.placeName && it.date == item.date }
        itinerary.items = updatedItems

        val gson = Gson()
        val updatedItineraryJson = gson.toJson(itinerary)
        intent.putExtra("EXTRA_ITINERARY", updatedItineraryJson)

        // Optional: Save updated itinerary to database if needed
        val itineraryDao = AppDatabase.getDatabase(this).itineraryDao()
        lifecycleScope.launch {
            itineraryDao.update(itinerary) // Assuming you have an update method in your DAO
        }
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

        // Ambil JSON itinerary dari intent
        val itineraryJson = intent.getStringExtra("EXTRA_ITINERARY")
        val gson = Gson()
        val type = object : TypeToken<Itinerary>() {}.type
        val itinerary = gson.fromJson<Itinerary>(itineraryJson, type)

        val numberOfPeople = itinerary.numberOfPeople
        var totalPrice = 0

        itineraryItems.forEach { item ->
            if (item is ListItem.RecommendationItem) {
                val priceString = item.price.replace("[^\\d]".toRegex(), "") // Remove non-numeric characters
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


    //CODE SAVE EDIT ITINERARY
    private fun saveItinerary(itinerary: Itinerary) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val startDate = dateFormat.format(dateFormat.parse(itinerary.startDate))
        val endDate = dateFormat.format(dateFormat.parse(itinerary.endDate))
        val totalPrice = calculateTotalPrice(items)

        val updatedItinerary = Itinerary(
            id = itinerary.id,  // Use the existing ID
            startDate = startDate,
            endDate = endDate,
            city = itinerary.city,
            totalPrice = totalPrice,
            items = items.filterIsInstance<ListItem.RecommendationItem>(),
            kategori = itinerary.kategori,
            numberOfPeople = itinerary.numberOfPeople
        )

        val gson = Gson()
        val itineraryJson = gson.toJson(updatedItinerary)

        // Log the JSON string
        Log.d("EditItinerary", "EXTRA_ITINERARY: $itineraryJson")

        val itineraryDao = AppDatabase.getDatabase(this).itineraryDao()
        lifecycleScope.launch {
            itineraryDao.update(updatedItinerary)  // Update the existing itinerary

            val intent = Intent(this@EditItineraryActivity, SavedActivity::class.java)
            intent.putExtra("EXTRA_ITINERARY", itineraryJson)
            startActivity(intent)
        }
    }
}
