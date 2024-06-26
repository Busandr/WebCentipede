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


        val toolbarMain = findViewById<Toolbar>(R.id.toolbar_browser)
        setSupportActionBar(toolbarMain)
        val linkStr = "google.com"
        var browserWebView: WebView = findViewById(R.id.browser_webview)
        var searchBar = findViewById<EditText>(R.id.searchbar_browser)

       searchBar.setOnEditorActionListener{
           
       }

        browserWebView.settings.javaScriptEnabled = true

        class browserWebViewClient : WebViewClient() {
            //not yet...
        }
        
        browserWebView.webViewClient = browserWebViewClient()
        val confirmLink: View = findViewById(R.id.confirmLink)
        
        browserWebView.loadUrl("https://$linkStr")

        

    }
}
