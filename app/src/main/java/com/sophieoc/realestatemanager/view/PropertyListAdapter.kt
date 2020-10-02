package com.sophieoc.realestatemanager.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.model.Property
import kotlinx.android.synthetic.main.list_properties_format.view.*

class PropertyListAdapter(
        private var onPropertyClickListener: OnPropertyClickListener,
        var glide: RequestManager,
) : RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder>() {
    var propertyList: ArrayList<Property> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_properties_format, parent, false)
        return PropertyViewHolder(view, onPropertyClickListener)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.bind(propertyList[position])
    }

    override fun getItemCount(): Int {
        return propertyList.size
    }

    fun updateList(propertyList: ArrayList<Property>) {
        this.propertyList = propertyList
        notifyDataSetChanged()
    }

    inner class PropertyViewHolder(itemView: View, onPropertyClickListener: OnPropertyClickListener) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onPropertyClickListener.onPropertyClick(propertyList[adapterPosition].id)
            }
        }

        fun bind(property: Property) {
            if (property.photos.isNotEmpty())
            glide.load(property.photos[0].urlPhoto)
                    .apply(RequestOptions().centerCrop())
                    .into(itemView.image_property)
            itemView.type_and_city.text = "${property.type.s} in ${property.address.city}"
            itemView.price_property.text = "$${property.price}"
            itemView.nbr_of_beds_input.text = property.numberOfBedrooms.toString()
            itemView.nbr_of_bath.text = property.numberOfBathrooms.toString()
            itemView.surface.text = property.surface.toString()
        }
    }

    interface OnPropertyClickListener {
        fun onPropertyClick(propertyId: String)
    }
}
