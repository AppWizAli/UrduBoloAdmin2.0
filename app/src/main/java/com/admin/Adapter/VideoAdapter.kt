package com.admin.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.admin.Constants
import com.admin.Models.ModelVideo
import com.bumptech.glide.Glide
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.ItemVideoBinding
import java.text.SimpleDateFormat
import java.util.Locale


class VideoAdapter (var activity: Context, val data: List<ModelVideo>, val lisnter:OnItemClickListener) : RecyclerView.Adapter<VideoAdapter.ViewHolder>(){


    var constant= Constants()

    interface OnItemClickListener {
        fun onItemClick(modelVideo: ModelVideo)
        fun onDeleteClick(modelVideo: ModelVideo)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(data[position]) }
    override fun getItemCount(): Int { return data.size }
    inner class ViewHolder(val itemBinding: ItemVideoBinding) : RecyclerView.ViewHolder(itemBinding.root){

        fun bind(modelVideo: ModelVideo) {

            itemBinding.episodeNo.text=modelVideo.episodeno
            itemBinding.totalpisodes.text=modelVideo.totalepisodes
            Glide.with(activity).load(modelVideo.thumbnail).placeholder(R.drawable.placeholder).centerCrop().into(itemBinding.thumbnail)

            itemBinding.videoConatiner.setOnClickListener{ lisnter.onItemClick(modelVideo)}


        }

    }

}