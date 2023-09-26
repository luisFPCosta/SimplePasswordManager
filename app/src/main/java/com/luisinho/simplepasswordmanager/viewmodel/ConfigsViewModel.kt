package com.luisinho.simplepasswordmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.luisinho.simplepasswordmanager.data.Constants
import com.luisinho.simplepasswordmanager.service.SharedPreferences

class ConfigsViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences: SharedPreferences =
        SharedPreferences(application)
    private val key = Constants.SharedPreferences.PREFERENCES_NAME
    fun checkOldPassword(password: String): Boolean {
        return password == preferences.getKeyValue(key)
    }

    fun checkNewPassword(newPassword: String, repeat: String): Boolean {
        return newPassword.length>=4
    }
    fun updatePassword(password: String): Boolean {
        return preferences.store(key, password)
    }

}