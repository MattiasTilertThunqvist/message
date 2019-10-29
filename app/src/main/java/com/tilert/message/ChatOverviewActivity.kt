package com.tilert.message

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_overview.*

class ChatOverviewActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_overview)
        setup()

        floatingActionButton_chat_overview.setOnClickListener {
            val intent = Intent(this, NewChatActivity::class.java)
            startActivity(intent)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu_chat_overview, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setup() {
        supportActionBar?.title = "Chats"

        val adapter = GroupAdapter<GroupieViewHolder>()
        recyclerview_chat_overview.adapter = adapter

        val chatOverviewItem = ChatOverviewItem()

        adapter.add(chatOverviewItem)
        adapter.add(chatOverviewItem)
        adapter.add(chatOverviewItem)
        adapter.add(chatOverviewItem)
        adapter.add(chatOverviewItem)
        adapter.add(chatOverviewItem)
        adapter.add(chatOverviewItem)
        adapter.add(chatOverviewItem)
        adapter.add(chatOverviewItem)

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(view.context, ChatActivity::class.java)
            startActivity(intent)
        }
    }
}

class ChatOverviewItem: Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.user_row_chat_overview
    }
}