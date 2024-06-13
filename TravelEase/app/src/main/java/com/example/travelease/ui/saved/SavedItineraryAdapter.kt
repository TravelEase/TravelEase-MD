package com.example.travelease.ui.saved

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.R
import com.example.travelease.databinding.ItemSavedBinding

data class Itinerary(
    val photoResId: Int,
    val date: String,
    val place: String,
    val price: String
)

class SavedItineraryAdapter(private val items: List<Itinerary>) : RecyclerView.Adapter<SavedItineraryAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemSavedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Itinerary) {
            binding.ivItemPhoto.setImageResource(item.photoResId)
            binding.tvItemDate.text = item.date
            binding.tvItemPlace.text = item.place
            binding.tvItemPrice.text = item.price
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSavedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
