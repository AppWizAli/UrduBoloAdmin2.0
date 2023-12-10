package com.admin.Ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.admin.Adapter.AdapterUser
import com.admin.Adapter.UserViewPagerAdapter
import com.admin.Adapter.VideoViewPagerAdapter
import com.admin.Constants
import com.admin.Data.SharedPrefManager
import com.admin.Models.ModelUser
import com.admin.Models.UserViewModel
import com.admin.Models.VideoViewModel
import com.admin.Utils
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.ActivityManageVideoBinding
import com.urduboltv.admin.databinding.FragmentUserBinding

class ActivityManageVideo : AppCompatActivity(){
private lateinit var binding:ActivityManageVideoBinding

    private val db = Firebase.firestore


    private lateinit var adapter: AdapterUser

    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var user: ModelUser
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityManageVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@ActivityManageVideo
        utils = Utils(mContext)
        constants=Constants()
        sharedPrefManager=SharedPrefManager(mContext)

        val dramaJson = intent.getStringExtra("user")
        if (dramaJson != null) {
            user = ModelUser.fromString(dramaJson) !!
        }

        setupViewPager()
        setupTabLayout()


    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            if(position==0) tab.text ="Assigned"
            else if(position==1) tab.text="UnAssigned"
        }.attach()
    }

    private fun setupViewPager() {
        val adapter = VideoViewPagerAdapter(this@ActivityManageVideo, 2, user)
        binding.viewPager.adapter = adapter
    }



}


/*class ActivityManageVideo : AppCompatActivity(), AdapterManageVideo.OnItemClickListener {

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



          *//*  // Now, `listaccessVideos` contains the selected items
            Toast.makeText(mContext, "Selected videos: ${listaccessVideos.size}", Toast.LENGTH_SHORT).show()*//*
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





    private fun AddVideoManagement()
    {
        utils.startLoadingAnimation()
        if(listaccessVideos.isEmpty())
        {
            utils.endLoadingAnimation()
            Toast.makeText(mContext, "Please Select Any Video", Toast.LENGTH_SHORT).show()
        }
        else
        {


            var list= ArrayList<ModelVideoManagment>()

            for (list1 in listaccessVideos)
            {
               modelVideoManagment.videoId=list1.docId
                modelVideoManagment.userid=user.userId
                modelVideoManagment.seasonId=list1.seasonId
                modelVideoManagment.status="Assigned"
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
}*/
