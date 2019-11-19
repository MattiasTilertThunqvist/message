package com.tilert.message

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_overview.*

class ChatOverviewActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_overview)
        verifyUserIsLoggedIn()
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_settings -> {

            }

            R.id.action_term_conditions -> {

            }

            R.id.action_licenses -> {

            }

            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                startRegisterActivity()
            }

            R.id.action_delete_account -> {

            }
        }


        return super.onOptionsItemSelected(item)
    }

    private fun startRegisterActivity() {
        val intent = Intent(this,  RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid

        if (uid == null) {
            startRegisterActivity()
        }
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
