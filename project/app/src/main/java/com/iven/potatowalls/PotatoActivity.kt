package com.iven.potatowalls

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.background_card.*
import kotlinx.android.synthetic.main.potato_activity.*
import kotlinx.android.synthetic.main.potato_card.*
import kotlinx.android.synthetic.main.presets_card.*

@Suppress("UNUSED_PARAMETER")
class PotatoActivity : AppCompatActivity() {

    private lateinit var mFab: FloatingActionButton
    private var mTheme = R.style.AppTheme

    companion object {

        //method to calculate colors luminance
        fun getTextColorForCard(color: Int): Int {
            return if (ColorUtils.calculateLuminance(color) < 0.35) Color.WHITE else Color.BLACK
        }

        fun getBackgroundColor(context: Context): Int {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(
                    context.getString(R.string.background_key),
                    ContextCompat.getColor(context, R.color.defaultBackgroundColor)
                )
        }

        fun getPotatoColor(context: Context): Int {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(
                    context.getString(R.string.potato_key),
                    ContextCompat.getColor(context, R.color.defaultPotatoColor)
                )
        }

        fun getTheme(context: Context): Int {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(
                    context.getString(R.string.theme_key),
                    R.style.AppTheme
                )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mTheme = getTheme(this)
        setTheme(mTheme)

        setContentView(R.layout.potato_activity)

        mFab = fab

        //update background card color and text from preferences
        val backgroundColor = getBackgroundColor(this)
        setBackgroundColorForUI(backgroundColor)

        //update potato card color and text from preferences
        val potatoColor = getPotatoColor(this)
        setPotatoColorForUI(potatoColor)

        //apply live wallpaper on fab click!
        mFab.setOnClickListener {
            val intent = Intent(
                WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER
            )
            intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this, PotateDaHomeLP::class.java)
            )

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        //set the bottom bar menu
        bar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.app_bar_info -> openGitHubPage()
                R.id.app_bar_theme -> setNewTheme()
                R.id.app_bar_restore -> setDefaultPotato()
            }
            return@setOnMenuItemClickListener true
        }

        //setup presets
        colors_rv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val colorsAdapter = ColorsAdapter(this)
        colors_rv.adapter = colorsAdapter

        colorsAdapter.onColorClick = { combo ->

            runOnUiThread {

                //update background card color and fab tint
                val comboBackgroundColor = ContextCompat.getColor(this, combo.first)
                setBackgroundColorForUI(comboBackgroundColor)

                //update potato card color and fab check
                val comboPotatoColor = ContextCompat.getColor(this, combo.second)
                setPotatoColorForUI(comboPotatoColor)

                //save the colors to preferences so we can retrieve in lp
                PreferenceManager.getDefaultSharedPreferences(this@PotatoActivity).edit()
                    .putInt(getString(R.string.potato_key), comboPotatoColor).apply()
                PreferenceManager.getDefaultSharedPreferences(this@PotatoActivity).edit()
                    .putInt(getString(R.string.background_key), comboBackgroundColor).apply()
            }
        }
    }

    private fun openGitHubPage() {

        //intent to open git link
        val openGitHubPageIntent = Intent(Intent.ACTION_VIEW)
        openGitHubPageIntent.data = Uri.parse(getString(R.string.app_git_link))

        //check if a browser is present
        if (openGitHubPageIntent.resolveActivity(packageManager) != null) startActivity(openGitHubPageIntent) else
            Toast.makeText(this, getString(R.string.install_browser_message), Toast.LENGTH_SHORT).show()
    }

    private fun setDefaultPotato() {
        val defaultBackgroundColor = ContextCompat.getColor(this, R.color.defaultBackgroundColor)
        val defaultPotatoColor = ContextCompat.getColor(this, R.color.defaultPotatoColor)
        setBackgroundColorForUI(defaultBackgroundColor)
        setPotatoColorForUI(defaultPotatoColor)

        //restore default colors
        PreferenceManager.getDefaultSharedPreferences(this@PotatoActivity).edit()
            .clear().apply()
        PreferenceManager.getDefaultSharedPreferences(this@PotatoActivity).edit()
            .clear().apply()
    }

    private fun getHexCode(color: Int): String {
        return getString(R.string.hex, Integer.toHexString(color)).toUpperCase()
    }

    private fun setBackgroundColorForUI(color: Int) {
        runOnUiThread {

            background_color.setCardBackgroundColor(color)
            val textColor = getTextColorForCard(color)
            background_color_head.setTextColor(textColor)
            background_color_subhead.setTextColor(textColor)
            background_color_subhead.text = getHexCode(color)
            mFab.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    private fun setPotatoColorForUI(color: Int) {
        runOnUiThread {
            potato_color.setCardBackgroundColor(color)
            val textColor = getTextColorForCard(color)
            potato_color_head.setTextColor(textColor)
            potato_color_subhead.setTextColor(textColor)
            potato_color_subhead.text = getHexCode(color)
            mFab.drawable.setTint(color)
        }
    }

    //set new theme
    private fun setNewTheme() {
        val theme = if (mTheme == R.style.AppTheme) R.style.AppTheme_Dark else R.style.AppTheme
        PreferenceManager.getDefaultSharedPreferences(this@PotatoActivity).edit()
            .putInt(getString(R.string.theme_key), theme).apply()

        val intent = Intent(this, PotatoActivity::class.java)
        startActivity(intent)
        finish()
    }

    //start material dialog
    private fun startColorPicker(key: String, materialCardView: MaterialCardView, head: TextView, subHead: TextView) {
        MaterialDialog(this).show {

            title(text = head.text.toString())

            colorChooser(
                colors = ColorPalette.Primary,
                subColors = ColorPalette.PrimarySub,
                allowCustomArgb = true,
                showAlphaSelector = false

            ) { _, color ->
                materialCardView.setCardBackgroundColor(color)
                val textColor = getTextColorForCard(color)
                head.setTextColor(textColor)
                subHead.setTextColor(textColor)
                subHead.text = getHexCode(color)

                when (key) {
                    getString(R.string.background_key) -> mFab.backgroundTintList =
                        ColorStateList.valueOf(color)
                    else -> mFab.drawable.setTint(color)
                }
                // Save color integer
                PreferenceManager.getDefaultSharedPreferences(this@PotatoActivity).edit().putInt(key, color).apply()
            }
            positiveButton(android.R.string.ok)
        }
    }

    fun startPotatoPicker(view: View) {
        startColorPicker(
            getString(R.string.potato_key),
            potato_color,
            potato_color_head,
            potato_color_subhead
        )
    }

    fun startBackgroundPicker(view: View) {
        startColorPicker(
            getString(R.string.background_key),
            background_color,
            background_color_head,
            background_color_subhead
        )
    }
}