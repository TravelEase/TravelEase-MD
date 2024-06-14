package com.example.travelease.ui.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.databinding.ExpandLayoutBinding
import com.example.travelease.databinding.ItemRecommendationBinding

class ExpandableAdapter(private val items: List<ListItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.DateHeader -> VIEW_TYPE_HEADER
            is ListItem.RecommendationItem -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ExpandLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DateHeaderViewHolder(binding)
            }
            VIEW_TYPE_ITEM -> {
                val binding = ItemRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                RecommendationViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DateHeaderViewHolder -> holder.bind(items[position] as ListItem.DateHeader)
            is RecommendationViewHolder -> holder.bind(items[position] as ListItem.RecommendationItem)
        }
    }

    override fun getItemCount(): Int = items.size

    class DateHeaderViewHolder(private val binding: ExpandLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.DateHeader) {
            binding.itemDate.text = item.date
            // Set up expand/collapse functionality if needed
        }
    }

    class RecommendationViewHolder(private val binding: ItemRecommendationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.RecommendationItem) {
            binding.ivItemPhoto.setImageResource(item.imageResId)
            binding.tvItemPlace.text = item.placeName
            binding.tvItemPrice.text = item.price
            binding.tfTime.setText(item.timeMinutes)
        }
    }
}
