package com.example.travelease.ui.create

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelease.databinding.ExpandLayoutBinding

class ExpandableListAdapter(
    private val context: Context,
    private val dates: List<String>,
    private val itineraryMap: HashMap<String, List<RecommendationItem>>
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

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val binding = if (convertView == null) {
            ExpandLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            ExpandLayoutBinding.bind(convertView)
        }
        val items = itineraryMap[dates[groupPosition]] ?: listOf()
        setupRecyclerView(binding.rvAutoItinerary, items)
        return binding.root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, items: List<RecommendationItem>) {
        val adapter = RecommendationAdapter(items)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }
}
