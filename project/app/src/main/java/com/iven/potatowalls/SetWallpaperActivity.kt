package com.iven.potatowalls

import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.iven.potatowalls.ui.Utils
import com.iven.potatowalls.ui.VectorView
import kotlinx.android.synthetic.main.save_activity.*
import kotlinx.android.synthetic.main.seekbar_card.*
import kotlinx.android.synthetic.main.toolbar.*

const val SAVE_WALLPAPER = 0
const val SET_WALLPAPER = 1
const val SET_LIVE_WALLPAPER = 2

class SetWallpaperActivity : AppCompatActivity() {

    private var mBackgroundColor = 0
    private var sUserIsSeeking = false
    private var sScaleChanged = false
    private var mScaleFactor = 0.35F

    private lateinit var mVectorView: VectorView

    override fun onResume() {
        super.onResume()
        checkSystemAccent()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAndRemoveTask()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> if (Utils.hasToRequestWriteStoragePermission(this)) Utils.checkPermissions(
                this,
                SAVE_WALLPAPER
            ) else handleWallpaperChanges(SAVE_WALLPAPER)
            R.id.set -> if (Utils.hasToRequestWriteStoragePermission(this)) Utils.checkPermissions(
                this,
                SET_WALLPAPER
            ) else handleWallpaperChanges(SET_WALLPAPER)
            R.id.go_live -> handleWallpaperChanges(SET_LIVE_WALLPAPER)
            android.R.id.home -> finishAndRemoveTask()
        }
        return super.onOptionsItemSelected(item)
    }

    //manage request permission result, continue loading ui if permissions was granted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) Utils.checkPermissions(
            this,
            requestCode
        ) else

            when (requestCode) {
                SAVE_WALLPAPER -> handleWallpaperChanges(SAVE_WALLPAPER)
                SET_WALLPAPER -> handleWallpaperChanges(SET_WALLPAPER)
            }
    }

    //method to check if accent theme has changed on resume
    private fun checkSystemAccent(): Boolean {

        val isBackgroundAccented = mPotatoPreferences.isBackgroundAccented
        val isVectorAccented = mPotatoPreferences.isVectorAccented

        return if (!isBackgroundAccented && !isVectorAccented) {
            false
        } else {
            //get system accent color
            val systemAccentColor = Utils.getSystemAccentColor(this)

            //if changed, update it!
            if (systemAccentColor != mPotatoPreferences.backgroundColor || systemAccentColor != mPotatoPreferences.vectorColor) {

                //update cards colors
                if (isBackgroundAccented) mVectorView.updateBackgroundColor(systemAccentColor)
                if (isVectorAccented) mVectorView.updateVectorColor(systemAccentColor)
            }
            return true
        }
    }

    private fun handleWallpaperChanges(which: Int) {
        //do all the save shit here
        if (sScaleChanged) mPotatoPreferences.scale = mScaleFactor

        when (which) {
            SAVE_WALLPAPER -> mVectorView.vectorifyDaHome(false)
            SET_WALLPAPER -> mVectorView.vectorifyDaHome(true)
            SET_LIVE_WALLPAPER -> Utils.openLiveWallpaperIntent(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBackgroundColor = mPotatoPreferences.backgroundColor

        //set ui theme and immersive mode
        setTheme(mPotatoPreferences.theme)
        hideSystemUI()

        setContentView(R.layout.save_activity)

        //get all the views
        mVectorView = vector_view


        //determine if background color is dark or light and select
        //the appropriate color for UI widgets
        val widgetColors = Utils.getSecondaryColor(mBackgroundColor)


        //set toolbar shit
        //match theme with background luminance
        if (Utils.isColorDark(mBackgroundColor)) toolbar.context.setTheme(R.style.ToolbarStyle_Dark)
        else toolbar.context.setTheme(R.style.ToolbarStyle)

        toolbar.setBackgroundColor(mBackgroundColor)
        toolbar.setTitleTextColor(widgetColors)
        toolbar.setNavigationIcon(R.drawable.ic_back)

        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        //set seekbar and seekbar card shit
        seek_size.progressTintList = ColorStateList.valueOf(widgetColors)
        seek_size.thumbTintList = ColorStateList.valueOf(widgetColors)
        seek_size.progressBackgroundTintList = ColorStateList.valueOf(widgetColors)

        val isPospFull = Utils.isPospFull()

        //fit posp logo (which is rectangular) reducing its size
        //by setting max seebar value to 30 instead of 100. Set 100
        //for square logos
        seek_size.max = isPospFull.second

        //multiply decimal value * 100
        val seekbarValue = mPotatoPreferences.scale * 100

        //calculate normalized progress if we are drawing posp logo
        seek_size.progress = if (isPospFull.first) (0.25F * seekbarValue).toInt() else seekbarValue.toInt()

        //set saved size
        scale_text.text = Utils.getDecimalFormat(seek_size.progress.toFloat() / isPospFull.second).toString()

        //observe seekbar changes
        seek_size.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            var calculatedScaleFactor = 0F

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                sUserIsSeeking = true
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                val calculatedSizeForText = Utils.getDecimalFormat(progress.toFloat() / isPospFull.second)

                if (fromUser && calculatedSizeForText >= 0.20) {

                    calculatedScaleFactor =
                        if (isPospFull.first) (progress.toFloat() / 0.25F) / 100 else (progress.toFloat()) / 100

                    scale_text.text = calculatedSizeForText.toString()

                    //update vector view by doing some proportions
                    mVectorView.setScaleFactor(calculatedScaleFactor)
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                sUserIsSeeking = false
                sScaleChanged = true
                mScaleFactor = calculatedScaleFactor
            }
        })

        seekbar_title.setTextColor(widgetColors)
        seekbar_card.setCardBackgroundColor(mBackgroundColor)

        //set scale text
        scale_text.setTextColor(widgetColors)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    //immersive mode
    //https://developer.android.com/training/system-ui/immersive
    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}
