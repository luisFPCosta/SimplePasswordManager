package com.luisinho.simplepasswordmanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "password")
data class PasswordModel(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name ="id")  var id:Int =0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "local") var local: String?,
    @ColumnInfo(name = "password") var password: String
)

