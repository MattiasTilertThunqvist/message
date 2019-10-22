package com.tilert.message

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_chat_overview.*

class ChatOverviewActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_overview)


        new_chat_button_chatoverview.setOnClickListener {
            val intent = Intent(this, NewChatActivity::class.java)
            startActivity(intent)
        }

    }
}