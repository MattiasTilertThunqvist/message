package com.tilert.message.views

import com.squareup.picasso.Picasso
import com.tilert.message.R
import com.tilert.message.models.User
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_row_from.view.*

class ChatRowFrom(val text: String, val user: User): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_chat_row_from.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView_chat_row_from
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_row_from
    }
}
