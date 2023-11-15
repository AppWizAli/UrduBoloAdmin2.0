package com.admin.Ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.ui.setupWithNavController
import com.admin.Models.ModelVideo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.urduboltv.admin.R
import com.urduboltv.admin.databinding.ActivityMainBinding
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private lateinit var modelVideo: ModelVideo
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val REQUEST_READ_EXTERNAL_STORAGE = 101
        private const val REQUEST_VIDEO_PICK = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modelVideo = ModelVideo()
        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_User
            )
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
