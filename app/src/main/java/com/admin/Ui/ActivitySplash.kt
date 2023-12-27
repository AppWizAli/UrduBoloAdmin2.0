package com.admin.Ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.Adapter.AdapterUserStatus
import com.admin.Data.SharedPrefManager
import com.admin.Models.ModelUser
import com.admin.Models.ModelVideo
import com.admin.Models.UserViewModel
import com.admin.Models.VideoViewModel
import com.urduboltv.admin.R
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.schedule

class ActivitySplash : AppCompatActivity() {
    private lateinit var sharedPrefManager: SharedPrefManager
    private val userViewModel:UserViewModel by viewModels()
    private val videoViewModel:VideoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        actionBar?.hide()
        supportActionBar?.hide()

sharedPrefManager= SharedPrefManager(this@ActivitySplash)
        Timer().schedule(1500){
            storelist()
if(sharedPrefManager.isLoggedIn())
{
    startActivity(Intent(this@ActivitySplash,MainActivity::class.java))
    finish()
}
            else
{

        startActivity(Intent(this@ActivitySplash,ActivityLogin::class.java))
        finish()
}



        }




    }


    private fun storelist()
    {
        var list= ArrayList<ModelUser> ()
        lifecycleScope.launch {

            userViewModel.getUserList()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        if (task.result.size() > 0) {

                            for (document in task.result)
                                list.add(
                                    document.toObject(
                                        ModelUser::class.java
                                    )
                                )


sharedPrefManager.putUserList(list)

                        }

                    }


                }
                .addOnFailureListener {

                }




        }
        var videolist= ArrayList<ModelVideo> ()
        lifecycleScope.launch {

            videoViewModel.getPrivateVideoList()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        if (task.result.size() > 0) {

                            for (document in task.result)
                                videolist.add(
                                    document.toObject(
                                        ModelVideo::class.java
                                    )
                                )


sharedPrefManager.putPrivateVideoList(videolist)

                        }

                    }


                }
                .addOnFailureListener {

                }




        }
    }
}