package com.luisinho.simplepasswordmanager.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
        binding.textBackupDatabase.setOnClickListener(this)
        binding.textRestoreDatabase.setOnClickListener(this)


    }

    override fun onClick(view: View) {
        when (view.id) {
            binding.textChangePassword.id -> {
                changePasswordDialog()
            }

            binding.textBackupDatabase.id -> {
                backupDialog()
            }

            binding.textRestoreDatabase.id -> {
                restoreDialog()
            }
        }
    }


    @SuppressLint("InflateParams")
    private fun restoreDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        layout = inflater.inflate(R.layout.dialog_restore_database, null)
        builder.setView(layout)
        val dialog = builder.create()
        val buttonConfirm = layout.findViewById<Button>(R.id.button_confirm)
        val buttonCancel = layout.findViewById<Button>(R.id.button_cancel)
        val selectPath = layout.findViewById<Button>(R.id.button_select_path)
        selectPath.setOnClickListener {
            Toast.makeText(this, "AAAAAAAAAAAAAAAAAAAAAA", Toast.LENGTH_LONG)
                .show()
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.parse("Android") // a directory

            intent.setDataAndType(uri, "text/plain")
            startActivity(Intent.createChooser(intent, "Open folder"))


        }
        buttonConfirm.setOnClickListener {
        }
        buttonCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    @SuppressLint("InflateParams")
    private fun backupDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        layout = inflater.inflate(R.layout.dialog_backup_database, null)
        builder.setView(layout)
        val dialog = builder.create()
        val buttonConfirm = layout.findViewById<Button>(R.id.button_confirm)
        val buttonCancel = layout.findViewById<Button>(R.id.button_cancel)
        val editConfirmation = layout.findViewById<EditText>(R.id.edit_confirm)
        val incorrectConfirmationText =
            layout.findViewById<TextView>(R.id.text_incorrect_confirmation)
        buttonConfirm.setOnClickListener {
            val confirmation = editConfirmation.text.toString().lowercase()
            if (confirmation != getString(R.string.yes).lowercase()) {
                incorrectConfirmationText.text = getString(R.string.confirm_that_you_understand)
                incorrectConfirmationText.visibility = View.VISIBLE
                editConfirmation.setText("")
            } else {
                Toast.makeText(this, viewModel.exportDatabase(this).toString(), Toast.LENGTH_LONG)
                    .show()
                dialog.dismiss()

            }
        }
        buttonCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun deleteBlankSpaces(password: String): String {
        return password.replace("\\s".toRegex(), "")
    }

    @SuppressLint("InflateParams")
    private fun changePasswordDialog() {
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
            val oldPasswordValue = deleteBlankSpaces(editOldPassword.text.toString())
            val newPasswordValue = deleteBlankSpaces(editNewPassword.text.toString())
            val repeatNewPasswordValue = deleteBlankSpaces(repeatNewPassword.text.toString())
            if (viewModel.passwordIsBlank(oldPasswordValue)) {/*If the field is blank, displays a red warning
            to the user and assigns the value “false” to the control variable*/
                editOldPassword.setHintTextColor(getColor(R.color.red_warning))
                editOldPassword.hint = getString(R.string.enter_your_old_password)
                blankPasswords = true
            } else if (viewModel.passwordIsBlank(newPasswordValue)) {
                editNewPassword.setText("")
                editNewPassword.setHintTextColor(getColor(R.color.red_warning))
                editNewPassword.hint = getString(R.string.enter_your_new_password)
                blankPasswords = true
            } else if (viewModel.passwordIsBlank(repeatNewPasswordValue)) {
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
        buttonCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}