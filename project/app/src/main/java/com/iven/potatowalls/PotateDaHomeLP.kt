package com.iven.potatowalls

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.util.DisplayMetrics
import android.view.SurfaceHolder
import android.view.WindowManager

class PotateDaHomeLP : WallpaperService() {

    private var mBackgroundPaint = Paint()
    private var mPotatoPaint = Paint()
    private var mStrokePaint = Paint()
    private var mPotatoPath = Path()
    private var mPotatoMatrix = Matrix()

    private var mDeviceWidth = 0F
    private var mDeviceHeight = 0F

    //the potato battery live potato_wallpaper service and engine
    override fun onCreateEngine(): Engine {

        if (baseContext != null) {
            //retrieve display specifications
            val window = baseContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val d = DisplayMetrics()
            window.defaultDisplay.getRealMetrics(d)
            mDeviceWidth = d.widthPixels.toFloat()
            mDeviceHeight = d.heightPixels.toFloat()

            getPaintProps()
        }

        return PotatoEngine()
    }

    private fun getPaintProps() {
        //set paints props
        mBackgroundPaint.isAntiAlias = true
        val backgroundColor = mPotatoPreferences.backgroundColor
        mBackgroundPaint.color = backgroundColor

        mPotatoPaint.isAntiAlias = true
        mPotatoPaint.style = Paint.Style.FILL
        val potatoColor = mPotatoPreferences.potatoColor
        mPotatoPaint.color = potatoColor
    }

    private fun checkSystemAccent() {

        val isBackgroundAccented = mPotatoPreferences.isBackgroundAccented
        val isPotatoAccented = mPotatoPreferences.isPotatoAccented

        if (isBackgroundAccented || isPotatoAccented) {
            //change only if system accent has changed
            val systemAccentColor = Utils.getSystemAccentColor(this)
            if (isBackgroundAccented && mBackgroundPaint.color != systemAccentColor) mBackgroundPaint.color =
                systemAccentColor
            if (isPotatoAccented && mPotatoPaint.color != systemAccentColor) mPotatoPaint.color = systemAccentColor
        }
    }

    private inner class PotatoEngine : WallpaperService.Engine() {

        private val handler = Handler()
        private var sVisible = true
        private val drawRunner = Runnable { draw() }

        override fun onVisibilityChanged(visible: Boolean) {
            sVisible = visible
            if (visible) {
                checkSystemAccent()
                handler.post(drawRunner)
            } else {
                handler.removeCallbacks(drawRunner)
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            sVisible = false
            handler.removeCallbacks(drawRunner)
        }

        override fun onDestroy() {
            super.onDestroy()
            sVisible = false
            handler.removeCallbacks(drawRunner)
        }

        //draw potato according to battery level
        private fun draw() {
            val holder = surfaceHolder
            var canvas: Canvas? = null
            try {
                //draw wallpaper
                canvas = holder.lockCanvas()
                if (canvas != null && baseContext != null) {
                    //draw potato!
                    PotatoObject.draw(
                        canvas, mBackgroundPaint, mPotatoPaint, mStrokePaint,
                        mPotatoMatrix, mPotatoPath, mDeviceWidth, mDeviceHeight
                    )
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas)
            }
            handler.removeCallbacks(drawRunner)
        }
    }
}