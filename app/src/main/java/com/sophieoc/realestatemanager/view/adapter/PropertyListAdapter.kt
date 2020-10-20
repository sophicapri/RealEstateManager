package com.sophieoc.realestatemanager.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sophieoc.realestatemanager.databinding.ItemPropertyBinding
import com.sophieoc.realestatemanager.model.Property

class PropertyListAdapter(
        private var onPropertyClickListener: OnPropertyClickListener)
    : RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder>() {
    var propertyList: ArrayList<Property> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding: ItemPropertyBinding = ItemPropertyBinding.inflate(layoutInflater, parent, false)
        itemBinding.executePendingBindings()
        return PropertyViewHolder(itemBinding)
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

    inner class PropertyViewHolder(val binding: ItemPropertyBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onPropertyClickListener.onPropertyClick(propertyList[adapterPosition].id)
            }
        }

        fun bind(property: Property) {
            binding.property = property
        }
    }

    interface OnPropertyClickListener {
        fun onPropertyClick(propertyId: String)
    }
}
