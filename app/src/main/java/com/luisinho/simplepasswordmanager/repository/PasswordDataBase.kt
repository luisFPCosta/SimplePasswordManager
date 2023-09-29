package com.luisinho.simplepasswordmanager.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.luisinho.simplepasswordmanager.data.Constants
import com.luisinho.simplepasswordmanager.model.PasswordModel
import net.sqlcipher.database.SupportFactory


@Database(entities = [PasswordModel::class], version = 2)
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
                            .addMigrations(MIGRATION_1_2)
                            .build()
                }
            }
            return INSTANCE
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val username = Constants.Model.USERNAME
                database.execSQL("ALTER TABLE password ADD COLUMN $username TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
