package com.luisinho.simplepasswordmanager.view.viewHolder

import android.app.AlertDialog
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.luisinho.simplepasswordmanager.R
import com.luisinho.simplepasswordmanager.databinding.RowPasswordBinding
import com.luisinho.simplepasswordmanager.model.PasswordModel
import com.luisinho.simplepasswordmanager.service.PasswordListener

class PasswordViewHolder(
    private val item: RowPasswordBinding,
    private val listener: PasswordListener,

    ) : RecyclerView.ViewHolder(item.root) {

    fun bind(password: PasswordModel, context: Context) {
        item.textName.text = password.name
        if (password.local.isNullOrEmpty()) {
            item.textLocal.text = context.getString(R.string.unknown_location)
        } else {
            item.textLocal.text = password.local
        }
        if (password.username.isEmpty()) {
            item.textUsername.text = context.getString(R.string.username_not_specified)
        } else {
            item.textUsername.text = password.username
        }
        itemView.setOnClickListener {
            listener.onClick(password)
        }
        itemView.setOnLongClickListener {
            val str =
                "${it.context.getString(R.string.are_you_sure_you_want_to_remove_the_password)} \"${item.textName.text}\"? \n${
                    it.context.getString(R.string.this_operation_cannot_be_undone)
                }"
            AlertDialog.Builder(it.context)
                .setTitle(R.string.delete)
                .setMessage(str)
                .setPositiveButton(R.string.yes) { _, _ -> listener.onLongClick(password) }
                .setNegativeButton(R.string.no, null).create().show()
            true
        }
    }
}