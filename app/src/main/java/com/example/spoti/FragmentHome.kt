package com.example.spoti

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoti.albums.AlbumAdapter
import com.example.spoti.albums.AlbumModel
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_album.*
import kotlinx.android.synthetic.main.fragment_home.*

class FragmentHome : AppCompatActivity() {
    private lateinit var ds: ArrayList<AlbumModel>
    private  lateinit var dbRef: DatabaseReference
    private lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home)
        recycler_trending_albums.layoutManager = LinearLayoutManager(this)
        recycler_trending_albums.setHasFixedSize(true)
        ds = arrayListOf<AlbumModel>()
        GetThongTinAlbum()
        textView = findViewById(R.id.textView3)
        textView.setOnClickListener {
            val intent = Intent(this, AlbumActivity::class.java)
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
                    recycler_trending_albums.adapter = mAdapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}