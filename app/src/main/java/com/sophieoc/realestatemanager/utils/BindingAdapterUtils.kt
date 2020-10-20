package com.sophieoc.realestatemanager.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.view.adapter.PicturesAdapter
import org.w3c.dom.Text

@BindingAdapter("imageUrl")
fun ImageView.bindImageUrl(url: String?) {
    url?.let {
        if (it.isNotEmpty())
            Glide.with(this.context)
                    .load(it)
                    .apply(RequestOptions().centerCrop())
                    .into(this)
        else
            NO_IMAGE_AVAILABLE
    }
}

@BindingAdapter("propertyImageUrl")
fun ImageView.bindPropertyImage(photo: Photo?) {
    val url = if (photo != null) {
        if (photo.urlPhoto.isNotEmpty())
            photo.urlPhoto
        else
            NO_IMAGE_AVAILABLE
    } else
        NO_IMAGE_AVAILABLE
    Glide.with(this.context)
            .load(url)
            .apply(RequestOptions().centerCrop())
            .into(this)
}


@BindingAdapter("dollarFormat")
fun TextView.formatIntToDollar(price: Int) {
    this.text = price.formatToDollars()
}
