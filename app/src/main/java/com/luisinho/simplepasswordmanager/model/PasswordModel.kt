package com.luisinho.simplepasswordmanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.luisinho.simplepasswordmanager.data.Constants


@Entity(tableName = Constants.Model.PASSWORD)
data class PasswordModel(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = Constants.Model.NAME) var name: String,
    @ColumnInfo(name = Constants.Model.USERNAME) var username: String = "",
    @ColumnInfo(name = Constants.Model.LOCAL) var local: String?,
    @ColumnInfo(name = Constants.Model.PASSWORD) var password: String
)

