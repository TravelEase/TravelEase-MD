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

    // View type constants for headers and items
    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1

    // Set to store the positions of expanded headers
    private val expandedItems = mutableSetOf<Int>()

    // Method to create view holders based on view type
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

    // Method to bind data to view holders based on position
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentPosition = getActualPosition(position)
        when (holder) {
            is DateHeaderViewHolder -> holder.bind(items[currentPosition] as ListItem.DateHeader, currentPosition)
            is RecommendationViewHolder -> holder.bind(items[currentPosition] as ListItem.RecommendationItem)
        }
    }

    // Method to determine the type of view based on position
    override fun getItemViewType(position: Int): Int {
        return when (items[getActualPosition(position)]) {
            is ListItem.DateHeader -> VIEW_TYPE_HEADER
            is ListItem.RecommendationItem -> VIEW_TYPE_ITEM
        }
    }

    // Method to calculate the total count based on expanded state
    override fun getItemCount(): Int {
        var count = 0
        items.forEachIndexed { index, item ->
            if (item is ListItem.DateHeader) {
                count++
                if (expandedItems.contains(index)) {
                    // Count the items under this header if it's expanded
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

    // ViewHolder class for headers
    inner class DateHeaderViewHolder(private val binding: ExpandLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.DateHeader, position: Int) {
            binding.itemDate.text = item.date
            binding.root.setOnClickListener {
                if (expandedItems.contains(position)) {
                    // If the header is already expanded, collapse it
                    expandedItems.remove(position)
                } else {
                    // If the header is not expanded, expand it
                    expandedItems.add(position)
                }
                // Notify the adapter to refresh the views
                notifyDataSetChanged()
            }
        }
    }

    // ViewHolder class for items
    inner class RecommendationViewHolder(private val binding: ItemRecommendationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.RecommendationItem) {
            binding.ivItemPhoto.setImageResource(item.imageResId)
            binding.tvItemPlace.text = item.placeName
            binding.tvItemPrice.text = item.price
            binding.tfTime.setText(item.timeMinutes)
            binding.ivDelete.setOnClickListener {
                onDeleteClick(item, item.date)
            }
        }
    }

    // Method to remove an item from the list and update the adapter
    fun removeItem(item: ListItem.RecommendationItem) {
        val position = items.indexOf(item)
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
            // Notify the adapter of the changes in the dataset
            notifyDataSetChanged()
        }

    }

    fun updateItems(newItems: List<ListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}


//import android.text.Editable
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.travelease.databinding.ExpandLayoutBinding
//import com.example.travelease.databinding.ItemRecommendationBinding
//
//class ExpandableAdapter(
//    private val items: MutableList<ListItem>,
//    private val onDelete: (ListItem.RecommendationItem, String) -> Unit
//) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    private val expandedDates = mutableSetOf<String>()
//    private val originalItems = items.toMutableList()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            VIEW_TYPE_DATE_HEADER -> DateHeaderViewHolder(
//                ExpandLayoutBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//            )
//            VIEW_TYPE_RECOMMENDATION_ITEM -> RecommendationViewHolder(
//                ItemRecommendationBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//            )
//            else -> throw IllegalArgumentException("Invalid view type")
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when (val item = items[position]) {
//            is ListItem.DateHeader -> {
//                (holder as DateHeaderViewHolder).bind(item)
//                holder.itemView.setOnClickListener {
//                    if (expandedDates.contains(item.date)) {
//                        collapseDate(item.date)
//                    } else {
//                        expandDate(item.date)
//                    }
//                }
//            }
//            is ListItem.RecommendationItem -> {
//                (holder as RecommendationViewHolder).bind(item)
//                holder.binding.ivDelete.setOnClickListener {
//                    onDelete(item, item.date)
////                    removeItem(item)
//                }
//            }
//        }
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return when (items[position]) {
//            is ListItem.DateHeader -> VIEW_TYPE_DATE_HEADER
//            is ListItem.RecommendationItem -> VIEW_TYPE_RECOMMENDATION_ITEM
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }
//
//    private fun toggleDate(date: String) {
//        if (expandedDates.contains(date)) {
//            collapseDate(date)
//        } else {
//            expandDate(date)
//        }
//    }
//
//    private fun expandDate(date: String) {
//        val index = items.indexOfFirst { it is ListItem.DateHeader && it.date == date }
//        if (index != -1 && !expandedDates.contains(date)) {
//            expandedDates.add(date)
//            val sublist = originalItems.filter { it is ListItem.RecommendationItem && it.date == date }
//            items.addAll(index + 1, sublist)
//            notifyItemRangeInserted(index + 1, sublist.size)
//        }
//    }
//
//    private fun collapseDate(date: String) {
//        val index = items.indexOfFirst { it is ListItem.DateHeader && it.date == date }
//        if (index != -1 && expandedDates.contains(date)) {
//            expandedDates.remove(date)
//            val sublist = items.subList(index + 1, items.size).filter { it is ListItem.RecommendationItem && it.date == date }
//            items.removeAll(sublist)
//            notifyItemRangeRemoved(index + 1, sublist.size)
//        }
//    }
//
//    fun addItemToDate(newItem: ListItem.RecommendationItem, date: String) {
//        val dateIndex = items.indexOfFirst { it is ListItem.DateHeader && it.date == date }
//        if (dateIndex != -1) {
//            if (!expandedDates.contains(date)) {
//                expandDate(date) // Ensure the date is expanded when adding a new item
//            }
//            items.add(dateIndex + 1, newItem)
//            originalItems.add(newItem)
//            notifyItemInserted(dateIndex + 1)
//        } else {
//            // If the date header is not found, add new header and item
//            val header = ListItem.DateHeader(date)
//            items.add(header)
//            items.add(newItem)
//            originalItems.add(header)
//            originalItems.add(newItem)
//            notifyDataSetChanged() // Notify adapter of large data change
//        }
//    }
//
//    inner class DateHeaderViewHolder(private val binding: ExpandLayoutBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(item: ListItem.DateHeader) {
//            binding.itemDate.text = item.date
//        }
//    }
//
//    inner class RecommendationViewHolder(val binding: ItemRecommendationBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(item: ListItem.RecommendationItem) {
//            binding.ivItemPhoto.setImageResource(item.imageResId)
//            binding.tvItemPlace.text = item.placeName
//            binding.tvItemPrice.text = item.price
//            binding.tfTime.text = Editable.Factory.getInstance().newEditable(item.timeMinutes)
//        }
//    }
//
//    fun removeItem(item: ListItem.RecommendationItem) {
//        val position = items.indexOf(item)
//        if (position != -1) {
//            items.removeAt(position)
//            notifyItemRemoved(position)
//            // Notify the adapter of the changes in the dataset
//            notifyDataSetChanged()
//        }
//
//    }
//
//    companion object {
//        private const val VIEW_TYPE_DATE_HEADER = 0
//        private const val VIEW_TYPE_RECOMMENDATION_ITEM = 1
//    }
//}
