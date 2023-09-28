package com.luisinho.simplepasswordmanager.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.luisinho.simplepasswordmanager.model.PasswordModel
import net.sqlcipher.database.SupportFactory


@Database(entities = [PasswordModel::class], version = 1)
abstract class PasswordDataBase : RoomDatabase() {
    abstract fun passwordDAO(): PasswordDAO

    companion object {
        //singleton
        private lateinit var INSTANCE: PasswordDataBase
        fun getDataBase(context: Context, masterKey: ByteArray): PasswordDataBase {
            if (!Companion::INSTANCE.isInitialized) {
                val factory = SupportFactory(masterKey, null, false)
                synchronized(PasswordDataBase::class) {
                    INSTANCE =
                        Room.databaseBuilder(context, PasswordDataBase::class.java, "passwordDB")
                            .openHelperFactory(factory)
                            .build()
                }
            }
            return INSTANCE
        }
    }
}
