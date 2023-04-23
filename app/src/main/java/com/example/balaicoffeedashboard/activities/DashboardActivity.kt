package com.example.balaicoffeedashboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.balaicoffeedashboard.App
import com.example.balaicoffeedashboard.SharedPref
import com.example.balaicoffeedashboard.databinding.ActivityDashboardBinding
import com.example.balaicoffeedashboard.models.*
import com.example.balaicoffeedashboard.viewModels.DashboardViewModel
import com.example.balaicoffeedashboard.views.MaterialDashboardAdapter

class DashboardActivity : AppCompatActivity() {
    private val sharedPref: SharedPref by lazy { App.prefs!! }
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: DashboardViewModel
    private var materialDashboardModelList: List<MaterialDashboardModel> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        initObserver()
        loadAPI()
        binding.leftArrowImageView.setOnClickListener {
            finish()
        }
    }

    private fun initObserver() {
        viewModel.isDoneGetCategoryListLiveData.observe(this) { isDone ->
            if (isDone) {
                val idList: MutableList<Int> = mutableListOf()
                viewModel.categoryList?.forEach {
                    idList.add(it?.categoryId ?: 0)
                }
                viewModel.getBahanFromCategoryList(auth = sharedPref.authenticationPref, context = this, request = CategoryIdListRequest(idList))
            }
        }
        viewModel.isDoneGetBahanFromCategoryLiveData.observe(this) { isDone ->
            if (isDone) {
                val idList: MutableList<Int> = mutableListOf()
                viewModel.categoryToBahanMap.forEach {
                    val bahanList = it.value
                    bahanList?.forEach {
                        idList.add(it?.bahanId ?: 0)
                    }
                }
                viewModel.getRekapTodayList(auth = sharedPref.authenticationPref, context = this, request = RekapTodayRequest(idList))
            }
        }
        viewModel.isDoneGetRekapTodayLiveData.observe(this) { isDone ->
            if (isDone) {
                materialDashboardModelList = viewModel.generateMaterialDashboardFromCategories()
                val adapter = MaterialDashboardAdapter(materialDashboardModelList)
                binding.accordionView.adapter = adapter
                binding.accordionView.updatePosition(0)
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun loadAPI() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getCategoryList(auth = sharedPref.authenticationPref, this)
    }

}