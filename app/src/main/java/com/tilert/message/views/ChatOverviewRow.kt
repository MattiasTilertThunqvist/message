package com.tilert.message.views

import com.squareup.picasso.Picasso
import com.tilert.message.R
import com.tilert.message.models.User
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_chat_overview.view.*

class ChatOverviewRow(val textMessage: String, val user: User, val timestamp: String): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.row_chat_overview_username.text = user.username
        viewHolder.itemView.row_chat_overview_message.text = textMessage
        viewHolder.itemView.row_chat_overview_timestamp.text = timestamp

        val targetImageView = viewHolder.itemView.row_chat_overview_profileImageView
        Picasso.get().load(user.profileImageUrl).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_chat_overview
    }
}
