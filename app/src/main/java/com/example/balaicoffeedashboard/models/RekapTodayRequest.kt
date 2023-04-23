package com.example.balaicoffeedashboard.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RekapTodayRequest(
    @SerializedName("bahan_id")
    val bahanIds: List<Int?>? = null
): Parcelable
