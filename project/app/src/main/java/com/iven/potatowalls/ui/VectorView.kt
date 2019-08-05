package com.iven.potatowalls.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import com.iven.potatowalls.SaveWallpaperAsync
import com.iven.potatowalls.mDeviceMetrics
import com.iven.potatowalls.mPotatoPreferences
import java.lang.ref.WeakReference

class VectorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var mDeviceWidth = 0
    private var mDeviceHeight = 0

    private var mBackgroundColor = 0
    private var mDrawableColor = 0
    private var mScaleFactor = 0.35F

    fun vectorifyDaHome(isSetAsWallpaper: Boolean) {
        SaveWallpaperAsync(
            WeakReference(context),
            drawToBitmap(),
            mDeviceWidth,
            mDeviceHeight,
            isSetAsWallpaper
        ).execute()
    }

    init {
        //retrieve display specifications
        mDeviceWidth = mDeviceMetrics.first
        mDeviceHeight = mDeviceMetrics.second

        mScaleFactor = mPotatoPreferences.scale
        mBackgroundColor = mPotatoPreferences.backgroundColor
        mDrawableColor = mPotatoPreferences.vectorColor
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas != null && context != null) {
            //draw the vector!
            canvas.drawColor(mBackgroundColor)
            setColorFilter(mDrawableColor)
            val bit = ContextCompat.getDrawable(context, mPotatoPreferences.vector) as VectorDrawable
            bit.mutate()
            bit.setTint(mDrawableColor)

            //darken or lighten color to increase vector visibility when the colors are the same
            if (mBackgroundColor == mDrawableColor) {
                if (Utils.isColorDark(mDrawableColor)) bit.setTint(Utils.lightenColor(mDrawableColor, 0.20F))
                else bit.setTint(Utils.darkenColor(mDrawableColor, 0.20F))
            }
            Utils.drawBitmap(bit, canvas, mDeviceWidth, mDeviceHeight, mScaleFactor)
        }
    }

    //update scale factor when the user stop seeking
    fun setScaleFactor(scale: Float) {
        mScaleFactor = scale
        invalidate()
    }

    //update background color
    fun updateBackgroundColor(color: Int) {
        mBackgroundColor = color
        invalidate()
    }

    //update vector color
    fun updateVectorColor(color: Int) {
        mDrawableColor = color
        invalidate()
    }
}