package com.example.balaicoffeedashboard

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences(PREF_BALAI_COFFEE, Context.MODE_PRIVATE)
    var loginStatusPref: Boolean
        get() = preferences.getBoolean(KEY_LOGIN_STATUS, false)
        set(value) = preferences.edit().putBoolean(KEY_LOGIN_STATUS, value).apply()

    var userPrivilegePref: String
        get() = preferences.getString(KEY_USER_PRIVILEGE, "USER") ?: "USER"
        set(value) = preferences.edit().putString(KEY_USER_PRIVILEGE, value).apply()

    var authenticationPref: String
        get() = preferences.getString(KEY_AUTHENTICATION, "") ?: ""
        set(value) = preferences.edit().putString(KEY_AUTHENTICATION, "Bearer $value").apply()

    fun resetPref() {
        preferences.edit().clear().apply()
    }
    companion object {
        const val PREF_BALAI_COFFEE = "PREFERENCE_BALAI_COFFEE"
        const val KEY_LOGIN_STATUS = "LOGIN_STATUS"
        const val KEY_USER_PRIVILEGE = "USER_PRIVILEGE"
        const val KEY_AUTHENTICATION = "KEY_AUTHENTICATION"
    }
}