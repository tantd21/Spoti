package com.example.spoti.albums

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoti.PlayMusic
import com.example.spoti.R
import com.example.spoti.RegisterActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.each_item.view.*


class AlbumAdapter(private val ds:ArrayList<AlbumModel>) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)
     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.each_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.itemView.apply {
           title.text = ds[position].title
           artist.text = ds[position].artist
           year.text = ds[position].year.toString()
           Glide.with(context)
               .load(ds[position].url)
               .into(holder.itemView.imageViewAlbum)
           holder.itemView.setOnClickListener {
               val intent = Intent(context, PlayMusic::class.java)
               context.startActivity(intent)
           }
       }

//        val storage = FirebaseStorage.getInstance().getReference("images")
//        val imageRef = storage.child("$.jpg")
//        storage.downloadUrl.addOnSuccessListener { uri ->
//            Glide.with(holder.itemView.context)
//                .load(uri)
//                .into(holder.year)
//        }.addOnFailureListener {
//            Log.e("AlbumAdapter", "Failed to load image")
//        }
    }


    override fun getItemCount() = ds.size

}
