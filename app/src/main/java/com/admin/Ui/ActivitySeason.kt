package com.admin.Ui

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.SyncStateContract
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.Adapter.AdapterDrama
import com.admin.Adapter.AdapterSeason
import com.admin.Constants
import com.admin.Data.SharedPrefManager
import com.admin.Models.DramaViewModel
import com.admin.Models.ModelDrama
import com.admin.Models.ModelSeason
import com.admin.Models.ModelUser
import com.admin.Models.ModelVideo
import com.admin.Models.SeasonViewModel
import com.admin.Models.VideoViewModel
import com.admin.Utils
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.ActivitySeasonBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ActivitySeason : AppCompatActivity(),AdapterSeason.OnItemClickListener {
    private val videoViewModel: VideoViewModel by viewModels()
    private val dramaViewModel: DramaViewModel by viewModels()
    private val seasonViewModel: SeasonViewModel by viewModels()
    private val db = Firebase.firestore

    private val IMAGE_PICKER_REQUEST_CODE = 200

    private lateinit var modelSeason: ModelSeason
    private var imageURI: Uri? = null

    private lateinit var adapter: AdapterSeason

    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var user: ModelUser
    private lateinit var modelDrama: ModelDrama
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var dialog: Dialog
   private  lateinit var binding:ActivitySeasonBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivitySeasonBinding.inflate(layoutInflater)
        setContentView(binding.root)
constants=Constants()
modelSeason= ModelSeason()
modelDrama= ModelDrama()
        mContext=this@ActivitySeason
        sharedPrefManager=SharedPrefManager(mContext)
        utils=Utils(mContext)


        val dramaJson = intent.getStringExtra("drama")
        if (dramaJson != null) {
             modelDrama = ModelDrama.fromString(dramaJson) !!
        }


        supportActionBar?.title = modelDrama.dramaName



        binding.floatingaction.setOnClickListener {



          showDialogAddSeason()

        }

        // Handle your logic for ActivitySeason using ModelSeason
setAdapter()


    }

    override fun onItemClick(modelSeason: ModelSeason) {
        val intent = Intent(mContext, ActivityVideoDetail::class.java)
        intent.putExtra("season", modelSeason.toString()) // Serialize to JSON
        mContext.startActivity(intent)
    }
    private fun showEditDialog(modelDrama: ModelSeason) {

        val dialog = Dialog(mContext, R.style.FullWidthDialog)
        dialog.setContentView(R.layout.dialog_season)

        val seasonNo = dialog.findViewById<EditText>(R.id.seasonNo)
        val totalSeason = dialog.findViewById<EditText>(R.id.toatalEpisodes)
        val thumbnail = dialog.findViewById<Button>(R.id.btnUploadThumbnail)
        val next = dialog.findViewById<Button>(R.id.btnNext)
        val back = dialog.findViewById<ImageView>(R.id.back)

        seasonNo.setText(modelDrama.seasonNo)
        totalSeason.setText(modelDrama.totalEpisode)
        thumbnail.setBackgroundColor(Color.parseColor("#FEC10F"))

        dialog.setCancelable(false)

        back.setOnClickListener { dialog.dismiss() }

        thumbnail.setOnClickListener {
            val pickImage =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST_CODE)
        }

        next.setOnClickListener {
            val seasonNo = seasonNo.text.toString()
            val totalSeason = totalSeason.text.toString()

            if (seasonNo.isEmpty() || totalSeason.isEmpty()|| imageURI.toString().isEmpty()) {
                Toast.makeText(mContext, "Please Enter All fields", Toast.LENGTH_SHORT).show()
            } else {
                utils.startLoadingAnimation()

                modelDrama.seasonNo = seasonNo
                modelDrama.totalEpisode = totalSeason

                if (imageURI != null) {
                    uploadThumbnailImage(imageURI!!) { thumbnailUrl ->
                        if (thumbnailUrl != null) {
                            modelDrama.thumbnail = thumbnailUrl

                            lifecycleScope.launch {

                                seasonViewModel.updateSeason(modelDrama).observe(this@ActivitySeason) { success ->
                                    if (success) {
                                        utils.endLoadingAnimation()
                                        Toast.makeText(
                                            mContext,
                                            "Drama updated successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        dialog.dismiss()
                                        setAdapter()
                                    } else {
                                        utils.endLoadingAnimation()
                                        Toast.makeText(
                                            mContext,
                                            "Failed to update the drama",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            }
                        } else {
                            utils.endLoadingAnimation()
                            Toast.makeText(
                                mContext,
                                "Failed to upload the thumbnail image.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        dialog.show()
    }


    override fun onDeleteClick(modelSeason: ModelSeason) {

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Confirmation")
            .setMessage("Are you sure you want to delete?")
            .setPositiveButton("Yes") { dialog, which ->
performDeleteAction()
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()



    }

    private fun performDeleteAction() {
        seasonViewModel.deleteSeason(modelSeason)
            .observe(this@ActivitySeason) { success ->
                if (success) {
                    utils.endLoadingAnimation()
                    Toast.makeText(
                        mContext,
                        "Season Deleted Succesfully",
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

    }
    override fun onEditClick(modelSeason: ModelSeason) {
        showEditDialog(modelSeason)
    }

    private fun showDialogAddSeason() {
        dialog = Dialog(mContext, R.style.FullWidthDialog)
        dialog.setContentView(R.layout.dialog_season)
        val seasonNo = dialog.findViewById<EditText>(R.id.seasonNo)
        val totalepisodes = dialog.findViewById<EditText>(R.id.toatalEpisodes)
        val thumbnail = dialog.findViewById<Button>(R.id.btnUploadThumbnail)
        val next = dialog.findViewById<Button>(R.id.btnNext)
        val back = dialog.findViewById<ImageView>(R.id.back)
        dialog.setCancelable(false)
        thumbnail.setBackgroundColor(Color.parseColor("#FEC10F"))
        next.setBackgroundColor(Color.parseColor("#FEC10F"))
        back.setOnClickListener {
            // utils.startLoadingAnimation()
            dialog.dismiss()
        }

        thumbnail.setOnClickListener {
            val pickImage =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST_CODE)
        }
        next.setOnClickListener {
            modelSeason.seasonNo = seasonNo.text.toString()
            modelSeason.totalEpisode = totalepisodes.text.toString()
            modelSeason.dramaId = modelDrama.docId

            if (seasonNo.text.toString().isEmpty() || totalepisodes.text.toString().isEmpty()
                || imageURI == null
            ) {
                Toast.makeText(mContext, "Please Enter All fields", Toast.LENGTH_SHORT).show()
            } else {
                utils.startLoadingAnimation()
                if (imageURI != null) {
                    // Upload the thumbnail image to Firebase Storage
                    uploadThumbnailImage(imageURI!!) { thumbnailUrl ->
                        if (thumbnailUrl != null) {
                            // Update the ModelDrama with the thumbnail URL
                            modelSeason.thumbnail = thumbnailUrl

modelSeason.dramaName=modelDrama.dramaName
                            // Add the updated ModelDrama to Firestore
                            lifecycleScope.launch {
                                seasonViewModel.addSeason(modelSeason)
                                    .observe(this@ActivitySeason) { success ->
                                        if (success) {
                                            utils.endLoadingAnimation()
                                            Toast.makeText(
                                                mContext,
                                                constants.SEASON_CREATED_SUCCESFULLY,
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
                            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
           // Glide.with(mContext).load(data?.data).into(thumnailview)
            imageURI = data?.data
          //  thumnailview.visibility = View.VISIBLE
        }
    }
/*    fun setAdapter() {
        utils.startLoadingAnimation()
        var list= ArrayList<ModelSeason> ()
        lifecycleScope.launch {
            seasonViewModel.getSeasonList(modelDrama.docId)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        utils.endLoadingAnimation()

                        if (task.result.size() > 0) {

                            for (document in task.result) list.add(
                                    document.toObject(
                                        ModelSeason::class.java
                                    )
                                )


                        }

                        binding.rvseasons.layoutManager = LinearLayoutManager(mContext)
                        binding.rvseasons.adapter=AdapterSeason("Home",list,this@ActivitySeason)


                    } else Toast.makeText(
                        mContext,
                        constants.SOMETHING_WENT_WRONG_MESSAGE,
                        Toast.LENGTH_SHORT
                    ).show()

sharedPrefManager.putSeasonList(list)
                }
                .addOnFailureListener {
                    utils.endLoadingAnimation()
                    Toast.makeText(mContext, it.message + "", Toast.LENGTH_SHORT).show()

                }


        }





    }*/

    fun setAdapter() {
        utils.startLoadingAnimation()
        val list = ArrayList<ModelSeason>()

        lifecycleScope.launch {
            try {
                val taskResult = seasonViewModel.getSeasonList(modelDrama.docId).await()

                if (taskResult != null) {
                    // Check if the task result is not null, indicating a successful result
                    if (taskResult.size() > 0) {
                        for (document in taskResult) {
                            list.add(document.toObject(ModelSeason::class.java))
                        }
                    }
                    val sortedList = list.sortedBy { it.seasonNo?.toIntOrNull() ?: 0 }
                    binding.rvseasons.layoutManager = LinearLayoutManager(mContext)
                    binding.rvseasons.adapter = AdapterSeason(mContext, sortedList, this@ActivitySeason)
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