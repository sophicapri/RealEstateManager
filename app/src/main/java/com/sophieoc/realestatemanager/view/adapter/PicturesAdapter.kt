package com.sophieoc.realestatemanager.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.model.Property
import kotlinx.android.synthetic.main.fragment_edit_create_property.view.*
import kotlinx.android.synthetic.main.pictures_property_edit_format.view.*

class PicturesAdapter(var glide: RequestManager) :
        RecyclerView.Adapter<PicturesAdapter.PicturesViewHolder>() {
    var pictures = ArrayList<Photo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicturesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pictures_property_edit_format, parent, false)
        return PicturesViewHolder(view)
    }

    override fun onBindViewHolder(holder: PicturesViewHolder, position: Int) {
        holder.bind(pictures[position])
    }

    override fun getItemCount() = pictures.size

    fun updatePictures(pictures: ArrayList<Photo>) {
        this.pictures = pictures
        notifyDataSetChanged()
    }

    inner class PicturesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(photo: Photo) {
            glide.load(photo.urlPhoto)
                    .apply(RequestOptions().centerCrop())
                    .into(itemView.picture_property)

            itemView.picture_description_input.text.insert(0, photo.description)
        }

        init {
            itemView.delete_picture.setOnClickListener {
                // TODO: add alertDialog to confirm action 4/10/2020
                pictures.removeAt(adapterPosition)
                notifyDataSetChanged()
            }
        }
    }
}