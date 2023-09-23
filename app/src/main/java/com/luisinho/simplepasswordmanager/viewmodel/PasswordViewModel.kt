package com.luisinho.simplepasswordmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.luisinho.simplepasswordmanager.data.Chars
import com.luisinho.simplepasswordmanager.data.MasterKey
import com.luisinho.simplepasswordmanager.model.PasswordModel
import com.luisinho.simplepasswordmanager.repository.PasswordRepository

class PasswordViewModel(application: Application) :
    AndroidViewModel(application) {
    private val key = MasterKey.getKey()
    private val repository: PasswordRepository =
        PasswordRepository(application.applicationContext, key)
    private val _password = MutableLiveData<PasswordModel>()
    val password: LiveData<PasswordModel> =
        _password//observed variable to assign values to the activity
    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> =
        _saveSuccess//variable observed to generate success or failure messages in the activity

    fun insert(password: PasswordModel) {
        _saveSuccess.value = repository.insert(password)
    }

    fun update(password: PasswordModel) {
        _saveSuccess.value = repository.update(password)
    }

    fun get(id: Int) {
        _password.value = repository.get(id)
    }

    fun validName(name: String): Boolean {
        //name validation, check if it is not blank
        return name != ""

    }

    fun generatePassword(chars: String, charsInPassword: Int): String {
        //generates a pseudo-random password with the previously created list of characters. The number of characters is defined by the user in the seekbar
        var step = 0
        var password = ""
        while (step < charsInPassword) {
            password += chars.random()
            step++
        }
        return password
    }

    fun makeCharsList(alphabetic: Boolean, numeric: Boolean, symbols: Boolean): String {
        //creates a list of characters based on user choices for password generation
        var chars = ""
        if (alphabetic) {
            chars += Chars.CHARS.ALPHABETICAL
        }
        if (numeric) {
            chars += Chars.CHARS.NUMERIC
        }
        if (symbols) {
            chars += Chars.CHARS.SYMBOLS
        }
        return chars
    }
}