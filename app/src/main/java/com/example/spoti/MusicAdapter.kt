package com.example.spoti

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MusicAdapter(
    private val context: Context,
    private val musicList: List<Music>
) : BaseAdapter() {

    private var mediaPlayer: MediaPlayer? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val music = musicList[position]

        holder.titleTextView.text = music.title
        holder.artistTextView.text = music.artist

        Glide.with(context)
            .load(music.urlimg)
            .into(holder.playButton)

        holder.playButton.setOnClickListener {
            if (mediaPlayer != null) {
                mediaPlayer?.stop()
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            }

            // Load music from Firebase Storage
            val storageReference = Firebase.storage.getReferenceFromUrl(music.url)
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                // Load music into MediaPlayer
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setDataSource(uri.toString())
                mediaPlayer?.prepareAsync()
                mediaPlayer?.setOnPreparedListener {
                    mediaPlayer?.start()
                }
            }
        }

        return view!!
    }

    override fun getItem(position: Int): Any {
        return musicList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return musicList.size
    }

    private class ViewHolder(view: View) {
        val titleTextView: TextView = view.findViewById(R.id.music_title)
        val artistTextView: TextView = view.findViewById(R.id.music_artist)
        val playButton: ImageView = view.findViewById(R.id.music_icon)
    }
}
