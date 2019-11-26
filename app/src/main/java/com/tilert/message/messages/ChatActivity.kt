package com.tilert.message.messages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tilert.message.models.User
import com.tilert.message.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setup()
    }

    private fun setup() {
        val user = intent.getParcelableExtra<User>(NewChatActivity.USER_KEY)
        supportActionBar?.title = user.username

        val adapter = GroupAdapter<GroupieViewHolder>()
        recyclerview_chat_log.adapter = adapter

        val chatItemFrom = ChatItemFrom()
        val chatItemTo = ChatItemTo()

        adapter.add(chatItemFrom)
        adapter.add(chatItemTo)
        adapter.add(chatItemFrom)
        adapter.add(chatItemTo)
        adapter.add(chatItemFrom)
        adapter.add(chatItemFrom)
        adapter.add(chatItemTo)
        adapter.add(chatItemFrom)
        adapter.add(chatItemTo)
        adapter.add(chatItemFrom)

    }
}

class ChatItemFrom: Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_row_from
    }
}

class ChatItemTo: Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_row_to
    }
}