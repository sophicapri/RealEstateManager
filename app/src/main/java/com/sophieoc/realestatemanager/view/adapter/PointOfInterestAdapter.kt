package com.sophieoc.realestatemanager.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.model.PointOfInterest
import com.sophieoc.realestatemanager.utils.formatToDollarsOrMeters
import com.sophieoc.realestatemanager.view.adapter.PointOfInterestAdapter.PointOfInterestViewHolder

class PointOfInterestAdapter(private val pointOfInterests: List<PointOfInterest>) : RecyclerView.Adapter<PointOfInterestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointOfInterestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_point_of_interest, parent, false)
        return PointOfInterestViewHolder(view)
    }

    override fun onBindViewHolder(holder: PointOfInterestViewHolder, position: Int) {
        holder.bind(pointOfInterests[position])
    }

    override fun getItemCount() = pointOfInterests.size

    inner class PointOfInterestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pointOfInterest: PointOfInterest) {
            val typeAndNamePoi = itemView.findViewById<TextView>(R.id.type_and_name_poi)
            val addressPoi = itemView.findViewById<TextView>(R.id.address_poi)
            val distance = itemView.findViewById<TextView>(R.id.distance)
            typeAndNamePoi.text = itemView.context.getString(R.string.poi_type_name,
                    pointOfInterest.type, pointOfInterest.name )
            addressPoi.text = pointOfInterest.address
            distance.text = itemView.context.getString(R.string.distance_format, pointOfInterest.distance.formatToDollarsOrMeters())

        }
    }
}