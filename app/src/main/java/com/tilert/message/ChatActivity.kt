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

        val adapter = GroupAdapter<GroupieViewHolder>()



        recyclerview_chat_log.adapter = adapter
    }
}

class ChatItem: Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        //TODO: Create layout recource file for row
        
    }
}