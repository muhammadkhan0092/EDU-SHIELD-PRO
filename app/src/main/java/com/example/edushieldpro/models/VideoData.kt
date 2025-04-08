package com.example.edushieldpro.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoData(
    val videoId : String = "",
    val videoLength : String = "",
    val videoThumbnail : String = "",
    val title : String = "",
    val timeData: TimeData = TimeData(0,0),
    var watchedData : TimeData = TimeData(0,0)
) : Parcelable
