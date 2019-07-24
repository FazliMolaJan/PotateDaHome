package com.iven.potatowalls

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager

class PotatoPreferences(context: Context) {

    private val mDefaultBackgroundColor = ContextCompat.getColor(context, R.color.defaultBackgroundColor)
    private val mDefaultPotatoColor = ContextCompat.getColor(context, R.color.defaultPotatoColor)

    private val prefBackgroundColor = context.getString(R.string.background_color_key)
    private val prefPotatoColor = context.getString(R.string.potato_color_key)
    private val prefIsBackgroundAccented = context.getString(R.string.accent_background_set)
    private val prefIsPotatoAccented = context.getString(R.string.accent_potato_set)
    private val prefTheme = context.getString(R.string.theme_key)

    private val mPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var backgroundColor: Int
        get() = mPrefs.getInt(prefBackgroundColor, mDefaultBackgroundColor)
        set(value) = mPrefs.edit().putInt(prefBackgroundColor, value).apply()

    var potatoColor: Int
        get() = mPrefs.getInt(prefPotatoColor, mDefaultPotatoColor)
        set(value) = mPrefs.edit().putInt(prefPotatoColor, value).apply()

    var isBackgroundAccented: Boolean
        get() = mPrefs.getBoolean(prefIsBackgroundAccented, false)
        set(value) = mPrefs.edit().putBoolean(prefIsBackgroundAccented, value).apply()

    var isPotatoAccented: Boolean
        get() = mPrefs.getBoolean(prefIsPotatoAccented, false)
        set(value) = mPrefs.edit().putBoolean(prefIsPotatoAccented, value).apply()

    var theme: Int
        get() = mPrefs.getInt(prefTheme, R.style.AppTheme)
        set(value) = mPrefs.edit().putInt(prefTheme, value).apply()

    fun clear() {
        mPrefs.edit().clear().apply()
    }
}