package com.example.edushieldpro.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoData(
    val videoId : String = "",
    val videoLength : String = "",
    val videoThumbnail : String = ""
) : Parcelable
