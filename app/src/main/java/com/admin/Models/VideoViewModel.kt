package com.admin.Models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.admin.Constants
import com.admin.Data.Repo
import com.admin.Data.SharedPrefManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

class VideoViewModel(context: Application) : AndroidViewModel(context) {

    private val repo = Repo(context)
    private val sharedPrefManager = SharedPrefManager(context)
    private var constants = Constants()
    private var context = context


    suspend fun addVideo(modelVideo: ModelVideo): LiveData<Boolean> {
        return repo.addVideo(modelVideo)
    }

    fun deleteVideo(modelSeason: ModelVideo): LiveData<Boolean> {
        return repo.deleteVideo(modelSeason)
    }

    suspend fun UpdateVideo(modelSeason: ModelVideo): LiveData<Boolean> {
        return repo.UpdateVideo(modelSeason)
    }

    suspend fun getVideoList(docId: String): Task<QuerySnapshot> {

        return repo.getVideoList(docId)
    }

    suspend fun getUnAssignedPrivateVideo(id: String): Task<QuerySnapshot> {

        return repo.getUnAssignedPrivateVideo(id)
    }



    suspend fun getAssignedVideoList(id: String): Task<QuerySnapshot> {

        return repo.getAssignedVideoList(id)
    }
    suspend fun getAssignedVideoList(id: List<String>): Task<QuerySnapshot> {

        return repo.getAssignedVideoList(id)
    }
    suspend fun getPrivateVideoList(): Task<QuerySnapshot> {

        return  repo.getPrivateVideoList()
    }
    suspend fun getAssignedVideo(id: String): Task<QuerySnapshot> {

        return repo.getAssignedVideo(id)
    }

    suspend fun assignPrivateVidoes(videoManagements:ModelVideoManagment): LiveData<Boolean> {
        return repo.assignPrivateVideos(videoManagements)
    }

}