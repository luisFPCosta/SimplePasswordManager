package com.luisinho.simplepasswordmanager.data

class Constants  {
    object SharedPreferences {
        const val PREFERENCES_NAME = "app_password"
        const val REGISTERED = "registered"
        const val CRYPTO_KEY = "crypto_key"
    }
    object Model{
        const val NAME = "name"
        const val PASSWORD = "password"
        const val LOCAL = "local"

    }
    object PasswordChangerConstants{
        const val INCORRECT_OLD_PASSWORD = "incorrect_old_password"
        const val BLANK_FIELDS = "blank_fields"
        const val PASSWORDS_DO_NOT_MATCH = "passwords_do_not_match"
    }
}