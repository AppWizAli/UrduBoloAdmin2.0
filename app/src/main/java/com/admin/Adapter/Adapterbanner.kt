package com.admin.Adapter

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.urduboltv.admin.R

class Adapterbanner(private val context: Context, private val videosList: List<Uri>) :
    RecyclerView.Adapter<Adapterbanner.VideoViewHolder>() {

    private var currentItemPosition = 0
    private val handler = Handler()
    private lateinit var runnable: Runnable

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoView: VideoView = itemView.findViewById(R.id.videoView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_videoo, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoUri = videosList[position]
        holder.videoView.setVideoURI(videoUri)

        holder.videoView.setOnCompletionListener {
            // Automatically move to the next video after completion
            currentItemPosition++
            if (currentItemPosition >= videosList.size) {
                currentItemPosition = 0
            }
            handler.postDelayed({
                holder.videoView.setVideoURI(videosList[currentItemPosition])
                holder.videoView.start()
            }, 3000)
        }

        holder.videoView.setOnClickListener {
            // Handle click on video item
            // Implement your logic when a video item is clicked
        }
    }

    override fun getItemCount(): Int {
        return videosList.size
    }
}
