package com.example.balaicoffeedashboard.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddNewBahanRequest(
    @SerializedName("bahan_name")
    val bahanName: String,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("bahan_unit")
    val bahanUnit: String,
    @SerializedName("minimum_amount")
    val minimumAmount: Int,
    @SerializedName("is_count_sell")
    val isCountSell: Boolean
): Parcelable
