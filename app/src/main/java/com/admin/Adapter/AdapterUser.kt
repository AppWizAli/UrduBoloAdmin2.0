package com.admin.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.Constants
import com.admin.Models.ModelDrama
import com.admin.Models.ModelUser
import com.urduboltv.admin.databinding.ItemDramaBinding
import com.urduboltv.admin.databinding.ItemUserBinding

class AdapterUser (var activity:String, val data: List<ModelUser>, val listener: OnItemClickListener) : RecyclerView.Adapter<AdapterUser.ViewHolder>(){


    var constant= Constants()

    interface OnItemClickListener {
        fun onupdateclick(modelUser: ModelUser)
        fun onDeleteClick(modelUser: ModelUser)
        fun onitemclick(modelUser: ModelUser)
        fun onViewClick(modelUser: ModelUser)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(data[position]) }
    override fun getItemCount(): Int { return data.size }
    inner class ViewHolder(val itemBinding: ItemUserBinding) : RecyclerView.ViewHolder(itemBinding.root){

        fun bind(modelUser: ModelUser) {

            itemBinding.userName.text=modelUser.name

            itemBinding.removeUser.setOnClickListener{ listener.onDeleteClick(modelUser)}
            itemBinding.updateUser.setOnClickListener{ listener.onupdateclick(modelUser)}
            itemBinding.containeruser.setOnClickListener{ listener.onitemclick(modelUser)}
            itemBinding.view.setOnClickListener{ listener.onViewClick(modelUser)}

        }

    }

}