package com.busandr.webcentipede.service

import android.app.Service

class VisitService: Service() {
  override fun onCreate() {
    
    super.onCreate()
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
    
    return START_STICKY
  }

  override fun onBind(intent: Intent): IBinder? {
    return null
    }

}
