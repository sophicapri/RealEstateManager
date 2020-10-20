package com.sophieoc.realestatemanager.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.model.Photo


@BindingAdapter("imageUrl")
fun ImageView.bindImageUrl(imageUrl: String?) {
    val url = if (imageUrl != null && imageUrl.isNotEmpty())
        imageUrl
    else if (this.id == R.id.profile_picture)
        NO_IMAGE_AVAILABLE_USER
    else
        NO_IMAGE_AVAILABLE_PROPERTY

    Glide.with(this.context)
            .load(url)
            .apply(RequestOptions().circleCrop())
            .into(this)
}

@BindingAdapter("propertyImageUrl")
fun ImageView.bindPropertyImage(photo: Photo?) {
    val url = if (photo != null) {
        if (photo.urlPhoto.isNotEmpty())
            photo.urlPhoto
        else
            NO_IMAGE_AVAILABLE_PROPERTY
    } else
        NO_IMAGE_AVAILABLE_PROPERTY
    Glide.with(this.context)
            .load(url)
            .apply(RequestOptions().centerCrop())
            .into(this)
}

@BindingAdapter("dollarFormat")
fun TextView.formatIntToDollar(price: Int) {
    this.text = price.formatToDollars()
}

@BindingAdapter("collapsingToolbarTitle")
fun Toolbar.bindCollapsingToolbarTitle(title: String?){
    title?.let {
        this.title = it
    }
}
