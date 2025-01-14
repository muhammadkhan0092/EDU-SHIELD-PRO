package com.example.edushieldpro.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Course(
    val courseId: String = "",
    val uid : String = "",
    val title : String = "",
    val category: String = "",
    val image: String = "",
    val rating: Double = 0.0,
    val sold:Int = 0,
    val price : String="",
    val description : String="",
    var videos : MutableList<VideoData> = mutableListOf()
) : Parcelable
