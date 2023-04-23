package com.example.balaicoffeedashboard.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(
    @field:SerializedName("status")
    val status: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null,
    @field:SerializedName("data")
    val data: Authentication? = null,
): Parcelable

@Parcelize
data class Authentication(
    @field:SerializedName("authentication")
    val authentication: String? = null,
    @field:SerializedName("credential_type")
    val credential_type: String? = null,
): Parcelable