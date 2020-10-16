package com.sophieoc.realestatemanager.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sophieoc.realestatemanager.view.adapter.PicturesAdapter

@BindingAdapter("imageUrl")
fun ImageView.bindImageUrl(url: String?) {
    url?.let {
        Glide.with(this.context)
                .load(it)
                .apply(RequestOptions().centerCrop())
                .into(this)
    }
}