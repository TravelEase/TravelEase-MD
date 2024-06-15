package com.example.travelease

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.travelease.databinding.ActivitySavedBinding

class SavedActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set Toolbar sebagai ActionBar
        setSupportActionBar(binding.toolbar)

        // Optional: Tambahkan navigasi ke Toolbar jika diperlukan
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_saved)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_saved, R.id.navigation_create, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        // Retrieve the EXTRA_ITINERARY
//        val itineraryJson = intent.getStringExtra("EXTRA_ITINERARY")
//        Log.d("SavedActivity", "EXTRA_ITINERARY: $itineraryJson")
//
//        if (itineraryJson != null) {
//            val bundle = Bundle().apply {
//                putString("EXTRA_ITINERARY", itineraryJson)
//            }
//            navController.navigate(R.id.navigation_saved, bundle)
//        }
    }
}