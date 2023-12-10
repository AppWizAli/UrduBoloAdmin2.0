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
import androidx.core.net.toUri
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
    private val IMAGE_PICKER_REQUEST_CODE = 200
    private lateinit var adapter: AdapterSeason
    private lateinit var modelVideo: ModelVideo
    private lateinit var modelSeason: ModelSeason
    private var imageURI: Uri? = null
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
        uploadThumbnailButton.setOnClickListener {
            val pickImage = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST_CODE)
        }
        // Set details of modelVideo to the EditText fields in the dialog
        episodeNumberEditText.setText(modelVideo.episodeno)
        videoLinkEditText.setText(modelVideo.videourl)
        descriptionEditText.setText(modelVideo.description)

        backButton.setOnClickListener {
            dialog.dismiss()
        }
        if (modelVideo.privacy == constants.VIDEO_PRIVACY_PRIVATE) {
            privacyRadioGroup.check(R.id.radioPrivate)
        } else {
            privacyRadioGroup.check(R.id.radioPublic)
        }
        if (modelVideo.downloadType == constants.VIDEO_DOWNLOAD_APPP) {
            privacyRadioGroup.check(R.id.radioAppStorage)
        } else if( modelVideo.downloadType == constants.VIDEO_DOWNLOAD_GALLERY) {
            privacyRadioGroup.check(R.id.radioGallery)
        }else if( modelVideo.downloadType == constants.VIDO_DOWNLOAD_BOTH) {
            privacyRadioGroup.check(R.id.radioBoth)
        }else if( modelVideo.downloadType == constants.VIDEO_DOWNLOAD_NEVER) {
            privacyRadioGroup.check(R.id.radioNever)
        }
        // Handle click on nextButton
        nextButton.setOnClickListener {
            // Update the modelVideo object with edited details from the dialog
            modelVideo.episodeno = episodeNumberEditText.text.toString()
            modelVideo.videourl = videoLinkEditText.text.toString()
            modelVideo.description = descriptionEditText.text.toString()
            if(imageURI.toString().isNotEmpty())modelVideo.thumbnail=imageURI.toString()


            val selectedRadioButtonId = privacyRadioGroup.checkedRadioButtonId
            when (selectedRadioButtonId) {
                R.id.radioPublic -> modelVideo.privacy = constants.VIDEO_PRIVACY_PUBLIC
                R.id.radioPrivate -> modelVideo.privacy = constants.VIDEO_PRIVACY_PRIVATE
            }

            val selectedAccess = downloadAccessRadioGroup.checkedRadioButtonId
            when (selectedAccess) {
                R.id.radioGallery -> modelVideo.downloadType = constants.VIDEO_DOWNLOAD_GALLERY
                R.id.radioAppStorage -> modelVideo.downloadType = constants.VIDEO_DOWNLOAD_APPP
                R.id.radioNever -> modelVideo.downloadType = constants.VIDEO_DOWNLOAD_NEVER
                R.id.radioBoth -> modelVideo.downloadType = constants.VIDO_DOWNLOAD_BOTH
            }
            uploadThumbnailImage(modelVideo.thumbnail.toUri()) { thumbnailUrl ->
                    if (thumbnailUrl != null) {
                        modelVideo.thumbnail = thumbnailUrl
                    }
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



            }



            dialog.dismiss()

            // Show a toast or perform other actions based on the update result
        }

        dialog.show()
    }


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imageURI = data?.data

        }

    }
    private fun uploadThumbnailImage(imageUri: Uri, callback: (String?) -> Unit) {
        utils.startLoadingAnimation()
        val storageRef =
            Firebase.storage.reference.child("thumbnails/${System.currentTimeMillis()}_${imageUri.lastPathSegment}")
        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnCompleteListener { downloadUrlTask ->
                    if (downloadUrlTask.isSuccessful) {
                        utils.endLoadingAnimation()
                        val downloadUrl = downloadUrlTask.result.toString()
                        callback(downloadUrl)
                    } else {
                        utils.endLoadingAnimation()
                        callback(null)
                    }
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

}