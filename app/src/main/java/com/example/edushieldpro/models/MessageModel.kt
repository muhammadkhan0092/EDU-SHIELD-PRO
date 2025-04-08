package com.example.edushieldpro.models

import android.os.Parcelable
import com.example.hazir.data.SingleMessage
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageModel(
    val id : String = "",
    val userId : String = "",
    var messages : List<SingleMessage> = mutableListOf(),
    val image : String = ""
) : Parcelable