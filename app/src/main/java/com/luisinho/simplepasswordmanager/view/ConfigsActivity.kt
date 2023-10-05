package com.luisinho.simplepasswordmanager.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.luisinho.simplepasswordmanager.R
import com.luisinho.simplepasswordmanager.databinding.ActivityConfigsBinding
import com.luisinho.simplepasswordmanager.viewmodel.ConfigsViewModel


class ConfigsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityConfigsBinding
    private lateinit var viewModel: ConfigsViewModel
    private lateinit var layout: View
    private var blankPasswords = false
    private var uri: Uri? = null
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                uri = result!!.data!!.data!!
                val fileName = viewModel.backupName(uri!!.pathSegments.last())
                layout.findViewById<TextView>(R.id.text_file_selected).text =
                    getString(R.string.selected, fileName)
                layout.findViewById<TextView>(R.id.text_file_selected).visibility = View.VISIBLE
                layout.findViewById<TextView>(R.id.text_confirm_restoration).visibility =
                    View.VISIBLE
            }
        }

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
                if (isStoragePermissionGranted()) {
                    backupDialog()
                } else {
                    if (viewModel.checkIfItIsTheFirstPermissionRequest()) {
                        viewModel.permissionAlreadyRequested()
                    }
                }
            }

            binding.textRestoreDatabase.id -> {
                if (isStoragePermissionGranted()) {
                    restoreDialog()
                } else {
                    if (!viewModel.checkIfItIsTheFirstPermissionRequest()) {
                        viewModel.permissionAlreadyRequested()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            snackbar(getString(R.string.permission_granted))
        } else {
            snackbar(getString(R.string.permission_denied))
        }
    }

    private fun snackbar(message: String) {
        val snackbar = Snackbar.make(binding.configLayout, message, Snackbar.LENGTH_LONG)
        snackbar.setTextMaxLines(5)
        snackbar.show()
    }


    @SuppressLint("InflateParams", "CutPasteId")
    private fun restoreDialog() {
        uri = null
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        layout = inflater.inflate(R.layout.dialog_restore_database, null)
        builder.setView(layout)
        val dialog = builder.create()
        val buttonConfirm = layout.findViewById<Button>(R.id.button_confirm)
        val buttonCancel = layout.findViewById<Button>(R.id.button_cancel)
        val selectPath = layout.findViewById<Button>(R.id.button_select_path)
        selectPath.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            val uri = Uri.parse(Environment.DIRECTORY_DOCUMENTS)
            intent.setDataAndType(uri, "application/zip")
            resultLauncher.launch(intent)
        }

        buttonConfirm.setOnClickListener {
            if (uri != null) {
                when (viewModel.restoreDataBase(application, uri!!)) {
                    /*restore database response codes
                    * 0 -> Successful restore
                    * 1 -> Invalid file, not the app's backup file
                    * 2 -> Unexpected errors*/
                    0 -> {
                        snackbar(getString(R.string.restoration_completed))
                        dialog.dismiss()
                    }

                    1 -> {
                        snackbar(getString(R.string.invalid_file))

                    }

                    2 -> {
                        snackbar(getString(R.string.unexpected_error))
                    }
                }
            } else {
                layout.findViewById<TextView>(R.id.text_file_selected).text =
                    getString(R.string.select_file_to_continue)
                layout.findViewById<TextView>(R.id.text_file_selected).visibility = View.VISIBLE
            }
        }
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
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
                if (viewModel.exportDatabase(this)) {

                    snackbar(getString(R.string.database_exported_successfully))
                    dialog.dismiss()
                } else {

                    snackbar(getString(R.string.unexpected_error))
                }

            }
        }
        buttonCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
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
            val oldPasswordValue = viewModel.deleteBlankSpaces(editOldPassword.text.toString())
            val newPasswordValue = viewModel.deleteBlankSpaces(editNewPassword.text.toString())
            val repeatNewPasswordValue =
                viewModel.deleteBlankSpaces(repeatNewPassword.text.toString())
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
                                    snackbar(getString(R.string.password_updated_successfully))
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


    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 1
                )
                false
            }
        } else {
            true
        }
    }

}