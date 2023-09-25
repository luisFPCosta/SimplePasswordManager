package com.luisinho.simplepasswordmanager.service

import com.luisinho.simplepasswordmanager.data.Chars

class PasswordGeneratorService {
    fun generatePassword(chars: String, charsInPassword: Int): String {
        //generates a pseudo-random password with the previously created list of characters. The number of characters is defined by the user in the seekbar
        var step = 0
        var password = ""
        while (step < charsInPassword) {
            password += chars.random()
            step++
        }
        return password
    }

    fun makeCharsList(alphabetic: Boolean = true, numeric: Boolean = true, symbols: Boolean = true): String {
        //creates a list of characters based on user choices for password generation
        var chars = ""
        if (alphabetic) {
            chars += Chars.CHARS.ALPHABETICAL
        }
        if (numeric) {
            chars += Chars.CHARS.NUMERIC
        }
        if (symbols) {
            chars += Chars.CHARS.SYMBOLS
        }
        return chars
    }
}