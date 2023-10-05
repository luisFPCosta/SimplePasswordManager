package com.luisinho.simplepasswordmanager.data

import android.content.Context
import com.luisinho.simplepasswordmanager.service.SharedPreferences

class MasterKey {
    companion object{
        fun getKey(context: Context): ByteArray{
            return SharedPreferences(context).getKeyValue(Constants.SharedPreferences.CRYPTO_KEY).toByteArray()
        }
    }


}