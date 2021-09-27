package com.sophieoc.realestatemanager.ui.property

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sophieoc.realestatemanager.databinding.ItemImageViewPagerBinding
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.ui.property.SliderAdapter.SliderViewHolder

class SliderAdapter(private val photos: List<Photo>) : RecyclerView.Adapter<SliderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemImageViewPagerBinding.inflate(layoutInflater, parent, false)
        itemBinding.executePendingBindings()
        return SliderViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    inner class SliderViewHolder(val binding: ItemImageViewPagerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: Photo) {
            binding.photo = photo
        }
    }
}