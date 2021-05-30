package com.sophieoc.realestatemanager.util

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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun Int.toBitmap(resources: Resources): BitmapDescriptor? {
    var drawable = ResourcesCompat.getDrawable(resources, this, null)
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

fun Int.formatToDollarsOrMeters(): String? {
    return NumberFormat.getNumberInstance(Locale.US).format(this)
}

fun LatLng.toStringFormat(): String {
    return "$latitude,$longitude"
}

fun Date?.toStringFormat(): String {
    val dateFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
    this?.let { return dateFormat.format(this) }
    return ""
}

fun String.toDate(): Date? {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    return formatter.parse(this)
}
