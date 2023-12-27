package com.admin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.Constants
import com.admin.Models.ModelVideo
import com.admin.Ui.ActivityManageVideo
import com.bumptech.glide.Glide
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.ItemVideoBinding
import com.urduboltv.admin.databinding.ItemVideoManagementBinding

class AdapterAssignedVideo(
    var from:String,
    var activity: Context,
    var data: List<ModelVideo>,
    val listener: OnItemClickListener
) : RecyclerView.Adapter<AdapterAssignedVideo.ViewHolder>() {

    interface OnItemClickListener {
        fun onUnAssignClick(modelVideo: ModelVideo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemVideoManagementBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateList(newList: List<ModelVideo>) {
        data = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(val itemBinding: ItemVideoManagementBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(modelVideo: ModelVideo) {
            if(from.equals("Assigned"))
            {
                itemBinding.btnAssign.text="UnAssign"
            }
            itemBinding.dramaName.text = modelVideo.dramaName
            itemBinding.episodeNumber.text = modelVideo.episodeno
            itemBinding.totalEpisode.text = modelVideo.totalepisodes
            Glide.with(activity).load(modelVideo.thumbnail).placeholder(R.drawable.img_4).centerCrop().into(itemBinding.dramaImage)


            itemBinding.btnAssign.setOnClickListener {
                listener.onUnAssignClick(modelVideo)
            }
        }
    }
}
