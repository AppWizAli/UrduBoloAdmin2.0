package com.admin.Ui
import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.urduboltv.admin.R

class ActivityTesterPlayer : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tester_player)

        webView = findViewById(R.id.webView) // Replace with your WebView ID from the layout file

        setupWebView()
        loadVideo()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true

        webView.webChromeClient = object : WebChromeClient() {}
    }

    private fun loadVideo() {
        val videoUrl = "https://share.vidyard.com/watch/rJ7oQ7TeMH1JwA6UNPQaAA" // Replace with your Vidyard video URL
        val html = """
        <html>
            <head>
                <style>
                    body { margin: 0; padding: 0; }
                    .vidyard-share-button { display: none !important; }
                    iframe { width: 100%; height: 100%; }
                </style>
            </head>
            <body style="margin:0">
                <iframe src="$videoUrl" frameborder="0" allowfullscreen></iframe>
            </body>
        </html>
    """.trimIndent()
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

}
