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
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

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
            views.setImageViewBitmap(R.id.album_art, getRoundedCornerBitmap(albumArt, 16f))
        }

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, MusicWidgetProvider::class.java)
        appWidgetManager.updateAppWidget(componentName, views)

        // Force widget refresh
        //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.song_title)

        //Log.d("MusicWidgetProvider", "Widget successfully updated with: $title - $artist")
    }

    private fun getRoundedCornerBitmap(bitmap: Bitmap, cornerRadius: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val path = Path()

        val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
        canvas.clipPath(path)

        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return output
    }


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            Log.d("MusicWidgetProvider", "Updating widget instance: $appWidgetId")
            updateWidget(context, "Waiting for update...", "No data", null)
        }
    }
}
