package com.admin.Ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.urduboltv.admin.R
import java.util.Timer
import kotlin.concurrent.schedule

class ActivitySplash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        actionBar?.hide()
        supportActionBar?.hide()


        Timer().schedule(1500){


                startActivity(Intent(this@ActivitySplash,MainActivity::class.java))
                finish()


        }




    }
}