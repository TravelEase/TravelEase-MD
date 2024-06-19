package com.example.travelease.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.travelease.SavedActivity
import com.example.travelease.databinding.ActivityMainBinding
import com.example.travelease.ui.welcome.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Check if user is already logged in
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, SavedActivity::class.java)
            // Assuming you have a way to retrieve the user ID (e.g., from SharedPreferences)
            val userId = getSavedUserId() // Replace with your logic to get the user ID
            intent.putExtra("USER_ID", userId) // Pass the UID
            startActivity(intent)
            finish()
            return // Exit the activity if already logged in
        }

        // Menampilkan splash screen selama 2 detik
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)


    }
    // Function to retrieve the saved user ID (replace with your implementation)
    private fun getSavedUserId(): String {
        val sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        return sharedPref.getString("user_id", "") ?: "" // Default to empty string if not found
    }
}