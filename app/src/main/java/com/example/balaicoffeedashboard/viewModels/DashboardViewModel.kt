package com.example.balaicoffeedashboard.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.balaicoffeedashboard.models.*
import com.example.balaicoffeedashboard.networks.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel: ViewModel() {

    val isDoneGetCategoryListLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val isDoneGetBahanFromCategoryLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val isDoneGetRekapTodayLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    var categoryList: MutableList<CategoryListData?>? = mutableListOf()
    // key = category id, value = list of bahan
    val categoryToBahanMap: MutableMap<Int, MutableList<BahanResponseData?>?> = mutableMapOf()
    // key = bahan id, value = rekap today data
    private val bahanToRekapMap: MutableMap<Int, RekapTodayData?> = mutableMapOf()

    fun generateMaterialDashboardFromCategories(): List<MaterialDashboardModel> {
        val materialDashboardList: MutableList<MaterialDashboardModel> = mutableListOf()
        categoryList?.forEach {
            val category = it
            val bahanList = categoryToBahanMap[it?.categoryId]
            val materialDashboardModel = MaterialDashboardModel(
                categoryName = category?.categoryName ?: "")
            val bahanRekapList: MutableList<BahanRekapData> = mutableListOf()
            bahanList?.forEach { bahan ->
                val rekap = bahanToRekapMap[bahan?.bahanId]
                val bahanRekapData = BahanRekapData(rekap, bahan)
                bahanRekapList.add(bahanRekapData)
            }
            materialDashboardModel.bahanRekapList = bahanRekapList
            materialDashboardList.add(materialDashboardModel)
        }
        return materialDashboardList
    }

    fun getCategoryList(auth: String, context: Context) {
        isDoneGetCategoryListLiveData.value = false
        RetrofitBuilder().getService().getCategoryList(authHeader = auth).enqueue(object: Callback<CategoryListModel>{
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
        isDoneGetBahanFromCategoryLiveData.value = false
        RetrofitBuilder().getService().getBahanListByCategoryId(authHeader = auth, listIds = request).enqueue(object: Callback<BahanListResponseModel> {
            override fun onResponse(call: Call<BahanListResponseModel>, response: Response<BahanListResponseModel>) {
                val responseBody = response.body()
                if (responseBody?.status == true) {
                    val bahan: List<BahanResponseData?>? = responseBody.data
                    request.categoryIds?.forEach { categoryId ->
                        categoryToBahanMap[categoryId ?: 0] = mutableListOf()
                        bahan?.forEach {
                            val bahanCategoryId = it?.categoryId
                            if(bahanCategoryId == categoryId) {
                                categoryToBahanMap[categoryId ?: 0]?.add(it)
                            }
                        }
                    }
                    isDoneGetBahanFromCategoryLiveData.value = true
                } else {
                    isDoneGetBahanFromCategoryLiveData.value = false
                    Toast.makeText(context, "Fail to fetch bahan list", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BahanListResponseModel>, t: Throwable) {
                isDoneGetBahanFromCategoryLiveData.value = false
                Toast.makeText(context, "Fail to fetch bahan list", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun getRekapTodayList(auth: String, context: Context, request: RekapTodayRequest) {
        isDoneGetRekapTodayLiveData.value = false
        RetrofitBuilder().getService().getRekapToday(authHeader =  auth, rekapTodayRequest = request).enqueue(object: Callback<RekapTodayResponse> {
            override fun onResponse(call: Call<RekapTodayResponse>, response: Response<RekapTodayResponse>) {
                val responseBody = response.body()
                if (responseBody?.status == true) {
                    val rekap: List<RekapTodayData?>? = responseBody.data
                    request.bahanIds?.forEachIndexed { i, bahanId ->
                        rekap?.forEach {
                            if (bahanId == it?.bahanId) {
                                bahanToRekapMap[bahanId ?: 0] = it
                            }
                        }
                    }
                    isDoneGetRekapTodayLiveData.value = true
                } else {
                    isDoneGetRekapTodayLiveData.value = false
                    Toast.makeText(context, "Fail to fetch rekap today", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RekapTodayResponse>, t: Throwable) {
                isDoneGetRekapTodayLiveData.value = false
                Toast.makeText(context, "Fail to fetch rekap today", Toast.LENGTH_SHORT).show()
            }

        })
    }
}