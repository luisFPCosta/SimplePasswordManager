package com.luisinho.simplepasswordmanager.service

import android.content.Context
import android.content.SharedPreferences
import com.luisinho.simplepasswordmanager.data.Constants

class SharedPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.SharedPreferences.REGISTERED, Context.MODE_PRIVATE)

    fun store(key: String, password: String?):Boolean {
        return if (password == null){
            false
        }else{
            sharedPreferences.edit().putString(key, password).apply()
            true
        }
    }

    fun getKeyValue(key: String): String {
        return sharedPreferences.getString(key, "null") ?: "null"
    }
}