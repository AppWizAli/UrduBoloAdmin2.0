package com.admin.Models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.admin.Constants
import com.admin.Data.Repo
import com.admin.Data.SharedPrefManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

class SeasonViewModel(context: Application) : AndroidViewModel(context) {

    private val repo = Repo(context)
    private val sharedPrefManager = SharedPrefManager(context)
    private var constants = Constants()
    private var context = context


    suspend fun addSeason(modelSeason: ModelSeason): LiveData<Boolean> {
        return repo.addSeason(modelSeason)
    }
    suspend fun getSeasonList(docId:String): Task<QuerySnapshot> {

        return  repo.getSeasonList(docId)
    }

}