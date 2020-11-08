package com.sophieoc.realestatemanager.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.BottomSheetDialogBinding
import java.text.DateFormat
import java.text.NumberFormat
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

fun BottomSheetDialog.buildBottomSheetDialog(): BottomSheetDialog {
    this.setContentView(getBinding().root)
    this.setCanceledOnTouchOutside(true)
    return this
}

private lateinit var bindingBottomSheet: BottomSheetDialogBinding
fun BottomSheetDialog.getBinding(): BottomSheetDialogBinding {
    val inflater = layoutInflater
    if (!::bindingBottomSheet.isInitialized)
        bindingBottomSheet = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_dialog, null, false)
    return bindingBottomSheet
}

