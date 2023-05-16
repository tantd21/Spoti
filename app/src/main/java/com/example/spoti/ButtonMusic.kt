package com.example.spoti

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.random.Random

class ButtonMusic : AppCompatActivity() {

    lateinit var songList: ListView
    lateinit var previousBtn: ImageButton
    lateinit var playPauseBtn: ImageButton
    lateinit var nextBtn: ImageButton
    lateinit var shuffleBtn: ImageButton
    lateinit var repeatBtn: ImageButton
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var music: Music



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_music)



        songList = findViewById(R.id.song_list)
        previousBtn = findViewById(R.id.btn_previous)
        playPauseBtn = findViewById(R.id.btn_play_pause)
        nextBtn = findViewById(R.id.btn_next)
        shuffleBtn = findViewById(R.id.btn_shuffle)
        repeatBtn = findViewById(R.id.btn_repeat)

        // Set up click listeners for the buttons
        previousBtn.setOnClickListener { previousSong() }
        playPauseBtn.setOnClickListener { playPauseSong() }
        nextBtn.setOnClickListener { nextSong() }
        shuffleBtn.setOnClickListener { toggleShuffle() }
        repeatBtn.setOnClickListener { toggleRepeat() }
        playPauseBtn.setOnClickListener{playSong()}


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
                musicAdapter = MusicAdapter(this@ButtonMusic, musicList)
                songList.adapter = musicAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun playSong() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setOnCompletionListener { onSongComplete() }
        } else {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
        }
        try {
            // Load music from Firebase Storage
            val storageReference = Firebase.storage.getReferenceFromUrl(music.url)
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                // Set data source for MediaPlayer using URL from Firebase Storage
                mediaPlayer!!.setDataSource(uri.toString())
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
                isPlaying = true
                playPauseBtn.setImageResource(R.drawable.ic_pause)
            }.addOnFailureListener { exception ->
                Log.e("ButtonMusic", "Failed to get music URL from Firebase Storage", exception)
            }
        } catch (e: Exception) {
            Log.e("ButtonMusic", "Failed to play song", e)
        }
    }



    private fun onSongComplete() {
        TODO("Not yet implemented")
    }

    private fun previousSong() {
        val currentPosition = songList.selectedItemPosition
        val newPosition = currentPosition - 1
        val lastPosition = songList.count - 1
        val newUrl = if (newPosition < 0) {
            songList.getItemAtPosition(lastPosition) as String
        } else {
            songList.getItemAtPosition(newPosition) as String
        }
        // TODO: Play the new song using a media player
    }


    private fun playPauseSong() {
        if (mediaPlayer == null) {
            // Create a new media player instance
            mediaPlayer = MediaPlayer().apply {
                setDataSource(songList.selectedItem as String)
                prepare()
            }
        }

        if (mediaPlayer!!.isPlaying) {
            // Pause the current song
            mediaPlayer!!.pause()
            playPauseBtn.setImageResource(R.drawable.ic_play)
        } else {
            // Start playing the current song
            mediaPlayer!!.start()
            playPauseBtn.setImageResource(R.drawable.ic_pause)
        }
    }

    private fun nextSong() {
        val currentPosition = songList.selectedItemPosition
        val newPosition = currentPosition + 1
        val lastPosition = songList.count - 1
        val newUrl = if (newPosition > lastPosition) {
            songList.getItemAtPosition(0) as String
        } else {
            songList.getItemAtPosition(newPosition) as String
        }

        if (mediaPlayer == null) {
            // Create a new media player instance
            mediaPlayer = MediaPlayer().apply {
                setDataSource(newUrl)
                prepare()
            }
        } else {
            // Stop and release the current media player instance
            mediaPlayer!!.stop()
            mediaPlayer!!.release()

            // Create a new media player instance
            mediaPlayer = MediaPlayer().apply {
                setDataSource(newUrl)
                prepare()
                start()
            }
        }
    }


    private var shuffleModeEnabled = false

    private fun toggleShuffle() {
        shuffleModeEnabled = !shuffleModeEnabled

        // Update UI to reflect the new shuffle mode state
        if (shuffleModeEnabled) {
            shuffleBtn.setColorFilter(ContextCompat.getColor(this, R.color.green))
        } else {
            shuffleBtn.colorFilter = null
        }
    }

    private fun getNextSong(): String {
        val currentPosition = songList.selectedItemPosition
        val lastPosition = songList.count - 1

        return if (shuffleModeEnabled) {
            // Select a random song from the list
            val randomPosition = Random.nextInt(0, lastPosition + 1)
            songList.getItemAtPosition(randomPosition) as String
        } else {
            // Play the next song in the list
            val newPosition = currentPosition + 1
            if (newPosition > lastPosition) {
                songList.getItemAtPosition(0) as String
            } else {
                songList.getItemAtPosition(newPosition) as String
            }
        }
    }


    private var repeatMode = REPEAT_MODE_OFF

    private fun toggleRepeat() {
        repeatMode = when (repeatMode) {
            REPEAT_MODE_OFF -> REPEAT_MODE_ALL
            REPEAT_MODE_ALL -> REPEAT_MODE_ONE
            REPEAT_MODE_ONE -> REPEAT_MODE_OFF
            else -> REPEAT_MODE_OFF
        }
        updateRepeatButtonState()
    }

    private fun updateRepeatButtonState() {
        repeatBtn.setImageResource(
            when (repeatMode) {
                REPEAT_MODE_OFF -> R.drawable.ic_repeat
                REPEAT_MODE_ALL -> R.drawable.ic_repeat_all
                REPEAT_MODE_ONE -> R.drawable.ic_repeat_one
                else -> R.drawable.ic_repeat
            }
        )
    }

    companion object {
        private const val REPEAT_MODE_OFF = 0
        private const val REPEAT_MODE_ALL = 1
        private const val REPEAT_MODE_ONE = 2
    }

}
