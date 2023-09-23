package com.luisinho.simplepasswordmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.luisinho.simplepasswordmanager.data.MasterKey
import com.luisinho.simplepasswordmanager.model.PasswordModel
import com.luisinho.simplepasswordmanager.repository.PasswordRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val key = MasterKey.getKey()
    private val repository = PasswordRepository(application.applicationContext, key)
    private val _listPasswords = MutableLiveData<List<PasswordModel>>()
    val listPasswords: LiveData<List<PasswordModel>> = _listPasswords
    fun getAll(): List<PasswordModel> {
        _listPasswords.value = repository.getAll()
        return repository.getAll()
    }

    fun delete(password: PasswordModel) {
        repository.delete(password)
        _listPasswords.value = repository.getAll()
    }
}