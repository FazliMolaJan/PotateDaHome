package com.iven.potatowallpapers

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.colors_panel.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //request permission to save png
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        } else {
            initView()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initView()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun applyWallpaper(view: View) {
        potato_view.potateDaHome()
    }

    private fun initView() {

        colors_rv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val colorsAdapter = ColorsAdapter(this)
        colors_rv.adapter = colorsAdapter

        //round the panel
        colors_panel.afterMeasured {
            radius = height / 2F
        }

        colorsAdapter.onColorClick = { combo ->

            val backgroundColor = ContextCompat.getColor(this, combo.first)
            val potatoColor = ContextCompat.getColor(this, combo.second)
            val isDark = isDark(backgroundColor)

            runOnUiThread {
                potato_view.updateColor(backgroundColor, potatoColor)

                window.statusBarColor = backgroundColor
                window.navigationBarColor = backgroundColor

                //set apply button color
                apply_button.setBackgroundColor(potatoColor)
                apply_button.drawable.setTint(backgroundColor)

                //enable or disable light system bars to improve their visibility
                //according to color luminance
                enableLightNavigationBar(isDark)
                enableLightStatusBar(isDark)
            }
        }
    }

    //method to calculate colors luminance
    private fun isDark(color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) < 0.35
    }

    //method to set light status bar
    private fun enableLightStatusBar(enable: Boolean) {

        val decorView = window.decorView
        val oldSystemUiFlags = decorView.systemUiVisibility
        var newSystemUiFlags = oldSystemUiFlags

        newSystemUiFlags = if (enable)
            newSystemUiFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        else
            newSystemUiFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        //just to avoid to set light status bar if it already enabled and viceversa
        if (newSystemUiFlags != oldSystemUiFlags) {
            decorView.systemUiVisibility = newSystemUiFlags
        }
    }

    //method to set light navigation bar
    private fun enableLightNavigationBar(enable: Boolean) {

        val decorView = window.decorView
        val oldSystemUiFlags = decorView.systemUiVisibility
        var newSystemUiFlags = oldSystemUiFlags

        newSystemUiFlags = if (enable)
            newSystemUiFlags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        else
            newSystemUiFlags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        //just to avoid to set light status bar if it already enabled and viceversa
        if (newSystemUiFlags != oldSystemUiFlags) {
            decorView.systemUiVisibility = newSystemUiFlags
        }
    }

    //viewTreeObserver extension to measure layout params
    //https://antonioleiva.com/kotlin-ongloballayoutlistener/
    private inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    f()
                }
            }
        })
    }
}
