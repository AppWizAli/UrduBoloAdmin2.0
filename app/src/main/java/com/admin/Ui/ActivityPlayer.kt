package com.admin.Ui

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.admin.Models.ModelVideo
import com.urduboltv.admin.databinding.ActivityPlayerBinding

class ActivityPlayer : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var modelVideo: ModelVideo
    private var isFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modelVideo = ModelVideo()

      /*  binding.zoom.setOnClickListener {
            toggleFullScreen()
        }*/

        val dramaJson = intent.getStringExtra("video")
        if (dramaJson != null) {
            modelVideo = ModelVideo.fromString(dramaJson)!!
        }

        playVideo(modelVideo.videourl)
    }

    private fun toggleFullScreen() {
        if (isFullScreen) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            supportActionBar?.show()
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            supportActionBar?.hide()
        }
        isFullScreen = !isFullScreen
    }

    private fun playVideo(videoUrl: String) {
        val mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)

        val videoUri = Uri.parse(videoUrl)
        binding.videoView.setVideoURI(videoUri)

        binding.videoView.setOnPreparedListener {
            // Start the video
            binding.videoView.start()

          /*  // Check the video position in a loop
            checkVideoPosition()*/
        }
    }

    private fun checkVideoPosition() {
        val duration = binding.videoView.duration
        val currentPosition = binding.videoView.currentPosition

        if (duration - currentPosition < 5000) {
            // If less than 5 seconds remaining, switch to portrait mode
            toggleFullScreen()
        }

        // Repeat the check every second
        binding.videoView.postDelayed({ checkVideoPosition() }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
