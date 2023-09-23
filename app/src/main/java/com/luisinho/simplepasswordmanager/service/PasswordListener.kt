package com.luisinho.simplepasswordmanager.service

import com.luisinho.simplepasswordmanager.model.PasswordModel

interface PasswordListener {
    fun onClick(password: PasswordModel)
    fun onLongClick(password: PasswordModel)
}
