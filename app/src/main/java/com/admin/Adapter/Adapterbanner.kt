package com.admin.Adapter

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.admin.Models.Admin
import com.bumptech.glide.Glide
import com.urduboltv.admin.R
class Adapterbanner(private val context: Context, private val imageUriList: List<Uri>, private val recyclerView: RecyclerView, val listener: Adapterbanner.OnItemClickListener,) :
    RecyclerView.Adapter<Adapterbanner.ImageViewHolder>() {
    interface OnItemClickListener {
        fun onDeleteClick(modelUser: String)

    }
    private var currentItemPosition = 0
    val handler = Handler()

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imgBanner)
        val delete: ImageView = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_videoo, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = imageUriList[position]

        Glide.with(context)
            .load(imageUri)
            .into(holder.imageView)
holder.delete.setOnClickListener()
{
listener.onDeleteClick("modelUser")
}
        holder.imageView.visibility = if (position == currentItemPosition) View.VISIBLE else View.GONE
    }

    private fun scrollToNext() {
        currentItemPosition = (currentItemPosition + 1) % imageUriList.size
        recyclerView.smoothScrollToPosition(currentItemPosition)
        handler.postDelayed({ notifyDataSetChanged() }, 100) // Update the visibility after scrolling
    }

    override fun getItemCount(): Int {
        return imageUriList.size
    }

    init {
        val runnable = object : Runnable {
            override fun run() {
                scrollToNext()
                handler.postDelayed(this, 7000) // Delay before scrolling to next image
            }
        }
        handler.postDelayed(runnable, 7000) // Initial delay before starting auto-scroll
    }
}

