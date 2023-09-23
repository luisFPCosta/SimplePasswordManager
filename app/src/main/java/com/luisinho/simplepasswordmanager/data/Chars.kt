package com.luisinho.simplepasswordmanager.data

class Chars private constructor(){
    //characters used to create the password
    object CHARS{
        const val ALPHABETICAL = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz"
        const val NUMERIC = "0123456789"
        const val SYMBOLS = "!@#$%&*()_+=-{[}]?/|.,<>;:"
    }
}