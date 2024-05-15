package com.busandr.webcentipede

import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class BrowserActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        val TAG = "BrowserActivity"
        
        val linkStr = intent.getStringExtra("site").toString()
        var browserWebView: WebView = findViewById(R.id.browser_webview)
        browserWebView.settings.javaScriptEnabled = true

        val intentMainActivity = Intent()

        class browserWebViewClient : WebViewClient() {
            //not yet...
        }
        
        browserWebView.webViewClient = browserWebViewClient()
        val confirmLink: View = findViewById(R.id.confirmLink)
        
        browserWebView.loadUrl("https://$linkStr")

        browserWebView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedIcon(view: WebView, icon: Bitmap) {
                super.onReceivedIcon(view, icon)
            }
        }
    }
}
