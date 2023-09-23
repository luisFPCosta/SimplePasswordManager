package com.luisinho.simplepasswordmanager.data

class MasterKey {
    companion object{
        private lateinit var appKey:ByteArray
        fun getKey(): ByteArray{
            return appKey
        }
        fun setKey(key:String){
            appKey = key.toByteArray()
        }
    }


}