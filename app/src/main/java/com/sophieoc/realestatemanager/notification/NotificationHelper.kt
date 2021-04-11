package com.sophieoc.realestatemanager.notification

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sophieoc.realestatemanager.R

class NotificationHelper(base: Context?) : ContextWrapper(base) {
    var manager: NotificationManager? = null
        get() {
            if (field == null) {
                field = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }
            return field
        }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(CHANNEL_ID, PROPERTY_SAVED, NotificationManager.IMPORTANCE_HIGH)
        manager?.createNotificationChannel(channel)
    }

    fun getChannelNotification(message: String?): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_logo_notification)
    }

    companion object {
        const val NOTIFICATION_ID: Int = 1
        const val CHANNEL_ID = "channelID"
        const val PROPERTY_SAVED = "Property saved successfully"
    }
}