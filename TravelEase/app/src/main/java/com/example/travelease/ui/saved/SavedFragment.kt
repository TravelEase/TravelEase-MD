package com.example.travelease.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelease.R
import com.example.travelease.databinding.FragmentSavedBinding

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private lateinit var savedViewModel: SavedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        savedViewModel = ViewModelProvider(this).get(SavedViewModel::class.java)
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()

        return root
    }

    private fun setupRecyclerView() {
        // Sample data
        val sampleData = listOf(
            Itinerary(R.drawable.image_sample, "10-06-2023", "Jakarta", "$100"),
            Itinerary(R.drawable.image_sample, "12-06-2023", "Bandung", "$150"),
            Itinerary(R.drawable.image_sample, "15-06-2023", "Surabaya", "$200")
        )

         //Setting up the RecyclerView
        binding.rvSavedItinerary.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSavedItinerary.adapter = SavedItineraryAdapter(sampleData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}