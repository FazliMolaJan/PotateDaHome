package com.iven.potatowalls

import android.app.Application

val mPotatoPreferences: PotatoPreferences by lazy {
    PotatoApp.prefs
}

val mSystemAccent: Int by lazy {
    PotatoApp.systemAccent
}

class PotatoApp : Application() {
    companion object {
        lateinit var prefs: PotatoPreferences
        var systemAccent = 0
        var backgroundColor = 0
        var potatoColor = 0
    }

    override fun onCreate() {
        prefs = PotatoPreferences(applicationContext)
        systemAccent = Utils.getSystemAccentColor(applicationContext)
        backgroundColor = prefs.backgroundColor
        potatoColor = prefs.potatoColor
        super.onCreate()
    }
}