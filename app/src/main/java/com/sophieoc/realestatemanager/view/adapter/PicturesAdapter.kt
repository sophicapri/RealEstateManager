package com.sophieoc.realestatemanager.view.adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sophieoc.realestatemanager.databinding.ItemEditPhotoPropertyBinding
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.utils.NO_IMAGE_AVAILABLE
import com.sophieoc.realestatemanager.viewmodel.PropertyViewModel


class PicturesAdapter(var onDeletePictureListener: OnDeletePictureListener, var propertyViewModel: PropertyViewModel) :
        RecyclerView.Adapter<PicturesAdapter.PicturesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicturesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding: ItemEditPhotoPropertyBinding = ItemEditPhotoPropertyBinding.inflate(layoutInflater, parent, false)
        itemBinding.propertyViewModel = propertyViewModel
        itemBinding.executePendingBindings()
        return PicturesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PicturesViewHolder, position: Int) {
        holder.bind(propertyViewModel.property.photos[position])
    }

    override fun getItemCount(): Int {
        return if (propertyViewModel.property.photos[0].urlPhoto != NO_IMAGE_AVAILABLE)
            propertyViewModel.property.photos.size
        else
            0
    }

    inner class PicturesViewHolder(val binding: ItemEditPhotoPropertyBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            binding.photo = photo
            binding.pictureDescriptionInput.addTextChangedListener(getTextWatcher())
        }

        init {
            binding.deletePicture.setOnClickListener {
                // TODO: add alertDialog to confirm action
                onDeletePictureListener.onDeleteClick(adapterPosition, propertyViewModel.property.photos)
                notifyDataSetChanged()
            }
        }

        private fun getTextWatcher() = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.propertyViewModel.property.photos[adapterPosition].description = s.toString()
            }
        }
    }

    interface OnDeletePictureListener {
        fun onDeleteClick(position: Int, photos: List<Photo>)
    }
    companion object{
        const val TAG = "AddPictureAdapter"
    }
}