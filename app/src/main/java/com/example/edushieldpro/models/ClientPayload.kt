package com.example.edushieldpro.models

import com.google.gson.annotations.SerializedName

data class ClientPayload(
    @SerializedName("x-amz-credential") val xAmzCredential: String?,
    @SerializedName("x-amz-algorithm") val xAmzAlgorithm: String?,
    @SerializedName("x-amz-date") val xAmzDate: String?,
    @SerializedName("x-amz-signature") val xAmzSignature: String?,
    @SerializedName("key") val key: String?,
    @SerializedName("policy") val policy: String?,
    @SerializedName("uploadLink") val uploadLink: String?
)