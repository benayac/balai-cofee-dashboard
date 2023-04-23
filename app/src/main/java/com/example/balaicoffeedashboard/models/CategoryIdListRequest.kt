package com.example.balaicoffeedashboard.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryIdListRequest (
    @SerializedName("category_id")
    val categoryIds: List<Int?>? = null
): Parcelable