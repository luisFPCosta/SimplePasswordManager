package com.luisinho.simplepasswordmanager.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.luisinho.simplepasswordmanager.R
import com.luisinho.simplepasswordmanager.databinding.ActivityLoginBinding
import com.luisinho.simplepasswordmanager.service.BiometricHelper
import com.luisinho.simplepasswordmanager.service.SharedPreferences
import com.luisinho.simplepasswordmanager.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        preferences = SharedPreferences(this)
        binding.buttonLogin.setOnClickListener(this)
        //Checks if the user has already created a password to configure the strings
        if (viewModel.passwordExists()) {
            binding.textLoginMessage.text = getString(R.string.enter_your_password)
            binding.buttonLogin.text = getString(R.string.login)
            //check if biometric authentication is available
            if (BiometricHelper.isBiometricAvailable(application)) {
                biometricAuthentication()
            }
        } else {
            binding.textLoginMessage.text = getString(R.string.create_password)
            binding.buttonLogin.text = getString(R.string.create_password_button)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_login -> {
                val password = binding.editPassword.text.toString().replace("\\s".toRegex(), "")
                if (viewModel.passwordIsBlank(password)) {
                    //password is blank, clears the EditText and displays a red incorrect password message in the hint
                    invalidPassword(R.string.blank_password)
                } else {
                    //password is not blank
                    if (viewModel.passwordHasAtLeastFourCharacters(password)) {
                        //the password has at least four characters
                        if (viewModel.passwordExists()) {
                            //Checks whether the user has already created a password to decide whether to create a new password or log in
                            if (!viewModel.login(password)) {
                                //If the login is unsuccessful, clears the EditText and displays a red incorrect password message in the hint
                                invalidPassword(R.string.incorrect_password)
                            } else {
                                startMainActivity()
                            }
                        } else {
                            //There is no saved password
                            if (viewModel.register(password)) {
                                //registration of a new password was successful
                                startMainActivity()
                            } else {
                                //registering a new password was unsuccessful
                                snackbar(getString(R.string.unexpected_error))
                            }
                        }
                    } else {
                        invalidPassword(R.string.dialog_password_too_short)
                    }
                }
            }
        }
    }
    private fun snackbar(message: String) {
        val snackbar = Snackbar.make(binding.loginLayout, message, Snackbar.LENGTH_LONG)
        snackbar.setTextMaxLines(5)
        snackbar.show()
    }

    private fun invalidPassword(message: Int) {
        binding.editPassword.setText("")
        binding.editPassword.hint = getString(message)
        binding.editPassword.setHintTextColor(getColor(R.color.red_warning))
    }


    private fun startMainActivity() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

    private fun biometricAuthentication() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    startMainActivity()
                }
            })
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_authentication))
            .setDescription(getString(R.string.tap_the_sensor))
            .setNegativeButtonText(getString(R.string.cancel))
            .build()
        biometricPrompt.authenticate(info)
    }
}