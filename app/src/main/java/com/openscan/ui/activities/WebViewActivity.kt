package com.openscan.ui.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.openscan.R

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val webView = findViewById<WebView>(R.id.webView)

        webView.webViewClient = WebViewClient()
        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        val url = intent.getStringExtra("uri")
        webView.loadUrl("https://drive.google.com/file/d/1uyN-S6YQTa4CCAfFfzmq8RUMuHeCo24T/view?usp=sharing")
        //webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$url")
    }
}