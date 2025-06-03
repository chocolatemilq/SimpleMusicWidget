package com.example.musicwidget.ui.theme

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import com.example.musicwidget.R
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Icon
import android.service.notification.StatusBarNotification
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.core.graphics.createBitmap

class MusicWidgetProvider : AppWidgetProvider() {

    companion object {
        private var lastKnownAlbumArt: Bitmap? = null
        private var lastKnownSongTitle: String = ""
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val title = intent.getStringExtra("TITLE") ?: "Unknown Title"
        val artist = intent.getStringExtra("ARTIST") ?: "Unknown Artist"
        val albumArt: Bitmap? = intent.getParcelableExtra("ALBUM_ART")
        updateWidget(context, title, artist, albumArt)
    }

    private fun updateWidget(context: Context, title: String, artist: String, albumArt: Bitmap?) {
        val views = RemoteViews(context.packageName, R.layout.music_widget)
        views.setTextViewText(R.id.song_title, title)
        views.setTextViewText(R.id.artist_name, artist)

        if (albumArt != null) {
            val newAlbumArt = Bitmap.createScaledBitmap(albumArt, 200, 200, true).copy(Bitmap.Config.ARGB_8888, true)
            views.setImageViewBitmap(R.id.album_art, getRoundedCornerBitmap(newAlbumArt, 18f))
            lastKnownAlbumArt = getRoundedCornerBitmap(newAlbumArt.copy(Bitmap.Config.ARGB_8888, true), 18f)
        } else {
            if (lastKnownSongTitle == title) {
                views.setImageViewBitmap(R.id.album_art, lastKnownAlbumArt)
                Log.d("MusicWidgetProvider", "Using Last Known")
            } else {
                // Set a default image if no album art is available
                val blankCover = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.music_note), 200, 200, true)
                val newBlankCover = blankCover.copy(Bitmap.Config.ARGB_8888, true)
                views.setImageViewBitmap(R.id.album_art, getRoundedCornerBitmap(newBlankCover, 18f))
            }
        }

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, MusicWidgetProvider::class.java)
        appWidgetManager.updateAppWidget(componentName, views)

        Log.d("MusicWidgetProvider", "Last Known Song Title: $lastKnownSongTitle")
        lastKnownSongTitle = title
        Log.d("MusicWidgetProvider", "Last Known Song Title: $lastKnownSongTitle")
    }


    private fun getRoundedCornerBitmap(bitmap: Bitmap, cornerRadius: Float): Bitmap? {
        val output = createBitmap(bitmap.width, bitmap.height)
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
            //Log.d("MusicWidgetProvider", "Updating widget instance: $appWidgetId")
            updateWidget(context, "Waiting for update...", "No data", null)
        }
    }
/*
    fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, fileName: String): String? {
        var savedImagePath: String? = null
        val file = File(context.filesDir, fileName)

        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream) // Save as PNG
            outputStream.flush()
            outputStream.close()
            savedImagePath = file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return savedImagePath
    }

    fun loadImageFromInternalStorage(context: Context, fileName: String): Bitmap? {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            null
        }
    }
*/
}
