package com.admin.Fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
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
import com.admin.Models.VideoViewModel
import com.admin.Ui.ActivitySeason
import com.admin.Utils
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FragmentHome : Fragment(), AdapterDrama.OnItemClickListener {
    private var _binding: FragmentHomeBinding? = null

    private val videoViewModel: VideoViewModel by viewModels()
    private val dramaViewModel: DramaViewModel by viewModels()
    private val db = Firebase.firestore
    private var imageURI: Uri? = null
    private val IMAGE_PICKER_REQUEST_CODE = 200

    private lateinit var modelDrama: ModelDrama

    private lateinit var thumnailview:ImageView
    private lateinit var adapter: AdapterDrama

    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var user: ModelUser
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var dialog: Dialog
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mContext = requireContext()
        utils = Utils(mContext)
        modelDrama = ModelDrama()
        var modellist = ArrayList<ModelDrama>()
        constants = Constants()
   setAdapter()
sharedPrefManager=SharedPrefManager(mContext)

        binding.floatingaction.setOnClickListener {
            showDialogAddDrama()
        }



        return root
    }

    private fun showDialogAddDrama() {
        dialog = Dialog(mContext, R.style.FullWidthDialog)
        dialog.setContentView(R.layout.dialog_add_drama)
        val dramaName = dialog.findViewById<EditText>(R.id.editTextDramaName)
        val dramaNo = dialog.findViewById<EditText>(R.id.editTextDramaNo)
        val toatlSeason = dialog.findViewById<EditText>(R.id.editTextTotalSeason)
        val thumbnail = dialog.findViewById<Button>(R.id.btnUploadThumbnail)
       thumnailview = dialog.findViewById<ImageView>(R.id.thumbnailview)
        val next = dialog.findViewById<Button>(R.id.btnNext)
        val back = dialog.findViewById<ImageView>(R.id.back)
        dialog.setCancelable(false)
        thumbnail.setBackgroundColor(Color.parseColor("#FEC10F"))
        next.setBackgroundColor(Color.parseColor("#FEC10F"))
        back.setOnClickListener { dialog.dismiss() }
        thumbnail.setOnClickListener {
            val pickImage =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST_CODE)
        }


        next.setOnClickListener {
            modelDrama.dramaName = dramaName.text.toString()
            modelDrama.dramaNumber = dramaNo.text.toString()
            modelDrama.totalSeason = toatlSeason.text.toString()

            if (dramaName.text.toString().isEmpty() || dramaNo.text.toString().isEmpty()
                || toatlSeason.text.toString().isEmpty()
            ) {
                Toast.makeText(mContext, "Please Enter All fields", Toast.LENGTH_SHORT).show()
            } else {
                utils.startLoadingAnimation()
                if (imageURI != null) {
                    // Upload the thumbnail image to Firebase Storage
                    uploadThumbnailImage(imageURI!!) { thumbnailUrl ->
                        if (thumbnailUrl != null) {
                            // Update the ModelDrama with the thumbnail URL
                            modelDrama.thumbnail = thumbnailUrl

                            // Add the updated ModelDrama to Firestore
                            lifecycleScope.launch {
                                dramaViewModel.addDrama(modelDrama).observe(viewLifecycleOwner) { success ->
                                    if (success) {
                                        utils.endLoadingAnimation()
                                        Toast.makeText(
                                            mContext,
                                            constants.DRAMA_CREATED_SUCCESSFULLY,
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
                    Toast.makeText(mContext, "Please select a thumbnail image.", Toast.LENGTH_SHORT).show()
                }
            }
        }





        dialog.show()
    }

    override fun onItemClick(modelDrama: ModelDrama) {
        val intent = Intent(context, ActivitySeason::class.java)
        intent.putExtra("drama", modelDrama.toString()) // Serialize to JSON
        context?.startActivity(intent)

    }

    override fun onDeleteClick(modelDrama: ModelDrama) {

    }

    override fun onEditClick(modelDrama: ModelDrama) {
        Toast.makeText(mContext, "Available Soon!!", Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imageURI = data?.data

        }
    }



    fun setAdapter() {
      //  utils.startLoadingAnimation()
        binding.shimmerLayout.startShimmer()
        val list = ArrayList<ModelDrama>()

        lifecycleScope.launch {
            try {
                val taskResult = dramaViewModel.getDramalist().await()

                if (taskResult != null) {
                    // Check if the task result is not null, indicating a successful result
                    if (taskResult.size() > 0) {
                        for (document in taskResult) {
                            list.add(document.toObject(ModelDrama::class.java))
                        }
                    }
                    binding.shimmerLayout.stopShimmer() // Stop shimmer effect once data is loaded
                    binding.shimmerLayout.visibility = View.GONE
                    val sortedList = list.sortedBy { it.dramaNumber?.toIntOrNull() ?: 0 }
                    if(list.isEmpty())
                    {
                        binding.nothing.visibility=View.VISIBLE
                    }
                    else
                    {
                        binding.nothing.visibility=View.GONE
                        binding.rvdrama.layoutManager = LinearLayoutManager(mContext)
                        binding.rvdrama.adapter = AdapterDrama(mContext, sortedList, this@FragmentHome)

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
