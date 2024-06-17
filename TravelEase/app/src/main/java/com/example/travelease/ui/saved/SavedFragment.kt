package com.example.travelease.ui.saved

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelease.R
import com.example.travelease.data.entity.Itinerary
import com.example.travelease.data.room.AppDatabase
import com.example.travelease.databinding.FragmentSavedBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private lateinit var savedItineraryAdapter: SavedItineraryAdapter
    private var isSelectionMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itineraryDao = AppDatabase.getDatabase(requireContext()).itineraryDao()
        lifecycleScope.launch {
            val itineraries = itineraryDao.getAllItineraries().toMutableList()

            savedItineraryAdapter = SavedItineraryAdapter(itineraries, object : SavedItineraryAdapter.OnClickListener {
                override fun onClick(itinerary: Itinerary) {
                    // Handle normal click
                    if (!isSelectionMode) {
                        val intent = Intent(requireContext(), EditItineraryActivity::class.java)
                        intent.putExtra("EXTRA_ITINERARY", Gson().toJson(itinerary))
                        startActivity(intent)
                    }
                }

                override fun onLongClick(position: Int): Boolean {
                    toggleSelectionMode(position)
                    return true
                }

                override fun onItemClick(position: Int) {
                    if (isSelectionMode) {
                        toggleSelection(position)
                    } else {
                        // Handle normal click
                        val item = savedItineraryAdapter.getItem(position)
                        val intent = Intent(requireContext(), EditItineraryActivity::class.java)
                        intent.putExtra("EXTRA_ITINERARY", Gson().toJson(item))
                        startActivity(intent)
                    }
                }
            })

            binding.rvSavedItinerary.layoutManager = LinearLayoutManager(requireContext())
            binding.rvSavedItinerary.adapter = savedItineraryAdapter
        }

        val toolbar: Toolbar = binding.tbSaved
        toolbar.inflateMenu(R.menu.menu_delete_all)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete_all_itinerary -> {
                    showDeleteAllConfirmationDialog()
                    true
                }
                R.id.delete_selected_itinerary -> {
                    deleteSelectedItems()
                    true
                }
                else -> false
            }
        }

        // Set the initial title of the toolbar
        toolbar.title = getString(R.string.saved_itineraries)
    }

    private fun toggleSelectionMode(position: Int) {
        isSelectionMode = true
        binding.tbSaved.menu.clear()
        binding.tbSaved.inflateMenu(R.menu.menu_delete_selected_itinerary)
        toggleSelection(position)
        updateToolbarTitle()
    }

    private fun toggleSelection(position: Int) {
        savedItineraryAdapter.toggleSelection(position)
        updateToolbarTitle()
        if (savedItineraryAdapter.selectedItems.isEmpty()) {
            isSelectionMode = false
            binding.tbSaved.menu.clear()
            binding.tbSaved.inflateMenu(R.menu.menu_delete_all)
            binding.tbSaved.title = getString(R.string.saved_itineraries) // Ganti dengan judul toolbar default
        }
    }

    private fun updateToolbarTitle() {
        val selectedCount = savedItineraryAdapter.selectedItems.size
        if (selectedCount > 0) {
            binding.tbSaved.title = "$selectedCount items selected"
        } else {
            binding.tbSaved.title = getString(R.string.saved_itineraries) // Ganti dengan judul toolbar default
        }
    }

    private fun showDeleteAllConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus semua itinerary?")
            .setPositiveButton("OK") { _, _ ->
                deleteAllItems()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteSelectedItems() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())
            val selectedPositions = savedItineraryAdapter.selectedItems.toList()
            for (position in selectedPositions) {
                val item = savedItineraryAdapter.getItem(position)
                db.itineraryDao().delete(item.id)
            }
            withContext(Dispatchers.Main) {
                savedItineraryAdapter.removeItems(selectedPositions)
                savedItineraryAdapter.selectedItems.clear()
                isSelectionMode = false
                binding.tbSaved.menu.clear()
                binding.tbSaved.inflateMenu(R.menu.menu_delete_all)
                binding.tbSaved.title = getString(R.string.saved_itineraries) // Ganti dengan judul toolbar default
            }
        }
    }

    private fun deleteAllItems() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())
            db.itineraryDao().deleteAll()
            withContext(Dispatchers.Main) {
                savedItineraryAdapter.clearItems()
                binding.tbSaved.title = getString(R.string.saved_itineraries) // Ganti dengan judul toolbar default
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
