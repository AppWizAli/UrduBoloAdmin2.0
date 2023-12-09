package com.admin.Ui

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.admin.Models.ModelSeason
import com.admin.Models.ModelVideo
import com.urduboltv.admin.R

class ActivityPlayer() : AppCompatActivity() {
    private lateinit var webView: WebView
    private var dialog: Dialog? = null
    private var handler: Handler? = null
    private lateinit var modelVideo: ModelVideo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_player)

        // Initialize the dialog
        dialog = Dialog(this)
        dialog!!.setContentView(R.layout.customdialog)
        dialog!!.setCancelable(false)
        handler = Handler()
        dismissDialogAfterDelay(5000)
        webView = findViewById(R.id.webView)
        modelVideo=ModelVideo()
        val dramaJson = intent.getStringExtra("video")
        if (dramaJson != null) {
            modelVideo = ModelVideo.fromString(dramaJson) !!
        }

        val webSettings = webView.getSettings()
        webSettings.javaScriptEnabled = true
        val driveLink =modelVideo.videourl
        val videoId = driveLink.substringAfterLast("/d/").substringBefore("/view")
        val videoUrl = "https://drive.google.com/file/d/$videoId/preview"

        webView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url) // Load the URL inside the WebView
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                view.loadUrl(
                    "javascript:(function() { " +
                            "var shareButton = document.querySelector('.ndfHFb-c4YZDc-Wrql6b'); " +
                            "if (shareButton) shareButton.style.display='none'; " +
                            "var downloadButton = document.querySelector('.ndfHFb-c4YZDc-MZArnb-Q9KMQd'); " +
                            "if (downloadButton) downloadButton.style.display='none'; " +
                            "})()"
                )
            }
        })
        webView.loadUrl(videoUrl)
        showDialog()
    }

    private fun showDialog() {
        if (dialog != null && !dialog!!.isShowing) {
            dialog!!.show()
        }
    }

    private fun dismissDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    private fun dismissDialogAfterDelay(delayMillis: Long) {
        handler!!.postDelayed({ dismissDialog() }, delayMillis)
    }
}