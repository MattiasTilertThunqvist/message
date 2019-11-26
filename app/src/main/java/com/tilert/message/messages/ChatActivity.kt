package com.tilert.message.messages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        supportActionBar?.title = "Username"

        val adapter = GroupAdapter<GroupieViewHolder>()
        recyclerview_chat_log.adapter = adapter

        val chatItem = ChatItem()

        adapter.add(chatItem)
        adapter.add(chatItem)
        adapter.add(chatItem)
        adapter.add(chatItem)
        adapter.add(chatItem)

    }
}

class ChatItem: Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_row_from
    }
}