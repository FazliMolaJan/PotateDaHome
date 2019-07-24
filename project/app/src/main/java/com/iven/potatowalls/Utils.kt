@file:JvmName("Utils")

package com.iven.potatowalls

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils

object Utils {

    //get system accent color
    @JvmStatic
    fun getSystemAccentColor(context: Context): Int {
        return ContextCompat.getColor(
            context,
            context.resources.getIdentifier("accent_device_default_dark", "color", "android")
        )
    }

    //method to calculate colors for cards titles
    @JvmStatic
    fun getTextColorForCardTitles(color: Int): Int {
        return if (isColorDark(color)) Color.WHITE else Color.BLACK
    }

    //method to calculate colors for whatever!
    @JvmStatic
    fun getTextColorForWhatever(context: Context, color: Int): Int {
        return if (isColorDark(color)) ContextCompat.getColor(context, R.color.light_mode_text) else
            ContextCompat.getColor(context, R.color.dark_mode_text)
    }

    //method to determine colors luminance
    private fun isColorDark(color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) < 0.35
    }
}