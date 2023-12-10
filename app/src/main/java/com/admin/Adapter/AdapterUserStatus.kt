package com.admin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.Constants
import com.admin.Models.ModelUser
import com.admin.Models.ModelVideo
import com.admin.Ui.ActivityManageVideo
import com.bumptech.glide.Glide
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.ItemUserStatusBinding
import com.urduboltv.admin.databinding.ItemVideoBinding
import com.urduboltv.admin.databinding.ItemVideoManagementBinding

class AdapterUserStatus(
    var from:String,
    var activity: Context,
    var data: List<ModelUser>,
    val listener: OnItemClickListener
) : RecyclerView.Adapter<AdapterUserStatus.ViewHolder>() {

    interface OnItemClickListener {
        fun onAssignUserClick(modelVideo: ModelUser)
        fun onUnAssignUserClcik(modelVideo: ModelUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUserStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateList(newList: List<ModelUser>) {
        data = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(val itemBinding: ItemUserStatusBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(modelVideo: ModelUser) {
           /* if(from.equals("Assigned"))
            {
                itemBinding.btnAssign.text="UnAssign"
            }*/
            itemBinding.name.text = modelVideo.name
            itemBinding.email.text = modelVideo.email
            itemBinding.assign.setOnClickListener {
                listener.onAssignUserClick(modelVideo)
            }
        }
    }
}
