package com.luisinho.simplepasswordmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.luisinho.simplepasswordmanager.data.Constants
import com.luisinho.simplepasswordmanager.service.PasswordGeneratorService
import com.luisinho.simplepasswordmanager.service.SharedPreferences

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences: SharedPreferences =
        SharedPreferences(application)
    private val key = Constants.SharedPreferences.PREFERENCES_NAME
    private val cryptoKey = Constants.SharedPreferences.CRYPTO_KEY

    fun login(password: String?): Boolean {
        return preferences.getKeyValue(key) == password
    }

    fun register(password: String?): Boolean {
        preferences.store(cryptoKey, generateMasterKey())
        return preferences.store(key, password)
    }

    private fun generateMasterKey(): String {
        val charList: String = PasswordGeneratorService().makeCharsList()
        return PasswordGeneratorService().generatePassword(charList, 64)
    }

    fun passwordExists(): Boolean {
        return preferences.getKeyValue(key) != "null"
    }

    fun passwordIsBlank(password: String?): Boolean {
        return password == null || password == ""
    }


    fun passwordHasAtLeastFourCharacters(password: String): Boolean {
        return password.length >= 4
    }


}