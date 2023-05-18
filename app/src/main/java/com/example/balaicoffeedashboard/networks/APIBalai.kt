package com.example.balaicoffeedashboard.networks

import com.example.balaicoffeedashboard.models.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface APIBalai {
    @Headers("Content-Type: application/json")
    @POST("/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("/register")
    fun registerUser(@Body registerRequest: RegisterRequest): Call<BaseResponse>

    @GET("/admin/category")
    fun getCategoryList(@Header("Authorization") authHeader: String): Call<CategoryListModel>

    @GET("/admin/bahan/id")
    fun getAllBahanId(@Header("Authorization") authHeader: String): Call<GetBahanIdsResponseModel>

    @GET("/admin/bahan?id={id}")
    fun getBahanFromId(@Header("Authorization") authHeader: String, @Path("id") id: String): Call<BahanResponseModel>

    @GET("/admin/bahan?category={id}")
    fun getBahanFromCategoryId(@Header("Authorization") authHeader: String, @Path("category") category: String): Call<BahanResponseModel>

    @POST("/admin/bahan/list")
    fun getBahanListFromId(@Header("Authorization") authHeader: String, @Body listIds: RekapTodayRequest):Call<BahanListResponseModel>

    @POST("/admin/bahan/category")
    fun getBahanListByCategoryId(@Header("Authorization") authHeader: String, @Body listIds: CategoryIdListRequest):Call<BahanListResponseModel>

    @POST("/admin/rekap/today")
    fun getRekapToday(@Header("Authorization") authHeader: String, @Body rekapTodayRequest: RekapTodayRequest): Call<RekapTodayResponse>

    @POST("/admin/rekap/update")
    fun updateRekapToday(@Header("Authorization") authHeader: String, @Body updateRekapTodayRequest: UpdateRekapTodayRequest): Call<BaseResponse>

    @POST("/admin/rekap/generate")
    fun generateRekap(@Header("Authorization") authHeader: String, @Body listIds: RekapTodayRequest): Call<BaseResponse>

    @POST("/admin/bahan")
    fun addNewBahan(@Header("Authorization") authHeader: String, @Body addNewBahanRequest: AddNewBahanRequest): Call<BaseResponse>

    @GET("/admin/rekap/export")
    @Streaming
    fun exportRekap(@Header("Authorization") authHeader: String, @Query("rekap_date") rekapDate: String): Call<ResponseBody>

    @GET("/admin/rekap/master")
    @Streaming
    fun exportMaster(@Header("Authorization") authHeader: String): Call<ResponseBody>
}