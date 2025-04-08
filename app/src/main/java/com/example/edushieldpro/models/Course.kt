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
    var sold:Int = 0,
    val price : String="",
    val description : String="",
    var videos : MutableList<VideoData> = mutableListOf(),
    var students : MutableList<String>  = mutableListOf(),
    val paid:Int = 0,
    var jazzcash : String = ""
) : Parcelable
