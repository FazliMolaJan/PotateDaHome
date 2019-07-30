package com.iven.potatowalls

import android.app.Application

val mPotatoPreferences: PotatoPreferences by lazy {
    PotatoApp.prefs
}

class PotatoApp : Application() {
    companion object {
        lateinit var prefs: PotatoPreferences
    }

    override fun onCreate() {
        prefs = PotatoPreferences(applicationContext)
        super.onCreate()
    }
}