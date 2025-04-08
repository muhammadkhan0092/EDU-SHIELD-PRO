package com.example.edushieldpro.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ProgressData(
  val uuid: String = "",
  val singleProgress: MutableList<SingleProgress> = mutableListOf()
) : Parcelable
