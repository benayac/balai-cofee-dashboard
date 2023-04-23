package com.example.balaicoffeedashboard

import android.app.Application

class App: Application() {

    companion object {
        var prefs: SharedPref? = null
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = SharedPref(applicationContext)
    }
}