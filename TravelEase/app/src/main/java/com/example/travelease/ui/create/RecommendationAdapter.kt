package com.example.travelease.ui.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.databinding.ItemRecommendationBinding

class RecommendationAdapter(
    private val items: MutableList<ListItem.RecommendationItem>,
    private val onDeleteClick: (ListItem.RecommendationItem) -> Unit
) : RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemRecommendationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.RecommendationItem, onDeleteClick: (ListItem.RecommendationItem) -> Unit) {
            binding.ivItemPhoto.setImageResource(item.imageResId)
            binding.tvItemPlace.text = item.placeName
            binding.tvItemPrice.text = item.price
            binding.tfTime.setText(item.timeMinutes)
            binding.ivDelete.setOnClickListener { onDeleteClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onDeleteClick)
    }

    override fun getItemCount(): Int = items.size

    fun removeItem(item: ListItem.RecommendationItem) {
        val position = items.indexOf(item)
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
