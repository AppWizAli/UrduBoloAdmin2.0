package com.admin.Ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.Adapter.AdapterManageVideo
import com.admin.Adapter.AdapterSeason
import com.admin.Adapter.VideoAdapter
import com.admin.Constants
import com.admin.Data.SharedPrefManager
import com.admin.Models.ModelDrama
import com.admin.Models.ModelSeason
import com.admin.Models.ModelUser
import com.admin.Models.ModelVideo
import com.admin.Models.ModelVideoManagment
import com.admin.Models.VideoViewModel
import com.admin.Utils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.ActivityManageVideoBinding
import com.urduboltv.admin.databinding.ActivityVideoDetailBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
/*

class ActivityManageVideo : AppCompatActivity(), AdapterManageVideo.OnItemClickListener {

    private val videoViewModel: VideoViewModel by viewModels()
    private val db = Firebase.firestore


    private lateinit var adapter: AdapterSeason

    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var user: ModelUser
    private lateinit var listaccessVideos:ArrayList<ModelVideo>
    private lateinit var sharedPrefManager : SharedPrefManager
    private lateinit var dialog : Dialog
    private  lateinit var binding: ActivityManageVideoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityManageVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)


listaccessVideos= ArrayList()

        mContext=this@ActivityManageVideo
        utils= Utils(mContext)
setAdapter()
binding.next.setOnClickListener {
    Toast.makeText(mContext, "Available soon!!", Toast.LENGTH_SHORT).show()
}
    }

    override fun onItemClick(modelVideo: ModelVideo) {
        listaccessVideos.add(modelVideo)
    }

    override fun onDeleteClick(modelVideo: ModelVideo) {

    }

    fun setAdapter() {
        utils.startLoadingAnimation()
        val list = ArrayList<ModelVideo>()

        lifecycleScope.launch {
            try {
                val taskResult = videoViewModel.getPrivateVideoList().await()

                if (taskResult != null) {
                    // Check if the task result is not null, indicating a successful result
                    if (taskResult.size() > 0) {
                        for (document in taskResult) {
                            list.add(document.toObject(ModelVideo::class.java))
                        }
                    }

                    binding.rvvideos.layoutManager = LinearLayoutManager(mContext)
                    binding.rvvideos.adapter = AdapterManageVideo(mContext, list, this@ActivityManageVideo)
                } else {
                    Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(mContext, e.message ?: "An error occurred", Toast.LENGTH_SHORT).show()
            } finally {
                utils.endLoadingAnimation()
            }
        }
    }
}*/




class ActivityManageVideo : AppCompatActivity(), AdapterManageVideo.OnItemClickListener {

    private val videoViewModel: VideoViewModel by viewModels()
    private val db = Firebase.firestore

    private lateinit var adapter: AdapterManageVideo
    private lateinit var modelVideoManagment: ModelVideoManagment
    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var user: ModelUser
    lateinit var listaccessVideos: ArrayList<ModelVideo>
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var dialog: Dialog
    private lateinit var binding: ActivityManageVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listaccessVideos = ArrayList()

        mContext = this@ActivityManageVideo
        utils = Utils(mContext)
modelVideoManagment= ModelVideoManagment()
user=ModelUser()

        val dramaJson = intent.getStringExtra("user")
        if (dramaJson != null) {
            user = ModelUser.fromString(dramaJson) !!
        }



        // Initialize the adapter with an empty list
        adapter = AdapterManageVideo(mContext, emptyList(), this@ActivityManageVideo)
        binding.rvvideos.layoutManager = GridLayoutManager(mContext,2)
        binding.rvvideos.adapter = adapter

        setAdapter()

        binding.next.setOnClickListener {
            AddVideoManagement()



          /*  // Now, `listaccessVideos` contains the selected items
            Toast.makeText(mContext, "Selected videos: ${listaccessVideos.size}", Toast.LENGTH_SHORT).show()*/
        }
    }

    override fun onItemClick(modelVideo: ModelVideo) {
        // Toggle the selection state of the video
        if (listaccessVideos.contains(modelVideo)) {
            listaccessVideos.remove(modelVideo)
        } else {
            listaccessVideos.add(modelVideo)
        }
    }

    override fun onDeleteClick(modelVideo: ModelVideo) {
        // Handle delete click if needed
    }

    private fun setAdapter() {
        utils.startLoadingAnimation()

        lifecycleScope.launch {
            try {
                val taskResult = videoViewModel.getPrivateVideoList().await()

                if (taskResult != null) {
                    val videoList = taskResult.map { documentSnapshot ->
                        documentSnapshot.toObject(ModelVideo::class.java)
                    }.filterNotNull()

                    // Update the adapter with the new videoList
                    adapter.updateList(videoList)
                } else {
                    Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(mContext, e.message ?: "An error occurred", Toast.LENGTH_SHORT).show()
            } finally {
                utils.endLoadingAnimation()
            }
        }
    }





    private fun AddVideoManagement()
    {
        utils.startLoadingAnimation()
        if(listaccessVideos.isEmpty())
        {
            utils.endLoadingAnimation()
            Toast.makeText(mContext, "Please Select ANy Video", Toast.LENGTH_SHORT).show()
        }
        else
        {


            var list= ArrayList<ModelVideoManagment>()

            for (list1 in listaccessVideos)
            {
               modelVideoManagment.videoId=list1.docId
                modelVideoManagment.userid=user.userId
                modelVideoManagment.seasonId=list1.seasonId
                list.add(modelVideoManagment)
            }

            lifecycleScope.launch {
                videoViewModel.assignPrivateVidoes(list).observe(this@ActivityManageVideo) { success ->
                    if (success) {
                        // Handle success
                        utils.endLoadingAnimation()
                        finish()
                        Toast.makeText(mContext, "Videos assigned successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        utils.endLoadingAnimation()
                        finish()
                        // Handle failure
                        Toast.makeText(mContext, "Failed to assign videos", Toast.LENGTH_SHORT).show()
                    }
                }
            }



        }
    }
}
