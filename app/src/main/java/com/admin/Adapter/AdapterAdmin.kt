package com.admin.Adapter

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

class AdapterAdmin (var activity:Context, val data: List<Admin>, val listener: OnItemClickListener) : RecyclerView.Adapter<AdapterAdmin.ViewHolder>(){
var sharedPrefManager=SharedPrefManager(activity)

    var constant= Constants()

    interface OnItemClickListener {
        fun onAdminupdateclick(modelUser: Admin)
        fun onAdminDeleteClick(modelUser: Admin)
        fun onAdminitemclick(modelUser: Admin)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(data[position]) }
    override fun getItemCount(): Int { return data.size }
    inner class ViewHolder(val itemBinding: ItemUserBinding) : RecyclerView.ViewHolder(itemBinding.root){

        fun bind(modelUser: Admin) {

                itemBinding.view.visibility=View.GONE

            itemBinding.userName.text=modelUser.name
            itemBinding.removeUser.setOnClickListener{ listener.onAdminDeleteClick(modelUser)}
            itemBinding.updateUser.setOnClickListener{ listener.onAdminupdateclick(modelUser)}
            itemBinding.containeruser.setOnClickListener{ listener.onAdminitemclick(modelUser)}
            val dateTimeFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val formattedDateTime = dateTimeFormat.format(modelUser.createdAt .toDate()) // Assuming timestamp is a Firebase Timestamp
            itemBinding.uploadedAt.text = formattedDateTime
        }

    }

}