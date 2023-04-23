package com.example.balaicoffeedashboard.models
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterRequest(
    @field:SerializedName("username")
    val username: String? = null,
    @field:SerializedName("password")
    val password: String? = null,
    @field:SerializedName("credential_type")
    val credentialType: String? = null,
): Parcelable