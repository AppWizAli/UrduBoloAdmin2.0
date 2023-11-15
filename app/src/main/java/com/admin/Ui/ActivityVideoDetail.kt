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

    private val IMAGE_PICKER_REQUEST_CODE = 200
    private var imageURI: Uri? = null
    private lateinit var adapter: AdapterSeason
    private lateinit var modelVideo: ModelVideo
    private lateinit var modelSeason: ModelSeason

    private lateinit var utils: Utils
    private lateinit var constants: Constants
    private lateinit var mContext: Context
    private lateinit var list:ArrayList<ModelVideo>
    private lateinit var user: ModelUser
    private lateinit var progressDialog: Dialog
    private lateinit var progressBar: ProgressBar
    private lateinit var textPercentage: TextView
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var dialog: Dialog
    private lateinit var videoUri:String
    private lateinit var binding: ActivityVideoDetailBinding
    companion object {
        private const val REQUEST_READ_EXTERNAL_STORAGE = 101
        private const val REQUEST_VIDEO_PICK = 100
    }

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
          //  showDialogAddvideo()
startActivity(Intent(mContext,ActivityAddVideo::class.java))
        }


    }


    private fun showDialogAddvideo() {
        dialog = Dialog(mContext, R.style.FullWidthDialog)
        dialog.setContentView(R.layout.dialog_video)
        val videoNo = dialog.findViewById<EditText>(R.id.episodeNumber)
        val description = dialog.findViewById<EditText>(R.id.editTextDescription)
        val thumbnail = dialog.findViewById<Button>(R.id.btnUploadThumbnail)
        val choosefile = dialog.findViewById<Button>(R.id.btnChooseFile)
        val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroupPrivacy)
        val radioGroupDownlaodAccess = dialog.findViewById<RadioGroup>(R.id.radioGroupDownloadAccess)


        val next = dialog.findViewById<Button>(R.id.btnNext)
        val back = dialog.findViewById<ImageView>(R.id.back)
        dialog.setCancelable(false)
        thumbnail.setBackgroundColor(Color.parseColor("#FEC10F"))
        next.setBackgroundColor(Color.parseColor("#FEC10F"))
        choosefile.setBackgroundColor(Color.parseColor("#FEC10F"))
        back.setOnClickListener {
            // utils.startLoadingAnimation()
            dialog.dismiss()
        }




        choosefile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            intent.type = "video/*"
            startActivityForResult(intent, REQUEST_VIDEO_PICK)
        }





        thumbnail.setOnClickListener {
            val pickImage =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST_CODE)
        }



        next.setOnClickListener {
            modelVideo.episodeno = videoNo.text.toString()
            modelVideo.description = description.text.toString()
            modelVideo.seasonId = modelSeason.docId
            modelVideo.dramaId=modelSeason.dramaId




            val selectedAccess = radioGroupDownlaodAccess.checkedRadioButtonId
            when (selectedAccess) {
                R.id.radioGallery -> {
                    // User selected "Gallery" option
                    modelVideo.downloadType = constants.VIDEO_DOWNLOAD_GALLERY
                }
                R.id.radioAppStorage -> {
                    // User selected "App Storage" option
                    modelVideo.downloadType = constants.VIDEO_DOWNLOAD_APPP
                }
                R.id.radioNever -> {
                    // User selected "Never" option
                    modelVideo.downloadType =constants.VIDEO_DOWNLOAD_NEVER
                }
                R.id.radioBoth -> {
                    // User selected "Gallery & App Storage" option
                    modelVideo.downloadType = constants.VIDO_DOWNLOAD_BOTH
                }
                else -> {
                    Toast.makeText(mContext, "Please!! Select Download Accesss", Toast.LENGTH_SHORT).show()
                }
            }
            
            
            

            
            


            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId == R.id.radioPublic) {
                modelVideo.privacy = constants.VIDEO_PRIVACY_PUBLIC
            } else if (selectedRadioButtonId == R.id.radioPrivate) {
                modelVideo.privacy = constants.VIDEO_PRIVACY_PRIVATE
            } else {
                Toast.makeText(mContext, "Please! Enable video Privacy", Toast.LENGTH_SHORT).show()
            }

            if (videoNo.text.toString().isEmpty() || description.text.toString().isEmpty()
                || imageURI == null ||
              videoUri.isEmpty()
            ) {
                Toast.makeText(mContext, "Please Enter All fields", Toast.LENGTH_SHORT).show()
            } else {
                utils.startLoadingAnimation()
                if (imageURI != null) {
                    // Upload the thumbnail image to Firebase Storage
                    uploadThumbnailImage(imageURI!!) { thumbnailUrl ->
                        if (thumbnailUrl != null) {
                            // Update the ModelDrama with the thumbnail URL
                            modelVideo.thumbnail = thumbnailUrl
                            list.add(modelVideo)
                            sharedPrefManager.putVideoList(list)
                            utils.endLoadingAnimation()
                            startActivity(Intent(mContext,ActivityAddVideo::class.java))
                            dialog.dismiss()
                            // Add the updated ModelDrama to Firestore
                           /* lifecycleScope.launch {
                                videoViewModel.addVideo(modelVideo)
                                    .observe(this@ActivityVideoDetail) { success ->
                                        if (success) {
                                            utils.endLoadingAnimation()
                                            Toast.makeText(
                                                mContext,
                                                constants.VIDEO_ADDED_SUCCESFULLY,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            dialog.dismiss()
                                            setAdapter()
                                        } else {

                                            Toast.makeText(
                                                mContext,
                                                constants.SOMETHING_WENT_WRONG_MESSAGE,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            dialog.dismiss()
                                        }
                                    }
                            }*/
                        } else {

                            utils.endLoadingAnimation()
                            Toast.makeText(
                                mContext,
                                "Failed to upload the thumbnail image.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(mContext, "Please select a thumbnail image.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }




        dialog.show()
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
                    }



                    binding.rvvideos.layoutManager = GridLayoutManager(mContext,2)
                    binding.rvvideos.adapter = VideoAdapter(mContext, list, this@ActivityVideoDetail)
                }else {
                    Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
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
                // Glide.with(mContext).load(data?.data).into(thumnailview)
                imageURI = data?.data
                //  thumnailview.visibility = View.VISIBLE
            }



        if (requestCode == REQUEST_VIDEO_PICK && resultCode == RESULT_OK) {
            val selectedVideoUri: Uri? = data?.data
            videoUri=data?.data.toString()
            if (selectedVideoUri != null) {
              //  uploadVideoToFirebase(selectedVideoUri)
            }
        }

    }


   /* private fun uploadVideoToFirebase(videoUri: Uri) {
        utils.startLoadingAnimation()

        val videoRef = storageRef.child("videos/${videoUri.lastPathSegment}")
        val uploadTask = videoRef.putFile(videoUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Get the download URL of the uploaded video
            videoRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                utils.endLoadingAnimation()
                // Set the download URL to your modelVideo.videourl
                modelVideo.videourl = downloadUrl.toString()
                Toast.makeText(mContext, "Upload successful", Toast.LENGTH_SHORT).show()
            }
        }

        uploadTask.addOnFailureListener { e ->
            utils.endLoadingAnimation()
            Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
        }
    }

*/




    private fun uploadVideoToFirebase(videoUri: Uri) {
        // Create and show the progress dialog
        showUploadProgressDialog()

        val videoRef = storageRef.child("videos/${videoUri.lastPathSegment}")
        val uploadTask = videoRef.putFile(videoUri)

        uploadTask.addOnProgressListener { taskSnapshot ->
            // Update the progress in the dialog
            val progress = ((100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount).toInt()
            updateUploadProgress(progress)
        }

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Get the download URL of the uploaded video
            videoRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                // Dismiss the progress dialog on success
                dismissUploadProgressDialog()

                // Set the download URL to your modelVideo.videourl
                modelVideo.videourl = downloadUrl.toString()
                Toast.makeText(mContext, "Upload successful", Toast.LENGTH_SHORT).show()
            }
        }

        uploadTask.addOnFailureListener { e ->
            // Dismiss the progress dialog on failure
            dismissUploadProgressDialog()

            Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
        }
    }

    // Function to show the progress dialog
    private fun showUploadProgressDialog() {
        progressDialog = Dialog(mContext)
        progressDialog.setContentView(R.layout.dialog_progress)
        progressDialog.setCancelable(false)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        progressBar = progressDialog.findViewById(R.id.progressBar)
        textPercentage = progressDialog.findViewById(R.id.textPercentage)

        // Show the dialog
        progressDialog.show()
    }

    // Function to update the progress in the dialog
    private fun updateUploadProgress(progress: Int) {
        progressBar.progress = progress
        textPercentage.text = "$progress%"
    }

    // Function to dismiss the progress dialog
    private fun dismissUploadProgressDialog() {
        progressDialog.dismiss()
    }





    private fun uploadThumbnailImage(imageUri: Uri, callback: (String?) -> Unit) {
        val storageRef = Firebase.storage.reference.child("thumbnails/${System.currentTimeMillis()}_${imageUri.lastPathSegment}")
        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnCompleteListener { downloadUrlTask ->
                    if (downloadUrlTask.isSuccessful) {
                        val downloadUrl = downloadUrlTask.result.toString()
                        callback(downloadUrl)
                    } else {
                        callback(null)
                    }
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

}