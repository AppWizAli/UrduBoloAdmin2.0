package com.admin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.Constants
import com.admin.Models.ModelDrama
import com.admin.Models.ModelSeason
import com.bumptech.glide.Glide
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.ItemDramaBinding
import com.urduboltv.admin.databinding.ItemSeasonBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterSeason (var activity: Context, val data: List<ModelSeason>, val listener: OnItemClickListener) : RecyclerView.Adapter<AdapterSeason.ViewHolder>(){


    var constant= Constants()

    interface OnItemClickListener {
        fun onItemClick(modelSeason: ModelSeason)
        fun onDeleteClick(modelSeason: ModelSeason)
        fun onEditClick(modelSeason: ModelSeason)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSeasonBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(data[position]) }
    override fun getItemCount(): Int { return data.size }
    inner class ViewHolder(val itemBinding: ItemSeasonBinding) : RecyclerView.ViewHolder(itemBinding.root){

        fun bind(modelSeason: ModelSeason) {

            itemBinding.dramaName.text=modelSeason.seasonNo
            itemBinding.totalepisodes.text=modelSeason.totalEpisode
            Glide.with(activity).load(modelSeason.thumbnail).centerCrop().placeholder(R.drawable.placeholder).into(itemBinding.dramaImage)
            val dateTimeFormat = SimpleDateFormat("dd MMMM yyyy, h:mm a", Locale.getDefault())
            val formattedDateTime = dateTimeFormat.format(modelSeason.uploadedAt.toDate()) // Assuming timestamp is a Firebase Timestamp
            itemBinding.uploadedAt.text = formattedDateTime
            itemBinding.containerDrama.setOnClickListener{ listener.onItemClick(modelSeason)}
            itemBinding.edit.setOnClickListener{ listener.onEditClick(modelSeason)}
            itemBinding.delete.setOnClickListener{ listener.onDeleteClick(modelSeason)}

        }

    }

}