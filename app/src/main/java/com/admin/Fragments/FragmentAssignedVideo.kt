package com.admin.Fragments

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import com.urduboltv.admin.databinding.FragmentAssignedVideoBinding
import com.urduboltv.admin.databinding.FragmentUnAssignedVideoBinding
import com.urduboltv.admin.databinding.FragmentUserBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FragmentAssignedVideo : Fragment(),AdapterManageVideo.OnItemClickListener {
    private var _binding: FragmentAssignedVideoBinding? = null

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
        fun newInstance(modelUser: ModelUser): FragmentAssignedVideo {
            val fragment = FragmentAssignedVideo()
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
        _binding = FragmentAssignedVideoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        listaccessVideos = ArrayList()

        mContext = requireContext()
        utils = Utils(mContext)
        modelVideoManagment= ModelVideoManagment()
        user=ModelUser()
        val userModelString: String? = arguments?.getString("user")
        user = userModelString?.let { ModelUser.fromString(it) } ?: ModelUser()





        // Initialize the adapter with an empty list
        adapter = AdapterManageVideo("Assigned",mContext, emptyList(), this@FragmentAssignedVideo)

        binding.rvvideos.layoutManager =LinearLayoutManager(mContext)
        binding.rvvideos.adapter = adapter

        setAdapter()

        return  root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onItemClick(modelVideo: ModelVideo) {
        lifecycleScope.launch {
            val userIdToRemove = user.userId // Replace this with the actual user ID you want to match

            // Remove the userId from the ModelVideo object
            val updatedModelVideo = removeUserIdFromModelVideo(modelVideo, userIdToRemove)

            // Call the update method with the modified ModelVideo object
            videoViewModel.UpdateVideo(updatedModelVideo).observe(this@FragmentAssignedVideo) { task ->
                if (task) {
                    // Update was successful
                    setAdapter()
                    Toast.makeText(mContext, "Video Unassigned Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle update failure
                    Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

// Function to remove a user ID from the users_Id list in ModelVideo
@RequiresApi(Build.VERSION_CODES.N)
fun removeUserIdFromModelVideo(modelVideo: ModelVideo, userIdToRemove: String): ModelVideo {
    val updatedUserIds = modelVideo.users_Id.toMutableList()
    updatedUserIds.removeIf { it == userIdToRemove }

    // Create a copy of the ModelVideo object with the updated users_Id list
    return modelVideo.copy(users_Id = updatedUserIds)
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