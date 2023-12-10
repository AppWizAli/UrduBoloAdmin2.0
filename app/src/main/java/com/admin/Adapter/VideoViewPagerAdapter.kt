package com.admin.Adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.admin.Fragments.FragmentAdminManagment
import com.admin.Fragments.FragmentAssignedVideo
import com.admin.Fragments.FragmentUnAssignedVideo
import com.admin.Fragments.FragmentUserManagement
import com.admin.Models.ModelUser

class VideoViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private var totalCount: Int,
    private val modelUser: ModelUser // Add ModelUser as a parameter
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return totalCount
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentAssignedVideo.newInstance(modelUser) // Pass ModelUser to FragmentUnAssignedVideo
            1 -> FragmentUnAssignedVideo.newInstance(modelUser) // If needed, pass ModelUser to FragmentAssignedVideo
            else -> FragmentAssignedVideo.newInstance(modelUser) // Default fragment creation
        }
    }
}
