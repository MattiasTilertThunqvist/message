package com.tilert.message.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.tilert.message.R
import com.tilert.message.models.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_chat.*
import kotlinx.android.synthetic.main.user_row_new_chat.view.*

class NewChatActivity: AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)
        setup()
        fetchUsers()
    }

    private fun setup() {
        supportActionBar?.title = "New chat"
    }

    private fun fetchUsers() {
        val ref = FirebaseFirestore.getInstance().collection("users")

        ref.get().addOnSuccessListener { result ->
            if (result == null) {
                Log.d("NewChatActivity", "No data in fetch users snapshot")
            }

            for (document in result) {
                val user = document.toObject(User::class.java)
                adapter.add(UserItem(user))
            }

            adapter.setOnItemClickListener { item, view ->
                startChatActivity ()
            }

            recyclerview_new_chat.adapter = adapter
        }
        
        ref.get().addOnFailureListener { exception ->  

        }
    }

    private fun startChatActivity() {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
        finish()
    }
}

class UserItem(val user: User): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_chat.text = user.username
        Picasso.get().isLoggingEnabled = true
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.profile_imageview_new_chat)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_chat
    }
}