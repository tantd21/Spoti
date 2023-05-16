package com.example.spoti

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class InsertMusic : AppCompatActivity() {

    private lateinit var selectButton: Button
    private lateinit var selectImg: Button
    private lateinit var uploadButton: Button
    private lateinit var imageView: ImageView
    private lateinit var fileName: TextView
    private lateinit var songTitle: EditText
    private lateinit var artist: EditText
    private lateinit var storageRef: StorageReference
    private var filePath: Uri? = null
    private var fileImg: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_music)

        selectButton = findViewById(R.id.select_button)
        selectImg = findViewById(R.id.select_img)
        uploadButton = findViewById(R.id.upload_button)
        imageView = findViewById(R.id.imageView)
        fileName = findViewById(R.id.file_name)
        songTitle = findViewById(R.id.song_title)
        artist = findViewById(R.id.artist)

        storageRef = FirebaseStorage.getInstance().reference

        selectButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/*"
            startActivityForResult(intent, 1)
        }
        selectImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 2)
        }

        uploadButton.setOnClickListener {
            val title = songTitle.text.toString()
            val artistName = artist.text.toString()


            if (title.isNotEmpty() && artistName.isNotEmpty() && filePath != null && fileImg != null) {
                val fileRef = storageRef.child("music/${filePath?.lastPathSegment}")
                val fileimgRef = storageRef.child("music/${fileImg?.lastPathSegment}")
                fileRef.putFile(filePath!!)
                    .addOnSuccessListener { taskSnapshot ->
                        fileRef.downloadUrl.addOnSuccessListener { uri ->
                            val audioUrl = uri.toString()
                            fileimgRef.putFile(fileImg!!)
                                .addOnSuccessListener { taskSnapshot ->
                                    fileimgRef.downloadUrl.addOnSuccessListener { uri ->
                                        val imageUrl = uri.toString()
                                        val databaseRef = FirebaseDatabase.getInstance().reference
                                        val music = Music(title, artistName, audioUrl, imageUrl)
                                        databaseRef.setValue(music)
                                        databaseRef.child("music").push().setValue(music)
                                        val intent = Intent(this@InsertMusic, PlayMusic::class.java)
                                        startActivity(intent)
                                        finish()

                                    }
                                }
                                .addOnFailureListener { exception ->
                                    // Handle any errors related to uploading the image
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle any errors related to uploading the audio file
                    }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            fileName.text = filePath?.lastPathSegment

        }
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            fileImg = data.data!!

            Glide.with(this)
                .load(fileImg)
                .into(imageView)
        }
    }
}
