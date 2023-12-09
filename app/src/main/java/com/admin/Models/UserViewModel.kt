package com.admin.Models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.admin.Constants
import com.admin.Data.Repo
import com.admin.Data.SharedPrefManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot


class UserViewModel(context: Application) : AndroidViewModel(context) {

    private val repo = Repo(context)
    private val sharedPrefManager = SharedPrefManager(context)
    private var constants = Constants()
    private var context = context


    suspend fun addUser(modelUser: ModelUser): LiveData<Boolean> {
        return repo.addUser(modelUser)
    }
    suspend fun addAdmin(modelUser: Admin): LiveData<Boolean> {
        return repo.addAdmin(modelUser)
    }    suspend fun getAdmin(modelUser: String): Task<QuerySnapshot> {
        return repo.getAdmin(modelUser)
    }
    suspend fun updateAdmin(modelUser: Admin): LiveData<Boolean> {
        return repo.updateAdmin(modelUser)
    } suspend fun updateUser(modelUser: ModelUser): LiveData<Boolean> {
        return repo.updateUser(modelUser)
    }
    suspend fun deletAdmin(modelUser: Admin): LiveData<Boolean> {
        return repo.deletAdmin(modelUser)
    }  suspend fun deleteUser(modelUser: ModelUser): LiveData<Boolean> {
        return repo.deleteUser(modelUser)
    }
    suspend fun getUserList(): Task<QuerySnapshot> {

        return  repo.getUserList()
    }
    suspend fun getAdminList(): Task<QuerySnapshot> {

        return  repo.getAdminList()
    }

}