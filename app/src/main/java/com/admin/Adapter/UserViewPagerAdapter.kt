package com.admin.Adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.admin.Fragments.FragmentAdminManagment
import com.admin.Fragments.FragmentUserManagement

class UserViewPagerAdapter(fragmentActivity: FragmentActivity, private var totalCount: Int) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return totalCount
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentUserManagement()
            1 -> FragmentAdminManagment()
            else -> FragmentUserManagement()
        }
    }
}
