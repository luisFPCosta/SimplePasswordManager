# SimplePasswordManager
#This is not a commercial application.

A simple password manager created for study purposes only.

![](app\src\main\res\drawable\previewimage.png)

This project was not created with real use in mind, it was created solely for study purposes. It lacks basic features such as cloud data backup. Its creation had the sole objective of exercising what I have been learning in my studies.

##Features
-Created using the MVVM pattern, Room, Coroutines and SQLCipher.
-Database encryption using SQLCipher.
-Access to the app only after authentication via password or fingerprint.
-Allows you to create or edit already saved passwords.
-Create passwords that can include letters (case sensitive), numbers or symbols. Passwords with 8 characters up to 64 characters. Everything is customizable by the user.

##Security
Access to the app
When logging into the application for the first time, you will need to create a password. This password is not the encryption key, it is only used to access the app without validating the fingerprint. The encryption key is a 64-character key generated by the same process that generates other passwords in the app. After the user validates their password or fingerprint, the app recovers the encryption key and decrypts the database. The user can change their password later as the encryption key remains secure.

##Encryption scheme
The database is encrypted using SQLCipher which uses 256-bit AES encryption
