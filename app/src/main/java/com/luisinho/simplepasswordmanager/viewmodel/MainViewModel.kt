package com.luisinho.simplepasswordmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.luisinho.simplepasswordmanager.model.PasswordModel
import com.luisinho.simplepasswordmanager.repository.PasswordRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PasswordRepository(application)
    private val _listPasswords = MutableLiveData<List<PasswordModel>>()
    val listPasswords: LiveData<List<PasswordModel>> = _listPasswords
    suspend fun getAll(): List<PasswordModel> {
        val passwordList = repository.getAll()
        _listPasswords.value = passwordList
        return passwordList
    }

    suspend fun delete(password: PasswordModel) {
        repository.delete(password)
        _listPasswords.value = repository.getAll()
    }
}