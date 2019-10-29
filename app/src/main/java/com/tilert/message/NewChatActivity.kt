package com.tilert.message

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_chat.*

class NewChatActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)
        setup()
    }

    private fun setup() {
        supportActionBar?.title = "New chat"

        val adapter = GroupAdapter<GroupieViewHolder>()
        recyclerview_new_chat.adapter = adapter
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(view.context, ChatActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

class UserItem: Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_chat
    }
}