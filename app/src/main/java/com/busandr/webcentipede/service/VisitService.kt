package com.busandr.webcentipede.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.busandr.webcentipede.DatabaseHelper
import com.busandr.webcentipede.Link
import com.busandr.webcentipede.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class VisitService : Service() {

    private val TAG = "VisitService"
    private val GROUP_KEY = "..."
    private val channelId = "Notification from Service"

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    var isActive = true

    override fun onCreate() {
        super.onCreate()
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                channelId,
                "VisitService channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        } else {
            Toast.makeText(
                this,
                "This app requires Android O or higher. Exiting...",
                Toast.LENGTH_LONG
            ).show();
            System.exit(0)
        }
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            channel as NotificationChannel
        )
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "service is started")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "you AGAIN dont wanna be aware of...", Toast.LENGTH_LONG).show()
        }
        val notifications = NotificationCompat.Builder(this, channelId)
            .setContentTitle("watching service")
            .setContentText("receivedContent")
            .setGroup(GROUP_KEY)
            .setGroupSummary(true)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(5, notifications)
        val dbHelper = DatabaseHelper.DatabaseManager.getInstance(this@VisitService)
        val linkList = dbHelper!!.readAllLinks().sortedBy { it.creationTime }

        coroutineScope.launch(Dispatchers.IO) {
            Log.i(TAG, "service coroutine has started")
            while (isActive) {
                for (link in linkList) {
                    println("link in cycle $link")
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url("https://${link.url}")
                        .build()
                    try {
                        client.newCall(request).execute().use { response ->
                            Log.i(TAG, "Response Code: ${response.code} $response")
                            var document = Jsoup.parse(response.body!!.string())
                            
                            fun cleanHtml(html: String): String {
                                val combinedRegex = Regex("\\{.*?\\}")
                                val cleanedHtml = html.replace(combinedRegex, "")
                                return cleanedHtml
                            }

                            var ch = document.body().text()

                            var receivedContent =
                                cleanHtml(ch.toString())

                            Log.i(
                                TAG,
                                "receivedContent $receivedContent \n link.content ${link.content}"
                            )
                            if (receivedContent != link.content) {
                                val notificationAboutChange =
                                    NotificationCompat.Builder(this@VisitService, channelId)
                                        .setContentTitle(link.url)
                                        .setContentText("changes has arrived")
                                        .setGroup(GROUP_KEY)
                                        .setGroupSummary(true)
                                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                                        .build()

                                dbHelper.insertLink(
                                    Link(
                                        name = isActive.toString(),
                                        url = link.url,
                                        content = receivedContent
                                    )
                                )
                                notificationManager.notify(4, notificationAboutChange)
                            }
                            Log.i(
                                TAG,
                                "after unique check \n receivedContent $receivedContent \n link.content ${link.content}"
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                }
                Thread.sleep(2000)
            }
            notificationManager.notify(5, notifications)
            Log.i(TAG, isActive.toString())
        }
        startForeground(1, notifications)
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isActive = false
        Log.i(TAG, "Service onDestroy")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

    }
}
