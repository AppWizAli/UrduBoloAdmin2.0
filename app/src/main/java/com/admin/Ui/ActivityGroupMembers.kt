package com.admin.Ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.experimental.UseExperimental
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.admin.Adapter.AdapterAssignedVideo
import com.admin.Adapter.AdapterManageVideo
import com.admin.Adapter.AdapterSeason
import com.admin.Adapter.AdapterUserStatus
import com.admin.Constants
import com.admin.Data.SharedPrefManager
import com.admin.Models.DramaViewModel
import com.admin.Models.ModelDrama
import com.admin.Models.ModelGroup
import com.admin.Models.ModelSeason
import com.admin.Models.ModelUser
import com.admin.Models.ModelVideo
import com.admin.Models.ModelVideoManagment
import com.admin.Models.SeasonViewModel
import com.admin.Models.UserViewModel
import com.admin.Models.VideoViewModel
import com.admin.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.ActivityGroupMembersBinding
import com.urduboltv.admin.databinding.ActivitySeasonBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class ActivityGroupMembers : AppCompatActivity(),AdapterManageVideo.OnItemClickListener,AdapterUserStatus.OnItemClickListener,AdapterAssignedVideo.OnItemClickListener {
    private val videoViewModel: VideoViewModel by viewModels()
    private val dramaViewModel: DramaViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val seasonViewModel: SeasonViewModel by viewModels()
    private val db = Firebase.firestore
    val videoDocumentList = mutableListOf<ModelVideo>() // Create an empty list to store documents
    var userNotInGroup = emptyList<ModelUser>()
    private val IMAGE_PICKER_REQUEST_CODE = 200

    private lateinit var modelSeason: ModelSeason
    private var imageURI: Uri? = null

    private lateinit var adapter: AdapterAssignedVideo
    private lateinit var adapterUser: AdapterUserStatus

    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var user: ModelUser
    private lateinit var modelGroup: ModelGroup
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var dialog: Dialog
    private  lateinit var binding: ActivityGroupMembersBinding
    private lateinit var videoList:List<ModelVideo>


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
videoList= emptyList()
        val dramaJson = intent.getStringExtra("group")
        if (dramaJson != null) {
            modelGroup = ModelGroup.fromString(dramaJson) !!
        }


        adapter = AdapterAssignedVideo("Assigned",mContext, emptyList(), this@ActivityGroupMembers)
        adapterUser = AdapterUserStatus("Assigned",mContext, emptyList(), this@ActivityGroupMembers)

        binding.rvvideos.layoutManager = LinearLayoutManager(mContext)
        binding.rvvideos.adapter = adapter
binding.floatingaction.setOnClickListener()
{

  showDialogAdd()
}
        setAdapter()
        binding.tvSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText)
                return false
            }
        })


    }




    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist = ArrayList<ModelVideo>()

            for (user in videoDocumentList) {
                // checking if the entered string matched with any item of our recycler view.
                if (user.episodeno.toLowerCase().contains(text.lowercase(Locale.getDefault()))) {
                    // if the item is matched we are
                    // adding it to our filtered list.
                    filteredlist.add(user)
                }
            }

            if (filteredlist.isEmpty()) {
                // if no item is added in filtered list we are
                // displaying a toast message as no data found.
                Toast.makeText(mContext, "No Data Found..", Toast.LENGTH_SHORT).show()

            } else {

                // at last we are passing that filtered
                // list to our adapter class.


adapter.updateList(filteredlist)


            }

        // running a for loop to compare elements.

    }
    private fun filterUser(text: String) {
        // creating a new array list to filter our data.
        val filteredlist = ArrayList<ModelUser>()

        for (user in userNotInGroup) {
            // checking if the entered string matched with any item of our recycler view.
            if (user.name.toLowerCase().contains(text.lowercase(Locale.getDefault()))) {
                // if the item is matched we are adding it to our filtered list.
                filteredlist.add(user)
            }
        }

        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are displaying a toast message as no data found.
            Toast.makeText(mContext, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered list to our adapter class.
            adapterUser.updateList(filteredlist)
        }
        // running a for loop to compare elements.
    }



    private fun showDialogAdd() {
        val builder = AlertDialog.Builder(mContext)

        builder.setTitle("Select Option")
            .setPositiveButton("Add Member") { dialog, which ->
                showDialogAddMember()
            }
            .setNegativeButton("Assign Video") { dialog, which ->
             showdialogAssignVideo()

            }


        val dialog = builder.create()
        dialog.show()
    }
    private fun showdialogAssignVideo() {
        var unassignedVideoList = mutableListOf<ModelVideo>()
        val bottomdialog = BottomSheetDialog(mContext)
        val bottomSheet = layoutInflater.inflate(R.layout.dialog_bottom, null)
        bottomdialog.setContentView(bottomSheet)

        var rv = bottomdialog.findViewById<RecyclerView>(R.id.recyclerViewCustomBottomSheet)
        var search = bottomdialog.findViewById<SearchView>(R.id.tvSearchView)!!
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                val filteredlist = ArrayList<ModelVideo>()

                for (user in unassignedVideoList) {
                    // checking if the entered string matched with any item of our recycler view.
                    if (user.episodeno.toLowerCase().contains(newText.lowercase(Locale.getDefault()))) {
                        // if the item is matched we are adding it to our filtered list.
                        filteredlist.add(user)
                    }
                }

                if (filteredlist.isEmpty()) {
                    // if no item is added in filtered list we are displaying a toast message as no data found.
                    Toast.makeText(mContext, "No Data Found..", Toast.LENGTH_SHORT).show()
                } else {
                    rv?.adapter = AdapterManageVideo("Unassigned", mContext, filteredlist, this@ActivityGroupMembers)

                }
                // running a for loop to compare elements.
                return false
            }
        })



        var list= sharedPrefManager.getPrivateVideoList()

            for (modelVideo in list) {
                if (!videoList.any { it.docId.contains(modelVideo.docId) }) {
                 unassignedVideoList.add(modelVideo)
                }
            }


        rv?.layoutManager = LinearLayoutManager(mContext)
        rv?.adapter = AdapterManageVideo("Unassigned", mContext, unassignedVideoList.sortedBy { it.episodeno.toInt() }, this@ActivityGroupMembers)


        bottomdialog.show()
    }


    private fun showDialogAddMember() {
        val bottomdialog = BottomSheetDialog(mContext)
        val bottomSheet = layoutInflater.inflate(R.layout.dialog_bottom, null)
        bottomdialog.setContentView(bottomSheet)

        var rv = bottomdialog.findViewById<RecyclerView>(R.id.recyclerViewCustomBottomSheet)
        var search = bottomdialog.findViewById<SearchView>(R.id.tvSearchView)!!
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                val filteredlist = ArrayList<ModelUser>()

                for (user in userNotInGroup) {
                    // checking if the entered string matched with any item of our recycler view.
                    if (user.name.toLowerCase().contains(newText.lowercase(Locale.getDefault()))) {
                        // if the item is matched we are adding it to our filtered list.
                        filteredlist.add(user)
                    }
                }

                if (filteredlist.isEmpty()) {
                    // if no item is added in filtered list we are displaying a toast message as no data found.
                    Toast.makeText(mContext, "No Data Found..", Toast.LENGTH_SHORT).show()
                } else {
                    // at last we are passing that filtered list to our adapter class.
                    rv?.adapter = AdapterUserStatus("UnAssigned", mContext, filteredlist, this@ActivityGroupMembers)

                }
                // running a for loop to compare elements.
                return false
            }
        })

        // Start animation
        utils.startLoadingAnimation()

        lifecycleScope.launch {

            userViewModel.getGroupMember(modelGroup.doc_Id).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentSnapshot = task.result
                    val groupMembersList = documentSnapshot.toObject(ModelGroup::class.java)?.users ?: emptyList()

                    val userList = sharedPrefManager.getUserList()

                    userNotInGroup = userList.filterNot { user ->
                        groupMembersList.contains(user.userId)
                    }

                    rv?.layoutManager = LinearLayoutManager(mContext)
                    rv?.adapter = AdapterUserStatus("UnAssigned", mContext, userNotInGroup.sortedBy { it.name }, this@ActivityGroupMembers)

                    // End animation
                    utils.endLoadingAnimation()
                } else {
                    // Handle failure or exceptions here
                    // End animation in case of failure
                    utils.endLoadingAnimation()
                }
            }
        }


        bottomdialog.show()
    }




    override fun onItemClick(modelVideo: ModelVideo) {

        var modelVideoManagment=ModelVideoManagment()
        modelVideoManagment.group_Id=modelGroup.doc_Id
        modelVideoManagment.video_Id=modelVideo.docId
        modelVideoManagment.seasonId=modelVideo.seasonId

lifecycleScope.launch {
    videoViewModel.assignPrivateVidoes(modelVideoManagment).observe(this@ActivityGroupMembers)
    {
        task->
        if(task)
        {
            Toast.makeText(mContext, "Video Assigned", Toast.LENGTH_SHORT).show()
            setAdapter()
        }
        else
        {
            Toast.makeText(mContext, "Video Assigned Failed ", Toast.LENGTH_SHORT).show()
        }
    }
}
    }

    override fun onUnAssignClick(modelVideo: ModelVideo) {
        Toast.makeText(mContext, modelVideo.videoId, Toast.LENGTH_SHORT).show()
        Toast.makeText(mContext, modelGroup.doc_Id, Toast.LENGTH_SHORT).show()
     lifecycleScope.launch {
         userViewModel.SetUnassignVideo(modelVideo.docId,modelGroup.doc_Id).observe(this@ActivityGroupMembers)
         {
             task->
             if(task)
             {
                 Toast.makeText(mContext, "Video Unassigned Successfully", Toast.LENGTH_SHORT).show()
                 setAdapter()
             }
             else
             {
                 Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE
                     , Toast.LENGTH_SHORT).show()
             }
         }
     }
    }

    override fun onDeleteClick(modelVideo: ModelVideo) {
        // Handle delete click if needed
    }
    private fun setAdapter() {
utils.startLoadingAnimation()
        val videoIdList = mutableListOf<String>()

        lifecycleScope.launch {
            try {
                val taskResult = videoViewModel.getAssignedVideoList(modelGroup.doc_Id).await()

                if (taskResult != null) {
                    val videoList = taskResult.mapNotNull { documentSnapshot ->
                        val video = documentSnapshot.toObject(ModelVideoManagment::class.java)
                        video?.let {
                            videoIdList.add(video.video_Id) // Add video_Id to the list
                            it
                        }
                    }

                } else {
                    Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(mContext, e.message ?: "An error occurred", Toast.LENGTH_SHORT).show()
            } finally {

            }

if(videoIdList.isNotEmpty())
{

            videoViewModel.getAssignedVideoList(videoIdList)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        utils.endLoadingAnimation()
                        val documents = task.result
                        if (documents != null && !documents.isEmpty) {

                            for (document in documents) {
                                val videoData = document.toObject(ModelVideo::class.java)
                                videoDocumentList.add(videoData) // Add each document to the list
                            }
                            binding.tvnothing.visibility=View.GONE
adapter.updateList(videoDocumentList.sortedBy { it.episodeno.toInt() })

                        } else {

                        }
                    } else {
                        utils.endLoadingAnimation()
                        val exception = task.exception

                    }
                }
}
            else
{
    utils.endLoadingAnimation()
    adapter.updateList(emptyList())

    binding.tvnothing.visibility=View.VISIBLE
}

        }

    }

/*    private fun setAdapter() {
        utils.startLoadingAnimation()
val list=modelGroup.users
        lifecycleScope.launch {
            try {
                val taskResult = videoViewModel.getAssignedVideoList(modelGroup.doc_Id).await()

                if (taskResult != null) {
                     videoList = taskResult.map { documentSnapshot ->
                        documentSnapshot.toObject(ModelVideo::class.java)
                    }.filterNotNull()
                    val sortedList = videoList.sortedBy { it.episodeno?.toIntOrNull() ?: 0 }
                    // Update the adapter with the new videoList
                    if(videoList.isEmpty())
                    {
                        binding.tvnothing.visibility= View.VISIBLE
                    }
                    else
                    {
                        binding.tvnothing.visibility= View.GONE
                        adapter.updateList(sortedList)

                    }
                } else {
                    Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(mContext, e.message ?: "An error occurred", Toast.LENGTH_SHORT).show()
            } finally {
                utils.endLoadingAnimation()
            }
        }
    }*/

    override fun onAssignUserClick(modelVideo: ModelUser) {
        val newUsersList = modelGroup.users.toMutableList() // Convert the existing list to a mutable list
        newUsersList.add(modelVideo.userId) // Add the new user ID to the mutable list

        modelGroup.users = newUsersList.toList()

        lifecycleScope.launch {
    userViewModel.updateGroup(modelGroup).observe(this@ActivityGroupMembers)
    {
        task->
        if(task)
        {
            Toast.makeText(mContext, "User has been added in group Successfully!! ", Toast.LENGTH_SHORT).show()
        }
        else
            Toast.makeText(mContext, "Not Assign", Toast.LENGTH_SHORT).show()
    }

}
    }

    override fun onUnAssignUserClcik(modelVideo: ModelUser) {

    }
}