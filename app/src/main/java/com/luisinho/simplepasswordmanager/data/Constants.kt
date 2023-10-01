package com.luisinho.simplepasswordmanager.data

class Constants {
    object SharedPreferences {
        const val PREFERENCES_NAME = "app_password"
        const val REGISTERED = "registered"
        const val CRYPTO_KEY = "crypto_key"
    }

    object Model {
        const val NAME = "name"
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val LOCAL = "local"

    }

    object Database {
        const val DATABASE_NAME = "passwordDB"
        const val BACKUP_NAME = "simple_password_db_backup.zip"
        const val TEXT_FILE_NAME = "encryption_password.txt"
    }
}