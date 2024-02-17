package com.example.qp

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("localData", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String) : String {
        return preferences.getString(key,defValue).toString()
    }

    fun getInt(key: String, defValue: Int) : Int {
        return preferences.getInt(key,defValue)
    }

    fun setString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }
    fun setInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }
}