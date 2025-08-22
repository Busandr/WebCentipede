package com.busandr.webcentipede


import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.busandr.webcentipede.Views.CustomWebView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

class BrowserActivity : AppCompatActivity() {
    private val TAG = "BrowserActivity"
    private lateinit var browserWebView: CustomWebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        Log.i(TAG, "onCreate")
        val toolbarMain = findViewById<Toolbar>(R.id.toolbar_browser)
        setSupportActionBar(toolbarMain)

        var linkStr = "ya.ru"
        var searchBar = findViewById<EditText>(R.id.searchbar_browser)
        var faviconImage: ImageView = findViewById(R.id.icon_image)
        val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        browserWebView = findViewById(R.id.browser_webview)
        val confirmLink: View = findViewById(R.id.confirmLink)
        var contentUrl: MutableList<MutableMap<LocalDateTime, String>> =
            mutableListOf(mutableMapOf(LocalDateTime.now() to "empty content"))
        val enterButton = findViewById<Button>(R.id.button_enter)

        searchBar.requestFocus()
        class WebAppInterface {
            @JavascriptInterface
            fun getPageContent(content: String) {
                Log.i(TAG, "Page Content: $content")
            }
        }

        browserWebView.settings.javaScriptEnabled = true
        browserWebView.addJavascriptInterface(WebAppInterface(), "ic")
        searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                println(searchBar.text.toString())
                searchBar.requestFocus()
                linkStr = searchBar.text.toString()
                browserWebView.loadUrl(linkStr)
            }
            false
        }
        enterButton.setOnClickListener {
            Log.i(TAG, "done ")
            linkStr = searchBar.text.toString()
            browserWebView.loadUrl(linkStr)
        }
        Log.i(TAG, linkStr)

        var faviconByteArray: ByteArray = R.drawable.baseline_add_24.toString().toByteArray()
        CoroutineScope(Dispatchers.Main).launch {
            browserWebView.loadUrl(linkStr)

            browserWebView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    view?.loadUrl(request?.url.toString())
                    return true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    var js = "document.body.textContent"
                    view?.evaluateJavascript(js) { result ->
                        Log.i(TAG, "onpagefinished got: $result")
                        contentUrl = mutableListOf(mutableMapOf(LocalDateTime.now() to result))
                    }

                }
            }

            browserWebView.webChromeClient = object : WebChromeClient() {
                override fun onReceivedIcon(view: WebView?, icon: Bitmap) {
                    super.onReceivedIcon(view, icon)
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    icon.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    faviconByteArray = byteArrayOutputStream.toByteArray()
                    faviconImage.setImageBitmap(icon)
                }
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            browserWebView.reload()
            Log.i(TAG, "refresh swipe")
            runOnUiThread {
                swipeRefreshLayout.isRefreshing = false
            }
        }

        confirmLink.setOnClickListener {
            val templink = Link(
                name = linkStr,
                url = linkStr,
                favicon = faviconByteArray,
                content = browserWebView.toString()
            )
            val resultIntent = Intent()
            resultIntent.putExtra("link", templink)
            setResult(RESULT_OK, resultIntent)
            finish()
            Log.i(TAG, "intent to main")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

}
