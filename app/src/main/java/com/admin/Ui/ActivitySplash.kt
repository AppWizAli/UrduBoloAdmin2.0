package com.admin.Ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.admin.Data.SharedPrefManager
import com.urduboltv.admin.R
import java.util.Timer
import kotlin.concurrent.schedule

class ActivitySplash : AppCompatActivity() {
    private lateinit var sharedPrefManager: SharedPrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        actionBar?.hide()
        supportActionBar?.hide()

sharedPrefManager= SharedPrefManager(this@ActivitySplash)
        Timer().schedule(1500){

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
}