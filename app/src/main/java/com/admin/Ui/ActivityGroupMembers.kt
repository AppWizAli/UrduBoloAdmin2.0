package com.admin.Ui

import android.app.Dialog
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.Adapter.AdapterManageVideo
import com.admin.Adapter.AdapterSeason
import com.admin.Constants
import com.admin.Data.SharedPrefManager
import com.admin.Models.DramaViewModel
import com.admin.Models.ModelDrama
import com.admin.Models.ModelGroup
import com.admin.Models.ModelSeason
import com.admin.Models.ModelUser
import com.admin.Models.ModelVideo
import com.admin.Models.SeasonViewModel
import com.admin.Models.VideoViewModel
import com.admin.Utils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.ActivityGroupMembersBinding
import com.urduboltv.admin.databinding.ActivitySeasonBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ActivityGroupMembers : AppCompatActivity(),AdapterManageVideo.OnItemClickListener {
    private val videoViewModel: VideoViewModel by viewModels()
    private val dramaViewModel: DramaViewModel by viewModels()
    private val seasonViewModel: SeasonViewModel by viewModels()
    private val db = Firebase.firestore

    private val IMAGE_PICKER_REQUEST_CODE = 200

    private lateinit var modelSeason: ModelSeason
    private var imageURI: Uri? = null

    private lateinit var adapter: AdapterManageVideo

    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var user: ModelUser
    private lateinit var modelGroup: ModelGroup
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var dialog: Dialog
    private  lateinit var binding: ActivityGroupMembersBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGroupMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        constants=Constants()
        modelSeason= ModelSeason()
        modelGroup= ModelGroup()
        mContext=this@ActivityGroupMembers
        sharedPrefManager=SharedPrefManager(mContext)
        utils=Utils(mContext)

        val dramaJson = intent.getStringExtra("group")
        if (dramaJson != null) {
            modelGroup = ModelGroup.fromString(dramaJson) !!
        }


        adapter = AdapterManageVideo("Assigned",mContext, emptyList(), this@ActivityGroupMembers)

        binding.rvvideos.layoutManager = LinearLayoutManager(mContext)
        binding.rvvideos.adapter = adapter

        setAdapter()


    }
    override fun onItemClick(modelVideo: ModelVideo) {

    }

    override fun onDeleteClick(modelVideo: ModelVideo) {
        // Handle delete click if needed
    }

    private fun setAdapter() {
        utils.startLoadingAnimation()

        lifecycleScope.launch {
            try {
                val taskResult = videoViewModel.getAssignedVideo(user.userId).await()

                if (taskResult != null) {
                    val videoList = taskResult.map { documentSnapshot ->
                        documentSnapshot.toObject(ModelVideo::class.java)
                    }.filterNotNull()
                    val sortedList = videoList.sortedBy { it.episodeno?.toIntOrNull() ?: 0 }
                    // Update the adapter with the new videoList
                    adapter.updateList(sortedList)
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
}