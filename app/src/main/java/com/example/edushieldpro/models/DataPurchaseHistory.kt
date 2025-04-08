package com.example.edushieldpro.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataPurchaseHistory(
    val id :String,
    val instructorId:String,
    val studentEmail : String,
    val title : String,
    val image : String,
    val date : String,
    val time : String
) : Parcelable{
    constructor() : this("","","","","","","")
}
