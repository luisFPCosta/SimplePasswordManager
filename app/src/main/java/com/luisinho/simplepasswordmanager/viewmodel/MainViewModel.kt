package com.luisinho.simplepasswordmanager.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.luisinho.simplepasswordmanager.model.PasswordModel
import com.luisinho.simplepasswordmanager.repository.PasswordRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var repository = PasswordRepository(application)
    private val _listPasswords = MutableLiveData<List<PasswordModel>>()
    val listPasswords: LiveData<List<PasswordModel>> = _listPasswords

    suspend fun getAll(): List<PasswordModel> {
        val passwordList = repository.getAll()
        _listPasswords.value = passwordList
        return passwordList
    }

    suspend fun search(term: String): List<PasswordModel> {
        return if (term == "") {
            val passwordList = repository.getAll()
            _listPasswords.value = passwordList
            passwordList
        } else {
            val passwordList = repository.search(term)
            _listPasswords.value = passwordList
            passwordList
        }
    }
    suspend fun itemCount():Int{
        /*returns the number of items saved in the database to know if a new item was saved to
         scroll to it in the MainActivity. This is necessary because with the search system the
         adapter only returns values for what was searched, no longer the total value of
         items*/
        return repository.getItemsCount()
    }

    suspend fun delete(password: PasswordModel): List<PasswordModel> {
        repository.delete(password)
        val passwordList = repository.getAll()
        _listPasswords.value = passwordList
        return passwordList
    }
    fun reloadDatabase(context: Context){
        repository = PasswordRepository(context)
    }
}