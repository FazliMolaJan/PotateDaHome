package com.iven.potatowallpapers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import java.lang.ref.WeakReference

class PotatoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private var mBackgroundPaint = Paint()
    private var mPotatoPaint = Paint()
    private var mPotatoPath = Path()
    private var mPotatoMatrix = Matrix()

    private var mDeviceWidth = 0
    private var mDeviceHeight = 0

    fun potateDaHome() {
        SavePotatoAsync(WeakReference(context), drawToBitmap(), mDeviceWidth, mDeviceHeight).execute()
    }

    init {
        //retrieve display specifications
        val window = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d = DisplayMetrics()
        window.defaultDisplay.getRealMetrics(d)
        mDeviceWidth = d.widthPixels
        mDeviceHeight = d.heightPixels

        //set paints props
        mBackgroundPaint.isAntiAlias = true
        mBackgroundPaint.color = ContextCompat.getColor(context, R.color.defaultBackgroundColor)

        mPotatoPaint.isAntiAlias = true
        mPotatoPaint.style = Paint.Style.FILL
        mPotatoPaint.color = ContextCompat.getColor(context, R.color.defaultPotatoColor)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //draw potato!
        PotatoObject.draw(
            canvas, mBackgroundPaint, mPotatoPaint,
            mPotatoMatrix, mPotatoPath, width.toFloat(), height.toFloat()
        )
    }

    //method to update wallpaper color
    fun updateColor(backgroundColor: Int, potatoColor: Int) {
        mBackgroundPaint.color = backgroundColor
        mPotatoPaint.color = potatoColor
        invalidate()
    }
}