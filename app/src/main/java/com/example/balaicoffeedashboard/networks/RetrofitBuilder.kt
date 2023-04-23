package com.example.balaicoffeedashboard.networks

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder {
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://104.208.68.255:8080")
            .build()
    }

    fun getService() = getRetrofit().create(APIBalai::class.java)
}