package com.example.travelease.ui.create

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelease.R
import com.example.travelease.databinding.ActivityContinueCreateItineraryBinding
import java.text.SimpleDateFormat
import java.util.*

class ContinueCreateItineraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContinueCreateItineraryBinding

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

        setupRecommendationRecyclerView()
        setupExpandableListView(dates)
    }

    private fun setupRecommendationRecyclerView() {
        val items = listOf(
            RecommendationItem(R.drawable.image_sample, "Place 1", "$ 100000"),
            RecommendationItem(R.drawable.image_sample, "Place 2", "$ 200000"),
            RecommendationItem(R.drawable.image_sample, "Place 3", "$ 300000")
        )

        val adapter = RecommendationAdapter(items)
        binding.rvRecommendationItinerary.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvRecommendationItinerary.adapter = adapter
    }

    private fun setupExpandableListView(dates: String?) {
        if (dates != null) {
            val dateList = dates.split(" to ")
            if (dateList.size == 2) {
                val startDateString = dateList[0]
                val endDateString = dateList[1]

                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val startDate = dateFormat.parse(startDateString)
                val endDate = dateFormat.parse(endDateString)

                val allDates = getDatesBetween(startDate, endDate, dateFormat)
                val itineraryMap = hashMapOf<String, List<ItineraryItem>>()
                allDates.forEach { date ->
                    itineraryMap[date] = listOf(
                        ItineraryItem(R.drawable.image_sample, "7.00-8.00", "Place 1", "$ 100000"),
                        ItineraryItem(R.drawable.image_sample, "8.00-9.00", "Place 2", "$ 200000")
                    )
                }

                val adapter = ExpandableListAdapter(this, allDates, itineraryMap)
                binding.expandableListView.setAdapter(adapter)
            }
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

}
