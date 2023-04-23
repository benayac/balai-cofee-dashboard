package com.example.balaicoffeedashboard.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BahanListResponseModel(
    @SerializedName("status")
    val status: Boolean? = false,
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("data")
    val data: List<BahanResponseData?>? = null
): Parcelable