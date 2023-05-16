package com.example.spoti

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoti.albums.AlbumAdapter
import com.example.spoti.albums.AlbumModel
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_album.*

class AlbumActivity :AppCompatActivity(){
    private lateinit var ds: ArrayList<AlbumModel>
    private  lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle? ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)
        recycler_view_library.layoutManager = LinearLayoutManager(this)
        recycler_view_library.setHasFixedSize(true)
        ds = arrayListOf<AlbumModel>()
        GetThongTinAlbum()
        val addMusicLayout = findViewById<AppCompatImageView>(R.id.add_album)
        addMusicLayout.setOnClickListener {
            val intent = Intent(this, InsertAlbum::class.java)
            startActivity(intent)
        }
    }

    private fun GetThongTinAlbum() {
        dbRef = FirebaseDatabase.getInstance().getReference("albums")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ds.clear()
                if(snapshot.exists()){
                    for(albumSnap in snapshot.children) {
                        val albumdata = albumSnap.getValue(AlbumModel::class.java)
                        ds.add(albumdata!!)
                    }
                    val mAdapter = AlbumAdapter(ds)
                    recycler_view_library.adapter = mAdapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}