package com.tilert.message.messages

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.tilert.message.models.User
import com.tilert.message.R
import com.tilert.message.models.ChatMessage
import com.tilert.message.views.ChatRowFrom
import com.tilert.message.views.ChatRowTo
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_row_from.view.*

class ChatActivity: AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    val messagesFirestoreRef = FirebaseFirestore.getInstance().collection("userMessages")
    val latestMessagesFirestoreRef = FirebaseFirestore.getInstance().collection("latestMessages")
    var toUser: User? = null

    companion object {
        val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setup()
        listenForMessages()
    }

    private fun setup() {
        toUser = intent.getParcelableExtra<User>(USER_KEY)
        supportActionBar?.title = toUser?.username

        recyclerview_chat_log.adapter = adapter

        button_send_chat_log.setOnClickListener {
            handleSendMessage()
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val documentRef = messagesFirestoreRef.document("$fromId").collection("$toId")
        documentRef.addSnapshotListener { snapshot, exception ->
            exception?.let { exception ->
                Log.d("ChatActivity", exception.localizedMessage)
                return@addSnapshotListener
            }

            // Remove items from adapter
            adapter.clear()

            val snapshot = snapshot.let { it } ?: return@addSnapshotListener

            // Layout objects in recyclerview
            snapshot.forEach {
                val chatMessage = it.toObject(ChatMessage::class.java)

                if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                    val currentUser = ChatOverviewActivity.currentUser ?: return@addSnapshotListener
                    adapter.add(ChatRowFrom(chatMessage.text, currentUser))
                } else {
                    adapter.add(ChatRowTo(chatMessage.text, toUser!!))
                }
            }

            scrollToBottom()
        }
    }

    private fun handleSendMessage() {
        val fromId = FirebaseAuth.getInstance().uid.let { it } ?: return
        val toId = intent.getParcelableExtra<User>(USER_KEY).uid

        val documentRef = messagesFirestoreRef.document("$fromId").collection("$toId").document()
        val toDocumentRef = messagesFirestoreRef.document("$toId").collection("$fromId").document()

        val id = documentRef.id
        val text = edittext_chat_log.text.toString()
        val timestamp = System.currentTimeMillis() / 1000 // Convert to seconds

        val chatMessage = ChatMessage(id, text, fromId, toId, timestamp)

        documentRef.set(chatMessage)
            .addOnSuccessListener {
                edittext_chat_log.text.clear()
                scrollToBottom()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Couldn't send message", Toast.LENGTH_LONG).show()
            }

        toDocumentRef.set(chatMessage)
            .addOnFailureListener {
                Toast.makeText(this, "Couldn't send message", Toast.LENGTH_LONG).show()
            }

         // Set latest message for logged in user
        val latestMessagesRef = latestMessagesFirestoreRef.document("$fromId").collection("messages").document("$toId")
        latestMessagesRef.set(chatMessage)

        // Set latest message for receiving user
        val latestMessagesToRef = latestMessagesFirestoreRef.document("$toId").collection("messages").document("$fromId")
        latestMessagesToRef.set(chatMessage)
    }

    private fun scrollToBottom() {
        recyclerview_chat_log.scrollToPosition(adapter.itemCount -1)
    }
}
