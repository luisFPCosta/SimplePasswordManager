package com.luisinho.simplepasswordmanager.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.luisinho.simplepasswordmanager.R
import com.luisinho.simplepasswordmanager.databinding.ActivityMainBinding
import com.luisinho.simplepasswordmanager.model.PasswordModel
import com.luisinho.simplepasswordmanager.service.PasswordListener
import com.luisinho.simplepasswordmanager.view.adapter.PasswordAdapter
import com.luisinho.simplepasswordmanager.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: PasswordAdapter
    private var itemCount: Int = 0 /*variable used to know if a new password was entered. If so,
    the screen scrolls to the end*/

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val toolbar = binding.toolbar
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.buttonNewPassword.setOnClickListener(this)
        binding.recyclerPasswords.layoutManager = LinearLayoutManager(this)
        binding.recyclerPasswords.setHasFixedSize(true)
        adapter = PasswordAdapter()
        binding.recyclerPasswords.adapter = adapter
        val listener = object : PasswordListener {
            override fun onClick(password: PasswordModel) {
                val intent = Intent(applicationContext, PasswordGeneratorActivity::class.java)
                val bundle = Bundle()
                bundle.putInt("id", password.id)
                intent.putExtras(bundle)
                startActivity(intent)
            }
            override fun onLongClick(password: PasswordModel) {
                //delete password with one long click
                viewModel.delete(password)
            }
        }
        observe()
        adapter.attachListener(listener)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAll()
        if (itemCount < adapter.itemCount && itemCount != 0) {
            //scrolls the screen to the end when entering a new password. New passwords appear at the bottom of the screen
            binding.recyclerPasswords.scrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun onStop() {
        super.onStop()
        itemCount = adapter.itemCount/*updates the variable to get the number of saved passwords when the
             user enters the password creation/editing activity. If he saves a new password when
             he returns there will be a check in "OnResume", if a new password was added the
             screen will scroll to the end*/
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_new_password -> {
                startActivity(Intent(applicationContext, PasswordGeneratorActivity::class.java))
            }
        }
    }

    private fun observe() {
        viewModel.listPasswords.observe(this) {
            adapter.updatePasswords(it)
        }
    }
}

