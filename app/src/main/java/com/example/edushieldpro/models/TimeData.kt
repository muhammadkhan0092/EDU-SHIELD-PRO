package com.example.edushieldpro.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeData(
  val minutes : Int = 0,
    val seconds : Int = 0
) : Parcelable
