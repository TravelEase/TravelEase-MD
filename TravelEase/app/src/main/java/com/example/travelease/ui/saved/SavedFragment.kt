package com.example.travelease.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelease.data.room.AppDatabase
import com.example.travelease.databinding.FragmentSavedBinding
import kotlinx.coroutines.launch

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
//    private lateinit var savedViewModel: SavedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSavedItineraries()
    }

    private fun loadSavedItineraries() {
        val itineraryDao = AppDatabase.getDatabase(requireContext()).itineraryDao()
        lifecycleScope.launch {
            val itineraries = itineraryDao.getAllItineraries()
            binding.rvSavedItinerary.layoutManager = LinearLayoutManager(requireContext())
            binding.rvSavedItinerary.adapter = SavedItineraryAdapter(itineraries)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}