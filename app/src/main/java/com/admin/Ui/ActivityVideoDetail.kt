package com.admin.Ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.SyncStateContract
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.Adapter.AdapterSeason
import com.admin.Adapter.VideoAdapter
import com.admin.Constants
import com.admin.Data.SharedPrefManager
import com.admin.Models.ModelDrama
import com.admin.Models.ModelSeason
import com.admin.Models.ModelUser
import com.admin.Models.ModelVideo
import com.admin.Models.VideoViewModel
import com.admin.Utils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.ActivitySeasonBinding
import com.urduboltv.admin.databinding.ActivityVideoDetailBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ActivityVideoDetail : AppCompatActivity(), VideoAdapter.OnItemClickListener {

    private val videoViewModel: VideoViewModel by viewModels()
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private lateinit var adapter: AdapterSeason
    private lateinit var modelVideo: ModelVideo
    private lateinit var modelSeason: ModelSeason

    private lateinit var utils: Utils
    private lateinit var constants: Constants
    private lateinit var mContext: Context
    private lateinit var list:ArrayList<ModelVideo>
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var videoUri:String
    private lateinit var binding: ActivityVideoDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this@ActivityVideoDetail
        utils = Utils(mContext)
        modelSeason= ModelSeason()
        modelVideo = ModelVideo()
        sharedPrefManager = SharedPrefManager(mContext)
        constants = Constants()

videoUri= ""
        list=ArrayList()

        val dramaJson = intent.getStringExtra("season")
        if (dramaJson != null) {
            modelSeason = ModelSeason.fromString(dramaJson) !!
        }


        list=sharedPrefManager.getVideoList()
        setAdapter()

        binding.floatingaction.setOnClickListener {
            val intent = Intent(mContext, ActivityAddVideo::class.java)
            intent.putExtra("season", modelSeason.toString()) // Serialize to JSON
            mContext.startActivity(intent)
        }


    }




    override fun onItemClick(modelVideo: ModelVideo) {
        val intent = Intent(mContext, ActivityPlayer::class.java)
        intent.putExtra("video", modelVideo.toString()) // Serialize to JSON
        mContext.startActivity(intent)
    }

    override fun onDeleteClick(modelVideo: ModelVideo) {

    }
    fun setAdapter() {
        utils.startLoadingAnimation()
        val list = ArrayList<ModelVideo>()

        lifecycleScope.launch {
            try {
                val taskResult = videoViewModel.getVideoList(modelSeason.docId).await()

                if (taskResult != null) {
                    // Check if the task result is not null, indicating a successful result
                    if (taskResult.size() > 0) {
                        for (document in taskResult) {
                            list.add(document.toObject(ModelVideo::class.java))
                        }

                        // Sort the list by episode number in ascending order
                        val sortedList = list.sortedBy { it.episodeno?.toIntOrNull() ?: 0 }

                        binding.rvvideos.layoutManager = GridLayoutManager(mContext, 2)
                        binding.rvvideos.adapter = VideoAdapter(mContext, sortedList, this@ActivityVideoDetail)
                    } else {
                        Toast.makeText(mContext, "Nothing to show,Add Video!!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(mContext, e.message ?: "An error occurred", Toast.LENGTH_SHORT).show()
            } finally {
                utils.endLoadingAnimation()
            }
        }
    }



}