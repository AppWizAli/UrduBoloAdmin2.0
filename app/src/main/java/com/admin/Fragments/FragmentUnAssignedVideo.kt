package com.admin.Fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.Adapter.AdapterManageVideo
import com.admin.Adapter.AdapterUser
import com.admin.Constants
import com.admin.Data.SharedPrefManager
import com.admin.Models.ModelUser
import com.admin.Models.ModelVideo
import com.admin.Models.ModelVideoManagment
import com.admin.Models.UserViewModel
import com.admin.Models.VideoViewModel
import com.admin.Utils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.FragmentUnAssignedVideoBinding
import com.urduboltv.admin.databinding.FragmentUserBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FragmentUnAssignedVideo : Fragment(),AdapterManageVideo.OnItemClickListener {
    private var _binding: FragmentUnAssignedVideoBinding? = null

    private val videoViewModel: VideoViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val db = Firebase.firestore


    private lateinit var adapter: AdapterManageVideo

    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var user: ModelUser
    private lateinit var modelVideoManagment: ModelVideoManagment
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var dialog: Dialog
    lateinit var listaccessVideos: ArrayList<ModelVideo>
    private val binding get() = _binding!!
    companion object {
        fun newInstance(modelUser: ModelUser): FragmentUnAssignedVideo {
            val fragment = FragmentUnAssignedVideo()
            val args = Bundle()
            args.putString("user", modelUser.toString()) // Pass modelUser as a String
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUnAssignedVideoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        listaccessVideos = ArrayList()

        mContext = requireContext()
        utils = Utils(mContext)
        modelVideoManagment= ModelVideoManagment()
        user=ModelUser()
        val userModelString: String? = arguments?.getString("user")
        user = userModelString?.let { ModelUser.fromString(it) } ?: ModelUser()





        // Initialize the adapter with an empty list
        adapter = AdapterManageVideo("UnAssigned", mContext, emptyList(), this@FragmentUnAssignedVideo)

        binding.rvvideos.layoutManager = LinearLayoutManager(mContext)
        binding.rvvideos.adapter = adapter

        setAdapter()


        return  root
    }

    override fun onItemClick(modelVideo: ModelVideo) {
        modelVideo.users_Id= listOf(user.userId)
        lifecycleScope.launch {

            videoViewModel.UpdateVideo(modelVideo)
                .observe(this@FragmentUnAssignedVideo)
                {
                        task->
                    if(task)
                    {
                        setAdapter()
                        Toast.makeText(mContext, "Video Assigned Successfully", Toast.LENGTH_SHORT).show()

                    }
                    else
                    {

                        Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    override fun onDeleteClick(modelVideo: ModelVideo) {
        // Handle delete click if needed
    }

    private fun setAdapter() {
        utils.startLoadingAnimation()
        lifecycleScope.launch {
            try {
                val taskResult = videoViewModel.getUnAssignedPrivateVideo(user.userId).await()

                if (taskResult != null) {
                    val videoList = taskResult.mapNotNull { documentSnapshot ->
                        documentSnapshot.toObject(ModelVideo::class.java)
                    }.filter { modelVideo ->
                        user.userId !in modelVideo.users_Id
                    }

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