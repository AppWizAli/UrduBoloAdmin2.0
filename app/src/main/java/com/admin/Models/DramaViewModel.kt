    package com.admin.Models

    import android.app.Application
    import android.net.Uri
    import androidx.lifecycle.AndroidViewModel
    import androidx.lifecycle.LiveData
    import com.admin.Constants
    import com.admin.Data.Repo
    import com.admin.Data.SharedPrefManager
    import com.google.android.gms.tasks.Task
    import com.google.firebase.firestore.QuerySnapshot
    import com.google.firebase.storage.UploadTask

    class DramaViewModel(context: Application) : AndroidViewModel(context) {

        private val repo = Repo(context)
        private val sharedPrefManager = SharedPrefManager(context)
        private var constants = Constants()
        private var context = context


        suspend fun addDrama(modelDrama: ModelDrama): LiveData<Boolean> {
            return repo.addDrama(modelDrama)
        }
        suspend fun updateDrama(modelDrama: ModelDrama): LiveData<Boolean> {
            return repo.updateDrama(modelDrama)
        }
        fun deleteDrama(modelSeason: ModelDrama): LiveData<Boolean>
        {
            return  repo.deleteDrama(modelSeason)
        }
        suspend fun getDramalist(): Task<QuerySnapshot> {

            return  repo.getDramaList()
        }






    }