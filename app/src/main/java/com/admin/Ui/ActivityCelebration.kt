package com.admin.Ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.admin.Data.SharedPrefManager
import com.admin.Utils
import com.urduboltv.admin.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ActivityCelebration : AppCompatActivity() {
    private lateinit var utils:Utils
    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_celebration)
        utils= Utils(this@ActivityCelebration)
        supportActionBar?.hide()
sharedPrefManager=SharedPrefManager(this@ActivityCelebration)


            utils.startCelebration()
            // Using CoroutineScope for handling coroutines
            CoroutineScope(Dispatchers.Main).launch {
                delay(7000) // Delay for 3 seconds
                utils.endcelebration()
                sharedPrefManager.SaveCelebration(false)
            startActivity(Intent(this@ActivityCelebration,MainActivity::class.java))// Call the method to end the celebration after 3 seconds
                }

    }
}