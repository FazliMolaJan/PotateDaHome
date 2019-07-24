package com.iven.potatowalls

import android.app.Application

val mPotatoPreferences: PotatoPreferences by lazy {
    PotatoApp.prefs
}

class PotatoApp : Application() {
    companion object {
        lateinit var prefs: PotatoPreferences
        var backgroundColor = 0
        var potatoColor = 0
    }

    override fun onCreate() {
        prefs = PotatoPreferences(applicationContext)
        backgroundColor = prefs.backgroundColor
        potatoColor = prefs.potatoColor
        super.onCreate()
    }
}