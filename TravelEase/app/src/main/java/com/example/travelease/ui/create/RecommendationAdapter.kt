package com.example.travelease.ui.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.databinding.ItemRecommendationBinding

class RecommendationAdapter(private val items: List<ListItem.RecommendationItem>) : RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemRecommendationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.RecommendationItem) {
            binding.ivItemPhoto.setImageResource(item.imageResId)
            binding.tvItemPlace.text = item.placeName
            binding.tvItemPrice.text = item.price
            binding.tfTime.setText(item.timeMinutes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
