package com.example.musicwidget

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.content.Intent
import android.util.Log

class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras

        // Extract title safely
        val titleObj = extras.get("android.title")
        val artistObj = extras.get("android.text")

        val title = when (titleObj) {
            is String -> titleObj
            is CharSequence -> titleObj.toString() // Handle SpannableString
            else -> "Unknown Title"
        }

        val artist = when (artistObj) {
            is String -> artistObj
            is CharSequence -> artistObj.toString()
            else -> "Unknown Artist"
        }

        Log.d("NotificationListener", "Now Playing: $title - $artist") // Debugging log

        if (title.isNotEmpty() && artist.isNotEmpty()) {
            sendSongUpdate(title, artist)
        }
    }


    private fun sendSongUpdate(title: String, artist: String) {
        Log.d("NotificationListener", "Sending broadcast: $title - $artist")
        val intent = Intent("android.appwidget.action.APPWIDGET_UPDATE")
        intent.putExtra("TITLE", title)
        intent.putExtra("ARTIST", artist)
        sendBroadcast(intent) // Broadcast the song info
    }
}
