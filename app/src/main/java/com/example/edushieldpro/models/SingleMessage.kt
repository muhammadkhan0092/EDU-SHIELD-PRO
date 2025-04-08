package com.example.hazir.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SingleMessage(
    val documentId : String = "",
    val sender : String = "",
    val content : String = ""
) : Parcelable