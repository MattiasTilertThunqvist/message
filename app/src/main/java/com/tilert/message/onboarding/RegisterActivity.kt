package com.tilert.message.onboarding

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tilert.message.messages.ChatOverviewActivity
import com.tilert.message.R
import com.tilert.message.models.User
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    // Properties

    val selectImageRequestCode = 0
    var selectedPhotoUri: Uri? = null

    // Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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

    // Handle image

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
            select_photo_imageview_register.setImageBitmap(bitmap)
            select_photo_button_register.alpha = 0f
        }
    }

    // Register user
    private fun registerUser() {
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if(email.isEmpty() || password.isEmpty() || username_edittext_register.text.toString().isEmpty()) {
            Toast.makeText(this, "Plese enter text in all fields", Toast.LENGTH_SHORT).show()
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
        val selectedPhotoUri = selectedPhotoUri?.let { it } ?: return

        val filename = UUID.randomUUID().toString()
        val reference = FirebaseStorage.getInstance().getReference("/images/$filename")

        reference.putFile(selectedPhotoUri)
            .addOnSuccessListener {

                reference.downloadUrl.addOnSuccessListener { profileImageUrl ->
                    saveUserToFirestore(profileImageUrl.toString())
                }
            }
    }

    private fun saveUserToFirestore(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val username = username_edittext_register.text.toString()
        val user = User(uid, username, profileImageUrl)

        val reference = FirebaseFirestore.getInstance().collection("users").document(uid)
        reference.set(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully saved user to Firestore")
                val intent = Intent(this, ChatOverviewActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            .addOnFailureListener { exception ->
                Log.d("RegisterActivity", "Failed to save user to Firestore: $exception")
                Toast.makeText(this, "Failed to create account", Toast.LENGTH_SHORT).show()
            }
    }
}




