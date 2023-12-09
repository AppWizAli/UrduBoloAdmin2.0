package com.admin.Fragments // Update with your package name

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.admin.Adapter.AdapterDrama
import com.admin.Adapter.Adapterbanner
import com.admin.Constants
import com.admin.Data.SharedPrefManager
import com.admin.Models.DramaViewModel
import com.admin.Models.ModelDrama
import com.admin.Models.ModelUser
import com.admin.Models.VideoViewModel
import com.admin.Utils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.FragmentDashboardBinding
import com.urduboltv.admin.databinding.FragmentHomeBinding
import java.util.*

class FragmentDashboard : Fragment() {
    private var _binding: FragmentDashboardBinding? = null

    private val videoViewModel: VideoViewModel by viewModels()
    private val dramaViewModel: DramaViewModel by viewModels()
    private val db = Firebase.firestore
    private var imageURI: Uri? = null
    private val IMAGE_PICKER_REQUEST_CODE = 200
    private var deleteDialog: AlertDialog? = null
    private lateinit var modelDrama: ModelDrama

    private lateinit var thumnailview: ImageView
    private lateinit var adapter: AdapterDrama

    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var user: ModelUser
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var dialog: Dialog
    private val binding get() = _binding!!
    private val VIDEO_PICKER_REQUEST_CODE = 300
    private lateinit var videoAdapter: Adapterbanner
    private val videosList: MutableList<Uri> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        mContext = requireContext()
        utils = Utils(mContext)
        modelDrama = ModelDrama()
        var modellist = ArrayList<ModelDrama>()
        constants = Constants()

        binding.rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        videosList.add(Uri.parse("https://firebasestorage.googleapis.com/v0/b/urdubolotv-c88cd.appspot.com/o/videos%2F1701793108145_1000032992?alt=media&token=7a182a44-2027-4b20-99c4-1b5b222266e5"))
        videosList.add(Uri.parse("https://firebasestorage.googleapis.com/v0/b/urdubolotv-c88cd.appspot.com/o/videos%2F1701793108145_1000032992?alt=media&token=7a182a44-2027-4b20-99c4-1b5b222266e5"))
        // Add more videos as needed

        videoAdapter = Adapterbanner(requireContext(), videosList)
        binding.rv.adapter = videoAdapter
        sharedPrefManager=SharedPrefManager(mContext)
/*binding.floatingaction.setOnClickListener()
{
    showVideoPicker()
}*/




        return root
    }
    private fun showVideoPicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, VIDEO_PICKER_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VIDEO_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val videoUri = data?.data
            videoUri?.let { uri ->
                // Video selected, now upload to Firebase Storage and save URL in Firestore
                uploadVideoToFirebaseStorage(uri)
            }
        }
    }


    private fun uploadVideoToFirebaseStorage(videoUri: Uri) {

        val storageRef = Firebase.storage.reference.child("videos/${System.currentTimeMillis()}_${videoUri.lastPathSegment}")

        // Start the upload
        val uploadTask = storageRef.putFile(videoUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Get the download URL from the completed upload task
            taskSnapshot.storage.downloadUrl.addOnCompleteListener { downloadUrlTask ->
                if (downloadUrlTask.isSuccessful) {
                    val downloadUrl = downloadUrlTask.result.toString()

                    // Save the download URL to Firestore
                    saveVideoUrlToFirestore(downloadUrl)
                } else {
                    // Handle failure to get the download URL
                    Toast.makeText(mContext, "Video Uploaded succesfulluy", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { exception ->
            // Handle unsuccessful upload
            Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show()

        }
    }

    private fun saveVideoUrlToFirestore(videoUrl: String) {
        // Here you need to save the videoUrl in your Firestore collection
        // For example, if you have a 'banner' collection:
        utils.startLoadingAnimation()
        val bannerCollectionRef = Firebase.firestore.collection("banner")

        // Create a new document with a unique auto-generated ID
        val newDocumentRef = bannerCollectionRef.document()

        // Set the 'videoUrl' field in the new document
        newDocumentRef
            .set(mapOf("videoUrl" to videoUrl))
            .addOnSuccessListener {
                utils.endLoadingAnimation()
                // New document created successfully in Firestore
                Toast.makeText(mContext, "Video URL saved in Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                utils.endLoadingAnimation()
                // Handle failures while creating the new document
                Toast.makeText(mContext, "Failed to save video URL: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }



}

