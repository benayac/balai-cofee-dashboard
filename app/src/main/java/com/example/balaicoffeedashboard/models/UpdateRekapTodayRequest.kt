package com.example.balaicoffeedashboard.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateRekapTodayRequest(
    @SerializedName("rekap_id")
    var rekapId: Int? = 0,
    @SerializedName("stok_awal")
    var stokAwal: Int? = 0,
    @SerializedName("stok_akhir")
    var stokAkhir: Int? = 0,
    @SerializedName("in_data")
    var inData: Int? = 0,
    @SerializedName("terpakai")
    var terpakai: Int? = 0,
): Parcelable
