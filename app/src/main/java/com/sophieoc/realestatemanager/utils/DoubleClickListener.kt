package com.sophieoc.realestatemanager.utils

import android.os.SystemClock.elapsedRealtime
import android.view.View

/**
 * Is an onClickListener that handles doubleClickListener
 *
 * @param doubleClickQualificationTime The time in which the second tap should be done
 * in order to qualify as a double click. Time in MS
 */
abstract class DoubleClickListener(
        private val doubleClickQualificationTime: Long = 200,
) : View.OnClickListener {
    private var timestampLastClick = 0L

    override fun onClick(v: View) {
        if ((elapsedRealtime() - timestampLastClick) < doubleClickQualificationTime)
            onDoubleClick(v)
        timestampLastClick = elapsedRealtime();
    }

    /** When the view is double clicked */
    abstract fun onDoubleClick(v: View)
}

/**
 * Creates a [DoubleClickListener] and applies it to a view
 */
inline fun View.setOnDoubleClickListener(
        crossinline onDoubleClick: (View) -> Unit,
) {
    setOnClickListener(object : DoubleClickListener() {
        override fun onDoubleClick(v: View) {
            onDoubleClick(v)
        }
    })
}