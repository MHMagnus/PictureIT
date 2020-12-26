package dk.nodes.template.presentation.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

fun Drawable.convertToBitmap(): Bitmap? {
    var bitmap: Bitmap? = null

    if (this is BitmapDrawable) {
        if (this.bitmap != null) {
            return this.bitmap
        }
    }

    bitmap = if (this.intrinsicWidth <= 0 || this.intrinsicHeight <= 0) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
    } else {
        Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight, Bitmap.Config.ARGB_8888)
    }

    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)
    return bitmap
}

fun Int.convertToTime(): String {
    if (this < 60) return "${this} min."
    return if (this % 60 == 0) {
        "${this / 60} ${if (this / 60 == 1) "time." else "timer."}"
    } else {
        "${this / 60} t ${this % 60} min."
    }
}