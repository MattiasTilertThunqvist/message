package com.tilert.message

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
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

        val adapter1 = GroupAdapter<GroupieViewHolder>()
        recyclerview_chat.adapter = adapter1

        val chatOverviewItem = ChatOverviewItem()

        adapter1.add(chatOverviewItem)
        adapter1.add(chatOverviewItem)
        adapter1.add(chatOverviewItem)
        adapter1.add(chatOverviewItem)
        adapter1.add(chatOverviewItem)

    }
}

class ChatItem: Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_row_sender
    }
}