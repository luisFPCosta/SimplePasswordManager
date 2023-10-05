package com.luisinho.simplepasswordmanager.service

import android.content.Context
import com.luisinho.simplepasswordmanager.data.Constants

class BackupRestored {
    companion object{
        var restored = false
        fun isRestored(){
            restored = true
        }
        fun databaseAlreadyUpdated(){
            restored = false
        }
    }
}