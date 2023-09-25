package com.luisinho.simplepasswordmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
}