package com.example.edushieldpro.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataPopularCourses(
    var title : String,
    var students : String,
    var image : String,
    var courseId : String
) : Parcelable{
    constructor() : this("","","","")
}
