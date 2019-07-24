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

    //method to calculate colors luminance
    @JvmStatic
    fun getTextColorForCard(color: Int): Int {
        return if (ColorUtils.calculateLuminance(color) < 0.35) Color.WHITE else Color.BLACK
    }
}