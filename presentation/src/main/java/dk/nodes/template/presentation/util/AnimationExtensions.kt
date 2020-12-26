package dk.nodes.template.presentation.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

fun Canvas.drawTextOnCanvas(text: String, drawable: Drawable, itemView: View, leftSide: Boolean) {
    val paint = Paint()
    val bitmap = drawable.convertToBitmap()

    val textHeight = 40f
    paint.color = Color.WHITE
    paint.textSize = textHeight

    val alignmentX = if (leftSide) 0.25 else 0.75
    val bitmapX = (alignmentX * itemView.width) - ((bitmap?.width ?: 0) /2)
    val textX = (alignmentX * itemView.width) - (paint.measureText(text) / 2)

    val alignmentY = 0.5
    val bitmapY = (alignmentY * itemView.height) - (bitmap?.height ?: 0)
    val textY = (alignmentY * itemView.height) + textHeight

    val finalTextY =  (textY+ itemView.top).toFloat()
    val finalBitmapY = (bitmapY+ itemView.top).toFloat()

    this.drawText(text, textX.toFloat(),finalTextY, paint)
    bitmap?.let { this.drawBitmap(it, bitmapX.toFloat(), finalBitmapY, paint) }
}

fun View.fadeOutAndHide(speed: Long = 400, startDelay: Long = 0) {
    this.animate()
            .alpha(0.0f)
            .setStartDelay(startDelay)
            .withEndAction { this.visibility = View.GONE }
            .setDuration(speed)
            .setInterpolator(FastOutSlowInInterpolator())
            .start()
}