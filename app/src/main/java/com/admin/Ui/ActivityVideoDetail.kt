package com.admin.Ui

import android.app.Activity
import android.app.AlertDialog
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
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleOwner
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

    override fun onLongClick(modelVideo: ModelVideo): Boolean {
        val options = arrayOf("Edit", "Delete")

        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Choose Action")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog(modelVideo) // Edit option selected
                    1 -> performDeleteAction(modelVideo) // Delete option selected
                }
            }

        val dialog = builder.create()
        dialog.show()

        return true // Return true to indicate the long click is handled
    }

    private fun showEditDialog(modelVideo: ModelVideo) {
        val dialog = Dialog(mContext, R.style.FullWidthDialog)
        dialog.setContentView(R.layout.dialog_video)

        val episodeNumberEditText = dialog.findViewById<EditText>(R.id.episodeNumber)
        val videoLinkEditText = dialog.findViewById<EditText>(R.id.videolink)
        val descriptionEditText = dialog.findViewById<EditText>(R.id.editTextDescription)
        val privacyRadioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroupPrivacy)
        val downloadAccessRadioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroupDownloadAccess)
        val uploadThumbnailButton = dialog.findViewById<Button>(R.id.btnUploadThumbnail)
        val nextButton = dialog.findViewById<Button>(R.id.btnNext)
        val backButton = dialog.findViewById<ImageView>(R.id.back)

        dialog.setCancelable(false)
        uploadThumbnailButton.setBackgroundColor(Color.parseColor("#FEC10F"))
        nextButton.setBackgroundColor(Color.parseColor("#FEC10F"))

        // Set details of modelVideo to the EditText fields in the dialog
        episodeNumberEditText.setText(modelVideo.episodeno)
        videoLinkEditText.setText(modelVideo.videourl)
        descriptionEditText.setText(modelVideo.description)

        backButton.setOnClickListener {
            dialog.dismiss()
        }
        // Set the privacy and download access based on the clicked ModelVideo
        val privacyRadioButton = if (modelVideo.privacy == constants.VIDEO_PRIVACY_PRIVATE) {
            dialog.findViewById<RadioButton>(R.id.radioPrivate)
        } else {
            dialog.findViewById<RadioButton>(R.id.radioPublic)
        }
        privacyRadioButton.isChecked = true
        val downloadAccessRadioButton = when (modelVideo.downloadType) {
            constants.VIDEO_DOWNLOAD_GALLERY -> dialog.findViewById<RadioButton>(R.id.radioGallery)
            constants.VIDEO_DOWNLOAD_APPP -> dialog.findViewById<RadioButton>(R.id.radioAppStorage)
            constants.VIDO_DOWNLOAD_BOTH -> dialog.findViewById<RadioButton>(R.id.radioBoth)
            else -> dialog.findViewById<RadioButton>(R.id.radioNever)
        }
        downloadAccessRadioButton.isChecked = true

        // Handle click on nextButton
        nextButton.setOnClickListener {
            // Update the modelVideo object with edited details from the dialog
            modelVideo.episodeno = episodeNumberEditText.text.toString()
            modelVideo.videourl = videoLinkEditText.text.toString()
            modelVideo.description = descriptionEditText.text.toString()

            // Update privacy and download access based on the selected radio buttons
            modelVideo.privacy = (privacyRadioGroup.checkedRadioButtonId == R.id.radioPublic).toString()
            // Get selected download access from radio group
            val selectedDownloadAccessRadioButton =
                dialog.findViewById<RadioButton>(downloadAccessRadioGroup.checkedRadioButtonId)
            modelVideo.downloadType = selectedDownloadAccessRadioButton.text.toString()
            
            lifecycleScope.launch { 
                
       
videoViewModel.UpdateVideo(modelVideo).observe(this@ActivityVideoDetail)
{
    task->
    if(task)
    {
        Toast.makeText(mContext, "Video Updated Successfully", Toast.LENGTH_SHORT).show()
        setAdapter()
    }
    else
    {

        Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()

    }
}
}
            dialog.dismiss()

            // Show a toast or perform other actions based on the update result
        }

        dialog.show()
    }



    /*
        private fun showEditDialog(modelVideo: ModelVideo) {
            val dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_video, null)

            val builder = AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setTitle("Edit Video Details")

            val alertDialog = builder.create()
            alertDialog.show()

            // Initialize your views from the dialog layout
            val episodeNumberEditText = dialogView.findViewById<EditText>(R.id.episodeNumber)
            val videoLinkEditText = dialogView.findViewById<EditText>(R.id.videolink)
            val descriptionEditText = dialogView.findViewById<EditText>(R.id.editTextDescription)
            val privacyRadioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroupPrivacy)
            val downloadAccessRadioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroupDownloadAccess)
            val uploadThumbnailButton = dialogView.findViewById<Button>(R.id.btnUploadThumbnail)
            val nextButton = dialogView.findViewById<Button>(R.id.btnNext)

            // Set the details of the clicked ModelVideo to the EditText fields
            episodeNumberEditText.setText(modelVideo.episodeno)
            videoLinkEditText.setText(modelVideo.videourl)
            descriptionEditText.setText(modelVideo.description)

            // Set the privacy and download access based on the clicked ModelVideo
            val privacyRadioButton = if (modelVideo.privacy==constants.VIDEO_PRIVACY_PRIVATE) {
                dialogView.findViewById<RadioButton>(R.id.radioPublic)
            } else {
                dialogView.findViewById<RadioButton>(R.id.radioPrivate)
            }
            privacyRadioButton.isChecked = true

            val downloadAccessRadioButton = when (modelVideo.downloadType) {
               constants.VIDEO_DOWNLOAD_GALLERY -> dialogView.findViewById<RadioButton>(R.id.radioGallery)
          constants.VIDEO_DOWNLOAD_APPP -> dialogView.findViewById<RadioButton>(R.id.radioAppStorage)
              constants.VIDO_DOWNLOAD_BOTH -> dialogView.findViewById<RadioButton>(R.id.radioBoth)
                else -> dialogView.findViewById<RadioButton>(R.id.radioNever)
            }
            downloadAccessRadioButton.isChecked = true

            // Handle the nextButton click
            nextButton.setOnClickListener {
                // Update the ModelVideo object with the edited details from the dialog
                modelVideo.episodeno = episodeNumberEditText.text.toString()
                modelVideo.videourl = videoLinkEditText.text.toString()
                modelVideo.description = descriptionEditText.text.toString()

                // Update privacy and download access based on the selected radio buttons
                modelVideo.privacy = (privacyRadioGroup.checkedRadioButtonId == R.id.radioPublic).toString()
                // Get selected download access from radio group
                val selectedDownloadAccessRadioButton =
                    dialogView.findViewById<RadioButton>(downloadAccessRadioGroup.checkedRadioButtonId)
                modelVideo.downloadType = selectedDownloadAccessRadioButton.text.toString()

                // Implement further logic here, e.g., call viewModel.editVideo(modelVideo) to update the video
                // Dismiss the dialog
                alertDialog.dismiss()

                // Show a toast or perform other actions based on the update result
            }
        }
    */


    // Method to perform delete action
    private fun performDeleteAction(modelVideo: ModelVideo) {
        // Implement your delete action logic here
        // For example, show a confirmation dialog before deletion
        val confirmDialog = AlertDialog.Builder(mContext)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Yes") { _, _ ->
           videoViewModel.deleteVideo(modelVideo).observe(this)
           {task->

               if(task)
               {

                   Toast.makeText(mContext, "Video deleted Successfully", Toast.LENGTH_SHORT).show()
                   setAdapter()
               }
               else{
                   Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
               }
           }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        confirmDialog.show()
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