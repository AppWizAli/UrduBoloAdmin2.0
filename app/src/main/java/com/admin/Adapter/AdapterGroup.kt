package com.admin.Adapter

import com.admin.Models.ModelGroup
import com.urduboltv.admin.databinding.GroupItemBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.admin.Constants
import com.admin.Data.SharedPrefManager
import com.admin.Models.Admin
import com.admin.Models.ModelDrama
import com.admin.Models.ModelUser
import com.urduboltv.admin.databinding.ItemDramaBinding
import com.urduboltv.admin.databinding.ItemUserBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterGroup ( val data: List<ModelGroup>, val listener: OnItemClickListener) : RecyclerView.Adapter<AdapterGroup.ViewHolder>(){


    var constant= Constants()

    interface OnItemClickListener {
        fun onGroupClick(modelUser: ModelGroup)
        fun onGroupdeleteClick(modelUser: ModelGroup)
        fun onGroupUpdateClick(modelUser: ModelGroup)
        fun onAddMemberClick(modelUser: ModelGroup)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(GroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(data[position]) }
    override fun getItemCount(): Int { return data.size }
    inner class ViewHolder(val itemBinding: GroupItemBinding) : RecyclerView.ViewHolder(itemBinding.root){

        fun bind(modelUser: ModelGroup) {
            itemBinding.textViewGroupName.text=modelUser.name
            itemBinding.textViewGroupDescription.text=modelUser.description
            itemBinding.imageViewDelete.setOnClickListener{ listener.onGroupdeleteClick(modelUser)}
            itemBinding.imageViewEdit.setOnClickListener{ listener.onGroupUpdateClick(modelUser)}
            itemBinding.contanier.setOnClickListener{ listener.onGroupClick(modelUser)}
            itemBinding.addMember.setOnClickListener{ listener.onAddMemberClick(modelUser)}
        }

    }

}