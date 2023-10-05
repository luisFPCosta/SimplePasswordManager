package com.luisinho.simplepasswordmanager.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import com.luisinho.simplepasswordmanager.data.Constants
import com.luisinho.simplepasswordmanager.data.MasterKey
import com.luisinho.simplepasswordmanager.repository.PasswordRepository
import com.luisinho.simplepasswordmanager.service.BackupRestored
import com.luisinho.simplepasswordmanager.service.SharedPreferences
import org.zeroturnaround.zip.ZipUtil
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileReader
import java.io.OutputStream
import java.nio.file.Paths


class ConfigsViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences: SharedPreferences = SharedPreferences(application)
    private val key = Constants.SharedPreferences.PREFERENCES_NAME
    private val repository = PasswordRepository(application)

    object GetPaths {
        //provides the necessary directories
        val documentsPath: File = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS
        )
        val filesPath: File =
            Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_DOCUMENTS}/simple_password_backup")
        val tempPath: File = File("${filesPath}/temp")
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

    private fun zip(directory: File): Boolean {
        return try {
            val targetOutput =
                Paths.get("${GetPaths.documentsPath}/${Constants.Database.BACKUP_NAME}").toFile()
            ZipUtil.pack(
                directory, targetOutput
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun unzip(file: File): Boolean {
        return try {
            ZipUtil.unpack(
                file,
                createDirectory(GetPaths.tempPath)
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun isFileExists(database: String, wal: String, shm: String, text: String): Boolean {
        return (File(database).exists() && !File(database).isDirectory) && (File(wal).exists() && !File(
            wal
        ).isDirectory) && (File(shm).exists() && !File(shm).isDirectory) && (File(text).exists() && !File(
            text
        ).isDirectory)

    }

    private fun createDirectory(path: File): File {
        if (!path.exists()) {
            path.mkdirs()
        }
        return path
    }

    private fun createPasswordTextFile(context: Context): Boolean {
        //creates a text file with the encryption password
        return try {
            val path = createDirectory(GetPaths.filesPath)
            val filename = Constants.Database.TEXT_FILE_NAME
            val file = File(path, filename)
            FileOutputStream(file).use {
                it.write(MasterKey.getKey(context))
            }
            true
        } catch (e: Exception) {
            false
        }

    }

    fun passwordIsBlank(password: String): Boolean {
        //checks if the password provided is blank
        return password == ""
    }

    fun exportDatabase(context: Context): Boolean {
        return try {
            val databaseName = Constants.Database.DATABASE_NAME
            val exportFolder = GetPaths.filesPath
            val database = context.getDatabasePath(databaseName).absolutePath
            val wal = context.getDatabasePath("$databaseName-wal").absolutePath
            val shm = context.getDatabasePath("$databaseName-shm").absolutePath
            File(database).copyTo(File(exportFolder, databaseName), true)
            File(wal).copyTo(File(exportFolder, "$databaseName-wal"), true)
            File(shm).copyTo(File(exportFolder, "$databaseName-shm"), true)
            if (createPasswordTextFile(context)) {
                if (zip(GetPaths.filesPath)) {
                    deleteBackupFiles(GetPaths.filesPath)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun restoreDataBase(context: Context, uri: Uri): Int {
        /*restore database response codes
        * 0 -> Successful restore
        * 1 -> Invalid file, not the app's backup file
        * 2 -> Unexpected errors*/
        return try {
            repository.closeDataBase()
            val tempZip = createTempZip(context, uri)
            if (unzip(tempZip)) {
                val databaseName = Constants.Database.DATABASE_NAME
                val databaseExternal = "${GetPaths.tempPath}/$databaseName"
                val walExternal = "${GetPaths.tempPath}/$databaseName-wal"
                val shmExternal = "${GetPaths.tempPath}/$databaseName-shm"
                val textKey = "${GetPaths.tempPath}/${Constants.Database.TEXT_FILE_NAME}"
                if (isFileExists(databaseExternal, walExternal, shmExternal, textKey)) {
                    val database = context.getDatabasePath(databaseName).absolutePath
                    val wal = context.getDatabasePath("$databaseName-wal").absolutePath
                    val shm = context.getDatabasePath("$databaseName-shm").absolutePath
                    File(databaseExternal).copyTo(File(database), true)
                    File(walExternal).copyTo(File(wal), true)
                    File(shmExternal).copyTo(File(shm), true)
                    val key: String =
                        readTxtFile("${GetPaths.tempPath}/${Constants.Database.TEXT_FILE_NAME}")
                    preferences.store(Constants.SharedPreferences.CRYPTO_KEY, key)
                    deleteBackupFiles(GetPaths.filesPath)
                    BackupRestored.isRestored()
                    0
                } else {
                    1
                }
            } else {
                1
            }

        } catch (e: FileNotFoundException) {
            1
        } catch (e: Exception) {
            2
        }

    }

    private fun readTxtFile(file: String): String {
        FileReader(file).use {
            val chars = CharArray(file.length)
            it.read(chars)
            return String(chars)
        }
    }

    private fun createTempZip(context: Context, uri: Uri): File {
        /*creates a copy of the file selected by the user to be used in the backup restore. This
        copy is deleted at the end of the process.*/
        val path = createDirectory(GetPaths.tempPath)
        val input = context.contentResolver.openInputStream(uri)
        val output: OutputStream = FileOutputStream(
            File(path, "/temp.zip")
        )
        val buf = ByteArray(1024)
        var len: Int
        while (input!!.read(buf).also { len = it } > 0) {
            output.write(buf, 0, len)
        }
        output.close()
        input.close()
        return File("${path}/temp.zip")
    }

    fun checkIfItIsTheFirstPermissionRequest(): Boolean {
        return preferences.getKeyValue("firstPermissionRequest") != "true"

    }

    fun permissionAlreadyRequested() {
        preferences.store("firstPermissionRequest", "true")
    }

    fun backupName(string: String): String {
        //manipulates the name of the file selected by the user for display in a TextView
        var fileName = ""
        for (char in string.length - 1 downTo 0) {
            if (string[char] != ':' && string[char] != '/') {
                fileName += string[char]
            } else {
                return fileName.reversed()
            }
        }
        return fileName.reversed()
    }
    fun deleteBlankSpaces(password: String): String {
        return password.replace("\\s".toRegex(), "")
    }


}