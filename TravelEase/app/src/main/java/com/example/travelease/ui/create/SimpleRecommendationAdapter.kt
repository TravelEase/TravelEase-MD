package com.example.travelease.ui.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.databinding.ItemAddBinding

class SimpleRecommendationAdapter(private val items: List<SimpleRecommendationItem>) : RecyclerView.Adapter<SimpleRecommendationAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemAddBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SimpleRecommendationItem) {
            binding.tvPlaceName.text = item.placeName
            binding.tvItemPrice.text = item.price
            binding.ivItemPhoto.setImageResource(item.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
