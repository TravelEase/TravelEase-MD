package com.example.travelease.ui.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.databinding.ExpandLayoutBinding
import com.example.travelease.databinding.ItemRecommendationBinding

class ExpandableAdapter(private val items: List<ListItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1
    private val expandedItems = mutableSetOf<Int>()

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
        val currentPosition = getActualPosition(position)
        when (holder) {
            is DateHeaderViewHolder -> holder.bind(items[currentPosition] as ListItem.DateHeader, currentPosition)
            is RecommendationViewHolder -> holder.bind(items[currentPosition] as ListItem.RecommendationItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[getActualPosition(position)]) {
            is ListItem.DateHeader -> VIEW_TYPE_HEADER
            is ListItem.RecommendationItem -> VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        var count = 0
        items.forEachIndexed { index, item ->
            if (item is ListItem.DateHeader) {
                count++
                if (expandedItems.contains(index)) {
                    count += items.subList(index + 1, items.size).takeWhile { it is ListItem.RecommendationItem }.size
                }
            }
        }
        return count
    }

    private fun getActualPosition(position: Int): Int {
        var actualPosition = 0
        var currentCount = 0
        items.forEachIndexed { index, item ->
            if (item is ListItem.DateHeader) {
                if (currentCount == position) {
                    actualPosition = index
                    return actualPosition
                }
                currentCount++
                if (expandedItems.contains(index)) {
                    val itemCount = items.subList(index + 1, items.size).takeWhile { it is ListItem.RecommendationItem }.size
                    if (currentCount + itemCount >= position) {
                        actualPosition = index + 1 + (position - currentCount)
                        return actualPosition
                    }
                    currentCount += itemCount
                }
            }
        }
        return actualPosition
    }

    inner class DateHeaderViewHolder(private val binding: ExpandLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.DateHeader, position: Int) {
            binding.itemDate.text = item.date
            binding.root.setOnClickListener {
                if (expandedItems.contains(position)) {
                    expandedItems.remove(position)
                } else {
                    expandedItems.add(position)
                }
                notifyDataSetChanged()
            }
        }
    }

    inner class RecommendationViewHolder(private val binding: ItemRecommendationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.RecommendationItem) {
            binding.ivItemPhoto.setImageResource(item.imageResId)
            binding.tvItemPlace.text = item.placeName
            binding.tvItemPrice.text = item.price
            binding.tfTime.setText(item.timeMinutes)
        }
    }
}
