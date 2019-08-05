package com.iven.potatowalls

import android.app.Application
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

val mPotatoPreferences: PotatoPreferences by lazy {
    PotatoApp.prefs
}

val mDeviceMetrics: Pair<Int, Int> by lazy {
    PotatoApp.metrics
}

class PotatoApp : Application() {
    companion object {
        lateinit var prefs: PotatoPreferences
        lateinit var metrics: Pair<Int, Int>
    }

    override fun onCreate() {
        prefs = PotatoPreferences(applicationContext)

        //retrieve display specifications
        val window = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d = DisplayMetrics()
        window.defaultDisplay.getRealMetrics(d)
        metrics = Pair(d.widthPixels, d.heightPixels)

        super.onCreate()
    }
}