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
    suspend fun insert(password: PasswordModel): Long

    @Update
    suspend fun update(password: PasswordModel): Int

    @Delete
    suspend fun delete(password: PasswordModel)

    @Query("SELECT * FROM password WHERE id = :id")
    suspend fun get(id: Int): PasswordModel

    @Query("SELECT * FROM password")
    suspend fun getAll(): List<PasswordModel>
}