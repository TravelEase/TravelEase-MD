package com.example.travelease.ui.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.R


class NestedRecommendationAdapter(
    private val items: List<ListItem.RecommendationItem>
) : RecyclerView.Adapter<NestedRecommendationAdapter.RecommendationItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recommendation, parent, false)
        return RecommendationItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendationItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class RecommendationItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivItemPhoto: ImageView = itemView.findViewById(R.id.iv_item_photo)
        private val tvItemPlace: TextView = itemView.findViewById(R.id.tv_item_place)
        private val tvTime: TextView = itemView.findViewById(R.id.tf_time)
        private val tvItemPrice: TextView = itemView.findViewById(R.id.tv_item_price)

        fun bind(recommendationItem: ListItem.RecommendationItem) {
            ivItemPhoto.setImageResource(R.drawable.image_sample)
            tvItemPlace.text = recommendationItem.placeName
            tvTime.text = recommendationItem.timeMinutes
            tvItemPrice.text = recommendationItem.price
        }
    }
}
