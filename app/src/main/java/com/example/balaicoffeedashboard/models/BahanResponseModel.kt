package com.example.balaicoffeedashboard.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BahanResponseModel(
    @SerializedName("status")
    val status: Boolean? = false,
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("data")
    val data: BahanResponseData? = null
): Parcelable

@Parcelize
data class BahanResponseData(
    @SerializedName("bahan_id")
    val bahanId: Int? = 0,
    @SerializedName("bahan_name")
    val bahanName: String? = "",
    @SerializedName("category_id")
    val categoryId: Int? = 0,
    @SerializedName("bahan_unit")
    val bahanUnit: String? = "",
    @SerializedName("minimum_amount")
    val minimumAmount: Int? = 0,
    @SerializedName("is_count_sell")
    val isCountSell: Boolean? = false,
): Parcelable