package com.example.travelease.ui.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelease.R
import com.example.travelease.databinding.ItemAddBinding

class SimpleRecommendationAdapter(
    private val items: List<SimpleRecommendationItem>,
    private val onAddClick: (SimpleRecommendationItem) -> Unit,
    private val onItemClick: (SimpleRecommendationItem) -> Unit // Add this line
) : RecyclerView.Adapter<SimpleRecommendationAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemAddBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SimpleRecommendationItem, onAddClick: (SimpleRecommendationItem) -> Unit, onItemClick: (SimpleRecommendationItem) -> Unit) {
            binding.tvPlaceName.text = item.placeName
            binding.tvItemPrice.text = item.price
            Glide.with(binding.root.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.image_sample) // Placeholder resource ID
                .into(binding.ivItemPhoto)
            binding.btnAdd.setOnClickListener { onAddClick(item) }
            binding.root.setOnClickListener { onItemClick(item) } // Add click listener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onAddClick, onItemClick)
    }

    override fun getItemCount(): Int = items.size
}
