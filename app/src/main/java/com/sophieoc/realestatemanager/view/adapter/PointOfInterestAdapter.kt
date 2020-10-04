package com.sophieoc.realestatemanager.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.model.PointOfInterest
import com.sophieoc.realestatemanager.view.adapter.PointOfInterestAdapter.PointOfInterestViewHolder
import kotlinx.android.synthetic.main.point_of_interest_format.view.*

class PointOfInterestAdapter(private val pointOfInterests: List<PointOfInterest>) : RecyclerView.Adapter<PointOfInterestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointOfInterestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.point_of_interest_format, parent, false)
        return PointOfInterestViewHolder(view)
    }

    override fun onBindViewHolder(holder: PointOfInterestViewHolder, position: Int) {
        holder.bind(pointOfInterests[position])
    }

    override fun getItemCount() = pointOfInterests.size

    inner class PointOfInterestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pointOfInterest: PointOfInterest) {
            itemView.type_and_name_poi.text = "${pointOfInterest.type}: ${pointOfInterest.name} "
            itemView.address_poi.text = pointOfInterest.address
            itemView.distance.text = "${pointOfInterest.distance}m away"

        }
    }
}