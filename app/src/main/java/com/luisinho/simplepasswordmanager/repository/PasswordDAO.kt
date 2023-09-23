package com.luisinho.simplepasswordmanager.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.luisinho.simplepasswordmanager.model.PasswordModel

@Dao
interface PasswordDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(password: PasswordModel): Long

    @Update
    fun update(password: PasswordModel): Int

    @Delete
    fun delete(password: PasswordModel)

    @Query("SELECT * FROM password WHERE id = :id")
    fun get(id:Int): PasswordModel

    @Query("SELECT * FROM password")
    fun getAll(): List<PasswordModel>
}