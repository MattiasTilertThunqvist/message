package com.tilert.message

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    // Properties

    val selectImageRequestCode = 0
    var selectedPhotoUri: Uri? = null


    // Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        select_photo_button_register.setOnClickListener {
            selectPhoto()
        }

        register_button_register.setOnClickListener {
            registerUser()
        }

        already_have_account_textview_register.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    // Handle select image

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, selectImageRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handle result for selectPhoto
        if(requestCode == selectImageRequestCode && resultCode == Activity.RESULT_OK && data != null) {

            selectedPhotoUri = data.data // Represents the location for the image on the device
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            select_photo_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }


    // Register user

    private fun registerUser() {
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Plese enter email/password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                // If successful
                Log.d("RegisterActivity", "Successfuly created user with uid: ${it.result!!.user!!.uid}")

                uploadImageToStorage()
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToStorage() {
        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val reference = FirebaseStorage.getInstance().getReference("/images/$filename")

        reference.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {

            }

    }
}
