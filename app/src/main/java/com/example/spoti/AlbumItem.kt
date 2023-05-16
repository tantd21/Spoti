package com.example.spoti

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoti.albums.AlbumAdapter
import com.example.spoti.albums.AlbumModel
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_album.*


class AlbumItem :AppCompatActivity(){
    private lateinit var ds: ArrayList<AlbumModel>
    private  lateinit var dbRef:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle? ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)
        recycler_view_library.layoutManager = LinearLayoutManager(this)
        recycler_view_library.setHasFixedSize(true)
        ds = arrayListOf<AlbumModel>()
        GetThongTinAlbum()
    }

    private fun GetThongTinAlbum() {
        dbRef = FirebaseDatabase.getInstance().getReference("albums")
        dbRef.addValueEventListener(object : ValueEventListener{
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