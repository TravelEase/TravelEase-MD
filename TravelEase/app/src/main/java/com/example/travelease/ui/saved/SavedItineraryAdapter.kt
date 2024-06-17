package com.example.travelease.ui.saved

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.R
import com.example.travelease.data.entity.Itinerary
import com.example.travelease.databinding.ItemSavedBinding

class SavedItineraryAdapter(
    private val items: MutableList<Itinerary>, // Ubah menjadi MutableList
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
            binding.ivItemPhoto.setImageResource(R.drawable.image_sample)
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
