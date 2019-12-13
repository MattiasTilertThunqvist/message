package com.tilert.message.messages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tilert.message.R
import com.tilert.message.models.ChatMessage
import com.tilert.message.models.User
import com.tilert.message.onboarding.RegisterActivity
import com.tilert.message.views.ChatOverviewRow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_overview.*
import java.text.DateFormat

class ChatOverviewActivity: AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()

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
            R.id.action_term_conditions -> {
                val uri = Uri.parse("https://www.termsfeed.com/blog/sample-terms-and-conditions-template/")
                startWebBrowser(uri)
            }

            R.id.action_licenses -> {
                val uri = Uri.parse("https://www.lawinsider.com/clause/software-license")
                startWebBrowser(uri)
            }

            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                startRegisterActivity()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid

        if (uid == null) {
            startRegisterActivity()
        }
    }

    private fun startRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
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
            val chatOverviewRow = item as ChatOverviewRow
            val chatPartner = chatOverviewRow.user
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

            val snapshot = snapshot.let { it } ?: return@addSnapshotListener

            var chatMessages = ArrayList<ChatMessage>()
            snapshot.forEach { chatMessages.add(it.toObject(ChatMessage::class.java)) }

            val sortedChatMessages = chatMessages.sortedBy { it.timestamp }

            // Layout objects in recyclerview
            sortedChatMessages.forEach { chatMessage ->
                val chatPartnerId: String
                // Extract partnerId
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
                    val displayableDateTime = convertTimestampToTime(chatMessage.timestamp)
                    val chatOverviewItem = ChatOverviewRow(chatMessage.text, chatPartner, displayableDateTime)
                    adapter.add(chatOverviewItem)
                }
            }
        }
    }

    private fun convertTimestampToTime(timestamp: Long): String {
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(timestamp)
    }

    private fun startChatActivity(chatPartner: User) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(ChatActivity.USER_KEY, chatPartner)
        startActivity(intent)
    }

    private fun startWebBrowser(uri: Uri) {
        val browserIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(browserIntent)
    }
}