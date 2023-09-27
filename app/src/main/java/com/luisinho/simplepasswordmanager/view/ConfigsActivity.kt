package com.luisinho.simplepasswordmanager.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.luisinho.simplepasswordmanager.R
import com.luisinho.simplepasswordmanager.databinding.ActivityConfigsBinding
import com.luisinho.simplepasswordmanager.viewmodel.ConfigsViewModel

class ConfigsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityConfigsBinding
    private lateinit var viewModel: ConfigsViewModel
    private lateinit var layout: View
    private var blankPasswords = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ConfigsViewModel::class.java]
        binding.toolbar.setTitle(R.string.settings)
        setSupportActionBar(binding.toolbar)
        binding.textChangePassword.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            binding.textChangePassword.id -> {
                createDialog()
            }
        }
    }

    private fun passwordIsBlank(password: String): Boolean {
        //checks if the password provided is blank
        return password == ""
    }

    private fun createDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        layout = inflater.inflate(R.layout.dialog_password_changer, null)
        builder.setView(layout)
        val dialog = builder.create()
        val buttonConfirm = layout.findViewById<Button>(R.id.button_confirm)
        val buttonCancel = layout.findViewById<Button>(R.id.button_cancel)
        val editOldPassword = layout.findViewById<EditText>(R.id.old_password)
        val editNewPassword = layout.findViewById<EditText>(R.id.new_password)
        val repeatNewPassword = layout.findViewById<EditText>(R.id.repeat_password)
        buttonConfirm.setOnClickListener {
            /*control variable used to know if any field is blank. It will only be false if
            all fields are filled in*/
            blankPasswords = false
            val oldPasswordValue = editOldPassword.text.replace("\\s".toRegex(), "")
            val newPasswordValue = editNewPassword.text.replace("\\s".toRegex(), "")
            val repeatNewPasswordValue = repeatNewPassword.text.replace("\\s".toRegex(), "")
            if (passwordIsBlank(oldPasswordValue)) {/*If the field is blank, displays a red warning to the user and assigns the value
                 “false” to the control variable*/
                editOldPassword.setHintTextColor(getColor(R.color.red_warning))
                editOldPassword.hint = getString(R.string.enter_your_old_password)
                blankPasswords = true
            } else if (passwordIsBlank(newPasswordValue)) {
                editNewPassword.setText("")
                editNewPassword.setHintTextColor(getColor(R.color.red_warning))
                editNewPassword.hint = getString(R.string.enter_your_new_password)
                blankPasswords = true
            } else if (passwordIsBlank(repeatNewPasswordValue)) {
                repeatNewPassword.setText("")
                repeatNewPassword.setHintTextColor(getColor(R.color.red_warning))
                repeatNewPassword.hint = getString(R.string.repeat_your_new_password)
                blankPasswords = true
            } else {
                //no fields are blank
                if (!viewModel.checkOldPassword(oldPasswordValue)) {/*checks whether the old password entered matches the saved password, If it
                    does not match, the user is informed*/
                    editOldPassword.setText("")
                    editOldPassword.setHintTextColor(getColor(R.color.red_warning))
                    editOldPassword.hint = getString(R.string.incorrect_old_password)
                } else {
                    if (!blankPasswords) {/*if the old password matches the saved one and there are no blank fields*/
                        if (viewModel.checkNewPassword(newPasswordValue)) {
                            //checks if the password has at least four characters
                            if (viewModel.passwordsAreTheSame(
                                    newPasswordValue, repeatNewPasswordValue
                                )
                            ) {/*Checks whether the new password was entered correctly twice. If
                                everything is ok so far it will be
                                saved by overwriting the old password*/
                                if (viewModel.updatePassword(newPasswordValue)) {
                                    Toast.makeText(
                                        this,
                                        getString(R.string.password_updated_successfully),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    dialog.dismiss()
                                }
                            } else {
                                //passwords don't match
                                repeatNewPassword.setText("")
                                repeatNewPassword.setHintTextColor(getColor(R.color.red_warning))
                                repeatNewPassword.hint =
                                    getString(R.string.repeat_your_new_password)
                            }
                        } else {
                            //new password is too short
                            editNewPassword.setText("")
                            editNewPassword.setHintTextColor(getColor(R.color.red_warning))
                            editNewPassword.hint = getString(R.string.dialog_password_too_short)
                        }

                    }
                }
            }


        }
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()


    }
}