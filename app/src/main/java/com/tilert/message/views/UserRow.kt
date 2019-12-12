package com.tilert.message.views

import com.squareup.picasso.Picasso
import com.tilert.message.R
import com.tilert.message.models.User
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_new_chat.view.*

class UserRow(val user: User): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_chat.text = user.username
        Picasso.get().isLoggingEnabled = true
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.profile_imageview_new_chat)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_chat
    }
}