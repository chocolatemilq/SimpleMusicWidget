package com.example.musicwidget

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.util.Log

class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras

        // Extract title safely
        val titleObj = extras.get("android.title")
        val artistObj = extras.get("android.text")
        val albumArtIcon = extras.getParcelable<Icon>("android.largeIcon")
        val albumArtBitmap = getBitmapFromIcon(albumArtIcon)

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
            sendSongUpdate(title, artist, albumArtBitmap)
        }
    }

    private fun getBitmapFromIcon(icon: Icon?): Bitmap? {
        return icon?.let {
            try {
                it.loadDrawable(applicationContext)?.let { drawable ->
                    if (drawable is android.graphics.drawable.BitmapDrawable) {
                        drawable.bitmap
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun sendSongUpdate(title: String, artist: String, albumArt: Bitmap?) {
        Log.d("NotificationListener", "Sending broadcast: $title - $artist")
        val intent = Intent("android.appwidget.action.APPWIDGET_UPDATE")
        intent.putExtra("TITLE", title)
        intent.putExtra("ARTIST", artist)
        intent.putExtra("ALBUM_ART", albumArt)
        sendBroadcast(intent) // Broadcast the song info
    }
}
