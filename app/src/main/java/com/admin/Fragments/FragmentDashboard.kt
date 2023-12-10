package com.admin.Fragments // Update with your package name

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.os.Handler
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

class FragmentDashboard : Fragment() ,Adapterbanner.OnItemClickListener{
    private var _binding: FragmentDashboardBinding? = null

    private lateinit var imageAdapter: Adapterbanner
    private val imageUriList: MutableList<Uri> = mutableListOf()
    private var currentItemPosition = 0
    private val IMAGE_CHANGE_INTERVAL = 7000 // 3 seconds
    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants

    private val binding get() = _binding!!
    private val VIDEO_PICKER_REQUEST_CODE = 6000
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mContext = requireContext()
        utils = Utils(mContext)
        var modellist = ArrayList<ModelDrama>()
        constants = Constants()

        binding.rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Add sample image URIs (Replace with your actual URIs)
        imageUriList.add(Uri.parse("https://firebasestorage.googleapis.com/v0/b/urdubolotv-c88cd.appspot.com/o/thumbnails%2F1702222629581_1000035865?alt=media&token=ada88887-a6e2-4a0f-b292-26b6113b6acd"))
  imageUriList.add(Uri.parse("https://firebasestorage.googleapis.com/v0/b/urdubolotv-c88cd.appspot.com/o/thumbnails%2F1702222629581_1000035865?alt=media&token=ada88887-a6e2-4a0f-b292-26b6113b6acd"))
  imageUriList.add(Uri.parse("https://firebasestorage.googleapis.com/v0/b/urdubolotv-c88cd.appspot.com/o/thumbnails%2F1702222629581_1000035865?alt=media&token=ada88887-a6e2-4a0f-b292-26b6113b6acd"))
  imageUriList.add(Uri.parse("https://firebasestorage.googleapis.com/v0/b/urdubolotv-c88cd.appspot.com/o/thumbnails%2F1702222629581_1000035865?alt=media&token=ada88887-a6e2-4a0f-b292-26b6113b6acd"))
   // Add more URIs as needed

        val recyclerView = binding.rv
        imageAdapter = Adapterbanner(requireContext(), imageUriList, recyclerView,this@FragmentDashboard)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = imageAdapter

        startImageSlideshow()

        return root
    }

    private fun startImageSlideshow() {
        val handler = Handler()
        val runnable = Runnable {
            currentItemPosition++
            if (currentItemPosition >= imageUriList.size) {
                currentItemPosition = 0
            }
            binding.rv.smoothScrollToPosition(currentItemPosition)
            startImageSlideshow()
        }
        handler.postDelayed(runnable, IMAGE_CHANGE_INTERVAL.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove callbacks when fragment is destroyed
        imageAdapter.handler.removeCallbacksAndMessages(null)
    }

    override fun onDeleteClick(modelUser: String) {
        Toast.makeText(mContext, "Available Soon!!", Toast.LENGTH_SHORT).show()
    }

}

