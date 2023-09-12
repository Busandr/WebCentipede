package com.example.webcentipede

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException
import kotlin.concurrent.thread

import android.Manifest
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts


//todo make this code works automatic, without the button
class LinkViewActivity : AppCompatActivity() {

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
              //todo method is not yet implemented
            } else {
                Toast.makeText(this, "you dont wanna be aware of...", Toast.LENGTH_LONG)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Activity"

        var numberCounts = 5
        var numberSleeps: Long = 2000

        val perm = Manifest.permission.POST_NOTIFICATIONS

        val fChannel = NotificationChannel("fch", "fchname", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(fChannel)

        val summaryBuilder = NotificationCompat.Builder(this@LinkViewActivity, fChannel.id)
            .setSmallIcon(R.drawable.baseline_add_24)
            .setContentTitle("Group Summary")
            .setContentText("You have new messages")
            .setGroupSummary(true)
            .setGroup("2")

        val builder = NotificationCompat.Builder(this@LinkViewActivity, fChannel.id)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Hey ya")
            .setContentText("Some changes.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setGroup("2")
            .build()

        fun goTo() {
            thread {
                val textView = findViewById<TextView>(R.id.text_View)
                val client = OkHttpClient()

                //todo add way through cloudflare n stuff
                // !
                //todo add parser of js
                val url = "https://dayspedia.com/time/online/"
                val request = Request.Builder()
                    .url(url)
                    .build()

                repeat(numberCounts) {
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            e.printStackTrace()
                        }
                        override fun onResponse(call: Call, response: Response) {
                            val responseBody =
                                response.body?.string()
                            val doc = Jsoup.parse(responseBody)
                            val element =
                                doc.select("div.time[location=\"Europe/Moscow\"]")
                                    .text()
                            
                            runOnUiThread {
                                textView.text = element
                            }
                        }
                    })
                    Thread.sleep(/* millis = */ numberSleeps)
                }
            }
        }

        findViewById<Button>(R.id.button)
            .setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT > 32
                ) {
                    requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    goTo()
                    val nManager = NotificationManagerCompat.from(applicationContext)
                    nManager.notify(3, summaryBuilder.build())
                    nManager.notify(1, builder)
                    nManager.notify(2, builder)

                }
            }
    }
}
