package com.luisinho.simplepasswordmanager.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.luisinho.simplepasswordmanager.R
import com.luisinho.simplepasswordmanager.databinding.ActivityMainBinding
import com.luisinho.simplepasswordmanager.model.PasswordModel
import com.luisinho.simplepasswordmanager.service.PasswordListener
import com.luisinho.simplepasswordmanager.view.adapter.PasswordAdapter
import com.luisinho.simplepasswordmanager.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: PasswordAdapter
    private lateinit var searchView: SearchView
    private var itemCount: Int = 0/*variable used to know if a new password was entered. If so,
    the screen scrolls to the end*/

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.imageSettings.setOnClickListener(this)
        binding.buttonNewPassword.setOnClickListener(this)
        binding.recyclerPasswords.layoutManager = LinearLayoutManager(this)
        binding.recyclerPasswords.setHasFixedSize(true)
        adapter = PasswordAdapter(this)
        binding.recyclerPasswords.adapter = adapter

        searchView = binding.searchPasswords
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                lifecycleScope.launch { viewModel.search(query) }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //performs the search after each character entered
                lifecycleScope.launch { viewModel.search(newText) }
                return true
            }
        })

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
                lifecycleScope.launch { viewModel.delete(password) }
            }
        }
        observe()
        adapter.attachListener(listener)
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch { viewModel.getAll() }
        searchView.onActionViewCollapsed()
    }

    override fun onStop() {
        super.onStop()/*updates the variable to get the number of saved passwords when the
         the user starts the password creation/editing activity. If you save a new password when
         return there will be a check. If a new password has been added
         the screen will scroll to the end*/
        lifecycleScope.launch { itemCount = viewModel.itemCount() }
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_new_password -> {
                startActivity(Intent(applicationContext, PasswordGeneratorActivity::class.java))
            }

            R.id.image_settings -> {
                startActivity(Intent(applicationContext, ConfigsActivity::class.java))
            }
        }
    }

    private fun observe() {
        viewModel.listPasswords.observe(this) {
            //observe a variable in the ViewModel to know when the database is updated
            adapter.updatePasswords(it)
            if (adapter.itemCount == 0) {/*If the adapter returns that there is no item being displayed, it is because there is
                nothing saved in the database, in this case a TextView is displayed informing the user
                of this*/
                binding.textNoPasswordSaved.visibility = View.VISIBLE

            } else {
                binding.textNoPasswordSaved.visibility = View.GONE
            }
            if (itemCount < adapter.itemCount && itemCount != 0) {
                //scrolls the screen to the end when entering a new password. New passwords appear at the bottom of the screen
                binding.recyclerPasswords.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }
}

