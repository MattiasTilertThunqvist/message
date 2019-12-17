package com.tilert.message.messages

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tilert.message.models.User
import com.tilert.message.R
import com.tilert.message.models.ChatMessage
import com.tilert.message.views.ChatRowFrom
import com.tilert.message.views.ChatRowTo
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat.*

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

        recyclerview_chat_log.addOnLayoutChangeListener { view, left, top, right, bottom, leftWas, topWas, rightWas, bottomWas ->
            // Scroll to bottom of recyclerview when keyboard opens
            if (bottom != bottomWas) {
                scrollToBottom()
            }
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

            var chatMessages = ArrayList<ChatMessage>()
            snapshot.forEach { chatMessages.add(it.toObject(ChatMessage::class.java)) }

            // Sort the list based on when it was sent
            val sortedChatMessages = chatMessages.sortedBy { it.timestamp }

            // Layout objects in recyclerview
            sortedChatMessages.forEach { chatMessage ->
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
        val text = edittext_chat_log.text.toString()
        if (text == "") return

        val fromId = FirebaseAuth.getInstance().uid.let { it } ?: return
        val toId = intent.getParcelableExtra<User>(USER_KEY).uid

        val documentRef = messagesFirestoreRef.document("$fromId").collection("$toId").document()
        val toDocumentRef = messagesFirestoreRef.document("$toId").collection("$fromId").document()

        val documentId = documentRef.id
        val timestamp = System.currentTimeMillis()
        val chatMessage = ChatMessage(documentId, text, fromId, toId, timestamp)

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
