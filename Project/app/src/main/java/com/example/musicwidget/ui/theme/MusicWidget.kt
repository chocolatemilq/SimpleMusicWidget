package com.example.musicwidget.ui.theme

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.RemoteViews
import com.example.musicwidget.R

class MusicWidgetProvider : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.d("MusicWidgetProvider", "Broadcast received: ${intent.action}")
        val title = intent.getStringExtra("TITLE") ?: "Unknown Title"
        val artist = intent.getStringExtra("ARTIST") ?: "Unknown Artist"
        val albumArt: Bitmap? = intent.getParcelableExtra("ALBUM_ART")

        Log.d("MusicWidgetProvider", "Updating widget with: $title - $artist")
        updateWidget(context, title, artist, albumArt)
    }

    private fun updateWidget(context: Context, title: String, artist: String, albumArt: Bitmap?) {
        val views = RemoteViews(context.packageName, R.layout.music_widget)
        views.setTextViewText(R.id.song_title, title)
        views.setTextViewText(R.id.artist_name, artist)

        if (albumArt != null) {
            views.setImageViewBitmap(R.id.album_art, albumArt)
        }

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, MusicWidgetProvider::class.java)
        appWidgetManager.updateAppWidget(componentName, views)

        // Force widget refresh
        //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.song_title)

        //Log.d("MusicWidgetProvider", "Widget successfully updated with: $title - $artist")
    }


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            Log.d("MusicWidgetProvider", "Updating widget instance: $appWidgetId")
            updateWidget(context, "Waiting for update...", "No data", null)
        }
    }
}
