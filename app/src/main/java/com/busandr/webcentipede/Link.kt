package com.busandr.webcentipede

import android.graphics.Bitmap
import java.util.UUID

data class Link(
    var name: String,
    val url: String,
    val id: String = createId(),
    val creationTime: Long = System.currentTimeMillis(),
    var favicon: Bitmap? = null,
    var isActive: Boolean = true,
    var keepData: Boolean = false,

    var wifiFrequency: Long = 1,
    var mobileFrequency: Long = 1
) {
    companion object{
        fun createId(): String{
            return UUID.randomUUID().toString()
        }
    }
}
