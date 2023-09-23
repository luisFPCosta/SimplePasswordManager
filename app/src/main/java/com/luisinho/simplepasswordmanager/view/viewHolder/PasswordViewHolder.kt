package com.luisinho.simplepasswordmanager.view.viewHolder

import android.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.luisinho.simplepasswordmanager.R
import com.luisinho.simplepasswordmanager.databinding.RowPasswordBinding
import com.luisinho.simplepasswordmanager.model.PasswordModel
import com.luisinho.simplepasswordmanager.service.PasswordListener

class PasswordViewHolder(
    private val item: RowPasswordBinding,
    private val listener: PasswordListener,

    ) : RecyclerView.ViewHolder(item.root) {

    fun bind(password: PasswordModel) {
        item.textName.text = password.name
        item.textLocal.text = password.local
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
                .setPositiveButton(R.string.yes) { dialog, which -> listener.onLongClick(password) }
                .setNegativeButton(R.string.no, null).create().show()
            true
        }
    }
}