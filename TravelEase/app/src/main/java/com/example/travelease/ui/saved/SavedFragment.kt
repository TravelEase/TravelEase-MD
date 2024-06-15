package com.example.travelease.ui.saved

//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.travelease.data.entity.Itinerary
//import com.example.travelease.data.room.AppDatabase
//import com.example.travelease.databinding.FragmentSavedBinding
//import com.example.travelease.ui.create.ExpandableAdapter
//import com.example.travelease.ui.create.ListItem
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import kotlinx.coroutines.launch
//
//class SavedFragment : Fragment() {
//
//    private var _binding: FragmentSavedBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var expandableAdapter: ExpandableAdapter
//    private val items = mutableListOf<ListItem>()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentSavedBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val itineraryDao = AppDatabase.getDatabase(requireContext()).itineraryDao()
//
//        lifecycleScope.launch {
//            val itineraries = itineraryDao.getAllItineraries()
//
//            val adapter = SavedItineraryAdapter(itineraries, object : SavedItineraryAdapter.OnClickListener {
//                override fun onClick(itinerary: Itinerary) {
//                    val intent = Intent(requireContext(), EditItineraryActivity::class.java)
//                    intent.putExtra("itinerary_id", itinerary.id)
//                    startActivity(intent)
//                }
//            })
//
//            binding.rvSavedItinerary.layoutManager = LinearLayoutManager(requireContext())
//            binding.rvSavedItinerary.adapter = adapter
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelease.data.entity.Itinerary
import com.example.travelease.data.room.AppDatabase
import com.example.travelease.databinding.FragmentSavedBinding
import com.example.travelease.ui.create.ExpandableAdapter
import com.example.travelease.ui.create.ListItem
import com.google.gson.Gson
import kotlinx.coroutines.launch

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private lateinit var expandableAdapter: ExpandableAdapter
    private val items = mutableListOf<ListItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itineraryDao = AppDatabase.getDatabase(requireContext()).itineraryDao()
        lifecycleScope.launch {
            val itineraries = itineraryDao.getAllItineraries()

            val adapter = SavedItineraryAdapter(itineraries, object : SavedItineraryAdapter.OnClickListener {
                override fun onClick(itinerary: Itinerary) {
                    val intent = Intent(requireContext(), EditItineraryActivity::class.java)
                    intent.putExtra("EXTRA_ITINERARY", Gson().toJson(itinerary))
                    startActivity(intent)
                }
            })

            binding.rvSavedItinerary.layoutManager = LinearLayoutManager(requireContext())
            binding.rvSavedItinerary.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
