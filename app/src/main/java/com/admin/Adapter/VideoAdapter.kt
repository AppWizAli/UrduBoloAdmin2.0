package com.admin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.Models.ModelVideo
import com.bumptech.glide.Glide
import com.urduboltv.admin.databinding.ItemVideoBinding

class VideoAdapter(
    private val activity: Context,
    private val data: List<ModelVideo>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(modelVideo: ModelVideo)
        fun onLongClick(modelVideo: ModelVideo): Boolean // Return a boolean indicating if the long click is handled
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(private val itemBinding: ItemVideoBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(modelVideo: ModelVideo) {
            itemBinding.episodeNo.text = modelVideo.episodeno
            itemBinding.totalpisodes.text = modelVideo.totalepisodes
            Glide.with(activity).load(modelVideo.thumbnail).centerCrop().into(itemBinding.thumbnail)

            itemBinding.videoConatiner.setOnClickListener {
                listener.onItemClick(modelVideo)
            }

            itemBinding.videoConatiner.setOnLongClickListener {
                listener.onLongClick(modelVideo)
            }
        }
    }
}
