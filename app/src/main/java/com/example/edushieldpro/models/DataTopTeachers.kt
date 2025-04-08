package com.example.edushieldpro.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataTopTeachers(
    var name : String,
    var coursesSold : String,
    var image : String
) : Parcelable{
    constructor() : this("","","")
}
