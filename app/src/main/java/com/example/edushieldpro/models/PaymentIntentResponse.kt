package com.example.edushieldpro.models

import com.google.gson.annotations.SerializedName

data class PaymentIntentResponse(
    @SerializedName("id") val id: String,
    @SerializedName("client_secret") val clientSecret: String
)