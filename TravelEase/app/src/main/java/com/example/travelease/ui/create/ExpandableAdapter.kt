package com.example.travelease.ui.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.databinding.ExpandLayoutBinding
import com.example.travelease.databinding.ItemRecommendationBinding

class ExpandableAdapter(
    private val items: MutableList<ListItem>,
    private val onDeleteClick: (ListItem.RecommendationItem, String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        when (holder) {
            is DateHeaderViewHolder -> holder.bind(items[position] as ListItem.DateHeader, position)
            is RecommendationViewHolder -> holder.bind(items[position] as ListItem.RecommendationItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.DateHeader -> VIEW_TYPE_HEADER
            is ListItem.RecommendationItem -> VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return items.size
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

            binding.ivDelete.setOnClickListener {
                val dateHeader = findDateHeaderForPosition(adapterPosition)
                if (dateHeader != null) {
                    onDeleteClick(item, dateHeader.date)
                }
            }
        }
    }

    private fun findDateHeaderForPosition(position: Int): ListItem.DateHeader? {
        for (i in position downTo 0) {
            if (items[i] is ListItem.DateHeader) {
                return items[i] as ListItem.DateHeader
            }
        }
        return null
    }
}

