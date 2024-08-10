package com.busandr.webcentipede.service

import android.app.Service

class VisitService: Service() {
  private val TAG = "VisitService"
  private val channelId = "Notification from Service"
  
  override fun onCreate() {
    super.onCreate()
    Log.i(TAG, "Service onCreate")
    
    val notificationChannel = NotificationChannel(
      channelId,
      "VisitService",
      NotificationManager.IMPORTANCE_DEFAULT
    )
    (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            notificationChannel
        )
  }

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

    Log.i(TAG, "service is started")
    val notifications = NotificationCompat.Builder(this, channelId)
        .setContentTitle("watching service")
        .setContentText("input")
        .setSmallIcon(R.drawable.notification_bg)
        .build()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT > 32
        ) {
            Toast.makeText(this, "you AGAIN dont wanna be aware of...", Toast.LENGTH_LONG).show()

        }
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(3, notifications)
        startForeground(1, notifications)

        GlobalScope.launch {
          val client = OkHttpClient()
          val url = "https://dayspedia.com/time/online/"       
          val request = Request.Builder()
            .url(url)
            .build()
        }
    
    return START_STICKY
  }

  override fun onBind(intent: Intent): IBinder? {
    return null
    }

  override fun onDestroy() {
    super.onDestroy()
    Log.i(TAG, "Service onDestroy")
    stopForeground(STOP_FOREGROUND_REMOVE)
    stopSelf()
    
  }


}
