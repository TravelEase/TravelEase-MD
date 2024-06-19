package com.example.travelease.ui.saved

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelease.BuildConfig
import com.example.travelease.R
import com.example.travelease.data.entity.Itinerary
import com.example.travelease.databinding.ItemSavedBinding

class SavedItineraryAdapter(
    private val items: MutableList<Itinerary>,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<SavedItineraryAdapter.SavedItineraryViewHolder>() {

    val selectedItems = mutableListOf<Int>()

    interface OnClickListener {
        fun onClick(itinerary: Itinerary)
        fun onLongClick(position: Int): Boolean
        fun onItemClick(position: Int)
    }

    inner class SavedItineraryViewHolder(private val binding: ItemSavedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Itinerary, position: Int) {
            binding.root.setOnClickListener {
                onClickListener.onItemClick(position)
            }
            binding.root.setOnLongClickListener {
                onClickListener.onLongClick(position)
            }
            binding.tvItemDate.text = "${item.startDate} to ${item.endDate}"
            binding.tvItemPlace.text = item.city
            binding.tvItemPrice.text = item.totalPrice.toString()

            // Get the coordinates from the first RecommendationItem
            if (item.items.isNotEmpty()) {
                val recommendationItem = item.items[0]
                val coordinates = recommendationItem.coordinate.replace("{", "").replace("}", "").replace("'", "").split(", ")
                val lat = coordinates[0].split(": ")[1].toDouble()
                val lng = coordinates[1].split(": ")[1].toDouble()
                val apiKey = BuildConfig.GOOGLE_MAPS_API_KEY
                val streetViewUrl = "https://maps.googleapis.com/maps/api/streetview?size=400x400&location=$lat,$lng&fov=90&heading=235&pitch=10&key=$apiKey"

                Glide.with(binding.ivItemPhoto.context)
                    .load(streetViewUrl)
                    .into(binding.ivItemPhoto)
            }

            binding.root.isSelected = selectedItems.contains(position)
        }
    }

    fun toggleSelection(position: Int) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position)
        } else {
            selectedItems.add(position)
        }
        notifyItemChanged(position)
    }

    fun removeItems(positions: List<Int>) {
        positions.sortedDescending().forEach { position ->
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Itinerary {
        return items[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedItineraryViewHolder {
        val binding = ItemSavedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedItineraryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedItineraryViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size
}
