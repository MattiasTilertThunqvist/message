package com.tilert.message.messages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tilert.message.models.User
import com.tilert.message.R
import com.tilert.message.models.ChatMessage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_row_from.view.*
import kotlinx.android.synthetic.main.chat_row_to.view.*

class ChatActivity: AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setup()
        listenForMessages()
    }

    private fun setup() {
        val user = intent.getParcelableExtra<User>(NewChatActivity.USER_KEY)
        supportActionBar?.title = user.username

        recyclerview_chat_log.adapter = adapter

        button_send_chat_log.setOnClickListener {
            handleSendMessage()
        }
    }

    private fun listenForMessages() {
         val reference = FirebaseFirestore.getInstance().collection("/messages")

        reference.addSnapshotListener { snapshot, exception ->
            exception?.let { exception ->
                Log.d("ChatActivity", exception.localizedMessage)
                return@addSnapshotListener
            }

            val snapshot = snapshot.let { it } ?: return@addSnapshotListener

            snapshot.forEach {
                val chatMessage = it.toObject(ChatMessage::class.java)

                if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                    adapter.add(ChatItemFrom(chatMessage.text))
                } else {
                    adapter.add(ChatItemTo(chatMessage.text))
                }
            }

        }
    }

    private fun handleSendMessage() {
        val reference = FirebaseFirestore.getInstance().collection("/messages").document()

        val id = reference.id
        val text = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid.let { it } ?: return
        val toId = intent.getParcelableExtra<User>(NewChatActivity.USER_KEY).uid
        val timestamp = System.currentTimeMillis() / 1000 // Convert to seconds

        val chatMessage = ChatMessage(id, text, fromId, toId, timestamp)

        reference.set(chatMessage)
            .addOnSuccessListener {

            }
            .addOnFailureListener {
                // Handle error
            }
    }
}

class ChatItemFrom(val text: String ): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_chat_row_from.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_row_from
    }
}

class ChatItemTo(val text: String): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_chat_row_to.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_row_to
    }
}