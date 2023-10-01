package com.luisinho.simplepasswordmanager.viewmodel

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import com.luisinho.simplepasswordmanager.data.Constants
import com.luisinho.simplepasswordmanager.data.MasterKey
import com.luisinho.simplepasswordmanager.service.SharedPreferences
import org.zeroturnaround.zip.ZipUtil
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths

class ConfigsViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences: SharedPreferences = SharedPreferences(application)
    private val key = Constants.SharedPreferences.PREFERENCES_NAME

    object GetPaths {
        val documentsPath: File = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS
        )
        val filesPath: File =
            Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_DOCUMENTS}/simple_password_backup/")
    }

    fun checkOldPassword(password: String): Boolean {
        return password == preferences.getKeyValue(key)
    }

    fun checkNewPassword(newPassword: String): Boolean {
        return newPassword.length >= 4
    }

    fun updatePassword(password: String): Boolean {
        return preferences.store(key, password)
    }

    fun passwordsAreTheSame(newPassword: String, repeat: String): Boolean {
        return newPassword == repeat
    }


    private fun deleteBackupFiles(fileOrDirectory: File) {
        //delete the files used for backup after everything is finished
        if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()!!) deleteBackupFiles(
            child
        )
        fileOrDirectory.delete()
    }

    private fun zip(directory: File) {
        val targetOutput = Paths.get("${GetPaths.documentsPath}/${Constants.Database.BACKUP_NAME}").toFile()
        ZipUtil.pack(directory, targetOutput)
    }

    private fun createPasswordTextFile(): Boolean {
        val path = GetPaths.filesPath
        if (!path.exists()) {
            path.mkdirs()
        }
        val filename = Constants.Database.TEXT_FILE_NAME
        val file = File(path, filename)
        FileOutputStream(file).use {
            it.write(MasterKey.getKey())
        }
        return true
    }

    fun passwordIsBlank(password: String): Boolean {
        //checks if the password provided is blank
        return password == ""
    }

    fun exportDatabase(context: Context) {
        val databaseName = Constants.Database.DATABASE_NAME
        val exportFolder = GetPaths.filesPath
        val database = context.getDatabasePath(databaseName).absolutePath
        val wal = context.getDatabasePath("$databaseName-wal").absolutePath
        val shm = context.getDatabasePath("$databaseName-shm").absolutePath
        File(database).copyTo(File(exportFolder, databaseName), true)
        File(wal).copyTo(File(exportFolder, "$databaseName-wal"), true)
        File(shm).copyTo(File(exportFolder, "$databaseName-shm"), true)
        createPasswordTextFile()
        zip(GetPaths.filesPath)
        deleteBackupFiles(GetPaths.filesPath)
    }
}