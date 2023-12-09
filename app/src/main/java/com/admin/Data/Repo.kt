package com.admin.Data

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.admin.Constants
import com.admin.Models.Admin
import com.admin.Models.ModelDrama
import com.admin.Models.ModelSeason
import com.admin.Models.ModelUser
import com.admin.Models.ModelVideo
import com.admin.Models.ModelVideoManagment
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await
import okhttp3.internal.concurrent.TaskQueue
import okhttp3.internal.userAgent

class Repo(var context: Context) {


    private var constants = Constants()
    private val sharedPrefManager = SharedPrefManager(context)


    ///////////////////////////   FIREBASE    //////////////////////////////////
    private val db = Firebase.firestore
    private val firebaseStorage = Firebase.storage
    private val storageRef = firebaseStorage.reference

    private var VideoCollection = db.collection(constants.VIDEO_COLLECTION)
    private var UserCollection = db.collection(constants.USER_COLLECTION)
    private var BannerCollection = db.collection(constants.BANNER_COLLECTION)
    private var AdminCollection = db.collection(constants.ADMIN_COLLECTION)
    private var DramaCollection = db.collection(constants.DRAMA_COLLECTION)
    private var SeasonCollection = db.collection(constants.SEASON_COLLECTION)
    private var VideoManagementCollection = db.collection(constants.VIDEOMANAGEMENT_COLLECTION)

    suspend fun addVideo(modelVideo: ModelVideo): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        VideoCollection.add(modelVideo)
            .addOnSuccessListener { documentReference ->
                // Store the generated document ID in the ModelDrama
                modelVideo.docId = documentReference.id

                // Update the document with the stored ID
                VideoCollection.document(documentReference.id).set(modelVideo)
                    .addOnSuccessListener {
                        result.value = true
                    }
                    .addOnFailureListener {
                        result.value = false
                    }
            }
            .addOnFailureListener {
                result.value = false
            }

        return result
    }

    suspend fun addDrama(modelDrama: ModelDrama): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        DramaCollection.add(modelDrama)
            .addOnSuccessListener { documentReference ->
                // Store the generated document ID in the ModelDrama
                modelDrama.docId = documentReference.id

                // Update the document with the stored ID
                DramaCollection.document(documentReference.id).set(modelDrama)
                    .addOnSuccessListener {
                        result.value = true
                    }
                    .addOnFailureListener {
                        result.value = false
                    }
            }
            .addOnFailureListener {
                result.value = false
            }

        return result
    }

    suspend fun updateDrama(modelDrama: ModelDrama):LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        DramaCollection.document(modelDrama.docId).set(modelDrama)
            .addOnSuccessListener {
                result.value=true
                // Update successful, handle any success cases if needed
            }
            .addOnFailureListener {
                result.value=false

                // Handle failure scenarios if needed
            }
        return result
    }

    suspend fun getDramaList(): Task<QuerySnapshot> {
        return DramaCollection.get()
    }

    suspend fun getSeasonList(docId:String): Task<QuerySnapshot> {
        return SeasonCollection.whereEqualTo(constants.DRAMA_ID,docId).get()
    }
    suspend fun getVideoList(docId:String): Task<QuerySnapshot> {
        return VideoCollection.whereEqualTo(constants.SEASON_ID,docId).get()
    }
    suspend fun getPrivateVideoList(): Task<QuerySnapshot> {
        return VideoCollection.whereEqualTo(constants.PRIVACY,constants.VIDEO_PRIVACY_PRIVATE).get()
    }
    suspend fun getUserList(): Task<QuerySnapshot> {
        return UserCollection.get()
    }
    suspend fun addAdmin(modelUser: Admin): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        try {
            val documentReference = AdminCollection.add(modelUser).await()
            modelUser.docId = documentReference.id

            AdminCollection.document(documentReference.id).set(modelUser).await()
            result.value = true
        } catch (e: Exception) {
            result.value = false
        }

        return result
    }

    suspend fun updateAdmin(modelDrama: Admin):LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        AdminCollection.document(modelDrama.docId).set(modelDrama)
            .addOnSuccessListener {
                result.value=true
                // Update successful, handle any success cases if needed
            }
            .addOnFailureListener {
                result.value=false

                // Handle failure scenarios if needed
            }
        return result
    }    suspend fun updateUser(modelDrama: ModelUser):LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        UserCollection.document(modelDrama.userId).set(modelDrama)
            .addOnSuccessListener {
                result.value=true
                // Update successful, handle any success cases if needed
            }
            .addOnFailureListener {
                result.value=false

                // Handle failure scenarios if needed
            }
        return result
    }
    fun deletAdmin(modelSeason: Admin) :LiveData<Boolean>{
      //  Toast.makeText(context, modelSeason.docId, Toast.LENGTH_SHORT).show()
        val result = MutableLiveData<Boolean>()
        AdminCollection.document(modelSeason.docId).delete()
            .addOnSuccessListener {
                result.value=true
                // Deletion successful, handle any success cases if needed
            }
            .addOnFailureListener {
                result.value=false
                // Handle failure scenarios if needed
            }
        return result
    }
    fun deleteUser(modelSeason: ModelUser) :LiveData<Boolean>{
      //  Toast.makeText(context, modelSeason.docId, Toast.LENGTH_SHORT).show()
        val result = MutableLiveData<Boolean>()
        UserCollection.document(modelSeason.userId).delete()
            .addOnSuccessListener {
                result.value=true
                // Deletion successful, handle any success cases if needed
            }
            .addOnFailureListener {
                result.value=false
                // Handle failure scenarios if needed
            }
        return result
    }

    suspend fun getAdminList(): Task<QuerySnapshot> {
        return AdminCollection.get()
    }
    suspend fun getAdmin(admin: String): Task<QuerySnapshot> {
        return AdminCollection.whereEqualTo("email",admin).get()
    }


    suspend fun addSeason(modelSeason: ModelSeason): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        SeasonCollection.add(modelSeason)
            .addOnSuccessListener { documentReference ->
                // Store the generated document ID in the ModelDrama
                modelSeason.docId = documentReference.id

                // Update the document with the stored ID
                SeasonCollection.document(documentReference.id).set(modelSeason)
                    .addOnSuccessListener {
                        result.value = true
                    }
                    .addOnFailureListener {
                        result.value = false
                    }
            }
            .addOnFailureListener {
                result.value = false
            }

        return result
    }


    suspend fun addUser(modelUser: ModelUser): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        UserCollection.add(modelUser)
            .addOnSuccessListener { documentReference ->
                // Store the generated document ID in the ModelDrama
                modelUser.userId = documentReference.id

                // Update the document with the stored ID
                UserCollection.document(documentReference.id).set(modelUser)
                    .addOnSuccessListener {
                        result.value = true
                    }
                    .addOnFailureListener {
                        result.value = false
                    }
            }
            .addOnFailureListener {
                result.value = false
            }

        return result
    }
    suspend fun assignPrivateVideos(videoManagements: ArrayList<ModelVideoManagment>): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        for (videoManagment in videoManagements) {
            VideoManagementCollection.add(videoManagment)
                .addOnSuccessListener {
                    // Handle success if needed
                }
                .addOnFailureListener {
                    result.value = false
                    // Handle failure if needed
                }
                .await()  // This suspends until the operation is complete
        }

        result.value = true  // Set true after all items are processed successfully
        return result
    }


fun deleteSeason(modelSeason: ModelSeason) :LiveData<Boolean>{
        val result = MutableLiveData<Boolean>()
        SeasonCollection.document(modelSeason.docId).delete()
            .addOnSuccessListener {
                result.value=true
                // Deletion successful, handle any success cases if needed
            }
            .addOnFailureListener {
                result.value=false
                // Handle failure scenarios if needed
            }
        return result
    }
    fun deleteDrama(modelSeason: ModelDrama) :LiveData<Boolean>{
        val result = MutableLiveData<Boolean>()
        DramaCollection.document(modelSeason.docId).delete()
            .addOnSuccessListener {
                result.value=true
                // Deletion successful, handle any success cases if needed
            }
            .addOnFailureListener {
                result.value=false
                // Handle failure scenarios if needed
            }
        return result
    }
    fun deleteVideo(modelSeason: ModelVideo) :LiveData<Boolean>{
        val result = MutableLiveData<Boolean>()
        VideoCollection.document(modelSeason.docId).delete()
            .addOnSuccessListener {
                result.value=true
                // Deletion successful, handle any success cases if needed
            }
            .addOnFailureListener {
                result.value=false
                // Handle failure scenarios if needed
            }
        return result
    }

    suspend fun updateSeason(modelDrama: ModelSeason):LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        SeasonCollection.document(modelDrama.docId).set(modelDrama)
            .addOnSuccessListener {
                result.value=true
                // Update successful, handle any success cases if needed
            }
            .addOnFailureListener {
                result.value=false

                // Handle failure scenarios if needed
            }
        return result
    }
    suspend fun UpdateVideo(modelDrama: ModelVideo):LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        VideoCollection.document(modelDrama.docId).set(modelDrama)
            .addOnSuccessListener {
                result.value=true
                // Update successful, handle any success cases if needed
            }
            .addOnFailureListener {
                result.value=false

                // Handle failure scenarios if needed
            }
        return result
    }

    suspend fun getSeasonbyId(type:String):Task<DocumentSnapshot>
    {
       return SeasonCollection.document(type).get()
    }
}