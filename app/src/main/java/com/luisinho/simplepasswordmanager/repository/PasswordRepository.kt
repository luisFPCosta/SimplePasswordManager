package com.luisinho.simplepasswordmanager.repository

import android.content.Context
import com.luisinho.simplepasswordmanager.model.PasswordModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class PasswordRepository(context: Context, masterKey:ByteArray) {
    private val passwordDataBase = PasswordDataBase.getDataBase(context, masterKey).passwordDAO()

    fun insert(password: PasswordModel):Boolean {
        return passwordDataBase.insert(password)> 0
    }
    fun update(password: PasswordModel): Boolean{
        return passwordDataBase.update(password)> 0
    }
    fun delete(password: PasswordModel){
        passwordDataBase.delete(password)
    }
    fun get(id:Int): PasswordModel{
        return passwordDataBase.get(id)
    }
    fun getAll():List<PasswordModel>{
        return passwordDataBase.getAll()
    }

}