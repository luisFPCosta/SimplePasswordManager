package com.luisinho.simplepasswordmanager.repository

import android.content.Context
import com.luisinho.simplepasswordmanager.data.MasterKey
import com.luisinho.simplepasswordmanager.model.PasswordModel

class PasswordRepository(context: Context) {
    private val passwordDataBase =
        PasswordDataBase.getDataBase(context, MasterKey.getKey()).passwordDAO()

    suspend fun insert(password: PasswordModel): Boolean {
        return passwordDataBase.insert(password) > 0
    }

    suspend fun update(password: PasswordModel): Boolean {
        return passwordDataBase.update(password) > 0
    }

    suspend fun delete(password: PasswordModel) {
        passwordDataBase.delete(password)
    }

    suspend fun get(id: Int): PasswordModel {
        return passwordDataBase.get(id)
    }

    suspend fun getAll(): List<PasswordModel> {
        return passwordDataBase.getAll()
    }
    suspend fun search(term:String):List<PasswordModel>{
        return passwordDataBase.search(term)
    }

}