package com.luisinho.simplepasswordmanager.view


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.luisinho.simplepasswordmanager.R
import com.luisinho.simplepasswordmanager.databinding.ActivityPasswordGeneratorBinding
import com.luisinho.simplepasswordmanager.model.PasswordModel
import com.luisinho.simplepasswordmanager.service.PasswordGeneratorService
import com.luisinho.simplepasswordmanager.viewmodel.PasswordViewModel
import kotlinx.coroutines.launch


class PasswordGeneratorActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener,
    View.OnClickListener {
    private var id = 0
    private lateinit var viewModel: PasswordViewModel
    private lateinit var binding: ActivityPasswordGeneratorBinding
    private var charsInPassword = 8
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordGeneratorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[PasswordViewModel::class.java]
        binding.seekbarPasswordSize.setOnSeekBarChangeListener(this)
        binding.textCharsInPassword.text = charsInPassword.toString()
        binding.buttonGeneratePassword.setOnClickListener(this)
        binding.buttonCopyPassword.setOnClickListener(this)
        binding.buttonSavePassword.setOnClickListener(this)
        observe()
        loadData()
    }

    override fun onProgressChanged(seek: SeekBar, progress: Int, byuser: Boolean) {
        charsInPassword = binding.seekbarPasswordSize.progress + 8
        binding.textCharsInPassword.text = charsInPassword.toString()
    }

    override fun onStartTrackingTouch(seek: SeekBar) {

    }

    override fun onStopTrackingTouch(seek: SeekBar) {

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_generate_password -> {
                //generate string of characters for password
                val chars = PasswordGeneratorService().makeCharsList(
                    binding.checkboxAllowAlphabetical.isChecked,
                    binding.checkboxAllowNumeric.isChecked,
                    binding.checkboxAllowSymbol.isChecked
                )
                if (chars != "") {
                    //generate password
                    password = PasswordGeneratorService().generatePassword(chars, charsInPassword)/*assigns the password to an edittext to be edited or copied according to
                the user's wishes*/
                    binding.editGeneratedPassword.setText(password)
                } else {
                    binding.editGeneratedPassword.setText("")
                    binding.editGeneratedPassword.hint = getString(R.string.select_boxes)
                    binding.editGeneratedPassword.setHintTextColor(Color.RED)
                }
            }

            R.id.button_copy_password -> {
                copyPassword(
                    binding.editGeneratedPassword.text.toString().replace("\\s".toRegex(), "")
                )
            }

            R.id.button_save_password -> {
                val name = binding.editName.text.toString().trim()
                if (viewModel.validName(name)) {
                    // checks if name is valid, not blank
                    val username = binding.editUsername.text.toString().replace("\\s".toRegex(), "")// checks if name is valid, not blank
                    val local = binding.editLocal.text.toString().replace("\\s".toRegex(), "")
                    val password =
                        binding.editGeneratedPassword.text.toString().replace("\\s".toRegex(), "")
                    val model = PasswordModel(id, name,username , local, password)
                    if (password.isEmpty()) {
                        binding.editGeneratedPassword.setText("")
                        binding.editGeneratedPassword.setHint(R.string.save_an_empty_password)
                        binding.editGeneratedPassword.setHintTextColor(Color.RED)
                    } else {
                        if (id == 0) {//if the id is equal to zero then it is a new password, calling the insert method
                            lifecycleScope.launch { viewModel.insert(model) }
                        } else {//if the id is different from zero then it is an update to the already saved password, calling the update method
                            lifecycleScope.launch { viewModel.update(model) }
                        }
                    }
                } else {//name is invalid
                    val message: String = getString(R.string.invalid_name)
                    Toast.makeText(this, message, Toast.LENGTH_SHORT)
                        .show()//show the invalid name message in a toast
                }
            }
        }
    }

    private fun loadData() {//receives information from a previously saved password in case of editing
        val bundle = intent.extras
        if (bundle != null) {
            val id = bundle.getInt("id")
            lifecycleScope.launch { viewModel.get(id) } //loads the saved password data into a variable observed in the view-model
        }
    }

    private fun observe() {
        viewModel.password.observe(this) {
            //If the variable observed in the view-model receives data, it will be assigned to the activity for editing
            binding.editName.setText(it.name)
            binding.editUsername.setText(it.username)
            binding.editLocal.setText(it.local)
            binding.editGeneratedPassword.setText(it.password)
            password = it.password
            binding.seekbarPasswordSize.progress = it.password.length - 8
            id = it.id
        }
        viewModel.saveSuccess.observe(this) {
            //observe a boolean variable in the view-model that receives true in cases of success and false in cases of failure
            if (it) {//If successful, generate a confirmation message to be displayed in a toast
                var message: String = if (id != 0) {
                    getString(R.string.updated)
                } else {
                    getString(R.string.added)
                }
                message =
                    ("${getString(R.string.password)} $message ${getString(R.string.successfully)}")
                toast(message)// show toast
                finish()//finish activity
            } else {//in case of error displays a message without closing the activity
                toast(getString(R.string.unexpected_error))
            }
        }
    }

    private fun toast(message: String) {
        //display a toast
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun copyPassword(password: String?) {
        //copy the password
        try {
            val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (password != null && password != "") {
                //checks that the password is not null or empty so as not to break the application if the user clicks on copy with the password field empty
                val clip = ClipData.newPlainText("password", password)
                clipBoard.setPrimaryClip(clip)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                    Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show()
                }
            }
        } catch (exception: Exception) {
            toast(getString(R.string.unexpected_error))
        }
    }
}