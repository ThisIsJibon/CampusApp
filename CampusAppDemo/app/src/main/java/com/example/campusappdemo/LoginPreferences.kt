package com.example.campusappdemo

import android.content.Context
import android.content.SharedPreferences

private const val KEY_LOGIN_STATE = "key_login_state"

class LoginPreferences(context: Context) {

    private val preferences: SharedPreferences
            = context.getSharedPreferences("travel-blog", Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean = preferences.getBoolean(KEY_LOGIN_STATE, false)

    fun setLoggedIn(loggedIn: Boolean) {
        preferences.edit().putBoolean(KEY_LOGIN_STATE, loggedIn).apply()
    }
}