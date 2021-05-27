package com.sophieoc.realestatemanager.presentation.ui.editproperty

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sophieoc.realestatemanager.databinding.ItemEditPhotoPropertyBinding
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.presentation.ui.PropertyViewModel


class PicturePropertyAdapter(var onDeletePictureListener: OnDeletePictureListener, var onSetAsCoverListener: OnSetAsCoverListener, var propertyViewModel: PropertyViewModel) :
        RecyclerView.Adapter<PicturePropertyAdapter.PicturesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicturesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemEditPhotoPropertyBinding.inflate(layoutInflater, parent, false)
        itemBinding.executePendingBindings()
        return PicturesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PicturesViewHolder, position: Int) {
        holder.bind(propertyViewModel.property.photos[position])
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return if (propertyViewModel.property.photos.isNotEmpty())
            propertyViewModel.property.photos.size
        else
            0
    }

    inner class PicturesViewHolder(val binding: ItemEditPhotoPropertyBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            binding.photo = photo
            if (adapterPosition == 0) {
                binding.setAsCoverBtn.visibility = GONE
                binding.coverPic.visibility = VISIBLE
            } else {
                binding.setAsCoverBtn.visibility = VISIBLE
                binding.coverPic.visibility = GONE
            }
            binding.setAsCoverBtn.setOnClickListener {
                onSetAsCoverListener.onSetAsCoverClick(adapterPosition, ArrayList(propertyViewModel.property.photos))
                notifyDataSetChanged()
            }
        }

        init {
            binding.deletePicture.setOnClickListener {
                onDeletePictureListener.onDeleteClick(adapterPosition, ArrayList(propertyViewModel.property.photos))
                notifyDataSetChanged()
            }
        }
    }

    interface OnDeletePictureListener {
        fun onDeleteClick(position: Int, photos: ArrayList<Photo>)
    }

    interface OnSetAsCoverListener {
        fun onSetAsCoverClick(position: Int, photos: ArrayList<Photo>)
    }

    companion object{
        const val TAG = "AddPictureAdapter"
    }
}