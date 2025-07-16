package com.busandr.webcentipede

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.UUID

@Parcelize
data class Link(// TODO: Snapshot
    var name: String = "defname",
    val url: String,
    val id: String = UUID.randomUUID().toString(),
    val favicon: ByteArray = "defname".toByteArray(),
    val dateTime: String = LocalDateTime.now().toString(),
    var content: String = "filler",
    val creationTime: String = LocalDateTime.now().toString()

) : Parcelable
