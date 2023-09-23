package com.luisinho.simplepasswordmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.luisinho.simplepasswordmanager.data.Constants
import com.luisinho.simplepasswordmanager.data.MasterKey
import com.luisinho.simplepasswordmanager.service.SharedPreferences

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences: SharedPreferences =
        SharedPreferences(application)
    private val key = Constants.SharedPreferences.PREFERENCES_NAME

    fun login(password: String?):Boolean{
        return preferences.getKeyValue(key)==password
    }

    fun register(password: String?):Boolean{
        return preferences.store(key, password)
    }

    fun passwordExists(): Boolean {
        return preferences.getKeyValue(key) != "null"
    }

    fun passwordIsBlank(password: String?):Boolean{
        return password==null || password==""
    }
    fun setMasterKey(){
        MasterKey.setKey(preferences.getKeyValue(key))
    }

    fun passwordHasAtLeastFourCharacters(password: String): Boolean {
        return password.length>=4
    }


}