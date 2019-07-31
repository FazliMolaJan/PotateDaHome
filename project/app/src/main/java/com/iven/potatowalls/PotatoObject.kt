package com.iven.potatowalls

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.NonNull
import androidx.core.graphics.ColorUtils

object PotatoObject {

    fun draw(
        @NonNull c: Canvas?,
        @NonNull backgroundPaint: Paint,
        @NonNull potatoPaint: Paint,
        @NonNull potatoMatrix: Matrix,
        @NonNull potatoPath: Path,
        cw: Float,
        ch: Float
    ) {

        //size of the potato calculated from canvas width, change float factor to change it
        val ph = cw * 0.75F

        val ow = 200f
        val oh = 200f

        val od = if (cw / ow < ph / oh) cw / ow else ph / oh

        c?.translate((cw - od * ow) / 2f, (ch - od * oh) / 2f)

        c?.drawPaint(backgroundPaint)
        potatoMatrix.reset()
        potatoMatrix.setScale(od, od)

        c?.scale(1.78f, 1.78f)

        potatoPath.reset()
        potatoPath.moveTo(56.32f, 0.0f)
        potatoPath.moveTo(86.22f, 82.0f)
        potatoPath.cubicTo(81.22f, 90.49f, 70.84f, 93.56f, 63.12f, 92.69f)
        potatoPath.cubicTo(52.07f, 91.43f, 45.0f, 81.86f, 37.83f, 72.06f)
        potatoPath.cubicTo(26.0f, 50.77f, 23.0f, 44.62f, 23.83f, 36.89f)
        potatoPath.cubicTo(24.1f, 34.05f, 24.64f, 28.73f, 28.91f, 24.57f)
        potatoPath.cubicTo(32.5f, 21.06f, 38.33f, 18.83f, 43.42f, 20.26f)
        potatoPath.cubicTo(47.25f, 21.33f, 47.83f, 23.66f, 54.71f, 31.09f)
        potatoPath.cubicTo(65.66f, 41.83f, 67.59f, 43.31f, 68.64f, 44.13f)
        potatoPath.cubicTo(70.57f, 45.66f, 71.53f, 46.42f, 72.8f, 47.25f)
        potatoPath.cubicTo(76.28f, 49.57f, 78.27f, 50.25f, 81.01f, 52.76f)
        potatoPath.cubicTo(87.7f, 60.81f, 91.82f, 72.43f, 86.22f, 82.0f)

        potatoPath.transform(potatoMatrix)

        //if the color is the same!
        if (backgroundPaint.color == potatoPaint.color) {
            potatoPaint.style = Paint.Style.STROKE
            potatoPaint.strokeWidth = 5F
            potatoPaint.color = ColorUtils.setAlphaComponent(Utils.getSecondaryColor(backgroundPaint.color), 255 / 2)
        }
        c?.drawPath(potatoPath, potatoPaint)
    }
}