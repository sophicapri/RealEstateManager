package com.sophieoc.realestatemanager.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Int.toBitmap(resources: Resources?): BitmapDescriptor? {
    var drawable = ResourcesCompat.getDrawable(resources!!, this, null)
    var bitmap: Bitmap? = null
    if (drawable != null) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable).mutate()
        }
        bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
    }
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun LatLng.toStringFormat(): String{
    return "$latitude,$longitude"
}

fun Date.format(): String {
    val dateFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
    return dateFormat.format(this)
}
