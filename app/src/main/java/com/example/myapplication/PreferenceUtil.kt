package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("myBestScore", Context.MODE_PRIVATE)
    fun getSharedPrefs(key: String, score: String): String {
        return prefs.getString(key, score).toString()
    }
    fun setSharedPrefs(key: String, newScore: String) {
        prefs.edit().putString(key, newScore).apply()
    }
}