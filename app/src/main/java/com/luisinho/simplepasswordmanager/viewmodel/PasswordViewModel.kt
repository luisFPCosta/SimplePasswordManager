package com.luisinho.simplepasswordmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.luisinho.simplepasswordmanager.model.PasswordModel
import com.luisinho.simplepasswordmanager.repository.PasswordRepository

class PasswordViewModel(application: Application) :
    AndroidViewModel(application) {
    private val repository = PasswordRepository(application)
    private val _password = MutableLiveData<PasswordModel>()
    val password: LiveData<PasswordModel> =
        _password//observed variable to assign values to the activity
    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> =
        _saveSuccess//variable observed to generate success or failure messages in the activity


    suspend fun insert(password: PasswordModel) {
        _saveSuccess.value = repository.insert(password)
    }

    suspend fun update(password: PasswordModel) {
        _saveSuccess.value = repository.update(password)
    }

    suspend fun get(id: Int) {
        _password.value = repository.get(id)
    }

    fun validName(name: String): Boolean {
        //name validation, check if it is not blank
        return name != ""

    }
}