package com.example.musicwidget

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Handler
import android.os.Looper
import android.util.Log

class NotificationListener : NotificationListenerService() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        val musicPlayerPackages = listOf(
            "com.spotify.music",
            "com.apple.android.music",
            "com.google.android.music",
            "com.google.android.apps.youtube.music",
            "com.apple.android.music",
            "com.maxmpz.audioplayer",
            "org.videolan.vlc",
            "com.amazon.mp3",
            "deezer.android.app",
            "no.tidal.android",
            "com.soundcloud.android",
            "org.akanework.gramophone"
        )
        Log.d("packageName", sbn.packageName)
        if (sbn.packageName in musicPlayerPackages) {
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
            var albumArtIcon = extras.getParcelable<Icon>("android.largeIcon")
            var albumArtBitmap = getBitmapFromIcon(albumArtIcon)
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
        val intent = Intent("android.appwidget.action.APPWIDGET_UPDATE")
        intent.putExtra("TITLE", title)
        intent.putExtra("ARTIST", artist)
        intent.putExtra("ALBUM_ART", albumArt)
        sendBroadcast(intent) // Broadcast the song info
    }
}
