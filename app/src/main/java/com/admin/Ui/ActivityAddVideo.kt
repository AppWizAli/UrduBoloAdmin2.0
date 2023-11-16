package com.admin.Ui

import android.app.Activity
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.Adapter.AdapteraddVideo
import com.admin.Constants
import com.admin.Data.SharedPrefManager
import com.admin.Models.ModelSeason
import com.admin.Models.ModelVideo
import com.admin.Models.SeasonViewModel
import com.admin.Models.VideoViewModel
import com.admin.Utils
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.ActivityAddVideoBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ActivityAddVideo : AppCompatActivity(), AdapteraddVideo.OnItemClickListener {

    private val videoViewModel: VideoViewModel by viewModels()
    private val seasonViewModel: SeasonViewModel by viewModels()
    private val storage = Firebase.storage
    private val storageRef = storage.reference

    private val IMAGE_PICKER_REQUEST_CODE = 200
    private var imageURI: Uri? = null
    private lateinit var modelVideo: ModelVideo
    private lateinit var modelSeason: ModelSeason

    private lateinit var utils: Utils
    private lateinit var constants: Constants
    private lateinit var mContext: Context
    private lateinit var listVideos: ArrayList<ModelVideo>
    private lateinit var listVideosToUpload: ArrayList<ModelVideo>
    private lateinit var progressDialog: Dialog
    private lateinit var progressBar: ProgressBar
    private lateinit var uploaded:TextView
    private lateinit var total:TextView
    private lateinit var textPercentage: TextView
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var dialog: Dialog
    private lateinit var videoUri: String
    private lateinit var binding: ActivityAddVideoBinding

private var count=1

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: Notification.Builder
    private val channelId = "com.admin.Ui"
    private val description = "Video Upload Progress"

    private var currentVideoIndex: Int = 0

    companion object {
        private const val REQUEST_VIDEO_PICK = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this@ActivityAddVideo
        utils = Utils(mContext)
        modelSeason = ModelSeason()
        modelVideo = ModelVideo()
        sharedPrefManager = SharedPrefManager(mContext)
        constants = Constants()
count=0
        videoUri = ""
        listVideos = ArrayList()
        listVideosToUpload = ArrayList()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()






        val dramaJson = intent.getStringExtra("season")
        if (dramaJson != null) {
            modelSeason = ModelSeason.fromString(dramaJson)!!
        }
        binding.floatingAction.setOnClickListener {
            showDialogAddVideo()
        }
        binding.upload.setOnClickListener {
            uploadVideoListToFirebase()
        }
    }



    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    // Function to show dialog for adding video details
    private fun showDialogAddVideo() {
        dialog = Dialog(mContext, R.style.FullWidthDialog)
        dialog.setContentView(R.layout.dialog_video)
        val videoNo = dialog.findViewById<EditText>(R.id.episodeNumber)
        val description = dialog.findViewById<EditText>(R.id.editTextDescription)
        val thumbnail = dialog.findViewById<Button>(R.id.btnUploadThumbnail)
        val choosefile = dialog.findViewById<Button>(R.id.btnChooseFile)
        val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroupPrivacy)
        val radioGroupDownloadAccess = dialog.findViewById<RadioGroup>(R.id.radioGroupDownloadAccess)

        val next = dialog.findViewById<Button>(R.id.btnNext)
        val back = dialog.findViewById<ImageView>(R.id.back)
        dialog.setCancelable(false)
        thumbnail.setBackgroundColor(Color.parseColor("#FEC10F"))
        next.setBackgroundColor(Color.parseColor("#FEC10F"))
        choosefile.setBackgroundColor(Color.parseColor("#FEC10F"))
        back.setOnClickListener {
            dialog.dismiss()
        }

        choosefile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            intent.type = "video/*"
            startActivityForResult(intent, REQUEST_VIDEO_PICK)
        }

        thumbnail.setOnClickListener {
            val pickImage = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST_CODE)
        }

        next.setOnClickListener {
            val newModelVideo = ModelVideo()

            newModelVideo.episodeno = videoNo.text.toString()
            newModelVideo.description = description.text.toString()

            // Set download type based on radio button selection
            val selectedAccess = radioGroupDownloadAccess.checkedRadioButtonId
            when (selectedAccess) {
                R.id.radioGallery -> newModelVideo.downloadType = constants.VIDEO_DOWNLOAD_GALLERY
                R.id.radioAppStorage -> newModelVideo.downloadType = constants.VIDEO_DOWNLOAD_APPP
                R.id.radioNever -> newModelVideo.downloadType = constants.VIDEO_DOWNLOAD_NEVER
                R.id.radioBoth -> newModelVideo.downloadType = constants.VIDO_DOWNLOAD_BOTH
                else -> Toast.makeText(mContext, "Please select download access", Toast.LENGTH_SHORT).show()
            }

            // Set privacy based on radio button selection
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            when (selectedRadioButtonId) {
                R.id.radioPublic -> newModelVideo.privacy = constants.VIDEO_PRIVACY_PUBLIC
                R.id.radioPrivate -> newModelVideo.privacy = constants.VIDEO_PRIVACY_PRIVATE
                else -> Toast.makeText(mContext, "Please select video privacy", Toast.LENGTH_SHORT).show()
            }

            if (videoNo.text.toString().isEmpty() || description.text.toString().isEmpty()
                || imageURI == null || videoUri.isEmpty()
            ) {
                Toast.makeText(mContext, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else {
                newModelVideo.videourl = videoUri // Assign the video URI
                newModelVideo.thumbnail = imageURI.toString()

                listVideos.add(newModelVideo) // Add the newly created ModelVideo to the list
                dialog.dismiss()
                setAdapter()
            }
        }

        dialog.show()
    }

    // Function to handle the result of picking video or image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imageURI = data?.data
        }

        if (requestCode == REQUEST_VIDEO_PICK && resultCode == Activity.RESULT_OK) {
            val selectedVideoUri: Uri? = data?.data
            videoUri = selectedVideoUri.toString()
        }
    }

    // Function to set up RecyclerView adapter
    private fun setAdapter() {
        binding.rvAddedVideos.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)
        binding.rvAddedVideos.adapter = AdapteraddVideo(mContext, listVideos, this@ActivityAddVideo)
    }

    // Function to handle item click in RecyclerView
    override fun onItemClick(modelDrama: ModelVideo) {
        binding.content.visibility = View.VISIBLE
        binding.episodeNumber.text = modelDrama.episodeno
        binding.privacy.text = modelDrama.privacy
        binding.downoladAccess.text = modelDrama.downloadType
        binding.description.text = modelDrama.description
    }

    // Function to handle delete item click in RecyclerView
    override fun onDeleteClick(modelDrama: ModelVideo) {
        // Handle delete item click as required
    }

    // Function to upload video list to Firebase
    private fun uploadVideoListToFirebase() {
        if (listVideos.isNotEmpty()) {

            showUploadProgressDialog()
          // Show progress dialog before uploading videos
            uploadNextVideo()
        } else {
            Toast.makeText(mContext, "No videos to upload", Toast.LENGTH_SHORT).show()
        }
    }


    private fun uploadNextVideo() {
        uploaded.text=(currentVideoIndex+1).toString()
        if(currentVideoIndex==listVideos.size)
        {
            Toast.makeText(mContext, "All Episodes Uploaded Successfully!!!", Toast.LENGTH_SHORT).show()
            val intent = Intent(mContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        if (currentVideoIndex < listVideos.size) {


            val modelVideo = listVideos[currentVideoIndex]
            modelVideo.seasonId=modelSeason.docId
            modelVideo.dramaId=modelSeason.dramaId

            uploadThumbnailImage(modelVideo.thumbnail.toUri()) { thumbnailUrl ->
                if (thumbnailUrl != null) {
                    modelVideo.thumbnail = thumbnailUrl

                    uploadVideoToFirebase(modelVideo.videourl.toUri()) { downloadUrl ->
                        if (downloadUrl != null) {
                            modelVideo.videourl = downloadUrl
modelVideo.totalepisodes=modelSeason.totalEpisode
                            modelVideo.dramaName=modelSeason.dramaName
                            lifecycleScope.launch {
                                videoViewModel.addVideo(modelVideo)
                                    .observe(this@ActivityAddVideo) { success ->
                                        if (success) {
                                            currentVideoIndex++
                                            updateSeason()
                                            uploadNextVideo() // Upload the next video in the list
                                        } else {
                                            Toast.makeText(
                                                mContext,
                                                "Failed to upload video ${currentVideoIndex + 1}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(
                                mContext,
                                "Failed to retrieve download URL for video ${currentVideoIndex + 1}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(mContext, "Failed to upload thumbnail for video ${currentVideoIndex + 1}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            dismissUploadProgressDialog() // Dismiss progress dialog when all videos are uploaded
        }
    }


    private fun updateSeason() {


        lifecycleScope.launch {

                val documentSnapshot = seasonViewModel.getSeasonbyId(modelSeason.docId).await()
                if (documentSnapshot.exists()) {
                    val modelSeason = documentSnapshot.toObject(ModelSeason::class.java)

                    if (modelSeason != null) {


                        if (modelSeason.uploadedepisodes.isNotEmpty()) {
                            val uploadedVideos = modelSeason.uploadedepisodes.toInt() + count
                            modelSeason.uploadedepisodes = uploadedVideos.toString()

                            seasonViewModel.updateSeason(modelSeason)
                        }
                        // Handle the modelSeason here
                    }
                }


        }
    }



    private fun uploadVideoToFirebase(videoUri: Uri, callback: (String?) -> Unit) {
        val videoRef = storageRef.child("videos/${videoUri.lastPathSegment}")
        val uploadTask = videoRef.putFile(videoUri)

        uploadTask.addOnProgressListener { taskSnapshot ->
            // Update the progress in the dialog
            val progress = ((100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount).toInt()
            updateUploadProgress(progress)
           /* showNotification(progress)*/
        }

        uploadTask.addOnSuccessListener { taskSnapshot ->
            videoRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                val downloadUriString = downloadUrl.toString()
                callback(downloadUriString) // Return download URL as a String via callback
            }
        }.addOnFailureListener { e ->
            dismissUploadProgressDialog()
            Toast.makeText(mContext, "Something went wrong: ${e.message}", Toast.LENGTH_SHORT).show()
            callback(null) // Return null in case of failure
        }
    }

    // Function to upload thumbnail image to Firebase storage
    private fun uploadThumbnailImage(imageUri: Uri, callback: (String?) -> Unit) {
        val storageRef =
            Firebase.storage.reference.child("thumbnails/${System.currentTimeMillis()}_${imageUri.lastPathSegment}")
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

    // Function to show upload progress dialog
    private fun showUploadProgressDialog() {
        progressDialog = Dialog(mContext)
        progressDialog.setContentView(R.layout.dialog_progress)
        progressDialog.setCancelable(false)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        progressBar = progressDialog.findViewById(R.id.progressBar)
        textPercentage = progressDialog.findViewById(R.id.textPercentage)
        total = progressDialog.findViewById(R.id.textTotal)
        uploaded = progressDialog.findViewById(R.id.textUploaded)
total.text=listVideos.size.toString()
        // Show the dialog
        progressDialog.show()
    }

    // Function to update upload progress
    private fun updateUploadProgress(progress: Int) {
        progressBar.progress = progress
        textPercentage.text = "$progress%"
    }

    // Function to dismiss upload progress dialog
    private fun dismissUploadProgressDialog() {
        progressDialog.dismiss()

    }
  /*  // Function to display notification
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(progress: Int) {
        builder = Notification.Builder(applicationContext, channelId)
            .setContentTitle("Uploading Video")
            .setContentText("Video upload progress: $progress%")
            .setSmallIcon(R.drawable.baseline_arrow_back_ios_24)

        notificationManager.notify(1234, builder.build())
    }

    // Function to update notification
    private fun updateNotification(progress: Int) {
        builder.setContentText("Video upload progress: $progress%")
        notificationManager.notify(1234, builder.build())
    }*/






}
