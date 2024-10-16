package com.busandr.webcentipede

import android.graphics.Bitmap
import java.util.UUID

data class Link(
    var name: String,
    val url: String,
    val id: String = createId(),
    val favicon: ByteArray,
    var content: String = "empty, for now",
    var isActive: Boolean = true,
    var lastCheckTime: LocalDateTime = LocalDateTime.MIN,
    var lastCheckResult: String = "404",
    var wifiFrequency: Long = 1,
    var mobileFrequency: Long = 1,
    val creationTime: String = LocalDateTime.now().toString(),
    var keepData: Boolean = false
) {
    companion object{
        fun createId(): String{
            return UUID.randomUUID().toString()
        }
    }
}
