package com.example.balaicoffeedashboard.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryListModel(
    @SerializedName("status")
    val status: Boolean? = false,
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("data")
    val data: List<CategoryListData?>? = null
): Parcelable

@Parcelize
data class CategoryListData(
    @SerializedName("category_id")
    val categoryId: Int? = 0,
    @SerializedName("category_name")
    val categoryName: String? = ""
): Parcelable