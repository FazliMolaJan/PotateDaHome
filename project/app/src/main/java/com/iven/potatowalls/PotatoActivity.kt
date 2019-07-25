package com.iven.potatowalls

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.background_card.*
import kotlinx.android.synthetic.main.cards_layout.*
import kotlinx.android.synthetic.main.potato_activity.*
import kotlinx.android.synthetic.main.potato_card.*
import kotlinx.android.synthetic.main.presets_card.*

@Suppress("UNUSED_PARAMETER")
class PotatoActivity : AppCompatActivity() {

    private var mTheme = R.style.AppTheme

    private var mBackgroundColor = 0
    private var mPotatoColor = 0

    private lateinit var mFab: FloatingActionButton

    private var sBackgroundColorChanged = false
    private var sPotatoColorChanged = false
    private var sBackgroundAccentSet = false
    private var sPotatoAccentSet = false

    override fun onResume() {
        super.onResume()
        //check if accent theme has changed
        checkSystemAccent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //set theme
        mTheme = mPotatoPreferences.theme
        setTheme(mTheme)

        setContentView(R.layout.potato_activity)

        //get the fab (don't move from this position)
        mFab = fab

        //apply live wallpaper on fab click!
        mFab.setOnClickListener {

            //do all the save shit here
            if (sBackgroundColorChanged) {
                mPotatoPreferences.isBackgroundAccented = false
                mPotatoPreferences.backgroundColor = mBackgroundColor
            }
            if (sPotatoColorChanged) {
                mPotatoPreferences.isPotatoAccented = false
                mPotatoPreferences.potatoColor = mPotatoColor
            }
            if (sBackgroundAccentSet) {
                mPotatoPreferences.isBackgroundAccented = true
                mPotatoPreferences.backgroundColor = mBackgroundColor
            }
            if (sPotatoAccentSet) {
                mPotatoPreferences.isPotatoAccented = true
                mPotatoPreferences.potatoColor = mPotatoColor
            }

            Utils.openLiveWallpaperIntent(this)
        }

        //update background card color and text from preferences
        mBackgroundColor = mPotatoPreferences.backgroundColor
        setBackgroundColorForUI(mBackgroundColor, false)

        //update potato card color and text from preferences
        mPotatoColor = mPotatoPreferences.potatoColor
        setPotatoColorForUI(mPotatoColor, false)

        //set the bottom bar menu
        val bottomBar = bar
        bottomBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.app_bar_info -> openGitHubPage()
                R.id.app_bar_theme -> setNewTheme()
                R.id.app_bar_restore -> setDefaultPotato()
            }
            return@setOnMenuItemClickListener true
        }

        //set bottom margin to show posp logo on top of the bottom bar
        bottomBar.afterMeasured {
            val pospLogo = posp_logo
            val lp = pospLogo.layoutParams as FrameLayout.LayoutParams
            lp.setMargins(0, 0, 0, height)
            pospLogo.layoutParams = lp
        }

        //setup presets
        colors_rv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val colorsAdapter = ColorsAdapter(this)
        colors_rv.adapter = colorsAdapter

        colorsAdapter.onColorClick = { combo ->

            setBackgroundAndPotatoColorsChanged()

            runOnUiThread {
                //update background card color and fab tint
                val comboBackgroundColor = ContextCompat.getColor(this, combo.first)
                setBackgroundColorForUI(comboBackgroundColor, false)

                //update potato card color and fab check drawable
                val comboPotatoColor = ContextCompat.getColor(this, combo.second)
                setPotatoColorForUI(comboPotatoColor, false)

                //save the colors to preferences so we can retrieve in lp
                sBackgroundColorChanged = true
                sPotatoColorChanged = true
            }
        }

        //setup long click behaviour on system accent buttons
        setAccentButtonsMessage(background_system_accent, potato_system_accent)
    }

    //update background card colors
    private fun setBackgroundColorForUI(color: Int, isSystemAccentChanged: Boolean) {
        mBackgroundColor = color

        //if system accent has changed update preferences on resume with the new accent
        if (isSystemAccentChanged) {
            //sBackgroundAccentSet = true
            mPotatoPreferences.backgroundColor = mBackgroundColor
        }

        //update shit colors
        runOnUiThread {
            background_color.setCardBackgroundColor(color)
            val textColor = Utils.getSecondaryColor(color)
            background_color_head.setTextColor(textColor)
            background_color_subhead.setTextColor(textColor)
            background_color_subhead.text = getHexCode(color)
            mFab.backgroundTintList = ColorStateList.valueOf(color)
            background_system_accent.drawable.setTint(textColor)

            //check if colors are the same so we enable stroke to make potato visible
            val fabDrawableColor = if (checkIfColorsEquals()) textColor else mPotatoColor
            mFab.drawable.setTint(fabDrawableColor)
        }
    }

    //update potato card colors
    private fun setPotatoColorForUI(color: Int, isSystemAccentChanged: Boolean) {
        mPotatoColor = color

        //if system accent has changed update preferences on resume with the new accent
        if (isSystemAccentChanged) mPotatoPreferences.potatoColor = mPotatoColor

        //update shit colors
        runOnUiThread {
            potato_color.setCardBackgroundColor(color)
            val textColor = Utils.getSecondaryColor(color)
            potato_color_head.setTextColor(textColor)
            potato_color_subhead.setTextColor(textColor)
            potato_color_subhead.text = getHexCode(color)
            potato_system_accent.drawable.setTint(textColor)

            //check if colors are the same so we enable stroke to make potato visible
            val fabDrawableColor = if (checkIfColorsEquals()) textColor else color
            mFab.drawable.setTint(fabDrawableColor)
        }
    }

    //set system accent as background color
    fun setSystemAccentForBackground(view: View) {
        sBackgroundAccentSet = true
        val systemAccent = Utils.getSystemAccentColor(this)
        setBackgroundColorForUI(systemAccent, false)
    }

    //set system accent as potato color
    fun setSystemAccentForPotato(view: View) {
        sPotatoAccentSet = true
        val systemAccent = Utils.getSystemAccentColor(this)
        setPotatoColorForUI(systemAccent, false)
    }

    //restore default background and potato colors
    private fun setDefaultPotato() {

        setBackgroundColorForUI(ContextCompat.getColor(this, R.color.default_background_color), false)
        setPotatoColorForUI(ContextCompat.getColor(this, R.color.default_potato_color), false)

        setBackgroundAndPotatoColorsChanged()
    }

    //start material dialog
    private fun startColorPicker(key: String, title: String) {
        MaterialDialog(this).show {

            title(text = title)

            colorChooser(
                colors = ColorPalette.Primary,
                subColors = ColorPalette.PrimarySub,
                allowCustomArgb = true,
                showAlphaSelector = false

            ) { _, color ->
                when (key) {
                    getString(R.string.background_color_key) -> {
                        //update the color only if it really changed
                        if (mBackgroundColor != color) {
                            sBackgroundColorChanged = true
                            sBackgroundAccentSet = false
                            setBackgroundColorForUI(color, false)
                        }
                    }
                    else -> {
                        //update the color only if it really changed
                        if (mPotatoColor != color) {
                            sPotatoColorChanged = true
                            sPotatoAccentSet = false
                            setPotatoColorForUI(color, false)
                        }
                    }
                }
            }
            positiveButton(android.R.string.ok)
        }
    }

    private fun setBackgroundAndPotatoColorsChanged() {
        sBackgroundAccentSet = false
        sPotatoAccentSet = false
        sBackgroundColorChanged = true
        sPotatoColorChanged = true
    }

    //method to start background color picker for background
    fun startBackgroundColorPicker(view: View) {
        startColorPicker(
            getString(R.string.background_color_key),
            getString(R.string.title_background)
        )
    }

    //method to start potato color picker for background
    fun startPotatoColorPicker(view: View) {
        startColorPicker(
            getString(R.string.potato_color_key),
            getString(R.string.title_potato)
        )
    }

    //update theme
    private fun setNewTheme() {
        val newTheme = if (mTheme == R.style.AppTheme) R.style.AppTheme_Dark else R.style.AppTheme
        mPotatoPreferences.theme = newTheme

        //smoothly set app theme
        val intent = Intent(this, PotatoActivity::class.java)
        startActivity(intent)
        finish()
    }

    //check if background and potato colors are equals
    private fun checkIfColorsEquals(): Boolean {
        return mBackgroundColor == mPotatoColor
    }

    //returns formatted hex string
    private fun getHexCode(color: Int): String {
        return getString(R.string.hex, Integer.toHexString(color)).toUpperCase()
    }

    private fun setAccentButtonsMessage(vararg views: View) {
        views.forEach {
            it.setOnLongClickListener {
                Toast.makeText(this, getString(R.string.title_set_accent), Toast.LENGTH_SHORT)
                    .show()
                return@setOnLongClickListener true
            }
        }
    }

    //method to check if accent theme has changed on resume
    private fun checkSystemAccent(): Boolean {

        val isBackgroundAccented = mPotatoPreferences.isBackgroundAccented
        val isPotatoAccented = mPotatoPreferences.isPotatoAccented

        return if (!isBackgroundAccented && !isPotatoAccented) {
            false
        } else {
            //get system accent color
            val systemAccentColor = Utils.getSystemAccentColor(this)

            //if changed, update it!
            if (systemAccentColor != mPotatoPreferences.backgroundColor || systemAccentColor != mPotatoPreferences.potatoColor) {

                //update cards colors
                if (isBackgroundAccented) setBackgroundColorForUI(systemAccentColor, true)
                if (isPotatoAccented) setPotatoColorForUI(systemAccentColor, true)
            }
            return true
        }
    }

    //method to open git page
    private fun openGitHubPage() {
        //intent to open git link
        val openGitHubPageIntent = Intent(Intent.ACTION_VIEW)
        openGitHubPageIntent.data = Uri.parse(getString(R.string.app_git_link))

        //check if a browser is present
        if (openGitHubPageIntent.resolveActivity(packageManager) != null) startActivity(openGitHubPageIntent) else
            Toast.makeText(this, getString(R.string.install_browser_message), Toast.LENGTH_SHORT).show()
    }

    //Generified function to measure layout params
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