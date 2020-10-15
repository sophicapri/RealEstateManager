package com.sophieoc.realestatemanager.view.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.model.Photo
import kotlinx.android.synthetic.main.item_edit_property.view.*

class PicturesAdapter(var glide: RequestManager, var onDeletePictureListener: OnDeletePictureListener) :
        RecyclerView.Adapter<PicturesAdapter.PicturesViewHolder>() {
    var pictures = ArrayList<Photo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicturesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_property, parent, false)
        return PicturesViewHolder(view, onDeletePictureListener)
    }

    override fun onBindViewHolder(holder: PicturesViewHolder, position: Int) {
            holder.bind(pictures[position])
    }

    override fun getItemCount() = pictures.size

    fun updatePictures(pictures: ArrayList<Photo>) {
        this.pictures = pictures
        notifyDataSetChanged()
    }

    inner class PicturesViewHolder(itemView: View, onDeletePictureListener: OnDeletePictureListener) : RecyclerView.ViewHolder(itemView) {
        fun bind(photo: Photo) {
       /*     glide.load(photo.urlPhoto)
                    .apply(RequestOptions().centerCrop())
                    .into(itemView.picture_property)

            if (itemView.picture_description_input.text.isEmpty() && photo.description.isNotEmpty())
                itemView.picture_description_input.text.insert(0, photo.description)
            itemView.picture_description_input.addTextChangedListener(getTextWatcher(photo))
        */

        }

        init {
            itemView.delete_picture.setOnClickListener {
                // TODO: add alertDialog to confirm action 4/10/2020
                onDeletePictureListener.onDeleteClick(adapterPosition, pictures)
                notifyDataSetChanged()
            }
        }
    }

    private fun getTextWatcher(photo: Photo) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            photo.description = s.toString()
        }
    }

    interface OnDeletePictureListener {
        fun onDeleteClick(position: Int, pictures: ArrayList<Photo>)
    }
}