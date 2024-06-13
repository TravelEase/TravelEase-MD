package com.example.travelease.ui.create

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelease.R
import com.example.travelease.databinding.ActivityContinueCreateItineraryBinding

class ContinueCreateItineraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContinueCreateItineraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContinueCreateItineraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecommendationRecyclerView()
        setupExpandableListView()
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

    private fun setupExpandableListView() {
        val dates = listOf("30 Desember 2024", "31 Desember 2024")
        val itineraryMap = hashMapOf(
            "30 Desember 2024" to listOf(
                ItineraryItem(R.drawable.image_sample, "7.00-8.00", "Place 1", "$ 100000"),
                ItineraryItem(R.drawable.image_sample, "8.00-9.00", "Place 2", "$ 200000")
            ),
            "31 Desember 2024" to listOf(
                ItineraryItem(R.drawable.image_sample, "7.00-8.00", "Place 3", "$ 300000"),
                ItineraryItem(R.drawable.image_sample, "8.00-9.00", "Place 4", "$ 400000")
            )
        )

        val adapter = ExpandableListAdapter(this, dates, itineraryMap)
        binding.expandableListView.setAdapter(adapter)
    }


}
