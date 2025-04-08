package com.example.edushieldpro.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SingleProgress(
    val videoId : String = "",
    var timeData: TimeData = TimeData(0,0)
) : Parcelable
