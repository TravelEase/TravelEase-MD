package com.example.travelease.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.travelease.databinding.FragmentProfileBinding
import com.example.travelease.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://travelease-7e34c-default-rtdb.asia-southeast1.firebasedatabase.app")

        // Get the current user
        val currentUser = auth.currentUser

        // Check if user is logged in
        if (currentUser != null) {
            val uid = currentUser.uid

            // Get a reference to the user's node in the Realtime Database
            reference = database.getReference("users/$uid")

            // Retrieve user's name and email from Realtime Database
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("username").value.toString()
                        val email = snapshot.child("email").value.toString()
                        binding.nameValue.text = name
                        binding.emailValue.text = email
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

//                override fun onCancelled(error: DatabaseException) {
//                    // Handle database errors
//                    Log.w(TAG, "Failed to read value.", error)
//                }
            })

//            // Update name and email on button click
//            binding.saveButton.setOnClickListener {
//                val newName = binding.nameEditText.text.toString().trim()
//                val newEmail = binding.emailEditText.text.toString().trim()
//
//                // Update user profile (optional)
//                currentUser.updateEmail(newEmail)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            Log.d(TAG, "User email updated.")
//                        } else {
//                            Log.w(TAG, "User email update failed.", task.exception)
//                        }
//                    }
//
//                // Update Realtime Database with new name and email
//                reference.child("name").setValue(newName)
//                reference.child("email").setValue(newEmail)
//            }
        } else {
            // User is not logged in, navigate to login
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        // Logout button click listener
        binding.logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Define a data class for user information (optional)
    data class User(val name: String, val email: String) {
        constructor() : this("", "") // Default constructor with empty values
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}

