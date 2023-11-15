package com.admin.Models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.admin.Constants
import com.admin.Data.Repo
import com.admin.Data.SharedPrefManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot


class UserViewModel(context: Application) : AndroidViewModel(context) {

    private val repo = Repo(context)
    private val sharedPrefManager = SharedPrefManager(context)
    private var constants = Constants()
    private var context = context


    suspend fun addUser(modelUser: ModelUser): LiveData<Boolean> {
        return repo.addUser(modelUser)
    }
    suspend fun getUserList(): Task<QuerySnapshot> {

        return  repo.getUserList()
    }

}