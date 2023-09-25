package com.luisinho.simplepasswordmanager.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luisinho.simplepasswordmanager.databinding.RowPasswordBinding
import com.luisinho.simplepasswordmanager.model.PasswordModel
import com.luisinho.simplepasswordmanager.service.PasswordListener
import com.luisinho.simplepasswordmanager.view.MainActivity
import com.luisinho.simplepasswordmanager.view.viewHolder.PasswordViewHolder

class  PasswordAdapter(val context: Context) : RecyclerView.Adapter<PasswordViewHolder>() {
    private var passwordList: List<PasswordModel> = arrayListOf()
    private lateinit var passwordListener: PasswordListener
    private lateinit var listPassword: RowPasswordBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        listPassword = RowPasswordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PasswordViewHolder(listPassword, passwordListener)
    }

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        holder.bind(passwordList[position], context)
    }


    override fun getItemCount(): Int {
        return passwordList.count()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePasswords(list: List<PasswordModel>) {
        passwordList = list
        notifyDataSetChanged()
    }

    fun attachListener(listener: PasswordListener) {
        passwordListener = listener
    }



}