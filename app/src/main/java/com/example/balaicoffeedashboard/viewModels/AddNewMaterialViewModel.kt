package com.example.balaicoffeedashboard.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.balaicoffeedashboard.models.AddNewBahanRequest
import com.example.balaicoffeedashboard.models.BaseResponse
import com.example.balaicoffeedashboard.models.CategoryListData
import com.example.balaicoffeedashboard.models.CategoryListModel
import com.example.balaicoffeedashboard.networks.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddNewMaterialViewModel: ViewModel() {
    val categoryList: MutableLiveData<MutableList<CategoryListData?>?> = MutableLiveData()
    var selectedCategory: MutableLiveData<CategoryListData> = MutableLiveData()
    val isDoneAddNewBahan: MutableLiveData<Boolean> = MutableLiveData()

    fun getCategoryList(auth: String, context: Context) {
        RetrofitBuilder().getService().getCategoryList(authHeader = auth).enqueue(object:
            Callback<CategoryListModel> {
            override fun onResponse(
                call: Call<CategoryListModel>,
                response: Response<CategoryListModel>
            ) {
                val responseBody = response.body()
                if(responseBody?.status == true) {
                    categoryList.value = responseBody.data?.toMutableList()
                } else {
                    Toast.makeText(context, "Fail to fetch category list", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CategoryListModel>, t: Throwable) {
                categoryList.value = null
                Toast.makeText(context, "Fail to fetch category list", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun addNewBahan(auth: String, context: Context, request: AddNewBahanRequest) {
        RetrofitBuilder().getService().addNewBahan(authHeader = auth, addNewBahanRequest = request).enqueue(object:
            Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                val responseBody = response.body()
                if(responseBody?.status == true) {
                    isDoneAddNewBahan.value = true
                    Toast.makeText(context, "Berhasil menambahkan bahan baru", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Fail to add new bahan", Toast.LENGTH_SHORT).show()
                    isDoneAddNewBahan.value = true
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Toast.makeText(context, "Fail to add new bahan", Toast.LENGTH_SHORT).show()
                isDoneAddNewBahan.value = true
            }
        })
    }
}