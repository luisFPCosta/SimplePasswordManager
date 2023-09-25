package com.luisinho.simplepasswordmanager.data

class MasterKey {
    companion object{
        private lateinit var cryptoKey:ByteArray
        fun getKey(): ByteArray{
            return cryptoKey
        }
        fun setKey(key:String){
            cryptoKey = key.toByteArray()
        }
    }


}