package com.example.musicwidget.ui.theme

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import com.example.musicwidget.R
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
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
        fun cutText(text: String): String {

            // Remove trailing spaces and symbols to leave alphanumeric
            fun removeTrailingSymbols(input: String): String {
                var result = input
                while (result.isNotEmpty() && !result.last().isLetterOrDigit()) {
                    result = result.dropLast(1) // Remove the last character
                }
                return result
            }

            // Trim to 20 characters plus (...)
            if (text.length > 20) {
                val shiftedText = text.substring(0, 20)
                val trimmedText = removeTrailingSymbols(shiftedText)
                return "$trimmedText..."
            }
            else
            {
                return text
            }
        }


        val views = RemoteViews(context.packageName, R.layout.music_widget)
        views.setTextViewText(R.id.song_title, cutText(title))
        views.setTextViewText(R.id.artist_name, cutText(artist))

        if (albumArt != null) {
            val newAlbumArt = Bitmap.createScaledBitmap(albumArt, 200, 200, true).copy(Bitmap.Config.ARGB_8888, true)
            views.setImageViewBitmap(R.id.album_art, getRoundedCornerBitmap(newAlbumArt, 18f))
            lastKnownAlbumArt = getRoundedCornerBitmap(newAlbumArt.copy(Bitmap.Config.ARGB_8888, true), 18f)
        } else {
            if (lastKnownSongTitle == title) {
                views.setImageViewBitmap(R.id.album_art, lastKnownAlbumArt)
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

        lastKnownSongTitle = title
    }

    private fun getRoundedCornerBitmap(bitmap: Bitmap, cornerRadius: Float): Bitmap? {
        var output: Bitmap = createBitmap(bitmap.width, bitmap.height)
        if(bitmap.height < bitmap.width) {
            output = createBitmap(bitmap.height, bitmap.height)
        }
        else{
            output = createBitmap(bitmap.width, bitmap.width)
        }
        val canvas = Canvas(output)
        val paint = Paint()
        val path = Path()

        val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
        canvas.clipPath(path)

        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return output
    }
}
