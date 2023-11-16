package com.admin.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.admin.Constants
import com.admin.Models.ModelDrama
import com.admin.Models.ModelVideo
import com.bumptech.glide.Glide
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.ItemAddVideoBinding
import com.urduboltv.admin.databinding.ItemDramaBinding
import com.urduboltv.admin.databinding.ItemVideoBinding
import java.text.SimpleDateFormat
import java.util.Locale


class AdapteraddVideo (var context: Context, val data: List<ModelVideo>, val listener: OnItemClickListener) : RecyclerView.Adapter<AdapteraddVideo.ViewHolder>(){


    var constant= Constants()

    interface OnItemClickListener {
        fun onItemClick(modelDrama: ModelVideo)
        fun onDeleteClick(modelDrama: ModelVideo)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAddVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(data[position]) }
    override fun getItemCount(): Int { return data.size }
    inner class ViewHolder(val itemBinding: ItemAddVideoBinding) : RecyclerView.ViewHolder(itemBinding.root){

        fun bind(modelDrama: ModelVideo) {

itemBinding.episodeNumber.text=modelDrama.episodeno
            Glide.with(context).load(modelDrama.thumbnail).centerCrop()
                .into(itemBinding.thumbnail)

            itemBinding.conatiner.setOnClickListener{ listener.onItemClick(modelDrama)}

        }

    }

}