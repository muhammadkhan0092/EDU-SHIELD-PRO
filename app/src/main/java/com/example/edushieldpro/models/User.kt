package com.example.edushieldpro.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uuid : String,
    var name : String,
    val email : String,
    val password : String,
    val cnic : String,
    val phone : String,
    var status : Int,
    val deviceId : String,
    val type:String,
    var violationType : String,
    var image : String,
    var courses:Int
) : Parcelable{
    constructor() : this("","","","","","",1,"","","","",0)
}
