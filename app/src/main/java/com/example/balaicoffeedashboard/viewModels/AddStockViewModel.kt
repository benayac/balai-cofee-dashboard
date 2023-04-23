package com.example.balaicoffeedashboard.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.balaicoffeedashboard.models.*
import com.example.balaicoffeedashboard.networks.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStockViewModel: ViewModel() {

    val isDoneGetCategoryListLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val isDoneUpdateRekapToday: MutableLiveData<Boolean> = MutableLiveData()

    var categoryList: MutableList<CategoryListData?>? = mutableListOf()
    var bahanList: MutableLiveData<MutableList<BahanResponseData?>?> = MutableLiveData<MutableList<BahanResponseData?>?>()
    var rekapToday: MutableLiveData<List<RekapTodayData?>?> = MutableLiveData()
    var selectedBahan: MutableLiveData<BahanResponseData?>? = MutableLiveData()

    fun getCategoryList(auth: String, context: Context) {
        isDoneGetCategoryListLiveData.value = false
        RetrofitBuilder().getService().getCategoryList(authHeader = auth).enqueue(object:
            Callback<CategoryListModel> {
            override fun onResponse(
                call: Call<CategoryListModel>,
                response: Response<CategoryListModel>
            ) {
                val responseBody = response.body()
                if(responseBody?.status == true) {
                    categoryList = responseBody.data?.toMutableList()
                    isDoneGetCategoryListLiveData.value = true
                } else {
                    isDoneGetCategoryListLiveData.value = false
                    Toast.makeText(context, "Fail to fetch category list", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CategoryListModel>, t: Throwable) {
                isDoneGetCategoryListLiveData.value = false
                Toast.makeText(context, "Fail to fetch category list", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getBahanFromCategoryList(auth: String, context: Context, request: CategoryIdListRequest) {
        RetrofitBuilder().getService().getBahanListByCategoryId(authHeader = auth, listIds = request).enqueue(object: Callback<BahanListResponseModel> {
            override fun onResponse(call: Call<BahanListResponseModel>, response: Response<BahanListResponseModel>) {
                val responseBody = response.body()
                if (responseBody?.status == true) {
                    bahanList.value = responseBody.data?.toMutableList()
                } else {
                    Toast.makeText(context, "Fail to fetch bahan list", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BahanListResponseModel>, t: Throwable) {
                Toast.makeText(context, "Fail to fetch bahan list", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun getRekapTodayList(auth: String, context: Context, request: RekapTodayRequest) {
        RetrofitBuilder().getService().getRekapToday(authHeader =  auth, rekapTodayRequest = request).enqueue(object: Callback<RekapTodayResponse> {
            override fun onResponse(call: Call<RekapTodayResponse>, response: Response<RekapTodayResponse>) {
                val responseBody = response.body()
                if (responseBody?.status == true) {
                    val rekap: List<RekapTodayData?>? = responseBody.data
                    rekapToday.value = rekap
                } else {
                    Toast.makeText(context, "Fail to fetch rekap today", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RekapTodayResponse>, t: Throwable) {
                Toast.makeText(context, "Fail to fetch rekap today", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updateRekapToday(auth: String, context: Context, request: UpdateRekapTodayRequest) {
        RetrofitBuilder().getService().updateRekapToday(authHeader = auth, updateRekapTodayRequest = request).enqueue(object: Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                val responseBody = response.body()
                if (responseBody?.status == true) {
                    isDoneUpdateRekapToday.value = true
                    Toast.makeText(context, "Success update rekap today", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Fail to update rekap today", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Toast.makeText(context, "Fail to update rekap today", Toast.LENGTH_SHORT).show()
            }

        })
    }
}