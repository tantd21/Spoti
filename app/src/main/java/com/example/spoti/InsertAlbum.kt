package com.example.spoti

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.spoti.albums.AlbumModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class InsertAlbum : AppCompatActivity() {
    private val REQUEST_CODE = 1
    private lateinit var selectedImage: Uri
    private lateinit var albumTitleEditText: EditText
    private lateinit var albumArtistEditText: EditText
    private lateinit var albumImageView: ImageView
    private lateinit var chooseImageButton: Button
    private lateinit var albumYearEditText: EditText
    private lateinit var uploadAlbumButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insertalbum)

        albumTitleEditText = findViewById(R.id.albumTitleEditText)
        albumArtistEditText = findViewById(R.id.albumArtistEditText)
        albumImageView = findViewById(R.id.albumImageView)
        chooseImageButton = findViewById(R.id.chooseImageButton)
        albumYearEditText = findViewById(R.id.albumYearEditText)
        uploadAlbumButton = findViewById(R.id.uploadAlbumButton)


        // ...

        // Set onClickListener for chooseImageButton
        chooseImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
        }

        // Set onClickListener for uploadAlbumButton
        uploadAlbumButton.setOnClickListener {
            uploadAlbum()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImage = data.data!!

            Glide.with(this)
                .load(selectedImage)
                .into(albumImageView)
        }
    }

    private fun uploadAlbum() {
        // ...

        val title = albumTitleEditText.text.toString()
        val artist = albumArtistEditText.text.toString()
        val year = albumYearEditText.text.toString()


        // Upload data to Firebase Realtime Database
        val database = FirebaseDatabase.getInstance().getReference("albums")
        val album = AlbumModel(artist,title,year)

        val albumId = database.push().key!!
        database.child(albumId).setValue(album)

        // Upload selected image to Firebase Storage
        val storage = FirebaseStorage.getInstance().getReference("images")
        val imageRef = storage.child("$albumId.jpg")

        imageRef.putFile(selectedImage)
            .addOnSuccessListener {
                Toast.makeText(this, "thành công!", Toast.LENGTH_SHORT).show()

                // Get the download URL of the uploaded image
                imageRef.downloadUrl.addOnSuccessListener { url ->
                    Log.d(TAG, "Download URL: $url")
                    val updatedAlbum = AlbumModel(artist, title, year, url.toString())
                    database.child(albumId).setValue(updatedAlbum)
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to get download URL: $exception")
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to upload image: ${it.message}")
            }
    }

    companion object {
        private const val TAG = "InsertAlbum"
    }
}
