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
  }

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    
    return START_STICKY
  }

}
