package com.tilert.message.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.tilert.message.R
import com.tilert.message.models.ChatMessage
import com.tilert.message.models.User
import com.tilert.message.onboarding.RegisterActivity
import com.tilert.message.views.ChatOverviewRow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_overview.*
import kotlinx.android.synthetic.main.user_row_chat_overview.view.*

class ChatOverviewActivity: AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    var chatPartners = ArrayList<User>()

    companion object {
        var currentUser: User? = null
        val TAG = "LatestMessages"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_overview)

        verifyUserIsLoggedIn()
        fetchCurrentUser()
        setup()
        listenForLatestMessages()
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

        floatingActionButton_chat_overview.setOnClickListener {
            val intent = Intent(this, NewChatActivity::class.java)
            startActivity(intent)
        }

        recyclerview_chat_overview.adapter = adapter
        recyclerview_chat_overview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            val index = adapter.getAdapterPosition(item)
            val chatPartner = chatPartners[index]
            startChatActivity(chatPartner)
        }
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

            // Remove users. The order of the list may have changed due to new messages.
            chatPartners.clear()

            val snapshot = snapshot.let { it } ?: return@addSnapshotListener

            // Layout objects in recyclerview
            snapshot.forEach {
                val chatMessage = it.toObject(ChatMessage::class.java)

                // Extract partnerId
                val chatPartnerId: String
                if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                    chatPartnerId = chatMessage.toId
                } else {
                    chatPartnerId = chatMessage.fromId
                }

                val ref = FirebaseFirestore.getInstance().collection("users").document("$chatPartnerId")
                ref.get().addOnSuccessListener {
                    if (it == null) {
                        Log.d("ChatOverViewActivity", "Failed to get chat partner from firestore")
                        return@addOnSuccessListener
                    }

                    val chatPartner = it.toObject(User::class.java) ?: return@addOnSuccessListener
                    chatPartners.add(chatPartner)

                    val chatOverviewItem = ChatOverviewRow(chatMessage.text, chatPartner.username, chatPartner.profileImageUrl, "MocketTimestamp")
                    adapter.add(chatOverviewItem)
                }
            }
        }
    }

    private fun startChatActivity(chatPartner: User) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(NewChatActivity.USER_KEY, chatPartner)
        startActivity(intent)
    }
}