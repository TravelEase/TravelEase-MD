package com.example.travelease.ui.create

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.example.travelease.databinding.ExpandLayoutBinding
import com.example.travelease.databinding.ItemRecommendationBinding

data class ItineraryItem(val imageResId: Int, val time: String, val place: String, val price: String)

class ExpandableListAdapter(
    private val context: Context,
    private val dates: List<String>,
    private val itineraryMap: HashMap<String, List<ItineraryItem>>
) : BaseExpandableListAdapter() {

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return itineraryMap[dates[groupPosition]]!![childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return itineraryMap[dates[groupPosition]]!!.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return dates[groupPosition]
    }

    override fun getGroupCount(): Int {
        return dates.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val binding = if (convertView == null) {
            ExpandLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            ExpandLayoutBinding.bind(convertView)
        }
        val date = getGroup(groupPosition) as String
        binding.itemDate.text = date
        return binding.root
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val binding = if (convertView == null) {
            ItemRecommendationBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            ItemRecommendationBinding.bind(convertView)
        }
        val item = getChild(groupPosition, childPosition) as ItineraryItem
        binding.ivItemPhoto.setImageResource(item.imageResId)
        binding.tfTime.setText(item.time)
        binding.tvItemPlace.text = item.place
        binding.tvItemPrice.text = item.price
        return binding.root
    }
}
