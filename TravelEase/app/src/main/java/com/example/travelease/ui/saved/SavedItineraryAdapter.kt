package com.example.travelease.ui.saved

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.R
import com.example.travelease.data.entity.Itinerary
import com.example.travelease.databinding.ItemSavedBinding

class SavedItineraryAdapter(private val items: List<Itinerary>) :
    RecyclerView.Adapter<SavedItineraryAdapter.SavedItineraryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedItineraryViewHolder {
        val binding = ItemSavedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedItineraryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedItineraryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class SavedItineraryViewHolder(private val binding: ItemSavedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Itinerary) {
            binding.tvItemDate.text = "${item.startDate} to ${item.endDate}"
            binding.tvItemPlace.text = item.city
            binding.tvItemPrice.text = item.totalPrice.toString()
            binding.ivItemPhoto.setImageResource(R.drawable.image_sample)
        }
    }
}
