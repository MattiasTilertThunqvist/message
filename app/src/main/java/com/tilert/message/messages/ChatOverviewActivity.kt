package com.tilert.message.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tilert.message.R
import com.tilert.message.models.ChatMessage
import com.tilert.message.models.User
import com.tilert.message.onboarding.RegisterActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_overview.*
import kotlinx.android.synthetic.main.user_row_chat_overview.view.*

class ChatOverviewActivity: AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    companion object {
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_overview)
        recyclerview_chat_overview.adapter = adapter

        verifyUserIsLoggedIn()
        fetchCurrentUser()
        setup()
        listenForLatestMessages()

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

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseFirestore.getInstance().collection("users").document("$uid")

        ref.get()
            .addOnSuccessListener {
                currentUser = it.toObject(User::class.java)
                Log.d("ChatOverViewActivity", "Current user: ${currentUser?.username}")
            }
    }

    private fun setup() {
        supportActionBar?.title = "Chats"


    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseFirestore.getInstance().collection("latestMessages").document("$fromId").collection("messages")

        ref.addSnapshotListener { snapshot, exception ->
            exception?.let { exception ->
                Log.d("ChatOverViewActivity", exception.localizedMessage)
                return@addSnapshotListener
            }

            // Remove items from adapter
            adapter.clear()

            val snapshot = snapshot.let { it } ?: return@addSnapshotListener

            // Layout objects in recyclerview
            snapshot.forEach {
                val chatMessage = it.toObject(ChatMessage::class.java)

                val chatOverviewItem = ChatOverviewItem(chatMessage.text, chatMessage.fromId, chatMessage.timestamp)
                adapter.add(chatOverviewItem)



                // Move to separate method and handle
                adapter.setOnItemClickListener { item, view ->
                    val intent = Intent(view.context, ChatActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}

class ChatOverviewItem(val text: String, val username: String, val timestamp: Long): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.row_chat_overview_username.text = username
        viewHolder.itemView.row_chat_overview_message.text = text
        viewHolder.itemView.row_chat_overview_timestamp.text = timestamp.toString()
    }

    override fun getLayout(): Int {
        return R.layout.user_row_chat_overview
    }
}
