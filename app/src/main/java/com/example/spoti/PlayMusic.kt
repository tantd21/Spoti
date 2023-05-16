package com.example.spoti

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class PlayMusic : AppCompatActivity() {
    private lateinit var musicListView: ListView
    private lateinit var musicAdapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_music)
        val addMusicLayout = findViewById<AppCompatImageView>(R.id.add_music)
        addMusicLayout.setOnClickListener {
            val intent = Intent(this, InsertMusic::class.java)
            startActivity(intent)
        }
        musicListView = findViewById(R.id.song_list)

//        val storageReference = Firebase.storage.getReferenceFromUrl(music.url)
//        storageReference.downloadUrl.addOnSuccessListener { uri ->
//            // Load music into MediaPlayer
//            val mediaPlayer = MediaPlayer()
//            mediaPlayer.setDataSource(uri.toString())
//            mediaPlayer.prepareAsync()
//            mediaPlayer.setOnPreparedListener {
//                holder.playButton.setOnClickListener {
//                    mediaPlayer.start()
//                }
//            }
//        }
        // Get music list from Firebase database
        val databaseReference = FirebaseDatabase.getInstance().getReference("music")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val musicList = mutableListOf<Music>()
                for (musicSnapshot in snapshot.children) {
                    val music = musicSnapshot.getValue(Music::class.java)
                    if (music != null) {
                        musicList.add(music)
                    }
                }
                musicAdapter = MusicAdapter(this@PlayMusic, musicList)
                musicListView.adapter = musicAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}